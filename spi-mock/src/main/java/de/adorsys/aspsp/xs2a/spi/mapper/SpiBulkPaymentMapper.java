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

import de.adorsys.aspsp.xs2a.spi.domain.payment.SpiBulkPayment;
import de.adorsys.aspsp.xs2a.spi.domain.payment.SpiSinglePayment;
import de.adorsys.psd2.aspsp.mock.api.payment.AspspBulkPayment;
import de.adorsys.psd2.aspsp.mock.api.payment.AspspSinglePayment;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class SpiBulkPaymentMapper {
    private final SpiSinglePaymentMapper spiSinglePaymentMapper;
    private final SpiPaymentMapper spiPaymentMapper;

    public AspspBulkPayment mapToAspspBulkPayment(@NotNull SpiBulkPayment spiBulkPayment) {
        AspspBulkPayment mockBulk = new AspspBulkPayment();
        mockBulk.setBatchBookingPreferred(spiBulkPayment.getBatchBookingPreferred());
        mockBulk.setDebtorAccount(spiPaymentMapper.mapToAspspAccountReference(spiBulkPayment.getDebtorAccount()));
        mockBulk.setRequestedExecutionDate(spiBulkPayment.getRequestedExecutionDate());
        mockBulk.setPayments(mapToAspspSinglePaymentList(spiBulkPayment.getPayments()));
        mockBulk.setPaymentStatus(spiPaymentMapper.mapToAspspTransactionStatus(spiBulkPayment.getPaymentStatus()));
        return mockBulk;
    }

    private List<AspspSinglePayment> mapToAspspSinglePaymentList(@NotNull List<SpiSinglePayment> spiSinglePayments) {
        return spiSinglePayments.stream()
                   .map(spiSinglePaymentMapper::mapToAspspSinglePayment)
                   .collect(Collectors.toList());
    }
}
