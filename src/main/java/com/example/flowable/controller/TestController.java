package com.example.flowable.controller;


import cn.hutool.core.bean.BeanUtil;
import com.example.flowable.entity.EventEntity;
import com.example.flowable.entity.ProcessEntity;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.engine.*;
import org.flowable.engine.runtime.Execution;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.image.ProcessDiagramGenerator;
import org.flowable.task.api.Task;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/flower")
@Api(tags = {"流程引擎-test"})
public class TestController {

    @Resource
    private RuntimeService runtimeService;

    @Resource
    private TaskService taskService;

    @Resource
    private RepositoryService repositoryService;

    @Resource
    private ProcessEngine processEngine;

    @Resource
    private HistoryService historyService;

    /**
     * 生成流程图
     *
     * @param processId 任务ID
     */
    @GetMapping(value = "/generateDiagram")
    @ApiOperation("生成流程图")
    public void generateDiagram(HttpServletResponse httpServletResponse, String processId) throws Exception {
        ProcessInstance pi = runtimeService.createProcessInstanceQuery().processInstanceId(processId).singleResult();

        //流程走完的不显示图
        if (pi == null) {
            return;
        }
        Task task = taskService.createTaskQuery().processInstanceId(pi.getId()).singleResult();
        //使用流程实例ID，查询正在执行的执行对象表，返回流程实例对象
        String InstanceId = task.getProcessInstanceId();
        List<Execution> executions = runtimeService
                .createExecutionQuery()
                .processInstanceId(InstanceId)
                .list();

        //得到正在执行的Activity的Id
        List<String> activityIds = new ArrayList<>();
        List<String> flows = new ArrayList<>();
        for (Execution exe : executions) {
            List<String> ids = runtimeService.getActiveActivityIds(exe.getId());
            activityIds.addAll(ids);
        }

        //获取流程图
        BpmnModel bpmnModel = repositoryService.getBpmnModel(pi.getProcessDefinitionId());
        ProcessEngineConfiguration engConf = processEngine.getProcessEngineConfiguration();
        ProcessDiagramGenerator diagramGenerator = engConf.getProcessDiagramGenerator();
        InputStream in = diagramGenerator.generateDiagram(bpmnModel, "png", activityIds, flows, engConf.getActivityFontName(), engConf.getLabelFontName(), engConf.getAnnotationFontName(), engConf.getClassLoader(), 1.0, true);
        OutputStream out = null;
        byte[] buf = new byte[1024];
        int length = 0;
        try {
            out = httpServletResponse.getOutputStream();
            while ((length = in.read(buf)) != -1) {
                out.write(buf, 0, length);
            }
        } finally {
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
        }
    }


    @PostMapping(value = "/createProcess")
    @ApiOperation("创建一个流程实例")
    public String genProcessDiagram(@RequestBody EventEntity entity) {
        ProcessInstance processInstance = runtimeService
                .startProcessInstanceByKey("approveFlow", entity.getEventID(), BeanUtil.beanToMap(entity));
        System.out.println("processInstance.toString() = " + processInstance.toString());
        System.out.println(processInstance.getId());
        System.out.println(processInstance.getBusinessKey());
        System.out.println(processInstance.getActivityId());

        Task autoTask = taskService.createTaskQuery()
                .processInstanceBusinessKey(entity.getEventID()).singleResult();
        taskService.complete(autoTask.getId());
        System.out.println("autoTask.getId() = " + autoTask.getId());

        return processInstance.getId();
    }


    @PostMapping(value = "/approveProcess")
    @ApiOperation("审核一个流程实例")
    public void approveProcess(@RequestBody ProcessEntity entity) {

        //查询当前办理人的任务ID
        Task task = taskService.createTaskQuery()
                //使用流程实例ID
                .processInstanceId(entity.getProcessId())
                //任务办理人
                .singleResult();
        if (task == null) {
            throw new RuntimeException("流程不存在");
        }
        System.out.println("task = " + task);
        Map<String, Object> map = BeanUtil.beanToMap(entity);
        taskService.complete(task.getId(), map);

    }


    @PostMapping(value = "/eventByProcess")
    @ApiOperation("获取某个流程")
    List eventByProcess(@RequestBody ProcessEntity entity) {
        List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery().processInstanceId(entity.getProcessId()).list();
        System.out.println("list = " + list);


        return list;
    }


    @PostMapping(value = "/eventByTaskID")
    @ApiOperation("获取指定人的代办任务")
    List eventByTaskID(@RequestBody ProcessEntity entity) {
        //查询当前办理人的任务ID
        List<Task> task = taskService.createTaskQuery()
                //使用流程实例ID
                .taskId(entity.getTaskID())
                .list();
        if (task == null) {
            throw new RuntimeException("流程不存在");
        }
        System.out.println("task = " + task);
        return new ArrayList<>(task);
    }

    @PostMapping(value = "/eventByAssignee")
    @ApiOperation("获取指定人的代办任务")
    List eventByAssignee(@RequestBody ProcessEntity entity) {
        //查询当前办理人的任务ID
        List<Task> task = taskService.createTaskQuery()
                //使用流程实例ID
                .processInstanceId(entity.getProcessId())
                //任务办理人
                .taskAssignee(entity.getUserID())
                .list();
        if (task == null) {
            throw new RuntimeException("流程不存在");
        }
        System.out.println("task = " + task);
        return new ArrayList<>(task);
    }

    @PostMapping(value = "/eventByProcessID")
    @ApiOperation("获取的代办任务")
    List eventByProcessID(@RequestBody ProcessEntity entity) {
        //查询当前办理人的任务ID
        List<Task> task = taskService.createTaskQuery()
                //使用流程实例ID
                .processInstanceId(entity.getProcessId())
                //任务办理人
                 .list();
        if (task == null) {
            throw new RuntimeException("流程不存在");
        }
        System.out.println("task = " + task);
        return new ArrayList<>(task);
    }


//    @PostMapping(value = "/eventByAssignee")
//    @ApiOperation("获取指定人的已办任务")
//    List taskInstance(@RequestBody ProcessEntity entity) {
//        //查询当前办理人的任务ID
//        List<Task> task = taskService.createTaskQuery()
//                //使用流程实例ID
//                .processInstanceId(entity.getProcessId())
//                //任务办理人
//                .taskAssignee(entity.getUserID())
//                .list();
//        if (task == null) {
//            throw new RuntimeException("流程不存在");
//        }
//        System.out.println("task = " + task);
//        return task;
//    }


    @PostMapping(value = "/eventHistory")
    @ApiOperation("获取某人的历史审批数据")
    List queryHistoryProcess(@RequestBody ProcessEntity entity) {
        return null;
    }

    @PostMapping(value = "/deleteProcess")
    @ApiOperation("终止流程实例")
    void deleteProcess(@RequestBody ProcessEntity entity) {
        //查询流程实例
        ProcessInstance instance = runtimeService.createProcessInstanceQuery()
                .processInstanceId(entity.getProcessId()).singleResult();
        if (instance == null) {
            throw new RuntimeException("流程实例不能为空");
        }

        //获取运行时执行实例表
        List<Execution> executions = runtimeService.createExecutionQuery().parentId(entity.getProcessId()).list();
        List<String> executionIds = new ArrayList<>();
        for (Execution x : executions) {
            executionIds.add(x.getId());
        }

        runtimeService.createChangeActivityStateBuilder()
                .moveExecutionsToSingleActivityId(executionIds, "endID").changeState();
    }


}
