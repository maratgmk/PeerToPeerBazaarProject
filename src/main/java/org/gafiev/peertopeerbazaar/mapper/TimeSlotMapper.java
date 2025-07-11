package org.gafiev.peertopeerbazaar.mapper;

import org.gafiev.peertopeerbazaar.dto.api.response.TimeSlotResponse;
import org.gafiev.peertopeerbazaar.entity.time.TimeSlot;
import org.springframework.stereotype.Component;

@Component
public class TimeSlotMapper {

    public TimeSlot toTimeSlot(TimeSlotResponse timeSlotResponse){
        return new TimeSlot(timeSlotResponse.start(), timeSlotResponse.end());
    }

    public TimeSlotResponse toTimeSlotResponse(TimeSlot timeSlot){
        return new TimeSlotResponse(timeSlot.getStart(), timeSlot.getEnd());
    }
}
