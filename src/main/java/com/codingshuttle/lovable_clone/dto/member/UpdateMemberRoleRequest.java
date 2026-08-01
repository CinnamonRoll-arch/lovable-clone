package com.codingshuttle.lovable_clone.dto.member;

import com.codingshuttle.lovable_clone.enums.ProjectRole;
import jakarta.validation.constraints.NotNull;

public record UpdateMemberRoleRequest(
        @NotNull ProjectRole role) {
}
