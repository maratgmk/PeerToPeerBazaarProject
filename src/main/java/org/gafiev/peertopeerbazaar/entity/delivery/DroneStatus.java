package org.gafiev.peertopeerbazaar.entity.delivery;

/**
 * Represents the operational status of a delivery drone.
 */
public enum DroneStatus {

    /**
     * Drone is assigned to a mission and awaiting takeoff.
     */
    ASSIGNED,

    /**
     * Cargo is secured; drone is in operational status for departure.
     */
    LOADED,

    /**
     * Drone is ascending to cruise altitude.
     */
    TAKE_OFF,

    /**
     * Navigation failure or loss of telemetry signal.
     */
    LOSE_WAY,

    /**
     * Emergency touchdown due to critical failure.
     */
    CRASH_LANDING,

    /**
     * Drone has successfully landed at the target coordinates.
     */
    LANDED,

    /**
     * Cargo released; drone completed the offloading phase.
     */
    OFFLOADED,

    /**
     * Mission aborted; drone is returning to the seller location.
     */
    BACK_TO_SELLER,

    /**
     * Returning to the home base for maintenance or recharging.
     */
    BACK_TO_BASE,

    /**
     * All pre-flight checks are passed; the drone is available for a new task.
     */
    READY_TO_FLY
}
