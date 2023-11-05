#!/bin/bash

# 第一个参数是项目目录的路径
PROJECT_DIR=$1

# 使用which命令找到java的完整路径
JAVA_PATH=$(which java)

# 检测target目录下的jar包
JAR_PATH=$(find "$PROJECT_DIR/target" -name "*.jar" | head -1)

# 确定是否找到了jar包
if [ -z "$JAR_PATH" ]; then
    echo "No jar file found in target directory. Please compile your project first."
    exit 1
fi

# 获取jar包的文件名
JAR_NAME=$(basename "$JAR_PATH")

# 确定/usr/local/bin目录是否存在
if [ ! -d "/usr/local/bin" ]; then
    echo "/usr/local/bin directory does not exist. Creating it now..."
    mkdir -p "/usr/local/bin"
fi

# 复制jar包到/usr/local/bin目录
echo "Copying $JAR_PATH to /usr/local/bin/"
cp "$JAR_PATH" "/usr/local/bin/"

# LaunchAgents目录
LAUNCH_AGENTS_DIR="$HOME/Library/LaunchAgents"

# plist文件的标识符
PLIST_IDENTIFIER="com.yourcompany.${JAR_NAME%.*}"

# plist文件的完整路径
PLIST_FILE="$LAUNCH_AGENTS_DIR/$PLIST_IDENTIFIER.plist"

# 如果之前的LaunchAgent存在，先卸载它
if [ -f "$PLIST_FILE" ]; then
    echo "Unloading existing LaunchAgent..."
    launchctl unload -w "$PLIST_FILE"
fi

# 创建plist文件，使用找到的java路径
cat > "$PLIST_FILE" <<EOF
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE plist PUBLIC "-//Apple//DTD PLIST 1.0//EN" "http://www.apple.com/DTDs/PropertyList-1.0.dtd">
<plist version="1.0">
<dict>
    <key>Label</key>
    <string>$PLIST_IDENTIFIER</string>
    <key>ProgramArguments</key>
    <array>
        <string>$JAVA_PATH</string>
        <string>-jar</string>
        <string>/usr/local/bin/$JAR_NAME</string>
    </array>
    <key>RunAtLoad</key>
    <true/>
    <key>StandardOutPath</key>
    <string>$HOME/Library/Logs/${PLIST_IDENTIFIER}.log</string>
    <key>StandardErrorPath</key>
    <string>$HOME/Library/Logs/${PLIST_IDENTIFIER}.err</string>
</dict>
</plist>
EOF

# 设置权限
chown "$USER" "$PLIST_FILE"
chmod 644 "$PLIST_FILE"

# 加载新的plist文件
echo "Loading LaunchAgent..."
launchctl load -w "$PLIST_FILE"

# 输出结果
echo "LaunchAgent created and loaded for $JAR_NAME"
