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

package de.adorsys.aspsp.xs2a.service.authorization.pis;

import de.adorsys.aspsp.xs2a.config.factory.ScaStage;
import de.adorsys.aspsp.xs2a.config.factory.ScaStageAuthorisationFactory;
import de.adorsys.aspsp.xs2a.config.rest.consent.PisConsentRemoteUrls;
import de.adorsys.aspsp.xs2a.domain.ResponseObject;
import de.adorsys.aspsp.xs2a.exception.MessageError;
import de.adorsys.aspsp.xs2a.spi.domain.authorisation.SpiScaMethod;
import de.adorsys.psd2.consent.api.pis.authorisation.CreatePisConsentAuthorisationResponse;
import de.adorsys.psd2.consent.api.pis.authorisation.GetPisConsentAuthorisationResponse;
import de.adorsys.psd2.consent.api.pis.authorisation.UpdatePisConsentPsuDataRequest;
import de.adorsys.psd2.consent.api.pis.authorisation.UpdatePisConsentPsuDataResponse;
import de.adorsys.psd2.model.ScaStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

import static de.adorsys.aspsp.xs2a.domain.MessageErrorCode.SCA_METHOD_UNKNOWN;

@Service
@RequiredArgsConstructor
public class PisAuthorisationService {
    @Qualifier("consentRestTemplate")
    private final RestTemplate consentRestTemplate;
    private final PisConsentRemoteUrls remotePisConsentUrls;
    private final ScaStageAuthorisationFactory scaStageAuthorisationFactory;

    /**
     * Sends a POST request to CMS to store created consent authorization
     *
     * @param paymentId String representation of identifier of stored consent
     * @return long representation of identifier of stored consent authorization
     */
    public CreatePisConsentAuthorisationResponse createPisConsentAuthorisation(String paymentId) {
        return consentRestTemplate.postForEntity(remotePisConsentUrls.createPisConsentAuthorisation(),
            null, CreatePisConsentAuthorisationResponse.class, paymentId)
                   .getBody();
    }

    /**
     * Updates PIS consent authorization according to psu's sca methods
     *
     * @param request Provides transporting data when updating consent authorization
     * @return sca status
     */
    //TODO change response type of the method to SpiResponse<UpdatePisConsentPsuDataResponse> https://git.adorsys.de/adorsys/xs2a/aspsp-xs2a/issues/299
    public ResponseObject<UpdatePisConsentPsuDataResponse> updatePisConsentAuthorisation(UpdatePisConsentPsuDataRequest request) {
        GetPisConsentAuthorisationResponse response = consentRestTemplate.exchange(remotePisConsentUrls.getPisConsentAuthorisationById(), HttpMethod.GET, new HttpEntity<>(request), GetPisConsentAuthorisationResponse.class, request.getAuthorizationId())
                                                          .getBody();
        ScaStage<UpdatePisConsentPsuDataRequest, GetPisConsentAuthorisationResponse, UpdatePisConsentPsuDataResponse> service = scaStageAuthorisationFactory.getService(response.getScaStatus().name());

        return isNecessaryAndCorrectScaMethod(request.getAuthenticationMethodId(), response.getScaStatus().name())
                   ? ResponseObject.<UpdatePisConsentPsuDataResponse>builder().body(service.apply(request, response)).build()
                   : ResponseObject.<UpdatePisConsentPsuDataResponse>builder().fail(new MessageError(SCA_METHOD_UNKNOWN)).build();
    }

    public UpdatePisConsentPsuDataResponse doUpdatePisConsentAuthorisation(UpdatePisConsentPsuDataRequest request) {
        return consentRestTemplate.exchange(remotePisConsentUrls.updatePisConsentAuthorisation(), HttpMethod.PUT, new HttpEntity<>(request),
            UpdatePisConsentPsuDataResponse.class, request.getAuthorizationId()).getBody();
    }

    private boolean isNecessaryAndCorrectScaMethod(String requestedScaMethod, String currentScaStatus) {
        if (currentScaStatus.equals(ScaStatus.PSUAUTHENTICATED.name())) {
            Optional<SpiScaMethod> scaMethod = SpiScaMethod.getByName(requestedScaMethod);
            return scaMethod.isPresent();
        }
        return true;
    }
}
