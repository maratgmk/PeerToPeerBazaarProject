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
 * Represents a drone responsible for completing deliveries from sellers to buyers.
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
     * Unique internal identifier for the drone.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * Unique identifier assigned by the external drone service.
     */
    @Column(name = "drone_service_id", unique = true)
    private Long droneServiceId;

    /**
     * Current operational status of the drone.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "drone_status")
    private DroneStatus droneStatus;

    /**
     * Timestamp when the drone record was created in the database.
     */
    @CreationTimestamp
    @Column(name = "created_at")
    private Instant createdAt;

    /**
     * Set of deliveries assigned to this drone.
     */
    @Builder.Default
    @OneToMany(mappedBy = "drone", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Delivery> deliverySet = new HashSet<>();

    /**
     * Associates a delivery with this drone and establishes a bidirectional relationship.
     *
     * @param delivery The delivery entity to associate.
     */
    public void addDelivery(Delivery delivery) {
        deliverySet.add(delivery);
        delivery.setDrone(this);
    }

    /**
     * Disassociates a delivery from this drone and breaks the bidirectional relationship.
     *
     * @param delivery The delivery entity to remove.
     */
    public void removeDelivery(Delivery delivery) {
        deliverySet.remove(delivery);
        delivery.setDrone(null);
    }
}
