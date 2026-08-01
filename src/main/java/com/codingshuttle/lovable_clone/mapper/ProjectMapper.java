package com.codingshuttle.lovable_clone.mapper;

import com.codingshuttle.lovable_clone.dto.project.ProjectResponse;
import com.codingshuttle.lovable_clone.dto.project.ProjectSummaryResponse;
import com.codingshuttle.lovable_clone.entity.Project;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProjectMapper {

    ProjectResponse toProjectResponse(Project project);

    List<ProjectSummaryResponse> toProjectSummaryResponse(List<Project> projects);

    ProjectSummaryResponse toProjectSummaryResponse(Project projects);
}
