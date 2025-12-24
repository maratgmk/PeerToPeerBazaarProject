package org.gafiev.peertopeerbazaar.entity.product;

/**
 * Represents the category of the goods, defining handling and transportation requirements.
 */
public enum Category {

    /** Standard stable goods that do not require special handling. */
    SOLID,

    /** Easily broken items that require careful handling and protective packaging. */
    FRAGILE,

    /** Fluid substances that require sealed, leak-proof containers. */
    LIQUID,

    /** Goods that decay quickly and require temperature-controlled or fast delivery. */
    PERISHABLE,

    /** Items sensitive to high temperatures that must be kept in a cool environment. */
    AVOID_HEAT,

    /** Hazardous or chemical substances requiring specialized safety protocols. */
    CHEMICAL
}
