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
import de.adorsys.psd2.xs2a.spi.domain.code.SpiFrequencyCode;
import de.adorsys.psd2.xs2a.spi.domain.common.SpiTransactionStatus;
import de.adorsys.psd2.xs2a.spi.domain.payment.SpiPaymentProduct;
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
        AspspPeriodicPayment aspspPayment = new AspspPeriodicPayment();
        aspspPayment.setEndToEndIdentification(spiPeriodicPayment.getEndToEndIdentification());
        aspspPayment.setDebtorAccount(spiPaymentMapper.mapToAspspAccountReference(spiPeriodicPayment.getDebtorAccount()));
        aspspPayment.setInstructedAmount(spiPaymentMapper.mapToAspspAmount(spiPeriodicPayment.getInstructedAmount()));
        aspspPayment.setCreditorAccount(spiPaymentMapper.mapToAspspAccountReference(spiPeriodicPayment.getCreditorAccount()));
        aspspPayment.setCreditorAgent(spiPeriodicPayment.getCreditorAgent());
        aspspPayment.setCreditorName(spiPeriodicPayment.getCreditorName());
        aspspPayment.setCreditorAddress(spiPaymentMapper.mapToAspspAddress(spiPeriodicPayment.getCreditorAddress()));
        aspspPayment.setRemittanceInformationUnstructured(spiPeriodicPayment.getRemittanceInformationUnstructured());
        aspspPayment.setRemittanceInformationStructured(spiPaymentMapper.mapToAspspRemittance(spiPeriodicPayment.getRemittanceInformationStructured()));
        aspspPayment.setPaymentStatus(AspspTransactionStatus.RCVD);
        aspspPayment.setStartDate(spiPeriodicPayment.getStartDate());
        aspspPayment.setEndDate(spiPeriodicPayment.getEndDate());
        aspspPayment.setExecutionRule(spiPeriodicPayment.getExecutionRule());
        aspspPayment.setFrequency(spiPeriodicPayment.getFrequency());
        aspspPayment.setDayOfExecution(spiPeriodicPayment.getDayOfExecution());
        return aspspPayment;
    }

    public SpiPeriodicPayment mapToSpiPeriodicPayment(@NotNull AspspPeriodicPayment aspspPeriodicPayment, SpiPaymentProduct paymentProduct) {
        SpiPeriodicPayment spiPayment = new SpiPeriodicPayment(paymentProduct);
        spiPayment.setEndToEndIdentification(aspspPeriodicPayment.getEndToEndIdentification());
        spiPayment.setDebtorAccount(spiPaymentMapper.mapToSpiAccountReference(aspspPeriodicPayment.getDebtorAccount()));
        spiPayment.setInstructedAmount(spiPaymentMapper.mapToSpiAmount(aspspPeriodicPayment.getInstructedAmount()));
        spiPayment.setCreditorAccount(spiPaymentMapper.mapToSpiAccountReference(aspspPeriodicPayment.getCreditorAccount()));
        spiPayment.setCreditorAgent(aspspPeriodicPayment.getCreditorAgent());
        spiPayment.setCreditorName(aspspPeriodicPayment.getCreditorName());
        spiPayment.setCreditorAddress(spiPaymentMapper.mapToSpiAddress(aspspPeriodicPayment.getCreditorAddress()));
        spiPayment.setRemittanceInformationUnstructured(aspspPeriodicPayment.getRemittanceInformationUnstructured());
        spiPayment.setPaymentStatus(SpiTransactionStatus.RCVD);
        spiPayment.setStartDate(aspspPeriodicPayment.getStartDate());
        spiPayment.setEndDate(aspspPeriodicPayment.getEndDate());
        spiPayment.setExecutionRule(aspspPeriodicPayment.getExecutionRule());
        spiPayment.setFrequency(SpiFrequencyCode.valueOf(aspspPeriodicPayment.getFrequency()));
        spiPayment.setDayOfExecution(aspspPeriodicPayment.getDayOfExecution());
        return spiPayment;
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
