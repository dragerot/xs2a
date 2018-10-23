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

package de.adorsys.aspsp.xs2a.spi.mapper;

import de.adorsys.psd2.aspsp.mock.api.common.AspspTransactionStatus;
import de.adorsys.psd2.aspsp.mock.api.payment.AspspPeriodicPayment;
import de.adorsys.psd2.xs2a.core.profile.PaymentProduct;
import de.adorsys.psd2.xs2a.spi.domain.code.SpiFrequencyCode;
import de.adorsys.psd2.xs2a.spi.domain.common.SpiTransactionStatus;
import de.adorsys.psd2.xs2a.spi.domain.payment.SpiPeriodicPayment;
import de.adorsys.psd2.xs2a.spi.domain.payment.response.SpiPeriodicPaymentInitiationResponse;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class SpiPeriodicPaymentMapper {
    private final SpiPaymentMapper spiPaymentMapper;

    public AspspPeriodicPayment mapToAspspPeriodicPayment(@NotNull SpiPeriodicPayment spiPeriodicPayment) {
        AspspPeriodicPayment aspspPayment = new AspspPeriodicPayment();
        aspspPayment.setPaymentId(spiPeriodicPayment.getPaymentId());
        aspspPayment.setEndToEndIdentification(spiPeriodicPayment.getEndToEndIdentification());
        aspspPayment.setDebtorAccount(spiPaymentMapper.mapToAspspAccountReference(spiPeriodicPayment.getDebtorAccount()));
        aspspPayment.setInstructedAmount(spiPaymentMapper.mapToAspspAmount(spiPeriodicPayment.getInstructedAmount()));
        aspspPayment.setCreditorAccount(spiPaymentMapper.mapToAspspAccountReference(spiPeriodicPayment.getCreditorAccount()));
        aspspPayment.setCreditorAgent(spiPeriodicPayment.getCreditorAgent());
        aspspPayment.setCreditorName(spiPeriodicPayment.getCreditorName());
        aspspPayment.setCreditorAddress(spiPaymentMapper.mapToAspspAddress(spiPeriodicPayment.getCreditorAddress()));
        aspspPayment.setRemittanceInformationUnstructured(spiPeriodicPayment.getRemittanceInformationUnstructured());
        aspspPayment.setPaymentStatus(AspspTransactionStatus.RCVD);
        aspspPayment.setStartDate(spiPeriodicPayment.getStartDate());
        aspspPayment.setEndDate(spiPeriodicPayment.getEndDate());
        aspspPayment.setExecutionRule(spiPeriodicPayment.getExecutionRule());
        aspspPayment.setFrequency(spiPeriodicPayment.getFrequency().name());
        aspspPayment.setDayOfExecution(spiPeriodicPayment.getDayOfExecution());
        return aspspPayment;
    }

    public AspspPeriodicPayment mapToAspspPeriodicPayment(@NotNull de.adorsys.aspsp.xs2a.spi.domain.payment.SpiPeriodicPayment spiPeriodicPayment) {
        AspspPeriodicPayment periodic = new AspspPeriodicPayment();
        periodic.setPaymentId(payment.getPaymentId());
        periodic.setEndToEndIdentification(spiPeriodicPayment.getEndToEndIdentification());
        periodic.setDebtorAccount(spiPaymentMapper.mapToAspspAccountReference(spiPeriodicPayment.getDebtorAccount()));
        periodic.setInstructedAmount(spiPaymentMapper.mapToAspspAmount(spiPeriodicPayment.getInstructedAmount()));
        periodic.setCreditorAccount(spiPaymentMapper.mapToAspspAccountReference(spiPeriodicPayment.getCreditorAccount()));
        periodic.setCreditorAgent(spiPeriodicPayment.getCreditorAgent());
        periodic.setCreditorName(spiPeriodicPayment.getCreditorName());
        periodic.setCreditorAddress(spiPaymentMapper.mapToAspspAddress(spiPeriodicPayment.getCreditorAddress()));
        periodic.setRemittanceInformationUnstructured(spiPeriodicPayment.getRemittanceInformationUnstructured());
        periodic.setRemittanceInformationStructured(spiPaymentMapper.mapToAspspRemittance(spiPeriodicPayment.getRemittanceInformationStructured()));
        periodic.setPaymentStatus(AspspTransactionStatus.RCVD);
        periodic.setStartDate(spiPeriodicPayment.getStartDate());
        periodic.setEndDate(spiPeriodicPayment.getEndDate());
        periodic.setExecutionRule(spiPeriodicPayment.getExecutionRule());
        periodic.setFrequency(spiPeriodicPayment.getFrequency());
        periodic.setDayOfExecution(spiPeriodicPayment.getDayOfExecution());
        return periodic;
    }

    public SpiPeriodicPayment mapToSpiPeriodicPayment(@NotNull AspspPeriodicPayment payment, PaymentProduct paymentProduct) {
        SpiPeriodicPayment periodic = new SpiPeriodicPayment(paymentProduct);
        periodic.setPaymentId(payment.getPaymentId());
        periodic.setEndToEndIdentification(payment.getEndToEndIdentification());
        periodic.setDebtorAccount(spiPaymentMapper.mapToSpiAccountReference(payment.getDebtorAccount()));
        periodic.setInstructedAmount(spiPaymentMapper.mapToSpiAmount(payment.getInstructedAmount()));
        periodic.setCreditorAccount(spiPaymentMapper.mapToSpiAccountReference(payment.getCreditorAccount()));
        periodic.setCreditorAgent(payment.getCreditorAgent());
        periodic.setCreditorName(payment.getCreditorName());
        periodic.setCreditorAddress(spiPaymentMapper.mapToSpiAddress(payment.getCreditorAddress()));
        periodic.setRemittanceInformationUnstructured(payment.getRemittanceInformationUnstructured());
        periodic.setPaymentStatus(SpiTransactionStatus.RCVD);
        periodic.setStartDate(payment.getStartDate());
        periodic.setEndDate(payment.getEndDate());
        periodic.setExecutionRule(payment.getExecutionRule());
        periodic.setFrequency(SpiFrequencyCode.valueOf(payment.getFrequency()));
        periodic.setDayOfExecution(payment.getDayOfExecution());
        return periodic;
    }

    public SpiPeriodicPaymentInitiationResponse mapToSpiPeriodicPaymentResponse(@NotNull AspspPeriodicPayment payment) {
        SpiPeriodicPaymentInitiationResponse spi = new SpiPeriodicPaymentInitiationResponse();
        spi.setPaymentId(payment.getPaymentId());
        if (payment.getPaymentId() == null) {
            spi.setTransactionStatus(SpiTransactionStatus.RJCT);
        } else {
            spi.setTransactionStatus(SpiTransactionStatus.RCVD);
        }
        return spi;
    }
}
