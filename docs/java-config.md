# 配置文件


**配置文件内容：**

```yml
server:
  port: 8920
  maxSize: 20
  
spring:
  application:
    name: kiss-nest
  datasource:
    driver-class-name: com.mysql.jdbc.Driver
    username: root
    password: Helloshic7!
    url: jdbc:mysql://rm-j6ca5pt36q4h487712o.mysql.rds.aliyuncs.com/kiss-backhub?characterEncoding=utf-8&useSSL=false
  messages:
    basename: i18n/codes
    baseFolder: i18n
    use-code-as-default-message: true
    
eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
      
mybatis:
  config-location: classpath:mybatis-config.xml
  
logging:
  level:
    com.kiss.kissnest.mapper: debug
    
jenkins:
  url: http://jenkins.com
  buildUrl: http://jenkins.com/job/%s/build
  buildWithParameterUrl: http://build.kisscloud.io/job/%s/buildWithParameters
  generateTokenUrl: http://build.kisscloud.io/user/%s/descriptorByName/jenkins.security.ApiTokenProperty/generateNewToken
  buildLogUrl: http://build.kisscloud.io/job/%s/%s
  buildOutputPath: /logText/progressiveHtml
  crumbPath: /crumbIssuer/api/json
  queuePath: /api/json
  bin:
    ip: 192.168.0.192
    
code:
  bin:
    ip: 10.100.100.185

kiss:
  nest:
    clientId: e489c5a6da934fe2b441824809b50ae6
    clientSecret: b1c1f5f3d6cb43a2a0b599daaa99b558
    clientExpired: 1296000
    webHook:
      url: http://localhost:8920/kiss/nest/note

gitlab:
  server:
    url: http://git.kisscloud.io
    token:
      path: /oauth/token
    commitPath: /%s/commit/%s
    user: http://git.kisscloud.io/api/v4/users?username=%s
    branchPath: /%s/tree/%s

build:
  log:
    maxSize: 20

feign:
  httpclient:
    connection-timeout: 60000
ribbon:
  ConnectTimeout: 60000
  ReadTimeout: 60000

project:
  type: 0,1,2
  roles: 0,1,2,3

member:
  roles: 0,1,2,3,4

group:
  roles: 0,1,2,3

package:
  nginx:
    url: http://packages.kisscloud.io/files/
  config:
    url: git@git.kisscloud.io

saltstack:
  url: http://47.100.235.203:8000
  username: salt-api
  password: 12345678


```

