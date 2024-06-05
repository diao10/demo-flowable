//package com.example.flowable.config;
//
//import cn.hutool.core.util.StrUtil;
//import lombok.extern.slf4j.Slf4j;
//import org.flowable.bpmn.model.BpmnModel;
//import org.flowable.bpmn.model.MultiInstanceLoopCharacteristics;
//import org.flowable.bpmn.model.Process;
//import org.flowable.bpmn.model.UserTask;
//import org.flowable.common.engine.api.delegate.event.FlowableEvent;
//import org.flowable.common.engine.api.delegate.event.FlowableEventListener;
//import org.flowable.common.engine.impl.event.FlowableEntityEventImpl;
//import org.flowable.engine.RepositoryService;
//import org.flowable.engine.RuntimeService;
//import org.flowable.engine.TaskService;
//import org.flowable.engine.runtime.ProcessInstance;
//import org.flowable.task.service.impl.persistence.entity.TaskEntity;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import java.util.ArrayList;
//import java.util.List;
//
//@Component
//@Slf4j
//public class GlobalTaskCreatedListener implements FlowableEventListener {
//    @Autowired
//    private TaskService taskService;
//    @Autowired
//    private RuntimeService runtimeService;
//    @Autowired
//    private RepositoryService repositoryService;
//
//    @Override
//    public void onEvent(FlowableEvent event) {
//        FlowableEntityEventImpl entityEvent = (FlowableEntityEventImpl) event;
//        TaskEntity taskEntity = (TaskEntity) entityEvent.getEntity();
//        String processInstanceId = taskEntity.getProcessInstanceId();
//        BpmnModel bpmnModel = repositoryService.getBpmnModel(taskEntity.getProcessDefinitionId());
//        List<Process> processes = bpmnModel.getProcesses();
//
//        processes.forEach(process -> process.findFlowElementsOfType(UserTask.class).forEach(userTask -> {
//            if (StrUtil.equals(userTask.getId(), taskEntity.getTaskDefinitionKey()) && !userTask.getCandidateGroups().isEmpty()) {
//                ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
//                //获取角色名字
//                List<String> roles = userTask.getCandidateGroups();
//                String userId = (String) processInstance.getProcessVariables().get("userId");
//                if (StrUtil.isBlank(userId)) {
//                    throw new RuntimeException("用户为空");
//                }
//                // TODO: 23/4/2023 在这里找上级角色和用户
//                if (roles.contains("同级部门经理")) {
//                    List<String> list = new ArrayList<>();
//                    list.add("lisi1");
//                    list.add("lisi2");
//                    list.add("lisi3");
//                    MultiInstanceLoopCharacteristics multiInstanceLoopCharacteristics=userTask.getLoopCharacteristics();
//                    multiInstanceLoopCharacteristics.setCollectionString(list.toString());
//                    multiInstanceLoopCharacteristics.setElementVariable("userId");
//                    userTask.setLoopCharacteristics(multiInstanceLoopCharacteristics);
//
//                } else if (roles.contains("上级经理")) {
////                    taskService.addCandidateUser(taskEntity.getId(), "wangwu1");
////                    taskService.addCandidateUser(taskEntity.getId(), "wangwu2");
////                    taskService.addCandidateUser(taskEntity.getId(), "wangwu3");
//                    List<String> list = new ArrayList<>();
//                    list.add("wangwu1");
//                    list.add("wangwu2");
//                    list.add("wangwu3");
//                    MultiInstanceLoopCharacteristics multiInstanceLoopCharacteristics=userTask.getLoopCharacteristics();
//                    multiInstanceLoopCharacteristics.setCollectionString(list.toString());
//                    multiInstanceLoopCharacteristics.setElementVariable("userId");
//                    userTask.setLoopCharacteristics(multiInstanceLoopCharacteristics);
//                } else if (roles.contains("支行行长")) {
////                    taskService.addCandidateUser(taskEntity.getId(), "liuliu1");
////                    taskService.addCandidateUser(taskEntity.getId(), "liuliu2");
////                    taskService.addCandidateUser(taskEntity.getId(), "liuliu3");
//                    List<String> list = new ArrayList<>();
//                    list.add("liuliu1");
//                    list.add("liuliu2");
//                    list.add("liuliu3");
//                    MultiInstanceLoopCharacteristics multiInstanceLoopCharacteristics=userTask.getLoopCharacteristics();
//                    multiInstanceLoopCharacteristics.setCollectionString(list.toString());
//                    multiInstanceLoopCharacteristics.setElementVariable("userId");
//                    userTask.setLoopCharacteristics(multiInstanceLoopCharacteristics);
//                }
//            }
//        }));
//    }
//
//    @Override
//    public boolean isFailOnException() {
//        return false;
//    }
//
//    @Override
//    public boolean isFireOnTransactionLifecycleEvent() {
//        return false;
//    }
//
//    @Override
//    public String getOnTransaction() {
//        return null;
//    }
//}
