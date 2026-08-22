package com.cms.models.dtos.responses;

import com.cms.entity.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Integer userId;
    private String username;
    private String email;
    private String fullName;
    private Role role;
    private Boolean isActive;
}