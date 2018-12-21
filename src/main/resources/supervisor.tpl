# %s &#x000A;
# Supervisor 启动配置 &#x000A;
[program:%s] &#x000A;
directory = /opt/apps/%s &#x000A;

#包名可用__BIN__代替,默认配置文件包路径可用__CONFIG__代替，配置文件名称需要自己填写&#x000A;
# command = &#x000A;

autostart = true &#x000A;
startsecs = 5 &#x000A;
autorestart = true &#x000A;
startretries = 3 &#x000A;
user = root &#x000A;
redirect_stderr = true &#x000A;
stdout_logfile_maxbytes = 20MB &#x000A;
stdout_logfile_backups = 20 &#x000A;
environment=GO_ENV=%s,NODE_ENV=%s,JAVA_ENV=%s,ENV=%s &#x000A;
stdout_logfile = /opt/logs/%s/%s-log-stdout.log &#x000A;