package com.codingshuttle.lovable_clone.mapper;

import com.codingshuttle.lovable_clone.dto.project.FileNode;
import com.codingshuttle.lovable_clone.entity.ProjectFile;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProjectFileMapper {

List<FileNode> toFileNodeList(List<ProjectFile> projectFileList);
}
