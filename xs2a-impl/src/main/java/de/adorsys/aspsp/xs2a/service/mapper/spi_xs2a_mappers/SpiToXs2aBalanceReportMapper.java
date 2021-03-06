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

package de.adorsys.aspsp.xs2a.service.mapper.spi_xs2a_mappers;

import de.adorsys.aspsp.xs2a.domain.account.Xs2aBalancesReport;
import de.adorsys.psd2.xs2a.spi.domain.account.SpiBalanceReport;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SpiToXs2aBalanceReportMapper {
    private final SpiToXs2aBalanceMapper balanceMapper;
    private final SpiToXs2aAccountReferenceMapper referenceMapper;

    public Xs2aBalancesReport mapToXs2aBalancesReport(SpiBalanceReport balanceReport) {
        return Optional.ofNullable(balanceReport)
                   .map(r -> {
                       Xs2aBalancesReport xs2aBalancesReport = new Xs2aBalancesReport();

                       xs2aBalancesReport.setBalances(balanceMapper.mapToXs2aBalanceList(r.getBalances()));
                       xs2aBalancesReport.setXs2aAccountReference(referenceMapper.mapToXs2aAccountReference(r.getAccountReference()).orElse(null));

                       return xs2aBalancesReport;
                   })
                   .orElse(null);
    }
}
