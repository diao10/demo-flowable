package com.example.flowable.entity;


import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class EventEntity {

    private String eventID;

    private String userId;

    private String parentUserId;

}
