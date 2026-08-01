package com.codingshuttle.lovable_clone.service.impl;

import com.codingshuttle.lovable_clone.dto.project.ProjectRequest;
import com.codingshuttle.lovable_clone.dto.project.ProjectResponse;
import com.codingshuttle.lovable_clone.dto.project.ProjectSummaryResponse;
import com.codingshuttle.lovable_clone.entity.Project;
import com.codingshuttle.lovable_clone.entity.ProjectMember;
import com.codingshuttle.lovable_clone.entity.ProjectMemberId;
import com.codingshuttle.lovable_clone.entity.User;
import com.codingshuttle.lovable_clone.enums.ProjectRole;
import com.codingshuttle.lovable_clone.exception.ResourceNotFoundException;
import com.codingshuttle.lovable_clone.mapper.ProjectMapper;
import com.codingshuttle.lovable_clone.repository.ProjectMemberRepository;
import com.codingshuttle.lovable_clone.repository.ProjectRepository;
import com.codingshuttle.lovable_clone.repository.UserRepository;
import com.codingshuttle.lovable_clone.security.JwtUtils;
import com.codingshuttle.lovable_clone.service.ProjectService;
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
@FieldDefaults(makeFinal = true,level = AccessLevel.PRIVATE)
@Transactional
public class ProjectServiceImpl implements ProjectService {

     ProjectRepository projectRepository;
     UserRepository userRepository;
     ProjectMapper projectMapper;
     ProjectMemberRepository projectMemberRepository;
     JwtUtils jwtUtils;

    @Override
    public List<ProjectSummaryResponse> getUserProjects() {
        // return projectRepository.findAllAccessibleByUser(userId)
        //  .stream()
        //  .map(project -> projectMapper.toProjectSummaryResponse(project))
        //  .collect(Collectors.toList());
        Long userId = jwtUtils.getCurrentUserId();
        var project = projectRepository.findAllAccessibleByUser(userId);
        return projectMapper.toProjectSummaryResponse(project);
    }

    @Override
    @PreAuthorize("@security.canViewProject(#projectId)")
    public ProjectResponse getUserProjectById(Long projectId) {
        Long userId = jwtUtils.getCurrentUserId();
        Project project = getAccessibleProjectById(projectId, userId);

        return projectMapper.toProjectResponse(project);
    }

    @Override
    public ProjectResponse createProject(ProjectRequest request) {
        Long userId = jwtUtils.getCurrentUserId();
      //  User owner = userRepository.findById(userId)
      //          .orElseThrow(
       //                 () -> new ResourceNotFoundException("User",userId.toString())
     //
        //        );

        // THIS WILL NOT MAKE A B CALL AND JUST GIVE A REFERENCE
        // OF THE OBJECT INSTEAD OF GIVING THE ENTIRE OBJECT

        User owner = userRepository.getReferenceById(userId);

        Project project = Project.builder()
                .name(request.name())
                .isPublic(false)
                .build();
        project = projectRepository.save(project);

        ProjectMemberId projectMemberId = new ProjectMemberId(owner.getId(), project.getId());
        ProjectMember projectMember = ProjectMember.builder()
                .id(projectMemberId)
                .role(ProjectRole.OWNER)
                .project(project)
                .user(owner)
                .acceptedAt(Instant.now())
                .invitedAt(Instant.now())
                .build();
        projectMemberRepository.save(projectMember);

        return projectMapper.toProjectResponse(project);
    }

    @Override
    @PreAuthorize("@security.canEditProject(#projectId)")
    public ProjectResponse updateProject(Long projectId,ProjectRequest request) {
        Long userId = jwtUtils.getCurrentUserId();
        Project project = getAccessibleProjectById(projectId,userId);

        project.setName(request.name());
    //    projectRepository.save(project); DIRTY CHECKING

        return projectMapper.toProjectResponse(project);
    }

    @Override
    @PreAuthorize("@security.canDeleteProject(#projectId)")
    public void softDelete(Long projectId) {
        Long userId = jwtUtils.getCurrentUserId();
        Project project = getAccessibleProjectById(projectId,userId);

        project.setDeletedAt(Instant.now());
        projectRepository.save(project);
    }

    // INTERNAL FUNCTIONS
    public Project getAccessibleProjectById(Long id,Long userId) {
       return projectRepository.findAccessibleProjectById(id,userId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", id.toString()));
    }
}
