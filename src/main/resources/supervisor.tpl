# %s 进程守护配置 &#x000A;
[program:%s] &#x000A;
directory = /opt/apps/%s &#x000A;
autostart = false &#x000A;
startsecs = 5 &#x000A;
autorestart = true &#x000A;
startretries = 3 &#x000A;
user = root &#x000A;
redirect_stderr = true &#x000A;
stdout_logfile_maxbytes = 100MB &#x000A;
stdout_logfile_backups = 10 &#x000A;
environment=GO_ENV=%s,NODE_ENV=%s,JAVA_ENV=%s,ENV=%s &#x000A;
stdout_logfile = /opt/logs/%s/%s-log-stdout.log &#x000A;

&#x000A;

# Java 程序启动示例 &#x000A;
# command = java -jar Dspring.config.location=/opt/configs/path/to/config.yml  __BIN__ &#x000A;
&#x000A;

# Go 程序启动示例 &#x000A;
# command = /opt/apps/path/to/__BIN__ -config /opt/configs/path/to/config.yml &#x000A;

&#x000A;

# 自定义启动命令
# command = ... &#x000A;

&#x000A;