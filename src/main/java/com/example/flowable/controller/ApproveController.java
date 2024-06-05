package com.example.flowable.controller;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.example.flowable.entity.BusinessApproveEntity;
import com.example.flowable.entity.BusinessApproveEntity2;
import com.example.flowable.entity.BusinessEntity;
import com.example.flowable.entity.TaskEntity;
import com.example.flowable.entity.rep.FlowInfoEntityRep;
import com.example.flowable.entity.rep.FlowInfoHistoryEntityRep;
import com.example.flowable.entity.rep.ProcessEntityRep;
import com.example.flowable.entity.rep.TaskInfoRep;
import com.example.flowable.service.DeleteTaskCmd;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.IOUtils;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.EndEvent;
import org.flowable.bpmn.model.FlowElement;
import org.flowable.common.engine.impl.identity.Authentication;
import org.flowable.common.engine.impl.util.IoUtil;
import org.flowable.engine.*;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.history.HistoricProcessInstanceQuery;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.repository.ProcessDefinitionQuery;
import org.flowable.engine.runtime.Execution;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.engine.runtime.ProcessInstanceQuery;
import org.flowable.engine.task.Comment;
import org.flowable.image.ProcessDiagramGenerator;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskQuery;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.flowable.task.api.history.HistoricTaskInstanceQuery;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/flow")
@Api(tags = {"流程引擎-API"})
public class ApproveController {

    @Resource
    private RuntimeService runtimeService;

    @Resource
    private TaskService taskService;

    @Resource
    private RepositoryService repositoryService;

    @Resource
    private HistoryService historyService;

    @Resource
    private IdentityService identityService;

    @Resource
    ProcessEngineConfiguration processEngineConfiguration;


    @PostMapping(value = "/query")
    @ApiOperation("查看工作流")
    public List<ProcessEntityRep> query() {
        ProcessDefinitionQuery queryCondition = repositoryService.createProcessDefinitionQuery();
        List<ProcessDefinition> pageList = queryCondition.orderByDeploymentId().orderByProcessDefinitionVersion().desc().listPage(0, 20);

        List<ProcessEntityRep> result = new ArrayList<>();
        pageList.forEach(i -> {
            ProcessEntityRep p = new ProcessEntityRep();
            p.setId(i.getId());
            p.setDeploymentId(i.getDeploymentId());
            p.setName(i.getName());
            p.setResourceName(i.getResourceName());
            p.setKey(i.getKey());
            p.setDiagramResourceName(i.getDiagramResourceName());
            p.setVersion(i.getVersion());
            result.add(p);
        });


        return result;
    }


    @GetMapping(value = "/queryImage")
    @ApiOperation("查看工作流图片")
    public void queryImage(@RequestParam("id") String id,
                           HttpServletResponse response) {
        BpmnModel bpmnModel = repositoryService.getBpmnModel(id);
        ProcessDiagramGenerator diagramGenerator = processEngineConfiguration.getProcessDiagramGenerator();
        InputStream is = diagramGenerator.generateDiagram(bpmnModel, "bmp", "宋体", "宋体", "宋体", processEngineConfiguration.getClassLoader(), true);
        ServletOutputStream output = null;
        try {
            output = response.getOutputStream();
            IOUtils.copy(is, output);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("图片错误");
        }
    }


    @PostMapping(value = "/process/create")
    @ApiOperation("创建一个流程实例")
    public String processCreate(@RequestBody BusinessEntity entity) {

        HistoricProcessInstanceQuery query = historyService.createHistoricProcessInstanceQuery();
        if (StrUtil.isNotBlank(entity.getBusinessId())) {
            query.processInstanceBusinessKey(entity.getBusinessId());
        }
        List<HistoricProcessInstance> historyList = query.list();
        if (!historyList.isEmpty()) {
            return "业务号已存在";
        }

        if (StrUtil.isBlank(entity.getProcessDefinitionKey())) {
            entity.setProcessDefinitionKey("approveFlow");
        }
        Authentication.setAuthenticatedUserId(entity.getUserId());
        //        identityService.setAuthenticatedUserId(entity.getUserId());
        ProcessInstance processInstance = runtimeService
                .startProcessInstanceByKey(entity.getProcessDefinitionKey(), entity.getBusinessId(), BeanUtil.beanToMap(entity));
        //自动提交的流程
        Task autoTask = taskService.createTaskQuery()
                .processInstanceBusinessKey(entity.getBusinessId()).singleResult();

        taskService.complete(autoTask.getId(), BeanUtil.beanToMap(entity));
        return processInstance.getId();
    }


    @PostMapping(value = "/process/submit")
    @ApiOperation("重新提交一个流程实例")
    public String processSubmit(@RequestBody BusinessEntity entity) {
        //自动提交的流程
        Task autoTask = taskService.createTaskQuery()
                .processInstanceBusinessKey(entity.getBusinessId()).singleResult();

        taskService.complete(autoTask.getId(), BeanUtil.beanToMap(entity));
        return autoTask.getId();
    }


    @PostMapping(value = "/process/approve")
    @ApiOperation("审核一个流程")
    public String processApprove(@RequestBody BusinessApproveEntity entity) {
        //查询当前办理人的任务ID
        Task task = taskService.createTaskQuery()
                //使用流程实例ID
                .processInstanceBusinessKey(entity.getBusinessId())
                .taskAssignee(entity.getUserId())
                //任务办理人
                .singleResult();

        if (task == null) {
            return "流程不存在";

        }
        //审批人
        Authentication.setAuthenticatedUserId(task.getAssignee());
        taskService.addComment(task.getId(), task.getProcessInstanceId(), entity.getApproveComments());
        taskService.complete(task.getId(), BeanUtil.beanToMap(entity), true);
        return task.getProcessInstanceId();
    }

    @PostMapping(value = "/process/approve2")
    @ApiOperation("审核一个流程2")
    public String processApprove2(@RequestBody BusinessApproveEntity2 entity) {
        //查询当前办理人的任务ID
        Task task = taskService.createTaskQuery()
                //使用流程实例ID
                .processInstanceBusinessKey(entity.getBusinessId())
                .taskAssignee(entity.getUserId())
                //任务办理人
                .singleResult();

        if (task == null) {
            return "流程不存在";

        }
        //审批人
        Authentication.setAuthenticatedUserId(task.getAssignee());
        taskService.addComment(task.getId(), task.getProcessInstanceId(), entity.getApproveComments());
        taskService.complete(task.getId(), BeanUtil.beanToMap(entity));
        return task.getProcessInstanceId();
    }


    @PostMapping(value = "/process/back")
    @ApiOperation("驳回一个流程")
    public void processBack(@RequestBody BusinessEntity entity) {

        ProcessInstanceQuery query = runtimeService.createProcessInstanceQuery();
        if (StrUtil.isNotBlank(entity.getBusinessId())) {
            query.processInstanceBusinessKey(entity.getBusinessId());
        }

        if (StrUtil.isNotBlank(entity.getProcessInstanceId())) {
            query.processInstanceId(entity.getProcessInstanceId());
        }
        ProcessInstance process = query.singleResult();
        if (process != null) {
            runtimeService.createChangeActivityStateBuilder()
                    .processInstanceId(entity.getProcessInstanceId())
                    .moveActivityIdToParentActivityId(process.getActivityId(), process.getParentId())
                    .changeState();
        }

    }

    @PostMapping(value = "/process/delete")
    @ApiOperation("删除一个流程")
    public void processDelete(@RequestBody BusinessEntity entity) {

        ProcessInstanceQuery query = runtimeService.createProcessInstanceQuery();
        if (StrUtil.isNotBlank(entity.getBusinessId())) {
            query.processInstanceBusinessKey(entity.getBusinessId());
        }

        if (StrUtil.isNotBlank(entity.getProcessInstanceId())) {
            query.processInstanceId(entity.getProcessInstanceId());
        }
        ProcessInstance process = query.singleResult();
        if (process != null) {
            runtimeService.deleteProcessInstance(process.getId(), "删除");
        }
        // 删除历史数据
        HistoricProcessInstanceQuery history = historyService.createHistoricProcessInstanceQuery();
        if (StrUtil.isNotBlank(entity.getBusinessId())) {
            history.processInstanceBusinessKey(entity.getBusinessId());
        }

        if (StrUtil.isNotBlank(entity.getProcessInstanceId())) {
            history.processInstanceId(entity.getProcessInstanceId());
        }
        HistoricProcessInstance historyProcess = history.singleResult();
        if (historyProcess != null) {
            historyService.deleteHistoricProcessInstance(historyProcess.getId());
        }
    }


    @PostMapping(value = "/process/queryActive")
    @ApiOperation("获取代办任务")
    List processQueryActive(@RequestBody TaskEntity entity) {

        TaskQuery query = taskService.createTaskQuery();

        if (StrUtil.isNotBlank(entity.getProcessId())) {
            query.processInstanceId(entity.getProcessId());
        }

        if (StrUtil.isNotBlank(entity.getBusinessId())) {
            query.processInstanceBusinessKey(entity.getBusinessId());
        }

        if (StrUtil.isNotBlank(entity.getUserId())) {
            query.taskAssignee(entity.getUserId());
        }

        List<Task> tasks = query.orderByTaskCreateTime().desc().list();

        List<TaskInfoRep> taskList = new ArrayList<>();

        tasks.forEach(a -> {
            ProcessInstance process = runtimeService.createProcessInstanceQuery().processInstanceId(a.getProcessInstanceId()).singleResult();
            TaskInfoRep info = new TaskInfoRep();
            info.setAssignee(a.getAssignee());
            info.setBusinessKey(process.getBusinessKey());
            info.setCreateTime(a.getCreateTime());
            info.setTaskName(a.getName());
            info.setExecutionId(a.getExecutionId());
            info.setProcessInstanceId(a.getProcessInstanceId());
            info.setProcessName(process.getProcessDefinitionName());
            info.setStarter(process.getStartUserId());
            info.setStartTime(process.getStartTime());
            info.setTaskId(a.getId());
            taskList.add(info);
        });
        return taskList;
    }


    @PostMapping(value = "/process/end")
    @ApiOperation("终止一个流程")
    public void processEnd(@RequestBody BusinessEntity entity) {

        ProcessInstanceQuery query = runtimeService.createProcessInstanceQuery();
        if (StrUtil.isNotBlank(entity.getBusinessId())) {
            query.processInstanceBusinessKey(entity.getBusinessId());
        }

        if (StrUtil.isNotBlank(entity.getProcessInstanceId())) {
            query.processInstanceId(entity.getProcessInstanceId());
        }
        ProcessInstance process = query.singleResult();
        if (process == null) {
            throw new RuntimeException("流程不存在");
        }
        //1、获取终止节点
        BpmnModel bpmnModel = repositoryService.getBpmnModel(process.getProcessDefinitionId());
        Collection<FlowElement> list = bpmnModel.getMainProcess().getFlowElements();
        if (CollectionUtils.isEmpty(list)) {
            throw new RuntimeException("流程不存在");
        }
        List<FlowElement> endNodes = list.stream().filter(f -> f instanceof EndEvent).collect(Collectors.toList());
        String endId = endNodes.get(0).getId();

        List<Execution> executions = runtimeService.createExecutionQuery().parentId(process.getProcessInstanceId()).list();
        List<String> executionIds = new ArrayList<>();
        executions.forEach(execution -> executionIds.add(execution.getId()));
        runtimeService.createChangeActivityStateBuilder().moveExecutionsToSingleActivityId(executionIds, endId).changeState();

    }

    @PostMapping(value = "/process/query")
    @ApiOperation("查看所有代办流程")
    List processQuery(@RequestBody BusinessEntity entity) {

        ProcessInstanceQuery query = runtimeService.createProcessInstanceQuery();
        if (StrUtil.isNotBlank(entity.getBusinessId())) {
            query.processInstanceBusinessKey(entity.getBusinessId());
        }

        if (StrUtil.isNotBlank(entity.getProcessInstanceId())) {
            query.processInstanceId(entity.getProcessInstanceId());
        }

        List<ProcessInstance> processList = query.orderByProcessDefinitionId().desc().list();
        System.out.println("processList = " + processList);
        List<FlowInfoEntityRep> flows = new ArrayList<>();
        processList.forEach(p -> {
            FlowInfoEntityRep info = new FlowInfoEntityRep();
            info.setProcessInstanceId(p.getProcessInstanceId());
            info.setBusinessKey(p.getBusinessKey());
            info.setName(p.getProcessDefinitionName());
            info.setStartTime(p.getStartTime());
            info.setStartUserId(p.getStartUserId());
            info.setSuspended(p.isSuspended());
            info.setEnded(p.isEnded());
            // 查看当前活动任务
            List<Task> tasks = taskService.createTaskQuery().processInstanceId(p.getProcessInstanceId()).list();
            String taskName = "";
            String assignee = "";
            for (Task t : tasks) {
                taskName += t.getName() + ",";
                assignee += t.getAssignee() + ",";
            }
            taskName = taskName.substring(0, taskName.length() - 1);
            assignee = assignee.substring(0, assignee.length() - 1);
            info.setCurrentTask(taskName);
            info.setAssignee(assignee);
            flows.add(info);
        });
        return flows;
    }


    @GetMapping(value = "/process/queryImage")
    @ApiOperation("查看流程进度图")
    public void processQueryImage(@RequestParam("id") String id, HttpServletResponse httpServletResponse) throws Exception {
        // 获取历史流程实例
        HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId(id)
                .singleResult();
        // 获取流程中已经执行的节点
        String processDefinitionId = historicProcessInstance.getProcessDefinitionId();
        List<String> highLightedActivities = new ArrayList<>();
        // 获得活动的节点
        List<HistoricActivityInstance> highLightedActivityList = historyService.createHistoricActivityInstanceQuery().processInstanceId(id).orderByHistoricActivityInstanceStartTime().asc().list();
        List<String> highLightedFlows = new ArrayList<>();
        for (HistoricActivityInstance tempActivity : highLightedActivityList) {
            String activityId = tempActivity.getActivityId();
            highLightedActivities.add(activityId);
            if ("sequenceFlow".equals(tempActivity.getActivityType())) {
                highLightedFlows.add(activityId);
            }
        }
        // 获取流程图
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);
        ProcessEngineConfiguration engConf = processEngineConfiguration.getProcessEngineConfiguration();

        ProcessDiagramGenerator diagramGenerator = engConf.getProcessDiagramGenerator();
        InputStream in = diagramGenerator.generateDiagram(bpmnModel, "bmp", highLightedActivities, highLightedFlows, "宋体",
                "宋体", "宋体", engConf.getClassLoader(), 1.0, true);
        OutputStream outputStream = null;
        byte[] buf = new byte[1024];
        int length;
        try {
            outputStream = httpServletResponse.getOutputStream();
            while ((length = in.read(buf)) != -1) {
                outputStream.write(buf, 0, length);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IoUtil.closeSilently(outputStream);
            IoUtil.closeSilently(in);
        }
    }


    @PostMapping(value = "/process/queryHistoryTask")
    @ApiOperation("查看历史流程节点（包括代办）")
    List<FlowInfoHistoryEntityRep> queryHistoryTask(@RequestBody BusinessEntity entity) {
        // TODO: 9/5/2023  id和name
        HistoricTaskInstanceQuery query = historyService.createHistoricTaskInstanceQuery();
        if (StrUtil.isNotBlank(entity.getBusinessId())) {
            query.processInstanceBusinessKey(entity.getBusinessId());
        }

        if (StrUtil.isNotBlank(entity.getProcessInstanceId())) {
            query.processInstanceId(entity.getProcessInstanceId());
        }

        if (StrUtil.isNotBlank(entity.getUserId())) {
            query.taskAssignee(entity.getUserId());
        }
        query.processVariableValueEquals("1",2);
        List<HistoricTaskInstance> instances = query.orderByHistoricTaskInstanceStartTime().desc().list();
        List<FlowInfoHistoryEntityRep> flows = new ArrayList<>();
        instances.forEach(i -> {
            FlowInfoHistoryEntityRep info = new FlowInfoHistoryEntityRep();
            info.setProcessInstanceId(i.getProcessInstanceId());
            info.setExecutionId(i.getExecutionId());
            info.setName(i.getName());
            info.setDeleteReason(i.getDeleteReason());
            info.setTaskDefinitionKey(i.getTaskDefinitionKey());
            info.setCreateTime(i.getCreateTime());
            info.setEndTime(i.getEndTime());
            info.setDurationInMillis(i.getDurationInMillis());
            info.setAssignee(i.getAssignee());
            List<Comment> commentList = taskService.getTaskComments(i.getId());
            if (!commentList.isEmpty()) {
                info.setApproveComments(commentList.get(0).getFullMessage());
            }
            flows.add(info);
        });
        return flows;
    }

    @PostMapping(value = "/process/queryHistoryProcess")
    @ApiOperation("查看历史流程（包括代办）")
    List<HistoricProcessInstance> queryHistoryProcess(@RequestBody BusinessEntity entity) {

        HistoricProcessInstanceQuery query = historyService.createHistoricProcessInstanceQuery();
        if (StrUtil.isNotBlank(entity.getBusinessId())) {
            query.processInstanceBusinessKey(entity.getBusinessId());
        }

        if (StrUtil.isNotBlank(entity.getProcessInstanceId())) {
            query.processInstanceId(entity.getProcessInstanceId());
        }

        List<HistoricProcessInstance> instances = query.orderByProcessInstanceEndTime().desc().list();
        return instances;
    }

    @PostMapping(value = "/process/updateAssignee")
    @ApiOperation("修改流程人")
    String updateAssignee(@RequestBody BusinessEntity entity) {
        //查询当前办理人的任务ID
        List<Task> taskList = taskService.createTaskQuery()
                //使用流程实例ID
                .processInstanceBusinessKey(entity.getBusinessId()).list();

        if (taskList == null || taskList.isEmpty()) {
            return "流程不存在";
        }
        for (Task task : taskList) {
            taskService.setAssignee(task.getId(), task.getAssignee().replace("liuliu", "qiqi"));
        }
        return "success";
    }

    @Resource
    private ManagementService managementService;

    @PostMapping(value = "/task/delete")
    @ApiOperation("taskDelete")
    String taskDelete(@RequestBody BusinessEntity entity) {

        TaskQuery query = taskService.createTaskQuery();
        List<Task> list = query.processInstanceBusinessKey(entity.getBusinessId())
                .list();
        list.forEach(i -> {
            managementService.executeCommand(new DeleteTaskCmd(i.getExecutionId(), "deleteReason"));
        });

        return "success";
    }
}
