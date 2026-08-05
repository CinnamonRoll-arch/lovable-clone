package com.codingshuttle.lovable_clone.service.impl;

import com.codingshuttle.lovable_clone.dto.project.FileContentResponse;
import com.codingshuttle.lovable_clone.dto.project.FileNode;
import com.codingshuttle.lovable_clone.entity.Project;
import com.codingshuttle.lovable_clone.entity.ProjectFile;
import com.codingshuttle.lovable_clone.exception.ResourceNotFoundException;
import com.codingshuttle.lovable_clone.mapper.ProjectFileMapper;
import com.codingshuttle.lovable_clone.repository.ProjectFileRepository;
import com.codingshuttle.lovable_clone.repository.ProjectRepository;
import com.codingshuttle.lovable_clone.service.ProjectFileService;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectFileServiceImpl implements ProjectFileService {

    private final ProjectRepository projectRepository;
    private final ProjectFileRepository projectFileRepository;
    private final ProjectFileMapper projectFileMapper;

    private final MinioClient minioClient;

    @Value("${minio.project-bucket}")
    private String projectBucket;

    @Override
    public FileContentResponse getFile(Long projectId, String path, Long userId) {

        return null;
    }

    @Override
    public void saveFile(Long projectId, String filePath, String fileContent) {
        log.info("Saving file: , {}",filePath);

        Project project= projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project",projectId.toString()));

        String cleanPath = filePath.startsWith("/") ? filePath.substring(1) : filePath;
        String objectKey = projectId + "/" + cleanPath;

        try {
            byte[] contentBytes = fileContent.getBytes(StandardCharsets.UTF_8);
            InputStream inputStream = new ByteArrayInputStream(contentBytes);
            // saving the file content
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(projectBucket)
                            .object(objectKey)
                            .stream(inputStream, (long) contentBytes.length, (long) -1)
                            .contentType(determineContentType(filePath))
                            .build());

            // Saving the metaData
            ProjectFile file = projectFileRepository.findByProjectIdAndPath(projectId, cleanPath)
                    .orElseGet(() -> ProjectFile.builder()
                            .project(project)
                            .path(cleanPath)
                            .minioObjectKey(objectKey) // Use the key we generated
                            .createdAt(Instant.now())
                            .build());

            file.setUpdatedAt(Instant.now());
            projectFileRepository.save(file);
            log.info("Saved file: {}", objectKey);
        } catch (Exception e) {
            log.error("Failed to save file {}/{}", projectId, cleanPath, e);
            throw new RuntimeException("File save failed", e);
        }

        // save the file metadata in postgres
        // save the file content in minio
    }

    @Override
    public List<FileNode> getFileTree(Long projectId, Long userId) {
        List<ProjectFile> projectFileList = projectFileRepository.findByProjectId(projectId);
        return projectFileMapper.toFileNodeList(projectFileList);

    }

    private String determineContentType(String path) {
        String type = URLConnection.guessContentTypeFromName(path);
        if (type != null) return type;
        if (path.endsWith(".jsx") || path.endsWith(".ts") || path.endsWith(".tsx")) return "text/javascript";
        if (path.endsWith(".json")) return "application/json";
        if (path.endsWith(".css")) return "text/css";

        return "text/plain";
    }
}
