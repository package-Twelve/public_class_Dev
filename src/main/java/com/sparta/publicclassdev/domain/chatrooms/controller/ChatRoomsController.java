package com.sparta.publicclassdev.domain.chatrooms.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sparta.publicclassdev.domain.chatrooms.dto.ChatRoomsRequestDto;
import com.sparta.publicclassdev.domain.chatrooms.dto.ChatRoomsResponseDto;
import com.sparta.publicclassdev.domain.chatrooms.dto.MessagesRequestDto;
import com.sparta.publicclassdev.domain.chatrooms.dto.MessagesResponseDto;
import com.sparta.publicclassdev.domain.chatrooms.service.ChatRoomsService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/chatrooms")
@RequiredArgsConstructor
public class ChatRoomsController {
    
    private final ChatRoomsService chatRoomsService;
    
    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload MessagesRequestDto messagesRequestDto) throws JsonProcessingException {
        log.info("Receive message : {}", messagesRequestDto);
        chatRoomsService.sendMessage(messagesRequestDto);
    }
    
    @MessageMapping("/chat.addUser")
    public void addUser(@Payload ChatRoomsRequestDto chatRoomsRequestDto, SimpMessageHeaderAccessor headerAccessor)
        throws JsonProcessingException {
        log.info("User join : {}", chatRoomsRequestDto);
        chatRoomsService.addUser(chatRoomsRequestDto, headerAccessor);
    }
    
    @GetMapping("/{teamsId}/messages")
    public List<MessagesResponseDto> getMessages(@PathVariable Long teamsId) {
        return chatRoomsService.getChatMessages(teamsId);
    }
}
