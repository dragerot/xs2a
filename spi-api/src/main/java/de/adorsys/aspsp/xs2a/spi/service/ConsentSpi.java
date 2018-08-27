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

package de.adorsys.aspsp.xs2a.spi.service;

import de.adorsys.aspsp.xs2a.consent.api.ActionStatus;
import de.adorsys.aspsp.xs2a.consent.api.ais.CreateAisConsentRequest;
import de.adorsys.aspsp.xs2a.consent.api.pis.proto.PisConsentRequest;
import de.adorsys.aspsp.xs2a.spi.domain.account.SpiAccountConsent;
import de.adorsys.aspsp.xs2a.spi.domain.consent.SpiConsentStatus;

public interface ConsentSpi {

    /**
     * Sends a POST request to CMS to store created AISconsent
     *
     * @param createAisConsentRequest Provides transporting data when creating an consent
     * @return String representation of identifier of stored consent
     */
    String createConsent(CreateAisConsentRequest createAisConsentRequest);

    /**
     * Requests CMS to retrieve AIS consent by its identifier
     *
     * @param consentId String representation of identifier of stored consent
     * @return Response containing AIS Consent
     */
    SpiAccountConsent getAccountConsentById(String consentId);

    /**
     * Requests CMS to retrieve AIS consent status by its identifier
     *
     * @param consentId String representation of identifier of stored consent
     * @return Response containing AIS Consent Status
     */
    SpiConsentStatus getAccountConsentStatusById(String consentId);

    /**
     * Requests CMS to update consent status to "Revoked by PSU" state
     *
     * @param consentId String representation of identifier of stored consent
     */
    void revokeConsent(String consentId);

    /**
     * Sends a POST request to CMS to perform decrement of consent usages and report status of the operation held with certain AIS consent
     *
     * @param tppId       String representation of TPP`s identifier from TPP Certificate
     * @param consentId   String representation of identifier of stored consent
     * @param actionStatus Enum value representing whether the acition is successful or errors occured
     */
    void consentActionLog(String tppId, String consentId, ActionStatus actionStatus);

    /**
     * Sends a POST request to CMS to store created PIS consent for single payment
     *
     * @param pisConsentRequest Provides transporting data when creating an consent
     * @return String identifier of created PIS consent for single payment
     */
    String createPisConsentForSinglePaymentAndGetId(PisConsentRequest pisConsentRequest);

    /**
     * Sends a POST request to CMS to store created PIS consent for bulk payment
     *
     * @param pisConsentRequest Provides transporting data when creating an consent
     * @return String identifier of created PIS consent for bulk payment
     */
    String createPisConsentForBulkPaymentAndGetId(PisConsentRequest pisConsentRequest);

    /**
     * Sends a POST request to CMS to store created PIS consent for periodic payment
     *
     * @param pisConsentRequest Provides transporting data when creating an consent
     * @return String identifier of created PIS consent periodic payment
     */
    String createPisConsentForPeriodicPaymentAndGetId(PisConsentRequest pisConsentRequest);
}
