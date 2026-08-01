package com.codingshuttle.lovable_clone.service;

import com.codingshuttle.lovable_clone.dto.project.ProjectRequest;
import com.codingshuttle.lovable_clone.dto.project.ProjectResponse;
import com.codingshuttle.lovable_clone.dto.project.ProjectSummaryResponse;

import java.util.List;

public interface ProjectService {
    List<ProjectSummaryResponse> getUserProjects ();

    ProjectResponse getUserProjectById(Long id);

    ProjectResponse createProject(ProjectRequest request);

    ProjectResponse updateProject(Long id, ProjectRequest request);

    void softDelete(Long id);
}
