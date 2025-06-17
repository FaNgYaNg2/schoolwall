package com.fy.schoolwall;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;

@SpringBootApplication
@MapperScan("com.fy.schoolwall.*.repository") // 扫描所有模块的 Mapper 接口
@Configuration // 确保配置类被 Spring 扫描
public class SchoolwallApplication {

	public static void main(String[] args) {
		SpringApplication.run(SchoolwallApplication.class, args);
	}
}
