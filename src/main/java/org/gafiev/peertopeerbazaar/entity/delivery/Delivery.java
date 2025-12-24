package org.gafiev.peertopeerbazaar.entity.delivery;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.gafiev.peertopeerbazaar.entity.order.BuyerOrder;
import org.gafiev.peertopeerbazaar.entity.time.TimeSlot;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

/**
 * Represents a delivery process from a seller's address to a buyer's address.
 * This entity tracks the delivery status, associated locations, the original buyer order,
 *  and the assigned time window for the drone service.
 */
@Getter
@Setter
@EqualsAndHashCode(exclude = {"buyerOrder", "drone","toAddress","fromAddress"})
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"buyerOrder", "drone","toAddress","fromAddress"})
@Entity
@Builder(toBuilder = true)
@Table(name = "delivery")
public class Delivery {
    /**
     * Unique identifier  for the delivery.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * The current lifecycle status of the delivery.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "delivery_status")
    private DeliveryStatus deliveryStatus;

    /**
     * The scheduled time window for the delivery.
     * This field is optional (nullable) and is populated once the drone service confirms a slot.
     */
    @Embedded
    private TimeSlot timeSlot;

    /**
     * Timestamp when the Delivery entity was first recorded in the database.
     */
    @CreationTimestamp
    @Column(name = "created_at")
    private Instant createdAt;

    /**
     * The buyer's order associated with this delivery.
     */
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    private BuyerOrder buyerOrder;

    /**
     * The destination address where the buyer's order will be delivered.
     */
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE,CascadeType.PERSIST})
    private Address toAddress;

    /**
     * The origin address where the drone picks up the order.
     */
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE,CascadeType.PERSIST})
    private Address fromAddress;

    /**
     * The drone assigned to perform this delivery.
     */
    @ManyToOne (fetch = FetchType.LAZY, cascade = {CascadeType.MERGE,CascadeType.PERSIST})
    private Drone drone;
}
