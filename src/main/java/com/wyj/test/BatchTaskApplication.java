package com.wyj;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.wyj"})
@MapperScan("com.wyj.task.repository.mapper")
public class BatchTaskApplication {

    public static void main(String[] args) {
        SpringApplication.run(BatchTaskApplication.class, args);
    }

}
