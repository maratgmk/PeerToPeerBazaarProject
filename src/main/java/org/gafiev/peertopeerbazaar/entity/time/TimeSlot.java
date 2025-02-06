package org.gafiev.peertopeerbazaar.entity.time;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Сущность TimeSlot представляет временной диапазон, предоставляемый внешним сервисом,
 * в рамках этого временного диапазона могут быть предоставлены дроны
 */
@EqualsAndHashCode
@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "time_slot")
public class TimeSlot {
    /**
     * Поле id есть уникальный идентификатор слота
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * дата и время начала слота
     */
    @Column(name = "start")
    private LocalDateTime start;

    /**
     * дата и время окончание слота
     */
    @Column(name = "end")
    private LocalDateTime end;


}


