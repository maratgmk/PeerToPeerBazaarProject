package org.gafiev.peertopeerbazaar.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.net.URI;

/**
 * настройки платежной системы
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "bazaar.payment")
public class PaymentProperties {

    /**
     * идентификатор нашего приложения в платежной системе.
     */
  private String  merchantId;

    /**
     * секретный ключ переданный платежной системой для формирования подписей в наших запросах и проверки подписи в callback запросах платежной системы.
     */
  private String  secretKey;

    /**
     * Uri страница, куда платежная система вернет клиента после оплаты
     */
  private URI returnUri;

    /**
     * Uri на который придет запрос callback о результатах выполнения операции от внешнего сервиса
     */
  private URI  callbackUri;

  /**
   * Uri на который шлет запрос наше приложение в качестве клиента
   */
  private String clientUri;

}
