package com.example.google.tabavtive.mac;

import org.springframework.core.io.ClassPathResource;
import org.springframework.util.FileCopyUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ScriptExecutor {

    public static void executeAppleScriptFromResources(String path) throws IOException {
        // 定位 resources 目录下的脚本
        ClassPathResource classPathResource = new ClassPathResource(path);

        // 创建临时文件
        File tempScript = File.createTempFile("script", ".scpt");

        // 将脚本从资源文件复制到临时文件
        try (InputStream is = classPathResource.getInputStream();
             FileOutputStream os = new FileOutputStream(tempScript)) {
            FileCopyUtils.copy(is, os);
        }

        // 执行临时脚本文件
        try {
            String[] cmd = { "osascript", tempScript.getAbsolutePath() };
            Runtime.getRuntime().exec(cmd);
        } finally {
            // 删除临时文件
            tempScript.deleteOnExit();
        }
    }
}
