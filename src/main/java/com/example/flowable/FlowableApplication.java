package com.example.flowable;

import com.github.xiaoymin.swaggerbootstrapui.annotations.EnableSwaggerBootstrapUI;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableSwaggerBootstrapUI
public class FlowableApplication {

    public static void main(String[] args) {
        SpringApplication.run(FlowableApplication.class, args);
    }

}
