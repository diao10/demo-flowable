package com.example.flowable.service;

import org.flowable.engine.ManagementService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskQuery;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class ApproveService {


    @Resource
    private RuntimeService runtimeService;
    @Resource
    private TaskService taskService;


    public void approvePass(String businessId, String approveStatus, String approveComments) {
        System.out.println("businessId = " + businessId);
        System.out.println("approveStatus = " + approveStatus);
        System.out.println("approveComments = " + approveComments);
        System.out.println("approvePass");
    }


    public void approveRefuse(DelegateExecution execution, String businessId, String approveStatus, String approveComments) {
        System.out.println("businessId = " + businessId);
        System.out.println("approveStatus = " + approveStatus);
        System.out.println("approveComments = " + approveComments);

        ProcessInstance instance = runtimeService.createProcessInstanceQuery().processInstanceBusinessKey(businessId).singleResult();
        execution.setVariable("userId", instance.getStartUserId());

    }


    public void approvePass2(String businessId, Boolean isPass, String approveComments) {
        System.out.println("businessId = " + businessId);
        System.out.println("isPass = " + isPass);
        System.out.println("approveComments = " + approveComments);
        System.out.println("approvePass");
    }

    @Resource
    private ManagementService managementService;

    public void approveRefuse2(DelegateExecution execution, String businessId, Boolean isPass, String approveComments) {
        System.out.println("businessId = " + businessId);
        System.out.println("isPass = " + isPass);
        System.out.println("approveComments = " + approveComments);

        ProcessInstance instance = runtimeService.createProcessInstanceQuery().processInstanceBusinessKey(businessId).singleResult();

        TaskQuery query = taskService.createTaskQuery();
        List<Task> list = query.processInstanceBusinessKey(execution.getProcessInstanceBusinessKey())
                .active().list();
        list.forEach(i -> {
            managementService.executeCommand(new DeleteTaskCmd(i.getExecutionId(), "deleteReason"));
        });


        execution.setVariable("userId", instance.getStartUserId());

    }
}
