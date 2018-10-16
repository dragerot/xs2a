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

package de.adorsys.aspsp.xs2a.domain.pis;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.adorsys.aspsp.xs2a.domain.Links;
import de.adorsys.aspsp.xs2a.domain.Xs2aChallengeData;
import de.adorsys.aspsp.xs2a.domain.Xs2aTransactionStatus;
import de.adorsys.aspsp.xs2a.domain.consent.Xs2aAuthenticationObject;
import de.adorsys.aspsp.xs2a.domain.consent.Xs2aChosenScaMethod;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class CancelPaymentResponse {
    private boolean startAuthorisationRequired;
    private String authorisationId;
    private Xs2aTransactionStatus transactionStatus;
    private Xs2aAuthenticationObject[] scaMethods;
    private Xs2aChosenScaMethod chosenScaMethod;
    private Xs2aChallengeData challengeData;
    @NotNull
    @JsonProperty("_links")
    private Links links;
}