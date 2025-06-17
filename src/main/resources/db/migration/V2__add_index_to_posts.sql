-- V2__add_index_to_posts.sql
-- 为 posts 表添加全文索引，以支持内容搜索功能

-- 为帖子的标题 (title) 和内容 (content) 字段创建全文索引。
-- 这个索引将极大地加速基于这些字段的全文搜索查询（例如使用 MATCH AGAINST）。
-- 注意：FULLTEXT 索引在 MySQL 的 InnoDB 存储引擎中可用（MySQL 5.6+）。

-- 全文索引
ALTER TABLE posts
ADD FULLTEXT INDEX idx_posts_fulltext (title, content);

-- 为经常需要按照社区和创建时间来获取帖子的查询添加复合索引
-- 这通常对瀑布流页面的特定过滤和排序场景有优化作用
ALTER TABLE posts
ADD INDEX idx_posts_community_created_at (community_id, created_at DESC);

-- 示例：为评论的 post_id 和 created_at 添加复合索引，优化按帖子获取评论并按时间排序的查询。
ALTER TABLE comments
ADD INDEX idx_comments_post_created_at (post_id, created_at DESC);