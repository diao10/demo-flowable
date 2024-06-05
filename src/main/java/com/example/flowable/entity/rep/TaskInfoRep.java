package com.example.flowable.entity.rep;


import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
public class TaskInfoRep {

    String taskId;

    String processInstanceId;

    String executionId;

    String businessKey;

    String processName;

    String taskName;

    String starter;

    String assignee;

    Date startTime;

    Date endTime;

    Date createTime;

    String formKey;

    String comment;

}
