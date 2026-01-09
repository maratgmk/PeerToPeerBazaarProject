package org.gafiev.peertopeerbazaar.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for the external drone service.
 * This class holds settings loaded from application properties
 * with the prefix "bazaar.drone".
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "bazaar.drone")
public class DroneProperties {
    /**
     * The base URI of the drone page on the external drone service side,
     * used for assigning delivery drones.
     */
    private String clientUri;
}
