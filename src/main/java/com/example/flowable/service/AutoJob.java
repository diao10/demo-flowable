package com.example.flowable.service;


import org.flowable.common.engine.impl.identity.Authentication;
import org.flowable.engine.TaskService;
import org.flowable.task.service.delegate.DelegateTask;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Service
public class AutoJob {

    @Resource
    private TaskService taskService;

    public void autoApprove(DelegateTask delegateTask) {

        if (delegateTask.getAssignee().contains("lisi")
                || delegateTask.getAssignee().contains("wangwu")) {

            Map<String, Object> variables = new HashMap<>();
            variables.put("approveStatus", "1");
            variables.put("approveComments", "pass");
            variables.put("businessId", delegateTask.getVariable("businessId"));
            variables.put("userId", delegateTask.getAssignee());
            //审批人
            Authentication.setAuthenticatedUserId(delegateTask.getAssignee());
            taskService.addComment(delegateTask.getId(), delegateTask.getProcessInstanceId(), String.valueOf(variables.get("approveComments")));
            taskService.complete(delegateTask.getId(), variables);

        }

//        if (delegateTask.getAssignee().contains("lisi")
//        ) {
//
//            Map<String, Object> variables = new HashMap<>();
//            variables.put("approveStatus", "1");
//            variables.put("approveComments", "pass");
//            variables.put("businessId", delegateTask.getVariable("businessId"));
//            variables.put("userId", delegateTask.getAssignee());
//            //审批人
//            Authentication.setAuthenticatedUserId(delegateTask.getAssignee());
//            taskService.addComment(delegateTask.getId(), delegateTask.getProcessInstanceId(), String.valueOf(variables.get("approveComments")));
//            taskService.complete(delegateTask.getId(), variables);
//
//        }

//        if (delegateTask.getAssignee().contains("wangwu")) {
//            Map<String, Object> variables = new HashMap<>();
//            delegateTask.setVariable("skip", true);
//            delegateTask.setVariable("_FLOWABLE_SKIP_EXPRESSION_ENABLED", true);
//            delegateTask.setVariable();
//    }

    }
}
