package com.example.flowable.service;


import org.flowable.engine.HistoryService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.flowable.engine.runtime.ProcessInstance;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class SendMsgService implements JavaDelegate {

    @Resource
    private RuntimeService runtimeService;

    @Override
    public void execute(DelegateExecution execution) {
        // TODO: 9/5/2023  
        execution.getVariables();
        String businessId = execution.getProcessInstanceBusinessKey();
        ProcessInstance instance = runtimeService.createProcessInstanceQuery().processInstanceBusinessKey(businessId).singleResult();
        System.out.println("给" + instance.getStartUserId() + "：发送信息");
    }
}
