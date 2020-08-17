package com.demofactory.syscontrol.service;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan({"com.demofactory.syscontrol.dao","com.baomidou.mybatisplus.samples.quickstart.mapper"})
public class SyscontrolServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SyscontrolServiceApplication.class, args);
    }

}
