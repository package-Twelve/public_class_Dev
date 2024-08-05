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
            log.info("Received message from Redis: {}", message);
            MessagesDto messagesDto = objectMapper.readValue(message, MessagesDto.class);
            String destination = String.format("/topic/chatrooms/%s", messagesDto.getTeamsId());
            log.info("Sending WebSocket message to destination: {}, message: {}", destination, messagesDto);
            messagingTemplate.convertAndSend(destination, messagesDto);
        } catch (Exception e) {
            log.error("Exception while processing redis message: {}", e.getMessage());
        }
    }
}
