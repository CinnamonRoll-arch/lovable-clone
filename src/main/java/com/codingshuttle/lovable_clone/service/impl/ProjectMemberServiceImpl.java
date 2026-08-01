package com.codingshuttle.lovable_clone.service.impl;

import com.codingshuttle.lovable_clone.dto.member.InviteMemberRequest;
import com.codingshuttle.lovable_clone.dto.member.MemberResponse;
import com.codingshuttle.lovable_clone.dto.member.UpdateMemberRoleRequest;
import com.codingshuttle.lovable_clone.entity.Project;
import com.codingshuttle.lovable_clone.entity.ProjectMember;
import com.codingshuttle.lovable_clone.entity.ProjectMemberId;
import com.codingshuttle.lovable_clone.entity.User;
import com.codingshuttle.lovable_clone.exception.ResourceNotFoundException;
import com.codingshuttle.lovable_clone.mapper.ProjectMemberMapper;
import com.codingshuttle.lovable_clone.repository.ProjectMemberRepository;
import com.codingshuttle.lovable_clone.repository.ProjectRepository;
import com.codingshuttle.lovable_clone.repository.UserRepository;
import com.codingshuttle.lovable_clone.security.JwtUtils;
import com.codingshuttle.lovable_clone.service.ProjectMemberService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.Instant;

import java.util.List;


@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional
public class ProjectMemberServiceImpl implements ProjectMemberService {

    ProjectMemberRepository projectMemberRepository;
    ProjectRepository projectRepository;
    ProjectMemberMapper projectMemberMapper;
    UserRepository userRepository;
    JwtUtils jwtUtils;

    @Override
    @PreAuthorize("@security.canManageMembers(#projectId)")
    public Void removeProjectMember(Long projectId, Long memberId) {
        Long userId = jwtUtils.getCurrentUserId();
        Project project = getAccessibleProjectById(userId, projectId);

        ProjectMemberId projectMemberId = new ProjectMemberId(memberId, projectId);
        if (!projectMemberRepository.existsById(projectMemberId)) {
            throw new RuntimeException("Project member with id " + projectMemberId + " doesn't exists");
        }
        projectMemberRepository.deleteById(projectMemberId);
        return null;
    }

    @Override
    @PreAuthorize("@security.canViewMembers(#projectId)")
    public List<MemberResponse> getProjectMembers(Long projectId) {
        Long userId = jwtUtils.getCurrentUserId();
        Project project = getAccessibleProjectById(projectId, userId);

        List<MemberResponse> memberResponseList =
                projectMemberRepository.findByIdProjectId(projectId)
                        .stream()
                        .map(projectMemberMapper::toMemberResponseFromProjectMember)
                        .toList();

        return memberResponseList;
    }

    @Override
    @PreAuthorize("@security.canManageMembers(#projectId)")
    public MemberResponse inviteMember(Long projectId, InviteMemberRequest request) {
        Long userId = jwtUtils.getCurrentUserId();
        Project project = getAccessibleProjectById(projectId, userId);

        User invitee = userRepository.findByUsername(request.username()).orElseThrow();

        if (invitee.getId().equals(userId)) {
           throw new RuntimeException("Not allowed to invite yourself");
        }

        ProjectMemberId projectMemberId = new ProjectMemberId(projectId, invitee.getId());

        if (projectMemberRepository.existsById(projectMemberId)) {
            throw new RuntimeException("Already enrolled in project");
        }

        ProjectMember projectMember = ProjectMember.builder()
                .id(projectMemberId)
                .project(project)
                .user(invitee)
                .role(request.role())
                .invitedAt(Instant.now())
                .build();

        projectMemberRepository.save(projectMember);
        return projectMemberMapper.toMemberResponseFromProjectMember(projectMember);
    }


    @Override
    @PreAuthorize("@security.canManageMembers(#projectId)")
    public MemberResponse updateMemberRole(Long projectId, Long memberId, UpdateMemberRoleRequest request) {
        Long userId = jwtUtils.getCurrentUserId();
        Project project = getAccessibleProjectById(projectId, userId);

        ProjectMemberId projectMemberId = new ProjectMemberId(memberId, projectId);
        ProjectMember projectMember = projectMemberRepository.findById(projectMemberId)
                .orElseThrow(() -> new RuntimeException("Project member not found"));

        projectMember.setRole(request.role());
        projectMemberRepository.save(projectMember);

        return projectMemberMapper.toMemberResponseFromProjectMember(projectMember);
    }

    // INTERNAL FUNCTIONS
    public Project getAccessibleProjectById(Long id, Long userId) {
        return projectRepository.findAccessibleProjectById(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", id.toString()));
    }
}
