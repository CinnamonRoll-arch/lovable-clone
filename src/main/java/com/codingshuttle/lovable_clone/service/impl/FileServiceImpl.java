package com.codingshuttle.lovable_clone.service.impl;

import com.codingshuttle.lovable_clone.dto.project.FileContentResponse;
import com.codingshuttle.lovable_clone.dto.project.FileNode;
import com.codingshuttle.lovable_clone.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {
    @Override
    public FileContentResponse getFile(Long projectId, String path, Long userId) {
        return null;
    }

    @Override
    public List<FileNode> getFileTree(Long projectId, Long userId) {
        return List.of();
    }
}
