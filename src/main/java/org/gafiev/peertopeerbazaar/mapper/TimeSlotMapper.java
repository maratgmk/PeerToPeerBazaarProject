package org.gafiev.peertopeerbazaar.mapper;

import org.gafiev.peertopeerbazaar.dto.api.response.TimeSlotResponse;
import org.gafiev.peertopeerbazaar.entity.time.TimeSlot;
import org.springframework.stereotype.Component;

/**
 * Mapper class for converting between TimeSlot entity and its DTO representations.
 */
@Component
public class TimeSlotMapper {

    /**
     * Converts TimeSlotResponse DTO to TimeSlot entity.
     *
     * @param timeSlotResponse TimeSlotResponse DTO from external drone service.
     * @return TimeSlot entity to be embedded in a Delivery.
     */
    public TimeSlot toTimeSlot(TimeSlotResponse timeSlotResponse){
        return new TimeSlot(timeSlotResponse.start(), timeSlotResponse.end());
    }

    /**
     * Converts a TimeSlot entity to a TimeSlotResponse DTO.
     *
     * @param timeSlot TimeSlot entity.
     * @return TimeSlotResponse DTO.
     */
    public TimeSlotResponse toTimeSlotResponse(TimeSlot timeSlot){
        return new TimeSlotResponse(timeSlot.getStart(), timeSlot.getEnd());
    }
}
