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

package de.adorsys.aspsp.xs2a.domain.consent;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "AuthenticationObject type", value = "AuthenticationType")
public enum AuthenticationType {
    SMS_OTP("An SCA method, where an OTP linked to the transaction to be authorised is sent to the PSU through a SMS channel."),
    CHIP_OTP("An SCA method, where an OTP is generated by a chip card, e.g. an TOP derived from an EMV cryptogram. To contact the card, the PSU normally needs a (handheld) device. With this device, the PSU either reads the challenging data through a visual interface like flickering or the PSU types in the challenge through the device key pad. The device then derives an OTP from the challenge data and displays the OTP to the PSU."),
    PHOTO_OTP("An SCA method, where the challenge is a QR code or similar encoded visual data which can be read in by a consumer device or specific mobile app. The device resp. the specific app than derives an OTP from the visual challenge data and displays the OTP to the PSU."),
    PUSH_OTP("An OTP is pushed to a dedicated authentication APP and displayed to the PSU.");

    @ApiModelProperty(value = "description", example = "Will be defined later")
    private String description;

    AuthenticationType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
