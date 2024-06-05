package com.example.flowable.service;

import cn.hutool.core.util.StrUtil;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

@Service
public class IfLastRoleService implements JavaDelegate {
    @Override
    public void execute(DelegateExecution execution) {

        String userId = (String) execution.getVariable("userId");
        System.out.println("userId = " + userId);
        if (userId.contains("liuliu")  ||"admin".equals(userId)) {
            execution.setVariable("isLastRole", true);
        } else {
            execution.setVariable("isLastRole", false);
        }
    }
}
