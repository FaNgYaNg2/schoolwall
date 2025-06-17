package com.fy.schoolwall.admin.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AdminPostActionRequest {

    @NotBlank(message = "Action is required")
    private String action; // approve, reject, set_top, remove_top, set_recommended, remove_recommended,
                           // delete

    private String reason; // 可选的操作理由
}