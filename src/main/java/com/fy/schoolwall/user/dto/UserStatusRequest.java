package com.fy.schoolwall.user.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserStatusRequest {

    @NotNull(message = "Enabled status is required")
    private Boolean enabled;
}