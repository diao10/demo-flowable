package com.example.flowable.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BusinessApproveEntity2 {

    private String businessId;

    private String userId;

    private Boolean isPass;

    private String approveComments;


}
