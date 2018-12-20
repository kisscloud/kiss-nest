# 配置前端环境变量

需要编辑配置文件，配置 ClientId, 统一账户中心的访问地址等，授权 Nest 用户进行访问。


**编辑配置文件：**

```
$ vim site/static/env.js
```

**配置文件示例：**

```js
window.config = {
  API_HOST: "http://47.100.235.203:8100",
  LOGIN_URL: "http://localhost:1994/#/auth/login",
  CLIENT_ID: "5827dbd042004782b3e431b198c29268",
  ACCOUNT_CENTER_HOST: "http://localhost:1993/#/auth/login?client_id=5827dbd042004782b3e431b198c29268"
}
```