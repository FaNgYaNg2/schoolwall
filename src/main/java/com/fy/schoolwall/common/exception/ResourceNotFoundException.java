package com.fy.schoolwall.common.exception;

/**
 * 资源未找到异常
 * 
 * 意义：
 * 1. 提供语义化的异常类型，明确表示资源不存在的情况
 * 2. 便于全局异常处理器识别并返回适当的HTTP状态码（404）
 * 3. 提高代码可读性，使异常处理更加清晰
 * 4. 支持自定义错误消息，提供更详细的错误信息
 */
public class ResourceNotFoundException extends RuntimeException {

    /**
     * 默认构造函数
     */
    public ResourceNotFoundException() {
        super("请求的资源未找到");
    }

    /**
     * 带消息的构造函数
     * 
     * @param message 错误消息
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }

    /**
     * 带消息和原因的构造函数
     * 
     * @param message 错误消息
     * @param cause   异常原因
     */
    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * 便捷方法：根据资源类型和ID创建异常
     * 
     * @param resourceType 资源类型（如 "User", "Post"）
     * @param id           资源ID
     * @return ResourceNotFoundException实例
     */
    public static ResourceNotFoundException of(String resourceType, Object id) {
        return new ResourceNotFoundException(
                String.format("%s with id '%s' not found", resourceType, id));
    }

    /**
     * 便捷方法：根据资源类型和字段创建异常
     * 
     * @param resourceType 资源类型
     * @param field        字段名
     * @param value        字段值
     * @return ResourceNotFoundException实例
     */
    public static ResourceNotFoundException of(String resourceType, String field, Object value) {
        return new ResourceNotFoundException(
                String.format("%s with %s '%s' not found", resourceType, field, value));
    }
}