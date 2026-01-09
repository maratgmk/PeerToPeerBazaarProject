package org.gafiev.peertopeerbazaar.entity.user;
/**
 * Represents the security role and access level of a user within the system.
 */
public enum Role {

    /** Guest or anonymous user without an account. */
    UNREGISTERED,

    /** Registered user who has not yet verified their identity or email. */
    UNCONFIRMED,

    /** Standard registered user with basic access rights. */
    USER,

    /** User authorized to offer goods and manage sales. */
    SELLER,

    /** User authorized to purchase goods and manage orders. */
    BUYER,

    /** Product creator or owner of specific system entities. */
    AUTHOR,

    /** Administrator with full access to system management and moderation. */
    ADMIN,

    /** User whose access has been restricted due to policy violations. */
    BLOCKED
}
