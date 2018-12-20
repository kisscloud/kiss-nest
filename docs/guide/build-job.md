# 添加构建任务

项目在构建之前需要添加构建任务，添加构建脚本。

## 1. 选择需要添加任务的项目

采用下拉选的方式选择项目，下拉选列表展示的是项目的名称。

## 2. 添加构建脚本

需要手动填写的构建脚本并不多，主要分为三个部分：

### 2.1 构建项目

这个部分主要是将不同的项目打成相应的包，比如jar包或者war包。比如java的项目打成jar包：
```
mvn install -Dmaven.test.skip=true
mvn package -Dmaven.test.skip=true
```
ps:构建的操作是在项目的主目录下执行的。

### 2.2 切换到包的目录下面

这个部分需要切换到上一步打好的包所在的目录。如果打好的包就在当前目录，可以忽略此部分。比如java的项目：
```
# java的项目构建后会在项目的根目录下生成一个target目录，生成的包也会在这个目录下
cd target/
```

### 2.3 修改生成的包的名称

将生成的包的名称修改为`__PACKAGE__`，比如java的项目：
```
mv kiss-nest-0.0.1-SNAPSHOT.jar __PACKAGE__
```
