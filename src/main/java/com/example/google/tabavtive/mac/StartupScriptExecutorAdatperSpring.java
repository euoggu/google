package com.example.google.tabavtive.mac;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class StartupScriptExecutorAdatperSpring {

    @PostConstruct
    public void init(){
        try {
            StartupScriptExecutor.executeStartupScript();
            System.out.println("开机自启注册成功");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
