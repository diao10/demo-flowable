//package com.example.flowable.config;
//
//import org.flowable.common.engine.api.delegate.event.FlowableEngineEventType;
//import org.flowable.common.engine.api.delegate.event.FlowableEventDispatcher;
//import org.flowable.spring.SpringProcessEngineConfiguration;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.ApplicationListener;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.event.ContextRefreshedEvent;
//
//@Configuration
//public class FlowableGlobListenerConfig implements ApplicationListener<ContextRefreshedEvent> {
//    @Autowired
//    private SpringProcessEngineConfiguration configuration;
//
//    @Autowired
//    private GlobalTaskCreatedListener globalTaskCreatedListener;
//
//    @Override
//    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
//        FlowableEventDispatcher dispatcher = configuration.getEventDispatcher();
//        dispatcher.addEventListener(globalTaskCreatedListener, FlowableEngineEventType.TASK_CREATED);
//    }
//}
