-- V1__initial_schema.sql
-- 确保在正确的数据库中创建表
-- 如果你的数据库连接字符串已经指定了数据库，可以省略 USE 语句。
-- 例如：jdbc:mysql://localhost:3306/schoolwall?useSSL=false&serverTimezone=UTC
-- 如果不确定，可以保留以防万一。

-- 用户表
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    email VARCHAR(100) NOT NULL UNIQUE COMMENT '邮箱',
    password_hash VARCHAR(255) NOT NULL COMMENT '密码哈希值',
    role VARCHAR(20) DEFAULT 'USER' NOT NULL COMMENT '用户角色 (USER, ADMIN)',
    avatar_url VARCHAR(255) NULL COMMENT '用户头像URL',
    bio VARCHAR(500) NULL COMMENT '个人简介',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_enabled BOOLEAN DEFAULT TRUE COMMENT '账号是否启用',
    is_locked BOOLEAN DEFAULT FALSE COMMENT '账号是否锁定'
);

CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);

-- 社区表
CREATE TABLE communities (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '社区ID',
    name VARCHAR(100) NOT NULL UNIQUE COMMENT '社区名称',
    description TEXT NULL COMMENT '社区描述',
    creator_id BIGINT NOT NULL COMMENT '创建者用户ID',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (creator_id) REFERENCES users(id)
);

CREATE INDEX idx_communities_name ON communities(name);

-- 用户-社区成员关系表
CREATE TABLE user_community_membership (
    user_id BIGINT NOT NULL COMMENT '用户ID',
    community_id BIGINT NOT NULL COMMENT '社区ID',
    joined_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '加入时间',
    PRIMARY KEY (user_id, community_id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (community_id) REFERENCES communities(id)
);

-- 帖子表
CREATE TABLE posts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '帖子ID',
    title VARCHAR(200) NOT NULL COMMENT '帖子标题',
    content TEXT NOT NULL COMMENT '帖子内容',
    slug VARCHAR(255) UNIQUE NOT NULL COMMENT 'URL友好的标识符',
    author_id BIGINT NOT NULL COMMENT '作者ID',
    author_username VARCHAR(50) NOT NULL COMMENT '作者用户名（冗余字段）',
    community_id BIGINT NULL COMMENT '所属社区ID',
    status ENUM('DRAFT', 'PUBLISHED', 'HIDDEN', 'DELETED') DEFAULT 'DRAFT' COMMENT '帖子状态',
    category VARCHAR(50) NULL COMMENT '分类',
    tags VARCHAR(200) NULL COMMENT '标签，用逗号分隔',
    cover_image VARCHAR(255) NULL COMMENT '封面图片URL',
    view_count INT DEFAULT 0 COMMENT '浏览次数',
    comment_count INT DEFAULT 0 COMMENT '评论数',
    is_top BOOLEAN DEFAULT FALSE COMMENT '是否置顶',
    is_recommended BOOLEAN DEFAULT FALSE COMMENT '是否推荐',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    published_at TIMESTAMP NULL COMMENT '发布时间',
    
    INDEX idx_author_id (author_id),
    INDEX idx_community_id (community_id),
    INDEX idx_status (status),
    INDEX idx_category (category),
    INDEX idx_slug (slug),
    INDEX idx_published_at (published_at),
    INDEX idx_is_top (is_top),
    INDEX idx_is_recommended (is_recommended),
    INDEX idx_created_at (created_at DESC),
    
    FOREIGN KEY (author_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (community_id) REFERENCES communities(id) ON DELETE SET NULL
);

-- 评论表
CREATE TABLE comments (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '评论ID',
    content TEXT NOT NULL COMMENT '评论内容',
    user_id BIGINT NOT NULL COMMENT '评论者用户ID',
    post_id BIGINT NOT NULL COMMENT '所属帖子ID',
    parent_comment_id BIGINT NULL COMMENT '父评论ID (用于多级评论)',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted BOOLEAN DEFAULT FALSE COMMENT '是否已删除 (软删除)',
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (post_id) REFERENCES posts(id),
    FOREIGN KEY (parent_comment_id) REFERENCES comments(id)
);

CREATE INDEX idx_comments_post_id ON comments(post_id);
CREATE INDEX idx_comments_parent_comment_id ON comments(parent_comment_id);
CREATE INDEX idx_comments_created_at ON comments(created_at DESC);