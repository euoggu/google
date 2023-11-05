package com.example.google.tabavtive.mac;

import org.springframework.core.io.ClassPathResource;
import org.springframework.util.FileCopyUtils;

import java.io.*;

public class StartupScriptExecutor {

    private static String projectRootPath = System.getProperty("user.dir");


    public static void executeStartupScript() throws IOException {
        // 定位 resources 目录下的脚本
        ClassPathResource classPathResource = new ClassPathResource("mac/register_startup.sh");

        // 创建临时文件
        File tempScript = File.createTempFile("register_startup", ".sh");

        // 将脚本从资源文件复制到临时文件
        try (InputStream is = classPathResource.getInputStream();
             FileOutputStream os = new FileOutputStream(tempScript)) {
            FileCopyUtils.copy(is, os);
        }

        // 修改临时文件的执行权限
        if (!tempScript.setExecutable(true)) {
            throw new IOException("Failed to set script executable");
        }

        // 执行临时脚本文件
        Process process = null;
        try {
            // 包含项目目录路径作为参数
            String[] cmd = { "/bin/bash", tempScript.getAbsolutePath(), projectRootPath };
            process = Runtime.getRuntime().exec(cmd);

            // 获取脚本执行的输出
            printStream(process.getInputStream());
            // 获取脚本执行的错误输出
            printStream(process.getErrorStream());

            // 等待进程结束
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new IOException("Script executed with errors.");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Interrupted while executing startup script", e);
        } finally {
            // 删除临时文件
            tempScript.deleteOnExit();

            if (process != null) {
                process.destroy();
            }
        }
    }

    // ...

    private static void printStream(InputStream stream) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("开机自启注册结果: " + line);
            }
        }
    }
}
