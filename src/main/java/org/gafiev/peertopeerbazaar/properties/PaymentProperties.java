package org.gafiev.peertopeerbazaar.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.net.URI;

/**
 * Configuration properties for the external payment system.
 * This class holds settings loaded from application properties
 * with the prefix "bazaar.payment". These properties include merchant details, URIs for redirects and callbacks,
 * and security keys required for interacting with the payment provider.
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "bazaar.payment")
public class PaymentProperties {

    /**
     * The unique merchant account identifier assigned by the payment provider.
     */
  private String  merchantId;

    /**
     *  The secret key used to generate HMAC/SHA-256 signatures for request integrity and validation.
     *  This is a concatenation of alphabetically sorted fields encrypted with the secret key.
     */
  private String  secretKey;

    /**
     * The browser-redirect URI where the user is sent after completing the transaction
     * on the external payment page.
     */
  private URI returnUri;

    /**
     * The webhook URI where the external payment service sends server-to-server notifications
     * regarding the payment result (e.g., SUCCESS or DENIED).
     */
  private URI  callbackUri;

  // Note: This is the base URI; full URI comes from API response.
  /**
   * The base URI of the payment page on the external payment service side,
   * used for constructing or referencing the payment completion URL.
   */
  private String clientUri;
}
