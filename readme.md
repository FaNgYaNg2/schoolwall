# 说明

## 完成情况说明

情绪检测相关部分没有收到。所以 Post 的数据结构里也没有设计该部分。该部分我认为不是最重要的。
下述 API 中的核心部分（注册，登录，信息更改，密码更改，发布帖子等）已经过检测。大部分或许还需要测试。
运行环境：JDK 21，SpringBoot 3.5.0，MySQL 8.4
使用了 Maven Wrapper，导入时应该会自动下载相关仓库。

## 完整 API 接口测试指南

### 1. 认证相关接口 (Auth)

#### 1.1 用户注册

```bash
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "password123"
  }'
```

#### 1.2 用户登录

```bash
curl -X POST http://localhost:8080/login \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "username=testuser&password=password123" \
  -c cookies.txt
```

#### 1.3 用户注销

```bash
curl -X POST http://localhost:8080/logout \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -b cookies.txt
```

### 2. 用户相关接口 (Users)

#### 2.1 获取当前用户信息

```bash
curl -X GET http://localhost:8080/users/me \
  -H "Accept: application/json" \
  -b cookies.txt
```

#### 2.2 更新当前用户信息

```bash
curl -X PUT http://localhost:8080/users/me \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -b cookies.txt \
  -d '{
    "email": "newemail@example.com",
    "avatarUrl": "https://example.com/avatar.jpg",
    "bio": "This is my updated bio"
  }'
```

#### 2.3 修改当前用户密码

```bash
curl -X PUT http://localhost:8080/users/me/password \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -b cookies.txt \
  -d '{
    "currentPassword": "oldpassword123",
    "newPassword": "newpassword123",
    "confirmPassword": "newpassword123"
  }'
```

#### 2.4 删除当前用户账户

```bash
curl -X DELETE http://localhost:8080/users/me \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -b cookies.txt \
  -d '{
    "password": "password123"
  }'
```

### 3. 帖子相关接口 (Posts)

#### 3.1 创建帖子

```bash
curl -X POST http://localhost:8080/posts \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -b cookies.txt \
  -d '{
    "title": "我的第一篇帖子",
    "content": "这是帖子的详细内容，支持markdown格式",
    "category": "技术分享",
    "tags": "Java,Spring Boot,教程",
    "coverImage": "https://example.com/cover.jpg",
    "status": "PUBLISHED"
  }'
```

#### 3.2 更新帖子

```bash
curl -X PUT http://localhost:8080/posts/1 \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -b cookies.txt \
  -d '{
    "title": "更新后的帖子标题",
    "content": "更新后的帖子内容",
    "category": "技术分享",
    "tags": "Java,Spring Boot,更新",
    "status": "PUBLISHED"
  }'
```

#### 3.3 删除帖子

```bash
curl -X DELETE http://localhost:8080/posts/1 \
  -H "Accept: application/json" \
  -b cookies.txt
```

#### 3.4 根据 ID 获取帖子详情

```bash
curl -X GET http://localhost:8080/posts/1 \
  -H "Accept: application/json" \
  -b cookies.txt
```

#### 3.5 根据 slug 获取帖子详情

```bash
curl -X GET http://localhost:8080/posts/slug/my-first-post \
  -H "Accept: application/json"
```

#### 3.6 获取当前用户的帖子列表

```bash
curl -X GET http://localhost:8080/posts/me \
  -H "Accept: application/json" \
  -b cookies.txt
```

#### 3.7 获取帖子动态列表（分页）

```bash
curl -X GET "http://localhost:8080/posts/feed?page=0&size=10&sort=createdAt&direction=DESC" \
  -H "Accept: application/json"
```

#### 3.8 根据分类获取帖子

```bash
curl -X GET "http://localhost:8080/posts/category/技术分享?page=0&size=10" \
  -H "Accept: application/json"
```

#### 3.9 搜索帖子

```bash
curl -X GET "http://localhost:8080/posts/search?keyword=Java&page=0&size=10" \
  -H "Accept: application/json"
```

#### 3.10 获取置顶帖子

```bash
curl -X GET "http://localhost:8080/posts/top?limit=5" \
  -H "Accept: application/json"
```

#### 3.11 获取推荐帖子

```bash
curl -X GET "http://localhost:8080/posts/recommended?limit=5" \
  -H "Accept: application/json"
```

#### 3.12 发布帖子

```bash
curl -X PUT http://localhost:8080/posts/1/publish \
  -H "Accept: application/json" \
  -b cookies.txt
```

### 4. 评论相关接口 (Comments)

#### 4.1 创建评论

```bash
curl -X POST http://localhost:8080/comments \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -b cookies.txt \
  -d '{
    "postId": 1,
    "content": "这是一个很好的帖子，感谢分享！",
    "parentCommentId": null
  }'
```

#### 4.2 创建回复评论

```bash
curl -X POST http://localhost:8080/comments \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -b cookies.txt \
  -d '{
    "postId": 1,
    "content": "我同意你的观点！",
    "parentCommentId": 1
  }'
```

#### 4.3 更新评论

```bash
curl -X PUT http://localhost:8080/comments/1 \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -b cookies.txt \
  -d '{
    "content": "更新后的评论内容"
  }'
```

#### 4.4 删除评论

```bash
curl -X DELETE http://localhost:8080/comments/1 \
  -H "Accept: application/json" \
  -b cookies.txt
```

#### 4.5 获取评论详情

```bash
curl -X GET http://localhost:8080/comments/1 \
  -H "Accept: application/json"
```

#### 4.6 获取帖子的评论

```bash
curl -X GET "http://localhost:8080/comments/posts/1?page=0&size=10" \
  -H "Accept: application/json"
```

#### 4.7 获取评论的回复

```bash
curl -X GET "http://localhost:8080/comments/1/replies?page=0&size=10" \
  -H "Accept: application/json"
```

#### 4.8 获取我的评论历史

```bash
curl -X GET "http://localhost:8080/comments/me?page=0&size=10" \
  -H "Accept: application/json" \
  -b cookies.txt
```

#### 4.9 获取指定用户的评论

```bash
curl -X GET "http://localhost:8080/comments/users/1?page=0&size=10" \
  -H "Accept: application/json"
```

### 5. 管理员用户管理接口 (Admin Users)

#### 5.1 获取所有用户列表（分页）

```bash
curl -X GET "http://localhost:8080/admin/users?page=0&size=10&sort=createdAt&direction=DESC" \
  -H "Accept: application/json" \
  -b cookies.txt
```

#### 5.2 根据状态获取用户

```bash
curl -X GET "http://localhost:8080/admin/users?enabled=true&page=0&size=10" \
  -H "Accept: application/json" \
  -b cookies.txt
```

#### 5.3 获取用户详情

```bash
curl -X GET http://localhost:8080/admin/users/1 \
  -H "Accept: application/json" \
  -b cookies.txt
```

#### 5.4 删除用户账户

```bash
curl -X DELETE http://localhost:8080/admin/users/1 \
  -H "Accept: application/json" \
  -b cookies.txt
```

#### 5.5 切换用户状态

```bash
curl -X PUT http://localhost:8080/admin/users/1/status \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -b cookies.txt \
  -d '{
    "enabled": false
  }'
```

#### 5.6 批量切换用户状态

```bash
curl -X PUT http://localhost:8080/admin/users/batch/status \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -b cookies.txt \
  -d '{
    "userIds": [1, 2, 3],
    "enabled": false
  }'
```

### 6. 管理员帖子管理接口 (Admin Posts)

#### 6.1 获取所有帖子（分页）

```bash
curl -X GET "http://localhost:8080/admin/posts?page=0&size=10&sort=createdAt&direction=DESC" \
  -H "Accept: application/json" \
  -b cookies.txt
```

#### 6.2 根据状态获取帖子

```bash
curl -X GET "http://localhost:8080/admin/posts?status=PUBLISHED&page=0&size=10" \
  -H "Accept: application/json" \
  -b cookies.txt
```

#### 6.3 获取待审核帖子

```bash
curl -X GET "http://localhost:8080/admin/posts/review?page=0&size=10" \
  -H "Accept: application/json" \
  -b cookies.txt
```

#### 6.4 获取帖子详情

```bash
curl -X GET http://localhost:8080/admin/posts/1 \
  -H "Accept: application/json" \
  -b cookies.txt
```

#### 6.5 更新帖子状态

```bash
curl -X PUT http://localhost:8080/admin/posts/1/status \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -b cookies.txt \
  -d '{
    "status": "PUBLISHED"
  }'
```

#### 6.6 设置帖子置顶状态

```bash
curl -X PUT http://localhost:8080/admin/posts/1/top \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -b cookies.txt \
  -d '{
    "isTop": true
  }'
```

#### 6.7 设置帖子推荐状态

```bash
curl -X PUT http://localhost:8080/admin/posts/1/recommended \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -b cookies.txt \
  -d '{
    "isRecommended": true
  }'
```

#### 6.8 删除帖子

```bash
curl -X DELETE http://localhost:8080/admin/posts/1 \
  -H "Accept: application/json" \
  -b cookies.txt
```

#### 6.9 执行帖子操作

```bash
# 审核通过
curl -X POST http://localhost:8080/admin/posts/1/action \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -b cookies.txt \
  -d '{
    "action": "approve",
    "reason": "内容符合规范"
  }'

# 拒绝帖子
curl -X POST http://localhost:8080/admin/posts/1/action \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -b cookies.txt \
  -d '{
    "action": "reject",
    "reason": "内容不符合社区规范"
  }'

# 设置置顶
curl -X POST http://localhost:8080/admin/posts/1/action \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -b cookies.txt \
  -d '{
    "action": "set_top",
    "reason": "优质内容，置顶推荐"
  }'
```

#### 6.10 批量更新帖子状态

```bash
curl -X PUT http://localhost:8080/admin/posts/batch/status \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -b cookies.txt \
  -d '{
    "postIds": [1, 2, 3],
    "status": "PUBLISHED"
  }'
```

### 7. 管理员评论管理接口 (Admin Comments)

#### 7.1 获取所有评论（分页）

```bash
curl -X GET "http://localhost:8080/admin/comments?page=0&size=10" \
  -H "Accept: application/json" \
  -b cookies.txt
```

#### 7.2 获取用户的评论

```bash
curl -X GET "http://localhost:8080/admin/comments/users/1?page=0&size=10" \
  -H "Accept: application/json" \
  -b cookies.txt
```

#### 7.3 获取帖子的评论

```bash
curl -X GET "http://localhost:8080/admin/comments/posts/1?page=0&size=10" \
  -H "Accept: application/json" \
  -b cookies.txt
```

#### 7.4 软删除评论

```bash
curl -X PUT http://localhost:8080/admin/comments/1/soft-delete \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -b cookies.txt \
  -d '{
    "reason": "违反社区规范"
  }'
```

#### 7.5 物理删除评论

```bash
curl -X DELETE http://localhost:8080/admin/comments/1 \
  -H "Accept: application/json" \
  -b cookies.txt
```

#### 7.6 批量软删除评论

```bash
curl -X PUT http://localhost:8080/admin/comments/batch/soft-delete \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -b cookies.txt \
  -d '{
    "commentIds": [1, 2, 3],
    "reason": "批量处理违规评论"
  }'
```

#### 7.7 批量物理删除评论

```bash
curl -X DELETE http://localhost:8080/admin/comments/batch \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -b cookies.txt \
  -d '{
    "commentIds": [1, 2, 3]
  }'
```

#### 7.8 根据删除状态获取评论

```bash
curl -X GET "http://localhost:8080/admin/comments/deleted/true?page=0&size=10" \
  -H "Accept: application/json" \
  -b cookies.txt
```
