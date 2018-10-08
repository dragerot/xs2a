# Roadmap


## version 1.9 (planned date: 12.10.2018)
- Confirmation of funds request. Add interface on side of SPI.
- Add validation of TPP data
- Update of Payment cancellation request according to specification 1.2
- Change logic of implicit/explicit method of authorisation
- Update AspspConsentData field in the Consent. Support bytearray and base64 encoding.
- Validation of Consent (expiration date).
- Prototype online banking (PSU-ASPSP interface)
    - Payment initiation
    - Account information service. Bank offered.
    - Account information service. Dedicated accounts.
- Get list of reachable accounts. Embedded approach.
- Get balances for a given account. Embedded approach.
- Migration to package and Maven GroupId "de.adorsys.psd2": XS2A and Consent Management
- Extend cms-client to work with AspspConsentData endpoints

## version 1.10 (Planned date: 26.10.2018)
- Get account details of the list of accessible accounts. Embedded approach.
- Get a confirmation on the availability of funds. Embedded approach.
- Support encryption of aspspConsentData.
- PIS Support a matrix payment-product/payment-type in aspsp-profile and corresponding services.
- Get list of consents by psu-id in Consent Management System.
- PIIS Consent. Post, Get, Put. Read aspspConsentData. 
- Migration to package and Maven GroupId "de.adorsys.psd2": aspsp-mockserver
- Fix nullpointerexception while invoking /v1/accounts/

