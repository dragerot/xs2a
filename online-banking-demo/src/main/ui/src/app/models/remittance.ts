/**
 * XS2A REST API
 * No description provided (generated by Swagger Codegen https://github.com/swagger-api/swagger-codegen)
 *
 * OpenAPI spec version: 1.0
 * Contact: fpo@adorsys.de
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */


/**
 * Remittance
 */
export interface Remittance {
  /**
   * the actual reference
   */
  reference: string;
  /**
   * reference issuer
   */
  referenceIssuer?: string;
  /**
   * reference type
   */
  referenceType?: string;
}
