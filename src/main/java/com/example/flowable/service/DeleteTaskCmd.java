package com.example.flowable.service;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import org.flowable.common.engine.api.FlowableException;
import org.flowable.common.engine.impl.interceptor.Command;
import org.flowable.common.engine.impl.interceptor.CommandContext;
import org.flowable.engine.impl.persistence.entity.ExecutionEntity;
import org.flowable.engine.impl.persistence.entity.ExecutionEntityManager;
import org.flowable.engine.impl.util.CommandContextUtil;

import java.io.Serializable;

public class DeleteTaskCmd implements Command<String>, Serializable {


    public static final long serialVersionUID = 1L;
    protected String executionId;
    protected String deleteReason;

    public DeleteTaskCmd(String executionId, String deleteReason) {

        this.executionId = executionId;
        this.deleteReason = deleteReason;

    }


    @Override
    public String execute(CommandContext commandContext) {

        if (StrUtil.isBlank(executionId)) {

            throw new FlowableException("executionId cannot be empty");

        }

        ExecutionEntityManager executionEntityManager = CommandContextUtil.getExecutionEntityManager();

        ExecutionEntity executionEntity = CommandContextUtil.getExecutionEntityManager().findById(executionId);

        Assert.notNull(executionEntity, "ExecutionEntity:{}不存在", executionId);

        executionEntityManager.deleteExecutionAndRelatedData(executionEntity, deleteReason, false);

        return executionId;

    }

}

