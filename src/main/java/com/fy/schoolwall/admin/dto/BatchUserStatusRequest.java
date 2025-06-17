package com.fy.schoolwall.admin.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class BatchUserStatusRequest {

    @NotEmpty(message = "User IDs are required")
    private List<Long> userIds;

    @NotNull(message = "Enabled status is required")
    private Boolean enabled;
}