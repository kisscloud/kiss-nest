#!/bin/bash &#x000A;

name=%s &#x000A;
groupPath=%s &#x000A;
projectPath=%s &#x000A;
cloneUrl=%s &#x000A;

&#x000A;

rm -fr /opt/apps/$name &#x000A;
mkdir -p /opt/apps/$name &#x000A;
cd /opt/apps/$name &#x000A;
wget __TAR__PACKAGE__ &#x000A;
tar -xvf * &#x000A;

&#x000A;

# 克隆配置仓库 &#x000A;
if [ ! -d "/opt/configs/$groupPath/$projectPath" ];then &#x000A;
  cd /opt/configs/$groupPath&#x000A;
  git clone cloneUrl/$projectPath.git &#x000A;
else &#x000A;
  cd /opt/configs/$groupPath/$projectPath &#x000A;
  git pull &#x000A;
fi &#x000A;

&#x000A;

# 创建日志目录 &#x000A;
if [ ! -d "/opt/logs/$name" ];then &#x000A;
  mkdir -p /opt/logs/$name &#x000A;
fi &#x000A;