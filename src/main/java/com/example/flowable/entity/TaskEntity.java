package com.example.flowable.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TaskEntity {

    private String processId;

    private String userId;

    private String businessId;
}
