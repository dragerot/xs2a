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

package de.adorsys.psd2.consent.api.pis.proto;

import de.adorsys.psd2.consent.api.CmsTppInfo;
import de.adorsys.psd2.consent.api.pis.PisPayment;
import de.adorsys.psd2.xs2a.core.profile.PaymentProduct;
import de.adorsys.psd2.xs2a.core.profile.PaymentType;
import de.adorsys.psd2.xs2a.core.psu.PsuIdData;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(description = "Pis payment initialisation consent request", value = "PisConsentRequest")
public class PisConsentRequest {
    @ApiModelProperty(value = "Payment data", required = true)
    private List<PisPayment> payments;

    @ApiModelProperty(value = "Payment product", required = true, example = "sepa-credit-transfers")
    private PaymentProduct paymentProduct;

    @ApiModelProperty(value = "Payment type: BULK, SINGLE or PERIODIC.", required = true, example = "SINGLE")
    private PaymentType paymentType;

    @ApiModelProperty(value = "Tpp information", required = true)
    private CmsTppInfo tppInfo;

    @ApiModelProperty(value = "Corresponding PSU", required = true)
    private PsuIdData psuData;
}
