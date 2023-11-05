package com.example.google.tabavtive.mac;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import java.io.IOException;

public class GlobalKeyListener implements NativeKeyListener {
    private long lastPressTime = 0;
    private boolean controlPressed = false;

    public void nativeKeyPressed(NativeKeyEvent e) {
        if (e.getKeyCode() == NativeKeyEvent.VC_CONTROL) {
            long pressTime = System.currentTimeMillis();
            if (pressTime - lastPressTime < 500) { // 如果两次按压的时间小于500毫秒，认为是双击
                executeAppleScript();
                controlPressed = false; // 重置标志位
            } else {
                controlPressed = true; // 设置标志位
            }
            lastPressTime = pressTime;
        }
    }

    public void nativeKeyReleased(NativeKeyEvent e) {
        if (e.getKeyCode() == NativeKeyEvent.VC_CONTROL && controlPressed) {
            // 处理单击逻辑，如果需要的话
            controlPressed = false;
        }
    }

    public void nativeKeyTyped(NativeKeyEvent e) {
        // Ignore
    }

    private void executeAppleScript() {
        try {
//            String[] cmd = { "osascript", "/Users/gaoziteng/Library/Mobile\\ Documents/com~apple~ScriptEditor2/Documents/google-tab.scpt" };
//            String[] cmd = { "osascript", "/usr/local/bin/google-tab.scpt" };
//            Runtime.getRuntime().exec(cmd);
            ScriptExecutor.executeAppleScriptFromResources("mac/google-tab.scpt");
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    public static void registerNativeHook() {
        try {
            GlobalScreen.registerNativeHook();
        } catch (NativeHookException ex) {
            System.err.println("There was a problem registering the native hook.");
            System.err.println(ex.getMessage());
            System.exit(1);
        }

        GlobalScreen.addNativeKeyListener(new GlobalKeyListener());
    }
}
