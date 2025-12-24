package org.gafiev.peertopeerbazaar.entity.payment;
/**
 * Represents the payment mode used for transactions.
 */
public enum PaymentMode {

    /** Payment via exchange of goods or services without using money. */
    BARTER,

    /** Payment via the PayPal electronic payment system. */
    PAY_PAL,

    /** Payment using decentralized digital currencies (e.g., Bitcoin, Ethereum). */
    CRYPTO_CURRENCY,

    /** Payment via direct bank-to-bank wire transfer. */
    BANK_TRANSFER
}
