package org.gafiev.peertopeerbazaar.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * настройки системы сервиса дронов
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "bazaar.drone")
public class DroneProperties {
    /**
     * Uri на который шлет запрос наше приложение в качестве клиента
     */
    private String clientUri;

}
