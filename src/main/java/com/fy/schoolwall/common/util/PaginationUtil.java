package com.fy.schoolwall.common.util;

import java.util.List;

/**
 * 分页工具类
 * 
 * 意义：
 * 1. 提供统一的分页数据结构，确保API响应格式一致
 * 2. 封装分页计算逻辑，避免在业务代码中重复计算
 * 3. 提供便捷的分页参数验证和处理方法
 * 4. 支持前端分页组件的标准化对接
 */
public class PaginationUtil {

    /**
     * 默认页面大小
     */
    public static final int DEFAULT_PAGE_SIZE = 10;

    /**
     * 最大页面大小（防止一次查询过多数据）
     */
    public static final int MAX_PAGE_SIZE = 100;

    /**
     * 分页响应数据结构
     */
    public static class PageResponse<T> {
        private List<T> content; // 当前页数据
        private int page; // 当前页码（从0开始）
        private int size; // 页面大小
        private long totalElements; // 总记录数
        private int totalPages; // 总页数
        private boolean first; // 是否为第一页
        private boolean last; // 是否为最后一页
        private boolean hasNext; // 是否有下一页
        private boolean hasPrevious; // 是否有上一页

        public PageResponse() {
        }

        public PageResponse(List<T> content, int page, int size, long totalElements) {
            this.content = content;
            this.page = page;
            this.size = size;
            this.totalElements = totalElements;
            this.totalPages = (int) Math.ceil((double) totalElements / size);
            this.first = page == 0;
            this.last = page >= totalPages - 1;
            this.hasNext = page < totalPages - 1;
            this.hasPrevious = page > 0;
        }

        // Getters and Setters
        public List<T> getContent() {
            return content;
        }

        public void setContent(List<T> content) {
            this.content = content;
        }

        public int getPage() {
            return page;
        }

        public void setPage(int page) {
            this.page = page;
        }

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }

        public long getTotalElements() {
            return totalElements;
        }

        public void setTotalElements(long totalElements) {
            this.totalElements = totalElements;
        }

        public int getTotalPages() {
            return totalPages;
        }

        public void setTotalPages(int totalPages) {
            this.totalPages = totalPages;
        }

        public boolean isFirst() {
            return first;
        }

        public void setFirst(boolean first) {
            this.first = first;
        }

        public boolean isLast() {
            return last;
        }

        public void setLast(boolean last) {
            this.last = last;
        }

        public boolean isHasNext() {
            return hasNext;
        }

        public void setHasNext(boolean hasNext) {
            this.hasNext = hasNext;
        }

        public boolean isHasPrevious() {
            return hasPrevious;
        }

        public void setHasPrevious(boolean hasPrevious) {
            this.hasPrevious = hasPrevious;
        }
    }

    /**
     * 分页请求参数
     */
    public static class PageRequest {
        private int page = 0; // 页码（从0开始）
        private int size = DEFAULT_PAGE_SIZE; // 页面大小
        private String sort; // 排序字段
        private String direction = "ASC"; // 排序方向

        public PageRequest() {
        }

        public PageRequest(int page, int size) {
            this.page = Math.max(0, page);
            this.size = Math.min(Math.max(1, size), MAX_PAGE_SIZE);
        }

        public PageRequest(int page, int size, String sort, String direction) {
            this(page, size);
            this.sort = sort;
            this.direction = direction != null ? direction.toUpperCase() : "ASC";
        }

        // Getters and Setters
        public int getPage() {
            return page;
        }

        public void setPage(int page) {
            this.page = Math.max(0, page);
        }

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = Math.min(Math.max(1, size), MAX_PAGE_SIZE);
        }

        public String getSort() {
            return sort;
        }

        public void setSort(String sort) {
            this.sort = sort;
        }

        public String getDirection() {
            return direction;
        }

        public void setDirection(String direction) {
            this.direction = direction != null ? direction.toUpperCase() : "ASC";
        }

        /**
         * 计算数据库查询的偏移量
         */
        public int getOffset() {
            return page * size;
        }

        /**
         * 获取数据库查询的限制数量
         */
        public int getLimit() {
            return size;
        }
    }

    /**
     * 创建分页响应
     */
    public static <T> PageResponse<T> createPageResponse(List<T> content, PageRequest pageRequest, long totalElements) {
        return new PageResponse<>(content, pageRequest.getPage(), pageRequest.getSize(), totalElements);
    }

    /**
     * 验证并标准化分页参数
     */
    public static PageRequest validatePageRequest(Integer page, Integer size, String sort, String direction) {
        int validPage = page != null ? Math.max(0, page) : 0;
        int validSize = size != null ? Math.min(Math.max(1, size), MAX_PAGE_SIZE) : DEFAULT_PAGE_SIZE;
        String validDirection = direction != null && "DESC".equalsIgnoreCase(direction) ? "DESC" : "ASC";

        return new PageRequest(validPage, validSize, sort, validDirection);
    }
}