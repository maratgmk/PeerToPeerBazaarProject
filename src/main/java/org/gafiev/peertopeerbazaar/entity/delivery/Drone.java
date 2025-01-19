package org.gafiev.peertopeerbazaar.entity.delivery;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

/**
 * Сущность Drone осуществляет обмен заказов между пользователями
 */
@EqualsAndHashCode(exclude = "deliverySet")
@ToString(exclude = "deliverySet")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "drone")
public class Drone {
    /**
     * id есть уникальный идентификатор дрона
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * droneStatus показывает состояние полёта дрона
     */
    @Enumerated(EnumType.STRING)
    private DroneStatus droneStatus;

    /**
     * droneServiceId id переданное сторонним сервисом
     */
    @Column(name = "drone_service_id", unique = true)
    private Long droneServiceId;

    /**
     * deliverySet есть множество доставок,
     * один drone осуществляет много доставок,
     */
    @OneToMany(mappedBy = "drone", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Delivery> deliverySet = new HashSet<>();


    /**
     * метод добавления доставки во множество доставок этого дрона
     *
     * @param delivery доставка
     */
    public void addDelivery(Delivery delivery) {
        deliverySet.add(delivery);
        delivery.setDrone(this);
    }

    /**
     * метод удаления доставки из множества доставок этого дрона
     *
     * @param delivery доставка
     */
    public void removeDelivery(Delivery delivery) {
        deliverySet.remove(delivery);
        delivery.setDrone(null);
    }

}
