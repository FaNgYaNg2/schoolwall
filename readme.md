# 说明

## 完成情况说明

API 参考 API.yml 相关。
运行环境：JDK 21，SpringBoot 3.5.0，MySQL 8.4
使用了 Maven Wrapper，导入时应该会自动下载相关仓库。

## TODO List

### 1.密码传递

目前采用的是

```Scrpit
  security:
    user:
      name: admin
      password: "{noop}admin"
```

该行为可能带来问题，需要进行修改。这项修改需要前后端配合。

### 2.Emotion 模块

### 3.Community 模块

该模块目前缺乏实现。
