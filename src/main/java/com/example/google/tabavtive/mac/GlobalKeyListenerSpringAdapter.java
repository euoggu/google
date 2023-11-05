package com.example.google.tabavtive.mac;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
public class GlobalKeyListenerSpringAdapter {

    @PostConstruct
    public void init(){
        // Check if the OS is macOS
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.startsWith("mac")) {
            GlobalKeyListener.registerNativeHook();
        }
    }

}

