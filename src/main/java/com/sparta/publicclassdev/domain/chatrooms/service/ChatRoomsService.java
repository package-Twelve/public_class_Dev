package com.sparta.publicclassdev.domain.chatrooms.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.publicclassdev.domain.chatrooms.dto.ChatRoomsDto;
import com.sparta.publicclassdev.domain.chatrooms.dto.ChatRoomsDto.MessageType;
import com.sparta.publicclassdev.domain.chatrooms.dto.MessagesDto;
import com.sparta.publicclassdev.domain.chatrooms.entity.ChatRooms;
import com.sparta.publicclassdev.domain.chatrooms.entity.Messages;
import com.sparta.publicclassdev.domain.chatrooms.repository.ChatRoomsRepository;
import com.sparta.publicclassdev.domain.chatrooms.repository.MessagesRepository;
import com.sparta.publicclassdev.domain.users.entity.Users;
import com.sparta.publicclassdev.domain.users.repository.UsersRepository;
import com.sparta.publicclassdev.global.exception.CustomException;
import com.sparta.publicclassdev.global.exception.ErrorCode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatRoomsService {
    
    private final UsersRepository usersRepository;
    private final ChatRoomsRepository chatRoomsRepository;
    private final MessagesRepository messagesRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    private final ChannelTopic channelTopic;
    
    @Transactional
    public void sendMessage(MessagesDto messagesDto) throws JsonProcessingException {
        Users users = usersRepository.findByName(messagesDto.getSender())
            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        ChatRooms chatRooms = chatRoomsRepository.findById(messagesDto.getTeamsId())
            .orElseThrow(() -> new CustomException(ErrorCode.TEAM_NOT_FOUND));
        
        Messages messages = Messages.builder()
            .contents(messagesDto.getContent())
            .users(users)
            .chatRooms(chatRooms)
            .build();
        messagesRepository.save(messages);
        
        MessagesDto savedMessagesDto = MessagesDto.builder()
            .id(messages.getId())
            .content(messages.getContents())
            .sender(users.getName())
            .teamsId(messages.getId())
            .timestamp(messages.getCreatedAt())
            .build();
        
        redisTemplate.convertAndSend(channelTopic.getTopic(), objectMapper.writeValueAsString(savedMessagesDto));
    }
    
    
    @Transactional
    public void addUser(ChatRoomsDto chatRoomsDto, SimpMessageHeaderAccessor headerAccessor)
        throws JsonProcessingException {
        ChatRoomsDto joinMessage = ChatRoomsDto.builder()
            .type(MessageType.JOIN)
            .sender(chatRoomsDto.getSender())
            .teamsId(chatRoomsDto.getTeamsId())
            .content(chatRoomsDto.getSender() + "님이 입장하셨습니다.")
            .timestamp(LocalDateTime.now())
            .build();
        redisTemplate.convertAndSend(channelTopic.getTopic(), objectMapper.writeValueAsString(joinMessage));
    }
    
    @Transactional(readOnly = true)
    public List<MessagesDto> getChatMessages(Long teamsId) {
        List<Messages> messages = messagesRepository.findByChatRooms_Id(teamsId);
        return messages.stream().map(message -> MessagesDto.builder()
            .id(message.getId())
            .sender(message.getUsers().getName())
            .content(message.getContents())
            .teamsId(message.getChatRooms().getId())
            .timestamp(message.getCreatedAt())
            .build()).collect(Collectors.toList());
    }
}