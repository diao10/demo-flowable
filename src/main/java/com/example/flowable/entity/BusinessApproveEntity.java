package com.example.flowable.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BusinessApproveEntity {

    private String businessId;

    private String userId;

    private Integer approveStatus;

    private String approveComments;


}
