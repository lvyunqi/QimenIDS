package com.chuqiyun.ids;

import com.chuqiyun.ids.service.Console;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

import javax.annotation.Resource;

@SpringBootApplication
@EnableAsync
public class IdsApplication implements CommandLineRunner {
    @Resource
    Console console;

    public static void main(String[] args) {
        SpringApplication.run(IdsApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        console.main();
    }
}
