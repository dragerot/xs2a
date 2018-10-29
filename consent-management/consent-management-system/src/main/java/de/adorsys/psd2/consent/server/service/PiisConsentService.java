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

import de.adorsys.psd2.consent.api.CmsAspspConsentDataBase64;
import de.adorsys.psd2.consent.api.piis.PiisConsent;
import de.adorsys.psd2.consent.server.repository.PiisConsentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Base64;
import java.util.EnumSet;
import java.util.Optional;

import static de.adorsys.psd2.xs2a.core.consent.ConsentStatus.RECEIVED;
import static de.adorsys.psd2.xs2a.core.consent.ConsentStatus.VALID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PiisConsentService {
    private final PiisConsentRepository piisConsentRepository;

    /**
     * Update PIS consent aspsp consent data by id
     *
     * @param request   Aspsp provided pis consent data
     * @param consentId id of the consent to be updated
     * @return String consent id
     */
    @Transactional
    public Optional<String> updateAspspConsentDataInPiisConsent(String consentId, CmsAspspConsentDataBase64 request) {
        return getActualPiisConsent(consentId)
                   .map(cons -> updateAspspConsentDataInPiisConsent(request, cons));
    }

    private Optional<PiisConsent> getActualPiisConsent(String consentId) {
        return Optional.ofNullable(consentId)
                   .flatMap(c -> piisConsentRepository.findByExternalIdAndConsentStatusIn(consentId, EnumSet.of(RECEIVED, VALID)));
    }

    private String updateAspspConsentDataInPiisConsent(CmsAspspConsentDataBase64 request, PiisConsent consent) {
        byte[] aspspConsentData = Optional.ofNullable(request.getAspspConsentDataBase64())
                                      .map(aspspConsentDataBase64 -> Base64.getDecoder().decode(aspspConsentDataBase64))
                                      .orElse(null);
        //consent.setAspspConsentData(aspspConsentData);
        PiisConsent savedConsent = piisConsentRepository.save(consent);
        //return savedConsent.getExternalId();
        return "";
    }
}
