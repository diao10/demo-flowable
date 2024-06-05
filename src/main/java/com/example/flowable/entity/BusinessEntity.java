package com.example.flowable.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BusinessEntity {

    private String businessId;

    private String userId;

    private String processInstanceId;


    private String processDefinitionKey;


    private Boolean isBeiJing;
    private Boolean isShangHai;
    private Boolean isTianJin;

}
