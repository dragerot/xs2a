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

package de.adorsys.aspsp.xs2a.web.aspect;

import de.adorsys.aspsp.xs2a.domain.Links;
import de.adorsys.aspsp.xs2a.domain.ResponseObject;
import de.adorsys.aspsp.xs2a.domain.consent.Xs2aCreatePisConsentCancellationAuthorisationResponse;
import de.adorsys.aspsp.xs2a.service.message.MessageService;
import de.adorsys.aspsp.xs2a.service.profile.AspspProfileServiceWrapper;
import de.adorsys.aspsp.xs2a.web.PaymentController;
import de.adorsys.psd2.xs2a.core.profile.PaymentType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class CreatePisAuthorisationCancellationAspect extends AbstractLinkAspect<PaymentController> {
    public CreatePisAuthorisationCancellationAspect(AspspProfileServiceWrapper aspspProfileService, MessageService messageService) {
        super(aspspProfileService, messageService);
    }

    @AfterReturning(pointcut = "execution(* de.adorsys.aspsp.xs2a.service.ConsentService.createPisConsentCancellationAuthorization(..)) && args(paymentId, paymentType)", returning = "result", argNames = "result,paymentId,paymentType")
    public ResponseObject<Xs2aCreatePisConsentCancellationAuthorisationResponse> createPisConsentAuthorizationAspect(ResponseObject<Xs2aCreatePisConsentCancellationAuthorisationResponse> result, String paymentId, PaymentType paymentType) {
        if (!result.hasError()) {
            Xs2aCreatePisConsentCancellationAuthorisationResponse body = result.getBody();
            body.setLinks(buildLink(paymentType.getValue(), paymentId, body.getAuthorizationId()));
            return result;
        }
        return enrichErrorTextMessage(result);
    }

    private Links buildLink(String paymentService, String paymentId, String authorizationId) {
        Links links = new Links();
        links.setStartAuthorisationWithPsuAuthentication(buildPath("/v1/{paymentService}/{paymentId}/cancellation-authorisations/{authorizationId}", paymentService, paymentId, authorizationId));
        return links;
    }
}
