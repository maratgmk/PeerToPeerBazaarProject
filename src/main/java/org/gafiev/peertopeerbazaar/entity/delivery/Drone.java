package org.gafiev.peertopeerbazaar.entity.delivery;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

/**
 *  Drone осуществляет доставку заказов между пользователями.
 */
@EqualsAndHashCode(exclude = "deliverySet")
@ToString(exclude = "deliverySet")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder(toBuilder = true)
@Table(name = "drone")
public class Drone {
    /**
     * идентификатор дрона.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * идентификатор дрона в стороннем сервисе.
     */
    @Column(name = "drone_service_id", unique = true)
    private Long droneServiceId;


    /**
     * состояние дрона.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "drone_status")
    private DroneStatus droneStatus;

    @CreationTimestamp
    @Column(name = "created_at")
    private Instant createdAt;

    /**
     * deliverySet есть множество доставок.
     * один drone осуществляет много доставок,
     */
    @Builder.Default
    @OneToMany(mappedBy = "drone", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Delivery> deliverySet = new HashSet<>();

    /**
     * метод добавления доставки во множество доставок этого дрона.
     *
     * @param delivery доставка
     */
    public void addDelivery(Delivery delivery) {
        deliverySet.add(delivery);
        delivery.setDrone(this);
    }

    /**
     * метод удаления доставки из множества доставок этого дрона.
     *
     * @param delivery доставка
     */
    public void removeDelivery(Delivery delivery) {
        deliverySet.remove(delivery);
        delivery.setDrone(null);
    }

}
