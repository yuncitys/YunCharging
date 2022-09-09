package com.sharecharge.web;

import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication(exclude= DataSourceAutoConfiguration.class)//Failed to configure a DataSource: 'url' attribute is not specified .....
@MapperScan(value = "com.sharecharge.*.mapper")//No qualifying bean of type 'com.sharecharge.system.mapper.DbAdminUserMapper' available: expected at least 1 bean which qualifies as autowire candidate. Dependency annotations: {@org.springframework.beans.factory.annotation.Autowired(required=true)}
@ComponentScan(basePackages = {"com.sharecharge"})//Parameter 0 of constructor in com.sharecharge.web.token.filter.service.SysUserDetailsServiceImpl required a bean of type 'com.sharecharge.system.service.DbAdminUserService' that could not be found.
public class ShareChargeApplication {
    public static void main(String[] args) {
        SpringApplication.run(ShareChargeApplication.class,args);
    }
}
