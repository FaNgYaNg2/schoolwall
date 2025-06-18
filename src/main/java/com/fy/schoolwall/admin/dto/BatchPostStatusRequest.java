package com.fy.schoolwall.admin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

@Data
public class BatchPostStatusRequest {

    @NotNull(message = "Post IDs cannot be null.")
    @NotEmpty(message = "Post IDs cannot be empty.")
    private List<Long> postIds;

    @NotBlank(message = "Status cannot be blank.")
    private String status;
}
