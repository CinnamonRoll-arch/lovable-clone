package com.codingshuttle.lovable_clone.service;

import com.codingshuttle.lovable_clone.dto.project.FileContentResponse;
import com.codingshuttle.lovable_clone.dto.project.FileNode;

import java.util.List;

public interface ProjectFileService {
    List<FileNode> getFileTree(Long projectId, Long userId);

    FileContentResponse getFile(Long projectId, String path, Long userId);

    void saveFile(Long projectId, String filePath, String fileContent);
}
