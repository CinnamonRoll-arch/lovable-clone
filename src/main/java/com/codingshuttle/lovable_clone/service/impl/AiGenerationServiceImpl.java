package com.codingshuttle.lovable_clone.service.impl;

import com.codingshuttle.lovable_clone.llm.SystemPrompt;
import com.codingshuttle.lovable_clone.security.JwtUtils;
import com.codingshuttle.lovable_clone.service.AiGenerationService;
import com.codingshuttle.lovable_clone.service.ProjectFileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class AiGenerationServiceImpl implements AiGenerationService {

    private final ChatClient chatClient;
    private final JwtUtils jwtUtils;
    private final ProjectFileService projectFileService;

    private static final Pattern FILE_TAG_PATTERN =
            Pattern.compile("<file path=\"([^\"]+)\">(.*?)</file>", Pattern.DOTALL);


    @Override
    @PreAuthorize("@security.canEditProject(#projectId)")
    public Flux<String> streamResponse(String userPrompt, Long projectId) {
        Long userId = jwtUtils.getCurrentUserId();
        createChatSessionIfNotExists(projectId,userId);

        Map<String, Object> advisorParams = Map.of(
                "userId", userId,
                "projectId", projectId
        );

        StringBuilder bufferedContent = new StringBuilder();

        return chatClient.prompt()
                .system(SystemPrompt.CODE_GENERATION_SYSTEM_PROMPT)
                .user(userPrompt)
                .advisors(
                        advisorSpec -> {
                            advisorSpec.params(advisorParams);
                        }
                )
                .stream()
                .chatResponse()
                .doOnNext(response -> {
                    String content = response.getResult().getOutput().getText();
                    bufferedContent.append(content);
                })
                .doOnComplete(() -> {
                    Schedulers.boundedElastic().schedule(
                            () -> {
                                parseAndSaveFiles(bufferedContent.toString(),projectId);
                            });
                })
                .doOnError(error -> log.error("Error in streaming for project ID: ",projectId))
                .map(response -> Objects.requireNonNull(response.getResult().getOutput().getText()));
    }

    private void parseAndSaveFiles(String fullResponse, Long projectId) {
        Matcher matcher = FILE_TAG_PATTERN.matcher(fullResponse);
        while (matcher.find()) {
            String filePath = matcher.group(1);
            String fileContent = matcher.group(2).trim();

            projectFileService.saveFile(projectId,filePath, fileContent);
        }
    }

    private void createChatSessionIfNotExists(Long projectId, Long userId) {
    }
}
