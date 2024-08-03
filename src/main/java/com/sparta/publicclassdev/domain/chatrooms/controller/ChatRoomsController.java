package com.sparta.publicclassdev.domain.chatrooms.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sparta.publicclassdev.domain.chatrooms.dto.ChatRoomsDto;
import com.sparta.publicclassdev.domain.chatrooms.dto.MessagesDto;
import com.sparta.publicclassdev.domain.chatrooms.service.ChatRoomsService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
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

    @MessageMapping("/chat.sentMessage")
    public void sendMessage(@Payload MessagesDto messagesDto) throws JsonProcessingException {
        log.info("Receive message : {}", messagesDto);
        chatRoomsService.sendMessage(messagesDto);
    }

    @MessageMapping("/chat.addUser")
    public void addUser(@Payload ChatRoomsDto chatroomsDto, SimpMessageHeaderAccessor headerAccessor)
        throws JsonProcessingException {
        log.info("User join : {}", chatroomsDto);
        chatRoomsService.addUser(chatroomsDto, headerAccessor);
    }
    
    @GetMapping("/{teamsId}/messages")
    public List<MessagesDto> getMessages(@PathVariable Long teamsId) {
        return chatRoomsService.getChatMessages(teamsId);
    }
}
