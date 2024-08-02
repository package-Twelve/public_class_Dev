package com.sparta.publicclassdev.global.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.publicclassdev.domain.chatrooms.dto.MessagesDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisSubscriber {
    
    private final ObjectMapper objectMapper;
    private final SimpMessageSendingOperations messagingTemplate;
    
    public void onMessage(String message) {
        try {
            MessagesDto messagesDto = objectMapper.readValue(message, MessagesDto.class);
            messagingTemplate.convertAndSend("/topic/chatrooms/${teamsId}" + messagesDto.getTeamsId(), messagesDto);
        } catch (Exception e) {
            log.error("Exception while processing redis message: {}", e.getMessage());
        }
    }
}
