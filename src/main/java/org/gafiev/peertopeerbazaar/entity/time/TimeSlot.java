package org.gafiev.peertopeerbazaar.entity.time;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.time.LocalDateTime;

/**
 * TimeSlot это временной диапазон, предоставляемый внешним сервисом.
 * В рамках этого временного диапазона могут быть предоставлены дроны.
 */
@EqualsAndHashCode
@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class TimeSlot {

    /**
     * дата и время начала временного диапазона.
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Column(name = "start_slot")
    private LocalDateTime start;

    /**
     * дата и время окончание временного диапазона.
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Column(name = "end_slot")
    private LocalDateTime end;

}


