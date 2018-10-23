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

package de.adorsys.psd2.consent.server.service;

import de.adorsys.psd2.consent.server.service.security.CryptoProvider;
import de.adorsys.psd2.consent.server.service.security.JweCryptoProviderImpl;
import de.adorsys.psd2.consent.server.service.security.SecurityDataService;
import org.junit.Before;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

//@RunWith(MockitoJUnitRunner.class)
public class SecurityDataServiceTest {
    @InjectMocks
    private SecurityDataService securityDataService;
    @Mock
    private CryptoProvider cryptoProvider;
    private String consentId = "99391c7e-ad88-49ec-a2ad-99ddcb1f7721";
    private String server_key = "06d205ae-4afe-4f15-9583-2084d2210b2d";
    private String password = "password";



    @Before
    public void setUp() {
        System.getenv().put("server_key", server_key);
        when(cryptoProvider.encryptId(consentId, password)).thenReturn(Optional.of(consentId));
        when(cryptoProvider.decryptId(consentId, password)).thenReturn(Optional.of(consentId));
        when(securityDataService.getSERVER_KEY()).thenReturn(server_key);

    }

    //@Test
    public void test() {
        // When

        // Then
        Optional<String> encryptedExternalConsentId = securityDataService.getEncryptedId(consentId);

        // Assert
        assertTrue(encryptedExternalConsentId.isPresent());
        String decodedEncryptedExternalConsentId = decode(encryptedExternalConsentId.get());

        assert(decodedEncryptedExternalConsentId).contains(consentId);
        assert(decodedEncryptedExternalConsentId).contains(server_key);
    }

    public String encode(String raw) {
        return Base64.getUrlEncoder().encodeToString(raw.getBytes(StandardCharsets.UTF_8));
    }

    public String decode(String raw) {
        return new String(Base64.getDecoder().decode(raw), StandardCharsets.UTF_8);
    }
}
