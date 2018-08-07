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

package de.adorsys.psd2.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.validation.annotation.Validated;
import java.time.LocalDate;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * JSON response body consistion of the corresponding bulk cross-border payment initation JSON body together with an optional transaction
 * status field.
 */
@ApiModel(description = "JSON response body consistion of the corresponding bulk cross-border payment initation JSON body together with an optional transaction status field. ")
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2018-08-06T12:02:27.187+02:00[Europe/Berlin]")
public class BulkPaymentInitiationCrossBorderWithStatusResponse {

    @JsonProperty("batchBookingPreferred")
    private Boolean batchBookingPreferred = null;
    @JsonProperty("requestedExecutionDate")
    private LocalDate requestedExecutionDate = null;
    @JsonProperty("debtorAccount")
    private Object debtorAccount = null;
    @JsonProperty("payments")
    @Valid
    private List<PaymentInitiationCrossBorderJson> payments = new ArrayList<PaymentInitiationCrossBorderJson>();
    @JsonProperty("transactionStatus")
    private TransactionStatus transactionStatus = null;

    public BulkPaymentInitiationCrossBorderWithStatusResponse batchBookingPreferred(Boolean batchBookingPreferred) {
        this.batchBookingPreferred = batchBookingPreferred;
        return this;
    }

    /**
     * Get batchBookingPreferred
     *
     * @return batchBookingPreferred
     **/
    @ApiModelProperty(value = "")
    public Boolean getBatchBookingPreferred() {
        return batchBookingPreferred;
    }

    public void setBatchBookingPreferred(Boolean batchBookingPreferred) {
        this.batchBookingPreferred = batchBookingPreferred;
    }

    public BulkPaymentInitiationCrossBorderWithStatusResponse requestedExecutionDate(LocalDate requestedExecutionDate) {
        this.requestedExecutionDate = requestedExecutionDate;
        return this;
    }

    /**
     * Get requestedExecutionDate
     *
     * @return requestedExecutionDate
     **/
    @ApiModelProperty(value = "")
    @Valid
    public LocalDate getRequestedExecutionDate() {
        return requestedExecutionDate;
    }

    public void setRequestedExecutionDate(LocalDate requestedExecutionDate) {
        this.requestedExecutionDate = requestedExecutionDate;
    }

    public BulkPaymentInitiationCrossBorderWithStatusResponse debtorAccount(Object debtorAccount) {
        this.debtorAccount = debtorAccount;
        return this;
    }

    /**
     * Get debtorAccount
     *
     * @return debtorAccount
     **/
    @ApiModelProperty(required = true, value = "")
    @NotNull

    public Object getDebtorAccount() {
        return debtorAccount;
    }

    public void setDebtorAccount(Object debtorAccount) {
        this.debtorAccount = debtorAccount;
    }

    public BulkPaymentInitiationCrossBorderWithStatusResponse payments(List<PaymentInitiationCrossBorderJson> payments) {
        this.payments = payments;
        return this;
    }

    public BulkPaymentInitiationCrossBorderWithStatusResponse addPaymentsItem(PaymentInitiationCrossBorderJson paymentsItem) {
        this.payments.add(paymentsItem);
        return this;
    }

    /**
     * A list of JSON bodies for cross-border payments.
     *
     * @return payments
     **/
    @ApiModelProperty(required = true, value = "A list of JSON bodies for cross-border payments.")
    @NotNull
    @Valid
    public List<PaymentInitiationCrossBorderJson> getPayments() {
        return payments;
    }

    public void setPayments(List<PaymentInitiationCrossBorderJson> payments) {
        this.payments = payments;
    }

    public BulkPaymentInitiationCrossBorderWithStatusResponse transactionStatus(TransactionStatus transactionStatus) {
        this.transactionStatus = transactionStatus;
        return this;
    }

    /**
     * Get transactionStatus
     *
     * @return transactionStatus
     **/
    @ApiModelProperty(value = "")
    @Valid
    public TransactionStatus getTransactionStatus() {
        return transactionStatus;
    }

    public void setTransactionStatus(TransactionStatus transactionStatus) {
        this.transactionStatus = transactionStatus;
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BulkPaymentInitiationCrossBorderWithStatusResponse bulkPaymentInitiationCrossBorderWithStatusResponse = (BulkPaymentInitiationCrossBorderWithStatusResponse) o;
        return Objects.equals(this.batchBookingPreferred, bulkPaymentInitiationCrossBorderWithStatusResponse.batchBookingPreferred)
            && Objects.equals(this.requestedExecutionDate, bulkPaymentInitiationCrossBorderWithStatusResponse.requestedExecutionDate)
            && Objects.equals(this.debtorAccount, bulkPaymentInitiationCrossBorderWithStatusResponse.debtorAccount)
            && Objects.equals(this.payments, bulkPaymentInitiationCrossBorderWithStatusResponse.payments)
            && Objects.equals(this.transactionStatus, bulkPaymentInitiationCrossBorderWithStatusResponse.transactionStatus);
    }

    @Override
    public int hashCode() {
        return Objects.hash(batchBookingPreferred, requestedExecutionDate, debtorAccount, payments, transactionStatus);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class BulkPaymentInitiationCrossBorderWithStatusResponse {\n");

        sb.append("    batchBookingPreferred: ").append(toIndentedString(batchBookingPreferred)).append("\n");
        sb.append("    requestedExecutionDate: ").append(toIndentedString(requestedExecutionDate)).append("\n");
        sb.append("    debtorAccount: ").append(toIndentedString(debtorAccount)).append("\n");
        sb.append("    payments: ").append(toIndentedString(payments)).append("\n");
        sb.append("    transactionStatus: ").append(toIndentedString(transactionStatus)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces (except the first line).
     */
    private String toIndentedString(java.lang.Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }
}
