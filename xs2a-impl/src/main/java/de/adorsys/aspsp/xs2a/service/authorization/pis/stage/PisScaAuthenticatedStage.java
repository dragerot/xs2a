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

package de.adorsys.aspsp.xs2a.service.authorization.pis.stage;

import de.adorsys.aspsp.xs2a.domain.ErrorHolder;
import de.adorsys.aspsp.xs2a.domain.MessageErrorCode;
import de.adorsys.aspsp.xs2a.domain.OtpFormat;
import de.adorsys.aspsp.xs2a.domain.Xs2aChallengeData;
import de.adorsys.aspsp.xs2a.domain.consent.pis.Xs2aUpdatePisConsentPsuDataRequest;
import de.adorsys.aspsp.xs2a.domain.consent.pis.Xs2aUpdatePisConsentPsuDataResponse;
import de.adorsys.aspsp.xs2a.service.consent.PisConsentDataService;
import de.adorsys.aspsp.xs2a.service.mapper.consent.CmsToXs2aPaymentMapper;
import de.adorsys.aspsp.xs2a.service.mapper.consent.Xs2aPisConsentMapper;
import de.adorsys.aspsp.xs2a.service.mapper.spi_xs2a_mappers.*;
import de.adorsys.psd2.consent.api.pis.authorisation.GetPisConsentAuthorisationResponse;
import de.adorsys.psd2.xs2a.core.profile.PaymentType;
import de.adorsys.psd2.xs2a.spi.domain.authorisation.SpiAuthenticationObject;
import de.adorsys.psd2.xs2a.spi.domain.authorisation.SpiAuthorizationCodeResult;
import de.adorsys.psd2.xs2a.spi.domain.consent.AspspConsentData;
import de.adorsys.psd2.xs2a.spi.domain.psu.SpiPsuData;
import de.adorsys.psd2.xs2a.spi.domain.response.SpiResponse;
import de.adorsys.psd2.xs2a.spi.service.PaymentAuthorisationSpi;
import de.adorsys.psd2.xs2a.spi.service.SpiPayment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

import static de.adorsys.psd2.xs2a.core.sca.ScaStatus.SCAMETHODSELECTED;

@Slf4j
@Service("PIS_PSUAUTHENTICATED")
public class PisScaAuthenticatedStage extends PisScaStage<Xs2aUpdatePisConsentPsuDataRequest, GetPisConsentAuthorisationResponse, Xs2aUpdatePisConsentPsuDataResponse> {

    public PisScaAuthenticatedStage(PaymentAuthorisationSpi paymentAuthorisationSpi, PisConsentDataService pisConsentDataService, CmsToXs2aPaymentMapper cmsToXs2aPaymentMapper, Xs2aToSpiPeriodicPaymentMapper xs2aToSpiPeriodicPaymentMapper, Xs2aToSpiSinglePaymentMapper xs2aToSpiSinglePaymentMapper, Xs2aToSpiBulkPaymentMapper xs2aToSpiBulkPaymentMapper, SpiToXs2aAuthenticationObjectMapper spiToXs2aAuthenticationObjectMapper, Xs2aPisConsentMapper xs2aPisConsentMapper, SpiErrorMapper spiErrorMapper) {
        super(paymentAuthorisationSpi, pisConsentDataService, cmsToXs2aPaymentMapper, xs2aToSpiPeriodicPaymentMapper, xs2aToSpiSinglePaymentMapper, xs2aToSpiBulkPaymentMapper, spiToXs2aAuthenticationObjectMapper, xs2aPisConsentMapper, spiErrorMapper);
    }

    @Override
    public Xs2aUpdatePisConsentPsuDataResponse apply(Xs2aUpdatePisConsentPsuDataRequest request, GetPisConsentAuthorisationResponse pisConsentAuthorisationResponse) {
        PaymentType paymentType = pisConsentAuthorisationResponse.getPaymentType();
        SpiPayment payment = mapToSpiPayment(pisConsentAuthorisationResponse.getPayments(), paymentType);
        String authenticationMethodId = request.getAuthenticationMethodId();
        Xs2aChallengeData challengeData = null;

        SpiPsuData psuData = new SpiPsuData(request.getPsuId(), null, null, null);  // TODO get it from XS2A Interface https://git.adorsys.de/adorsys/xs2a/aspsp-xs2a/issues/458
        AspspConsentData aspspConsentData = pisConsentDataService.getAspspConsentDataByPaymentId(request.getPaymentId());

        SpiResponse<SpiAuthorizationCodeResult> spiResponse = paymentAuthorisationSpi.requestAuthorisationCode(psuData, authenticationMethodId, payment, aspspConsentData);
        aspspConsentData = spiResponse.getAspspConsentData();
        pisConsentDataService.updateAspspConsentData(aspspConsentData);
        SpiAuthorizationCodeResult authorizationCodeResult = spiResponse.getPayload();
        if(authorizationCodeResult != null && !authorizationCodeResult.isEmpty()) {
            challengeData = new Xs2aChallengeData(authorizationCodeResult.getImage(),
                authorizationCodeResult.getData(),
                authorizationCodeResult.getImageLink(),
                authorizationCodeResult.getOtpMaxLength(),
                authorizationCodeResult.getOtpFormatCharacters() ? OtpFormat.CHARACTERS : OtpFormat.INTEGER,
                authorizationCodeResult.getAdditionalInformation());
        }

        if (spiResponse.hasError()) {
            return new Xs2aUpdatePisConsentPsuDataResponse(spiErrorMapper.mapToErrorHolder(spiResponse));
        }

        // TODO remove second call to request sca methods since sca method is already choosen
        SpiResponse<List<SpiAuthenticationObject>> availableScaMethodsResponse = paymentAuthorisationSpi.requestAvailableScaMethods(psuData, payment, aspspConsentData);
        pisConsentDataService.updateAspspConsentData(availableScaMethodsResponse.getAspspConsentData());

        if (availableScaMethodsResponse.hasError()) {
            return new Xs2aUpdatePisConsentPsuDataResponse(spiErrorMapper.mapToErrorHolder(spiResponse));
        }

        List<SpiAuthenticationObject> availableScaMethods = availableScaMethodsResponse.getPayload();
        SpiAuthenticationObject chosenScaMethod = availableScaMethods.stream()
                                                      .filter(a -> authenticationMethodId.equals(a.getAuthenticationMethodId()))
                                                      .findFirst()
                                                      .orElse(null);

        if (chosenScaMethod == null) {
            ErrorHolder errorHolder = ErrorHolder.builder(MessageErrorCode.SCA_METHOD_UNKNOWN)
                                          .build();
            return new Xs2aUpdatePisConsentPsuDataResponse(errorHolder);
        }

        Xs2aUpdatePisConsentPsuDataResponse response = new Xs2aUpdatePisConsentPsuDataResponse(SCAMETHODSELECTED);
        response.setPsuId(psuData.getPsuId());
        response.setChosenScaMethod(spiToXs2aAuthenticationObjectMapper.mapToXs2aAuthenticationObject(chosenScaMethod));
        response.setChallengeData(challengeData);
        return response;
    }
}
