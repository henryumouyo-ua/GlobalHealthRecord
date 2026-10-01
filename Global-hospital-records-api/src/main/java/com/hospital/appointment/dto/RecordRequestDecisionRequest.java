package com.hospital.appointment.dto;

import lombok.Data;

@Data
public class RecordRequestDecisionRequest {

    // Optional note on approval/fulfillment (e.g. delivery instructions);
    // effectively required in practice for a rejection, though not
    // enforced at this layer — see RecordRequestService.rejectRequest.
    private String staffNotes;
}
