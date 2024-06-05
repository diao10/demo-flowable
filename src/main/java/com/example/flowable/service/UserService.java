package com.example.flowable.service;


import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

@Service
public class UserService {


    @Resource
    private RuntimeService runtimeService;

    @Resource
    private TaskService taskService;

    //会被多次调用
    private static final WeakHashMap<String, List<String>> weakHashMap = new WeakHashMap<>();


//    public List<String> getParentUserIdList(String userId, String roleName) {
//        System.out.println("userId = " + userId);
//        System.out.println("roleName = " + roleName);
//
//        if (weakHashMap.containsKey(userId + roleName)) {
//            return weakHashMap.get(userId + roleName);
//        }
//        List<String> list = new ArrayList<>();
//        if (roleName.contains("同级部门经理")) {
//            list.add("lisi1");
//            list.add("lisi2");
//            list.add("lisi3");
//        } else if (roleName.contains("上级经理")) {
//            list.add("wangwu1");
//            list.add("wangwu2");
//            list.add("wangwu3");
//        } else if (roleName.contains("支行行长")) {
//            list.add("liuliu1");
//            list.add("liuliu2");
//            list.add("liuliu3");
//        }
//        System.out.println("------------------查询----------------------");
//        weakHashMap.put(userId + roleName, list);
//        return list;
//
//    }

    public List<Map<String, String>> getParentUserIdList(String userId, String roleName) {
        System.out.println("userId = " + userId);
        System.out.println("roleName = " + roleName);

        List<Map<String, String>> list = new ArrayList<>();
        Map<String, String> map = new HashMap<>();
        Map<String, String> map1 = new HashMap<>();
        Map<String, String> map2 = new HashMap<>();
        Map<String, String> map3 = new HashMap<>();

        if (roleName.contains("同级部门经理")) {
            map1.put("userId", "lisi1");
            list.add(map1);
            map2.put("userId", "lisi2");
            list.add(map2);
            map3.put("userId", "lisi3");
            list.add(map3);
        } else if (roleName.contains("上级经理")) {
            map.put("userId", "wangwu1");
            list.add(map);
            map.put("userId", "wangwu2");
            list.add(map);
            map.put("userId", "wangwu3");
            list.add(map);

        } else if (roleName.contains("支行行长")) {
            map.put("userId", "liuliu1");
            list.add(map);
            map.put("userId", "liuliu2");
            list.add(map);
            map.put("userId", "liuliu3");
            list.add(map);

        }
        System.out.println("------------------查询----------------------");
        return list;

    }


    // TODO: 9/5/2023  多个参数呢
    public List<String> getParentUserIdList(String userId) {
        List<String> list = new ArrayList<>();

        if (userId.contains("zhangsan")) {
            list.add("lisi1");
            list.add("lisi2");
            list.add("lisi3");
        } else if (userId.contains("lisi")) {
            list.add("wangwu1");
            list.add("wangwu2");
            list.add("wangwu3");
        } else if (userId.contains("wangwu")) {
            list.add("liuliu1");
            list.add("liuliu2");
            list.add("liuliu3");
        }
        if (list.isEmpty()) {
//            list.add("admin");
        }
        return list;

    }


//    public String getStartUserId(DelegateExecution execution) {
//        String businessId = execution.getProcessInstanceBusinessKey();
//        ProcessInstance instance = runtimeService.createProcessInstanceQuery().processInstanceBusinessKey(businessId).singleResult();
//        return instance.getStartUserId();
//    }
}
