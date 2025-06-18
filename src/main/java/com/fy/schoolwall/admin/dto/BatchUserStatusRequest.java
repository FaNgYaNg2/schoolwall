package com.fy.schoolwall.admin.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

@Data
public class BatchUserStatusRequest {

    @NotNull(message = "User IDs cannot be null.")
    @NotEmpty(message = "User IDs cannot be empty.")
    private List<Long> userIds;

    @NotNull(message = "Enabled status cannot be null.")
    private Boolean enabled;
}