# Nest 发布系统

Nest 是一个基于 KISS CLOUD 架构，整合了 Jenkins、Gitlab、Salt Stuck 的一体构建部署管理平台。

## 1. 安装 Docker

安装参考教程：[https://docs.docker.com/install/](https://docs.docker.com/install/)

## 2. 安装 Docker Compose

安装参考教程：[https://docs.docker.com/compose/](https://docs.docker.com/compose/)

## 3. 启动 LDAP

编辑 docker-compose.yml 配置文件修改默认的超级管理员密码。

```
$ git clone https://github.com/kisscloud/kiss-ldap-docker-compose-yml.git
$ cd kiss-ldap-docker-compose-yml
$ docker-compose up -d
```

## 4. 启动 Gitlab

```
$ git clone git@gitlab.com:kisscloud/kiss-gitlab-docker-compose-yml.git
$ cd kiss-gitlab-docker-compose-yml
$ docker-compose up -d
```

容器启动后，容器内的配置文件夹 `/etc/gitlab` 会映射到主机 `config` 文件夹内，如果使用 ssh clone 时通道不通，可能是配置文件权限导致，可尝试如下操作：

```
$ chmod 600 ssh_host_rsa_key ssh_host_ecdsa_key ssh_host_ed25519_key
```

配置 LDAP：

修改 `config/gitlab.rb` 配置文件，写入 LDAP 配置，使用 docker-compose 重启容器即可。

```yaml
gitlab_rails['ldap_enabled'] = true

gitlab_rails['ldap_servers'] = YAML.load <<-'EOS'
  main:
    label: 'LDAP'
    host: 'localhost'
    port: 389
    uid: 'uid'
    bind_dn: 'cn=admin,dc=kisscloud,dc=io'
    password: 'kisscloud'
    encryption: 'plain'
    base: 'o=accounts,dc=kisscloud,dc=io'
EOS
```

## 5. 启动 Jenkins

```
$ git clone git@gitlab.com:kisscloud/kiss-jenkins-docker-compose-yml.git
$ cd kiss-jenkins-docker-compose-yml
$ docker-compose up -d
```

容器启动后，通过图形化界面配置 LDAP 和编译依赖的插件。

### 5.1 生成容器 root 用户公钥

在宿主机上生成公钥，通过容器映射到容器中。同时需要将该公钥添加至 Gitlab 的 root 用户，用来在构建时拉取代码仓库。

**生成公钥：**
```
$ ssh-keygen
```

**编辑 docker-compose.yml:**

```yml
version: '2'
services:
  jenkins:
    image: "jenkins/jenkins:lts"
    container_name: "kiss-jenkins"
    user: "root"
    volumes:
      - "$PWD/data:/var/jenkins_home"
      # 映射公私钥
      - "/root/.ssh/id_rsa:/root/.ssh/id_rsa"       
      - "/root/.ssh/id_rsa.pub:/root/.ssh/id_rsa.pub"
    ports:
      - "8080:8080"
      - "50000:50000"
```

### 5.2 Jenkins 配置 LDAP

安装 LDAP 插件后，在 Jenkin 全局安全配置中配置：

![](img/jenkins-ldap.png)

### 5.3 Jenkins 配置 golang 环境

[https://plugins.jenkins.io/golang](https://plugins.jenkins.io/golang)

### 5.4 Jenkins 配置 node.js 环境
[https://plugins.jenkins.io/nodejs](https://plugins.jenkins.io/nodejs)

## 6. 安装 SaltStuck

### 6.1 主节点

**安装：**
```
$ yum install salt-master -y
$ yum install salt-api -y
```

**添加用户：**

```
$ useradd -M -s /sbin/nologin salt-api
$ passwd salt-api
```

**编辑 API 配置：**

```
$ mkdir /etc/salt/master.d
$ vim /etc/salt/master.d/api.conf
```

输入内容：

```
rest_cherrypy:
  port: 8671
  disable_ssl: true
```

**编辑授权配置：**

```
$ vim /etc/salt/master.d/eauth.conf
```

输入内容：

```
external_auth:
  pam:
    salt-api:
      - .*
      - '@wheel'
      - '@runner'
```

**启动：**

```
$ salt-master -d
$ salt-api -d
```

**测试:**

```
curl -k http://127.0.0.1:8000/login -H "Accept: application/x-yaml"  -d username='salt-api' -d password='12345678'  -d eauth='pam'
```


### 6.1 子节点


**安装：**

```
$ yum install salt-minion -y
```

**编辑配置文件：**

```
$ vim /etc/salt/minion
```

编辑选项：

```
# Set the location of the salt master server. If the master server cannot be
# resolved, then the minion will fail to start.
master: 192.168.0.58

# Explicitly declare the id for this minion to use, if left commented the id
# will be the hostname as returned by the python call: socket.getfqdn()
# Since salt uses detached ids it is possible to run multiple minions on the
# same machine but with different ids, this can be useful for salt compute
# clusters.
id: node-192-168-0-56
```


**启动：**

```
$ salt-minion -d
```

此时需要在 salt-master 机器接受节点接入请求 :

```
$ salt-key -L
$ salt-key -a node-192-168-0-56
```

## 7. 安装 rsync

在 jenkins 容器和存放部署包的机器上安装 rsync。

```
$ yum install rsync -y
```

**配置：**

首先需要将 jenkins 容器的公钥放到部署包机器上的 /root/.ssh/authorized_keys 文件中。然后：

```
$ vim /etc/rsync.conf
```

追加内容：

```
uid = root
gid = root
use chroot = no
max connections = 4
lock file=/var/run/rsyncd.lock
log file = /var/log/rsyncd.log 
exclude = lost+found/
transfer logging = yes
timeout = 900
ignore nonreadable = yes 
dont compress   = *.gz *.tgz *.zip *.z *.Z *.rpm *.deb *.bz2 
```

**启动：**

在两台机器上分别执行：

```
$ rsync --daemon
```

**测试：**

```
$ rsync -v ok.txt root@192.168.0.80:/root/
```

## 8 安装 Ansible

在 jenkins 容器里安装。


```
$ yum install ansible -y
```

> 注意配置映射到容器里的 ansible hosts 配置文件，写入部署包机器的 IP 地址。





