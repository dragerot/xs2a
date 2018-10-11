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

package de.adorsys.aspsp.xs2a.spi.impl;

import de.adorsys.aspsp.xs2a.component.JsonConverter;
import de.adorsys.aspsp.xs2a.domain.security.AspspAuthorisationData;
import de.adorsys.aspsp.xs2a.exception.RestException;
import de.adorsys.aspsp.xs2a.spi.config.rest.AspspRemoteUrls;
import de.adorsys.aspsp.xs2a.spi.domain.SpiResponse;
import de.adorsys.aspsp.xs2a.spi.domain.SpiResponse.VoidResponse;
import de.adorsys.aspsp.xs2a.spi.domain.SpiResponseStatus;
import de.adorsys.aspsp.xs2a.spi.domain.account.SpiAccountConsent;
import de.adorsys.aspsp.xs2a.spi.domain.authorisation.SpiAuthorisationStatus;
import de.adorsys.aspsp.xs2a.spi.domain.authorisation.SpiAuthorizationCodeResult;
import de.adorsys.aspsp.xs2a.spi.domain.authorisation.SpiScaConfirmation;
import de.adorsys.aspsp.xs2a.spi.domain.authorisation.SpiScaMethod;
import de.adorsys.aspsp.xs2a.spi.domain.consent.AspspConsentData;
import de.adorsys.aspsp.xs2a.spi.impl.service.KeycloakInvokerService;
import de.adorsys.aspsp.xs2a.spi.service.v2.AisConsentSpi;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static de.adorsys.aspsp.xs2a.spi.domain.authorisation.SpiAuthorisationStatus.FAILURE;
import static de.adorsys.aspsp.xs2a.spi.domain.authorisation.SpiAuthorisationStatus.SUCCESS;

// TODO implement all the methods https://git.adorsys.de/adorsys/xs2a/aspsp-xs2a/issues/364
@Component
@Slf4j
@AllArgsConstructor
public class AisConsentSpiImpl implements AisConsentSpi {
    private final AspspRemoteUrls remoteSpiUrls;
    @Qualifier("aspspRestTemplate")
    private final RestTemplate aspspRestTemplate;
    private final KeycloakInvokerService keycloakInvokerService;
    private final JsonConverter jsonConverter;

    // Test data is used there for testing purposes to have the possibility to see if AisConsentSpiImpl is being invoked from xs2a.
    // TODO remove if some requirements will be received https://git.adorsys.de/adorsys/xs2a/aspsp-xs2a/issues/394
    private static final String TEST_ASPSP_DATA = "Test aspsp data";
    private static final String TEST_MESSAGE = "Test message";

    @Override
    public SpiResponse<VoidResponse> initiateAisConsent(SpiAccountConsent accountConsent, AspspConsentData initialAspspConsentData) {
        log.info("AisConsentSpi initiateAisConsent() mock implementation");
        return SpiResponse.<VoidResponse>builder()
                   .aspspConsentData(initialAspspConsentData.respondWith(TEST_ASPSP_DATA.getBytes()))     // added for test purposes TODO remove if some requirements will be received https://git.adorsys.de/adorsys/xs2a/aspsp-xs2a/issues/394
                   .message(Collections.singletonList(TEST_MESSAGE))                                      // added for test purposes TODO remove if some requirements will be received https://git.adorsys.de/adorsys/xs2a/aspsp-xs2a/issues/394
                   .success();
    }

    @Override
    public SpiResponse<VoidResponse> revokeAisConsent(SpiAccountConsent accountConsent, AspspConsentData aspspConsentData) {
        log.info("AisConsentSpi revokeAisConsent() mock implementation");
        return SpiResponse.<VoidResponse>builder()
                   .aspspConsentData(aspspConsentData.respondWith(TEST_ASPSP_DATA.getBytes()))            // added for test purposes TODO remove if some requirements will be received https://git.adorsys.de/adorsys/xs2a/aspsp-xs2a/issues/394
                   .message(Collections.singletonList(TEST_MESSAGE))                                      // added for test purposes TODO remove if some requirements will be received https://git.adorsys.de/adorsys/xs2a/aspsp-xs2a/issues/394
                   .success();
    }

    @Override
    public SpiResponse<SpiAuthorisationStatus> authorisePsu(String psuId, String password, SpiAccountConsent accountConsent, AspspConsentData aspspConsentData) {
        Optional<AspspAuthorisationData> accessToken = keycloakInvokerService.obtainAuthorisationData(psuId, password);
        SpiAuthorisationStatus spiAuthorisationStatus = accessToken.map(t -> SUCCESS)
                                                            .orElse(FAILURE);
        byte[] payload = accessToken.flatMap(jsonConverter::toJson)
                             .map(String::getBytes)
                             .orElse(null);
        return new SpiResponse<>(spiAuthorisationStatus, aspspConsentData.respondWith(payload));
    }

    @Override
    public SpiResponse<List<SpiScaMethod>> requestAvailableScaMethods(String psuId, SpiAccountConsent accountConsent, AspspConsentData aspspConsentData) {
        ResponseEntity<List<SpiScaMethod>> response = aspspRestTemplate.exchange(
            remoteSpiUrls.getScaMethods(), HttpMethod.GET, null, new ParameterizedTypeReference<List<SpiScaMethod>>() {
            }, psuId);
        List<SpiScaMethod> spiScaMethods = Optional.ofNullable(response.getBody())
                                               .orElse(Collections.emptyList());
        return new SpiResponse<>(spiScaMethods, aspspConsentData);
    }

    @Override
    public SpiResponse<SpiAuthorizationCodeResult> requestAuthorisationCode(String psuId, SpiScaMethod scaMethod, SpiAccountConsent accountConsent, AspspConsentData aspspConsentData) {
        try {
            aspspRestTemplate.exchange(remoteSpiUrls.getGenerateTanConfirmationForAis(), HttpMethod.POST, null, Void.class, psuId);
            return SpiResponse.<SpiAuthorizationCodeResult>builder()
                       .success();
        } catch (RestException e) {
            if (e.getHttpStatus() == HttpStatus.INTERNAL_SERVER_ERROR) {
                return SpiResponse.<SpiAuthorizationCodeResult>builder()
                           .fail(SpiResponseStatus.TECHNICAL_FAILURE);
            }

            return SpiResponse.<SpiAuthorizationCodeResult>builder()
                       .fail(SpiResponseStatus.LOGICAL_FAILURE);
        }
    }

    @Override
    public SpiResponse<VoidResponse> verifyAuthorisationCodeAndExecuteRequest(SpiScaConfirmation spiScaConfirmation, SpiAccountConsent accountConsent, AspspConsentData aspspConsentData) {
        try {
            aspspRestTemplate.exchange(remoteSpiUrls.applyStrongUserAuthorisationForAis(), HttpMethod.PUT, new HttpEntity<>(spiScaConfirmation), Void.class);
            return SpiResponse.<VoidResponse>builder()
                       .success();
        } catch (RestException e) {
            if (e.getHttpStatus() == HttpStatus.INTERNAL_SERVER_ERROR) {
                return SpiResponse.<VoidResponse>builder()
                           .fail(SpiResponseStatus.TECHNICAL_FAILURE);
            }

            return SpiResponse.<VoidResponse>builder()
                       .fail(SpiResponseStatus.LOGICAL_FAILURE);
        }
    }
}
