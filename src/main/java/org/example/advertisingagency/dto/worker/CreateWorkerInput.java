package org.example.advertisingagency.dto.worker;

import lombok.Data;

@Data
public class CreateWorkerInput {
    private String name;
    private String surname;
    private String email;
    private String phoneNumber;
    private Integer positionId;
    private Integer officeId;
    private Boolean isReviewer;
}
