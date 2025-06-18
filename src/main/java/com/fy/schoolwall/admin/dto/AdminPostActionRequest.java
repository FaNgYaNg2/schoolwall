package com.fy.schoolwall.admin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class AdminPostActionRequest {

    @NotBlank(message = "Action is required")
    @Pattern(regexp = "approve|reject|set_top|remove_top|set_recommended|remove_recommended|delete", 
             message = "Invalid action. Must be one of: approve, reject, set_top, remove_top, set_recommended, remove_recommended, delete")
    private String action;

    private String reason; // 可选的操作理由

    /**
     * 检查是否为需要理由的操作
     */
    public boolean requiresReason() {
        return "reject".equals(action) || "delete".equals(action);
    }

    /**
     * 验证操作是否有效
     */
    public boolean isValidAction() {
        return action != null && 
               (action.equals("approve") || action.equals("reject") || 
                action.equals("set_top") || action.equals("remove_top") ||
                action.equals("set_recommended") || action.equals("remove_recommended") ||
                action.equals("delete"));
    }
}