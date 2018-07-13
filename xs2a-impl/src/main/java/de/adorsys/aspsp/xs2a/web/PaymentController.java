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

package de.adorsys.aspsp.xs2a.web;

import de.adorsys.aspsp.xs2a.domain.TransactionStatus;
import de.adorsys.aspsp.xs2a.domain.pis.PaymentType;
import de.adorsys.aspsp.xs2a.service.PaymentService;
import de.adorsys.aspsp.xs2a.service.mapper.ResponseMapper;
import io.swagger.annotations.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping(path = "api/v1/")
@Api(value = "api/v1/", tags = "PISP, Access to Payments", description = "Provides access to the PIS payments")
public class PaymentController<T> {
    private final ResponseMapper responseMapper;
    private final PaymentService paymentService;

    @ApiOperation(value = "Get payment information", authorizations = {@Authorization(value = "oauth2", scopes = {@AuthorizationScope(scope = "read", description = "Access read API")})})
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK", response = TransactionStatus.class),
        @ApiResponse(code = 404, message = "Not found"),
        @ApiResponse(code = 403, message = "Wrong path variables")})
    @GetMapping(path = "/{payment-service}/{payment-product}/{paymentId}")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "Content-Type", value = "application/json", required = true, dataType = "String", paramType = "header"),
        @ApiImplicitParam(name = "X-Request-ID", value = "16d40f49-a110-4344-a949-f99828ae13c9", required = true, dataType = "UUID", paramType = "header"),
        @ApiImplicitParam(name = "tpp-request-id", value = "2f77a125-aa7a-45c0-b414-cea25a116035", required = true, dataType = "UUID", paramType = "header"),
        @ApiImplicitParam(name = "signature", value = "98c0", required = false, dataType = "String", paramType = "header"),
        @ApiImplicitParam(name = "tpp-certificate", value = "some certificate", required = false, dataType = "String", paramType = "header"),
        @ApiImplicitParam(name = "PSU-IP-Address", value = "192.168.0.26", dataType = "String", paramType = "header")})//NOPMD //Ip is required as description of the field
    public ResponseEntity getPaymentById(
        @ApiParam(name = "payment-service", value = "The addressed payment service", allowableValues = "payments, bulk-payments,periodic-payments")
        @PathVariable("payment-service") PaymentType paymentType,
        @ApiParam(name = "payment-product", value = "The addressed payment product ", allowableValues = "sepa-credit-transfers, target-2-payments, instant-sepa-credit-transfers, cross-border-credit-transfers")
        @PathVariable("payment-product") String paymentProduct,
        @ApiParam(name = "paymentId", value = "529e0507-7539-4a65-9b74-bdf87061e99b")
        @PathVariable("paymentId") String paymentId) {
        return responseMapper.ok(paymentService.getPaymentById(paymentType, paymentProduct, paymentId));
    }
}