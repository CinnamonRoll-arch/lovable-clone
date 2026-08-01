package com.codingshuttle.lovable_clone.security;

import com.codingshuttle.lovable_clone.enums.ProjectPermission;
import com.codingshuttle.lovable_clone.enums.ProjectRole;
import com.codingshuttle.lovable_clone.repository.ProjectMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

@Configuration("security")
@RequiredArgsConstructor
public class SecurityExpressions {

    private final ProjectMemberRepository projectMemberRepository;
    private final JwtUtils jwtUtils;

    public boolean canViewProject(Long projectId) {
       Long userId = jwtUtils.getCurrentUserId();
      return projectMemberRepository.findRoleByProjectIdAndUserId(projectId, userId)
              .map(role -> role.getPermissions().contains(ProjectPermission.VIEW))
              .orElse(false);
    }

    public boolean canEditProject(Long projectId) {
        Long userId = jwtUtils.getCurrentUserId();
        return projectMemberRepository.findRoleByProjectIdAndUserId(projectId, userId)
                .map(role -> role.getPermissions().contains(ProjectPermission.EDIT))
                .orElse(false);
    }

    public boolean canDeleteProject(Long projectId) {
        Long userId = jwtUtils.getCurrentUserId();
        return projectMemberRepository.findRoleByProjectIdAndUserId(projectId, userId)
                .map(role -> role.getPermissions().contains(ProjectPermission.DELETE))
                .orElse(false);
    }

    public boolean canViewMembers(Long projectId) {
        Long userId = jwtUtils.getCurrentUserId();
        return projectMemberRepository.findRoleByProjectIdAndUserId(projectId, userId)
                .map(role -> role.getPermissions().contains(ProjectPermission.VIEW_MEMBERS))
                .orElse(false);
    }

    public boolean canManageMembers(Long projectId) {
        Long userId = jwtUtils.getCurrentUserId();
        return projectMemberRepository.findRoleByProjectIdAndUserId(projectId, userId)
                .map(role -> role.getPermissions().contains(ProjectPermission.MANAGE_MEMBERS))
                .orElse(false);
    }
}
