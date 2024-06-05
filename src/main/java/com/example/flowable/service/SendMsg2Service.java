package com.example.flowable.service;


import org.flowable.engine.RuntimeService;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.runtime.ProcessInstance;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class SendMsg2Service {

    @Resource
    private RuntimeService runtimeService;

    public void send(DelegateExecution execution) {
        String businessId = execution.getProcessInstanceBusinessKey();
        ProcessInstance instance = runtimeService.createProcessInstanceQuery().processInstanceBusinessKey(businessId).singleResult();
        System.out.println("给" + instance.getStartUserId() + "：发送信息");
    }
}
