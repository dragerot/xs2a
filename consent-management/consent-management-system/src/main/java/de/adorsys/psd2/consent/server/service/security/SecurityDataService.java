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

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;

@Slf4j
@Service
@Data
public class SecurityDataService {
    private final CryptoProvider cryptoProvider;
    private final String SERVER_KEY;
    private static final String SEPARATOR = "_";

    public SecurityDataService(CryptoProvider cryptoProvider) {
        this.cryptoProvider = cryptoProvider;
        this.SERVER_KEY = System.getenv().get("server_key");
        if (StringUtils.isBlank(this.SERVER_KEY)) {
            String error = "Environment variable server_key is not set";
            log.warn(error);
            throw new IllegalArgumentException(error);
        }
    }

    /**
     * Encrypt consent ID
     *
     * @param consentId
     * @return String encrypted external consent ID
     */
    public Optional<String> getEncryptedId(String consentId) {
        //Get consent key
        String consent_key = getConsentKey();
        //Concatenate consent key with consent id
        String consentIdWithKey = concatWithSeparator(consentId, consent_key);
        //Encrypted with server key
        return cryptoProvider.encryptId(consentIdWithKey, getSERVER_KEY())
                   .map(this::enrichId);
    }

    private String enrichId(String encryptedId) {
        //Add algorithm version
        String consentIdWithKeyEncryptedVersion = concatWithSeparator(encryptedId, getVersion());
        //Base64 encode
        return encode(consentIdWithKeyEncryptedVersion);
    }

    /**
     * Decrypt consent ID
     *
     * @param encryptedConsentId
     * @return String external consent ID
     */
    public Optional<String> getConsentId(String encryptedConsentId) {
        Optional<String> compositeId = getCompositeId(encryptedConsentId);
        return compositeId.map(cid -> cid.split(SEPARATOR)[0]);
    }

    private String getConsentKey() {
        return RandomStringUtils.random(16, true, true);
    }

    private String getVersion() {
        return "v1";
    }

    private String concatWithSeparator(String leftPart, String rightPart) {
        StringBuilder sb = new StringBuilder();
        sb.append(leftPart);
        sb.append(SEPARATOR);
        sb.append(rightPart);
        return sb.toString();
    }

    private Optional<String> getCompositeId(String encryptedConsentId) {
        //Get consent id with key encrypted with version
        String consentIdWithKeyEncryptedVersion = decode(encryptedConsentId);
        //Decrypted
        return cryptoProvider.decryptId(consentIdWithKeyEncryptedVersion, getSERVER_KEY());
    }

    private String encode(String raw) {
        return Base64.getUrlEncoder().encodeToString(raw.getBytes(StandardCharsets.UTF_8));
    }

    private String decode(String raw) {
        return new String(Base64.getDecoder().decode(raw), StandardCharsets.UTF_8);
    }
}
