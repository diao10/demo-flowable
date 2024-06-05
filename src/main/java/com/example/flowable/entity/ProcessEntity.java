package com.example.flowable.entity;


import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProcessEntity {

    private String eventID;

    private String userID;

    private Integer approveStatus;

    private String approveContent;

    private String processId;

    private String taskID;

}
