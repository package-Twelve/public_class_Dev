package com.sparta.publicclassdev.global.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.publicclassdev.domain.chatrooms.dto.MessagesResponseDto;
import com.sparta.publicclassdev.global.exception.CustomException;
import com.sparta.publicclassdev.global.exception.ErrorCode;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisSubscriber implements MessageListener {
    
    private final ObjectMapper objectMapper;
    private final SimpMessagingTemplate messagingTemplate;
    private final ConcurrentMap<Long, MessagesResponseDto> messageCache = new ConcurrentHashMap<>();
    
    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            String messageBody = new String(message.getBody());
            MessagesResponseDto messagesResponseDto = objectMapper.readValue(messageBody, MessagesResponseDto.class);
            
            if (messagesResponseDto.getId() == null) {
                return;
            }
            if (messageCache.putIfAbsent(messagesResponseDto.getId(), messagesResponseDto) != null) {
                return;
            }
            
            messagingTemplate.convertAndSend("/topic/chatrooms/" + messagesResponseDto.getTeamsId(), messagesResponseDto);
        } catch (JsonProcessingException e) {
            throw new CustomException(ErrorCode.INVALID_REQUEST);
        }
    }
}
