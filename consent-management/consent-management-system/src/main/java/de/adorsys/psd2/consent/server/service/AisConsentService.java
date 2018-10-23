/*
 * Copyright 2018-2018 adorsys GmbH & Co KG
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.adorsys.psd2.consent.server.service;

import de.adorsys.psd2.consent.api.ActionStatus;
import de.adorsys.psd2.consent.api.CmsAspspConsentDataBase64;
import de.adorsys.psd2.consent.api.CmsConsentStatus;
import de.adorsys.psd2.consent.api.ais.*;
import de.adorsys.psd2.consent.server.account.AccountAccessHolder;
import de.adorsys.psd2.consent.server.domain.account.AccountAccess;
import de.adorsys.psd2.consent.server.domain.account.AisConsent;
import de.adorsys.psd2.consent.server.domain.account.AisConsentAction;
import de.adorsys.psd2.consent.server.domain.account.AisConsentAuthorization;
import de.adorsys.psd2.consent.server.repository.AisConsentActionRepository;
import de.adorsys.psd2.consent.server.repository.AisConsentAuthorizationRepository;
import de.adorsys.psd2.consent.server.repository.AisConsentRepository;
import de.adorsys.psd2.consent.server.service.mapper.AisConsentMapper;
import de.adorsys.psd2.xs2a.core.sca.ScaStatus;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static de.adorsys.psd2.consent.api.CmsConsentStatus.*;
import static de.adorsys.psd2.consent.api.TypeAccess.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // TODO temporary solution to switch off Hibernate dirty check. Need to understand why objects are changed here. https://git.adorsys.de/adorsys/xs2a/aspsp-xs2a/issues/364
public class AisConsentService {
    private final AisConsentRepository aisConsentRepository;
    private final AisConsentActionRepository aisConsentActionRepository;
    private final AisConsentAuthorizationRepository aisConsentAuthorizationRepository;
    private final AisConsentMapper consentMapper;

    /**
     * Create AIS consent
     *
     * @param request needed parameters for creating AIS consent
     * @return String consent id
     */
    @Transactional
    public Optional<String> createConsent(CreateAisConsentRequest request) {
        AisConsent consent = createConsentFromRequest(request);
        consent.setExternalId(UUID.randomUUID().toString());
        AisConsent saved = aisConsentRepository.save(consent);
        return saved.getId() != null
                   ? Optional.ofNullable(saved.getExternalId())
                   : Optional.empty();
    }

    /**
     * Read status of consent by id
     *
     * @param consentId id of consent
     * @return ConsentStatus
     */
    public Optional<CmsConsentStatus> getConsentStatusById(String consentId) {
        return getAisConsentById(consentId)
                   .map(this::checkAndUpdateOnExpiration)
                   .map(AisConsent::getConsentStatus);
    }

    /**
     * Update consent status by id
     *
     * @param consentId id of consent
     * @param status    new consent status
     * @return Boolean
     */
    @Transactional
    public boolean updateConsentStatusById(String consentId, CmsConsentStatus status) {
        return getActualAisConsent(consentId)
                   .map(con -> setStatusAndSaveConsent(con, status))
                   .map(con -> con.getConsentStatus() == status)
                   .orElse(false);
    }

    /**
     * Read full information of consent by id
     *
     * @param consentId id of consent
     * @return AisAccountConsent
     */
    public Optional<AisAccountConsent> getAisAccountConsentById(String consentId) {
        return getAisConsentById(consentId)
                   .map(this::checkAndUpdateOnExpiration)
                   .map(consentMapper::mapToAisAccountConsent);
    }

    /**
     * Save information about uses of consent
     *
     * @param request needed parameters for logging usage AIS consent
     */
    @Transactional
    public void checkConsentAndSaveActionLog(AisConsentActionRequest request) {
        AisConsent consent = getAisConsentById(request.getConsentId())
                                 .orElse(null);
        checkAndUpdateOnExpiration(consent);
        updateAisConsentCounter(consent);
        logConsentAction(request.getConsentId(), resolveConsentActionStatus(request, consent), request.getTppId());
    }

    /**
     * Update AIS consent account access by id
     *
     * @param request   needed parameters for updating AIS consent
     * @param consentId id of the consent to be updated
     * @return String   consent id
     */
    @Transactional
    public Optional<String> updateAccountAccess(String consentId, AisAccountAccessInfo request) {
        return getActualAisConsent(consentId)
                   .map(consent -> {
                       consent.addAccountAccess(readAccountAccess(request));
                       return aisConsentRepository.save(consent)
                                  .getExternalId();
                   });
    }

    /**
     * Get Ais aspsp consent data by id
     *
     * @param consentId id of the consent
     * @return Response containing aspsp consent data
     */
    public Optional<CmsAspspConsentDataBase64> getAspspConsentData(String consentId) {
        return getActualAisConsent(consentId)
                   .map(this::getConsentAspspData);
    }

    /**
     * Update AIS consent aspsp consent data by id
     *
     * @param request   Aspsp provided ais consent data
     * @param consentId id of the consent to be updated
     * @return String   consent id
     */
    @Transactional
    public Optional<String> saveAspspConsentDataInAisConsent(String consentId, CmsAspspConsentDataBase64 request) {
        return getActualAisConsent(consentId)
                   .map(cons -> saveAspspConsentDataInAisConsent(request, cons));
    }

    /**
     * Create consent authorization
     *
     * @param consentId id of consent
     * @param request   needed parameters for creating consent authorization
     * @return String authorization id
     */
    @Transactional
    public Optional<String> createAuthorization(String consentId, AisConsentAuthorizationRequest request) {
        return aisConsentRepository.findByExternalIdAndConsentStatusIn(consentId, EnumSet.of(RECEIVED, VALID))
                   .map(aisConsent -> saveNewAuthorization(aisConsent, request));
    }

    /**
     * Get consent authorization
     *
     * @param consentId       id of consent
     * @param authorizationId id of authorisation session
     * @return AisConsentAuthorizationResponse
     */
    public Optional<AisConsentAuthorizationResponse> getAccountConsentAuthorizationById(String authorizationId, String consentId) {
        return aisConsentRepository.findByExternalIdAndConsentStatusIn(consentId, EnumSet.of(RECEIVED, VALID)).isPresent()
                   ? aisConsentAuthorizationRepository.findByExternalId(authorizationId)
                         .map(consentMapper::mapToAisConsentAuthorizationResponse)
                   : Optional.empty();
    }

    /**
     * Update consent authorization
     *
     * @param authorizationId id of authorisation session
     * @param request         needed parameters for updating consent authorization
     * @return boolean
     */
    @Transactional
    public boolean updateConsentAuthorization(String authorizationId, AisConsentAuthorizationRequest request) {
        Optional<AisConsentAuthorization> aisConsentAuthorizationOptional = aisConsentAuthorizationRepository.findByExternalId(authorizationId);

        if (!aisConsentAuthorizationOptional.isPresent()) {
            return false;
        }

        AisConsentAuthorization aisConsentAuthorization = aisConsentAuthorizationOptional.get();

        if (ScaStatus.STARTED == aisConsentAuthorization.getScaStatus()) {
            aisConsentAuthorization.setPsuId(request.getPsuId());
            // TODO refactor logic and don't save tan and password data in plain text https://git.adorsys.de/adorsys/xs2a/aspsp-xs2a/issues/390
            aisConsentAuthorization.setPassword(request.getPassword());
        }

        if (ScaStatus.SCAMETHODSELECTED == request.getScaStatus()) {
            // TODO refactor logic and don't save tan and password data in plain text https://git.adorsys.de/adorsys/xs2a/aspsp-xs2a/issues/390
            aisConsentAuthorization.setAuthenticationMethodId(request.getAuthenticationMethodId());
        }

        aisConsentAuthorization.setScaStatus(request.getScaStatus());
        aisConsentAuthorization = aisConsentAuthorizationRepository.save(aisConsentAuthorization);

        return aisConsentAuthorization.getExternalId() != null;
    }


    private Set<AccountAccess> readAccountAccess(AisAccountAccessInfo access) {
        AccountAccessHolder holder = new AccountAccessHolder();
        holder.fillAccess(access.getAccounts(), ACCOUNT);
        holder.fillAccess(access.getBalances(), BALANCE);
        holder.fillAccess(access.getTransactions(), TRANSACTION);
        return holder.getAccountAccesses();
    }

    private CmsAspspConsentDataBase64 getConsentAspspData(AisConsent consent) {
        CmsAspspConsentDataBase64 response = new CmsAspspConsentDataBase64();
        String aspspConsentDataBase64 = Optional.ofNullable(consent.getAspspConsentData())
                                            .map(bytes -> Base64.getEncoder().encodeToString(bytes))
                                            .orElse(null);
        response.setAspspConsentDataBase64(aspspConsentDataBase64);
        response.setConsentId(consent.getExternalId());
        return response;
    }

    private String saveAspspConsentDataInAisConsent(CmsAspspConsentDataBase64 request, AisConsent consent) {
        byte[] aspspConsentData = Optional.ofNullable(request.getAspspConsentDataBase64())
                                      .map(aspspConsentDataBase64 -> Base64.getDecoder().decode(aspspConsentDataBase64))
                                      .orElse(null);
        consent.setAspspConsentData(aspspConsentData);
        AisConsent savedConsent = aisConsentRepository.save(consent);
        return savedConsent.getExternalId();
    }

    private AisConsent createConsentFromRequest(CreateAisConsentRequest request) {

        AisConsent consent = new AisConsent();
        consent.setConsentStatus(RECEIVED);
        consent.setAllowedFrequencyPerDay(request.getAllowedFrequencyPerDay());
        consent.setTppFrequencyPerDay(request.getRequestedFrequencyPerDay());
        consent.setUsageCounter(request.getAllowedFrequencyPerDay()); // Initially we set maximum and then decrement it by usage
        consent.setRequestDateTime(LocalDateTime.now());
        consent.setExpireDate(request.getValidUntil());
        consent.setPsuId(request.getPsuId());
        consent.setTppId(request.getTppId());
        consent.addAccountAccess(readAccountAccess(request.getAccess()));
        consent.setRecurringIndicator(request.isRecurringIndicator());
        consent.setTppRedirectPreferred(request.isTppRedirectPreferred());
        consent.setCombinedServiceIndicator(request.isCombinedServiceIndicator());
        consent.setAisConsentRequestType(getRequestTypeFromAccess(request.getAccess()));

        return consent;
    }

    private AisConsentRequestType getRequestTypeFromAccess(AisAccountAccessInfo accessInfo) {
        if (accessInfo.getAllPsd2() == AisAccountAccessType.ALL_ACCOUNTS) {
            return AisConsentRequestType.GLOBAL;
        } else if (EnumSet.of(AisAccountAccessType.ALL_ACCOUNTS, AisAccountAccessType.ALL_ACCOUNTS_WITH_BALANCES).contains(accessInfo.getAvailableAccounts())) {
            return AisConsentRequestType.ALL_AVAILABLE_ACCOUNTS;
        } else if (isEmptyAccess(accessInfo)) {
            return AisConsentRequestType.BANK_OFFERED;
        }
        return AisConsentRequestType.DEDICATED_ACCOUNTS;
    }

    private boolean isEmptyAccess(AisAccountAccessInfo accessInfo) {
        return CollectionUtils.isEmpty(accessInfo.getAccounts())
                   && CollectionUtils.isEmpty(accessInfo.getBalances())
                   && CollectionUtils.isEmpty(accessInfo.getTransactions());
    }

    private ActionStatus resolveConsentActionStatus(AisConsentActionRequest request, AisConsent consent) {
        return consent == null
                   ? ActionStatus.BAD_PAYLOAD
                   : request.getActionStatus();
    }

    private void updateAisConsentCounter(AisConsent consent) {
        if (consent != null && consent.hasUsagesAvailable()) {
            int usageCounter = consent.getUsageCounter();
            int newUsageCounter = --usageCounter;
            consent.setUsageCounter(newUsageCounter);
            consent.setLastActionDate(LocalDate.now());
            aisConsentRepository.save(consent);
        }
    }

    private void logConsentAction(String requestedConsentId, ActionStatus actionStatus, String tppId) {
        AisConsentAction action = new AisConsentAction();
        action.setActionStatus(actionStatus);
        action.setRequestedConsentId(requestedConsentId);
        action.setTppId(tppId);
        action.setRequestDate(LocalDate.now());
        aisConsentActionRepository.save(action);
    }

    private Optional<AisConsent> getActualAisConsent(String consentId) {
        return Optional.ofNullable(consentId)
                   .flatMap(c -> aisConsentRepository.findByExternalIdAndConsentStatusIn(consentId, EnumSet.of(RECEIVED, VALID)));
    }

    private Optional<AisConsent> getAisConsentById(String consentId) {
        return Optional.ofNullable(consentId)
                   .flatMap(aisConsentRepository::findByExternalId);
    }

    private AisConsent checkAndUpdateOnExpiration(AisConsent consent) {
        if (consent != null && consent.isExpiredByDate() && consent.isStatusNotExpired()) {
            consent.setConsentStatus(EXPIRED);
            consent.setExpireDate(LocalDate.now());
            consent.setLastActionDate(LocalDate.now());
            aisConsentRepository.save(consent);
        }
        return consent;
    }

    private AisConsent setStatusAndSaveConsent(AisConsent consent, CmsConsentStatus status) {
        consent.setLastActionDate(LocalDate.now());
        consent.setConsentStatus(status);
        return aisConsentRepository.save(consent);
    }

    private String saveNewAuthorization(AisConsent aisConsent, AisConsentAuthorizationRequest request) {
        AisConsentAuthorization consentAuthorization = new AisConsentAuthorization();
        consentAuthorization.setExternalId(UUID.randomUUID().toString());
        consentAuthorization.setPsuId(request.getPsuId());
        consentAuthorization.setConsent(aisConsent);
        consentAuthorization.setScaStatus(request.getScaStatus());
        return aisConsentAuthorizationRepository.save(consentAuthorization).getExternalId();
    }
}
