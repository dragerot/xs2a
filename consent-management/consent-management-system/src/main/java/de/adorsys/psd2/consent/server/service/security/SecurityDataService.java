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

package de.adorsys.psd2.consent.server.service.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SecurityDataService {
    @Qualifier(value = "serverKey")
    private final String serverKey;
    @Qualifier(value = "cryptoProviderId")
    private final CryptoProvider cryptoProviderId;
    @Qualifier(value = "cryptoProviderConsentData")
    private final CryptoProvider cryptoProviderConsentData;
    private static final String SEPARATOR = "_";
    private static final String VERSION = "v1";

    /**
     * Encrypts external consent ID with secret consent key via configuration server key
     *
     * @param consentId
     * @return String encrypted external consent ID
     */
    public Optional<String> getEncryptedId(String consentId) {
        String consent_key = getConsentKey();
        String compositeConsentId = concatWithSeparator(consentId, consent_key);
        byte[] bytesCompositeConsentId = compositeConsentId.getBytes();
        return cryptoProviderId.encryptData(bytesCompositeConsentId, serverKey)
                   .map(EncryptedData::getData)
                   .map(this::encode)
                   .map(this::addVersion);
    }

    /**
     * Decrypts encrypted external consent ID
     *
     * @param encryptedConsentId
     * @return String external consent ID
     */
    public Optional<String> getConsentId(String encryptedConsentId) {
        Optional<String> compositeId = getCompositeId(encryptedConsentId);
        return compositeId.map(this::getConsentIdFromCompositeId);
    }

    private Optional<String> getCompositeId(String encryptedConsentId) {
        String encryptedConsentIdWithoutVersion = minusVersion(encryptedConsentId);
        byte[] bytesCompositeConsentId = decode(encryptedConsentIdWithoutVersion);
        return cryptoProviderId.decryptData(bytesCompositeConsentId, serverKey)
                   .map(ed -> new String(ed.getData()));
    }

    private String getConsentIdFromCompositeId(String compositeId) {
        return compositeId.split(SEPARATOR)[0];
    }

    private String getConsentKey() {
        return RandomStringUtils.random(16, true, true);
    }

    private String encode(byte[] raw) {
        return Base64.getUrlEncoder().encodeToString(raw);
    }

    private byte[] decode(String raw) {
        return Base64.getUrlDecoder().decode(raw);
    }

    private String addVersion(String id) {
        return concatWithSeparator(id, VERSION);
    }

    private String minusVersion(String id) {
        return id.substring(0, id.length() - VERSION.length() - 1);
    }

    private String concatWithSeparator(String leftPart, String rightPart) {
        StringBuilder sb = new StringBuilder();
        sb.append(leftPart);
        sb.append(SEPARATOR);
        sb.append(rightPart);
        return sb.toString();
    }
}
