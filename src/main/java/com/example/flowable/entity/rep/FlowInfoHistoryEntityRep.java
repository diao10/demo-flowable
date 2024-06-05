package com.example.flowable.entity.rep;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 流程实例信息
 */
@Data
@NoArgsConstructor
public class FlowInfoHistoryEntityRep {

    String processInstanceId;

    String executionId;

    String name;

    String deleteReason;


    String taskDefinitionKey;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    Date endTime;

    Long durationInMillis;

    // 办理人
    String assignee;

  String  approveComments;

}
