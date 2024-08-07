package com.sparta.publicclassdev.domain.chatrooms.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.publicclassdev.domain.chatrooms.dto.ChatRoomsRequestDto;
import com.sparta.publicclassdev.domain.chatrooms.dto.ChatRoomsResponseDto;
import com.sparta.publicclassdev.domain.chatrooms.dto.MessagesRequestDto;
import com.sparta.publicclassdev.domain.chatrooms.dto.MessagesResponseDto;
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
    public void sendMessage(MessagesRequestDto messagesRequestDto) throws JsonProcessingException {
        Users user = usersRepository.findByName(messagesRequestDto.getSender())
            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        ChatRooms chatRoom = chatRoomsRepository.findById(messagesRequestDto.getTeamsId())
            .orElseThrow(() -> new CustomException(ErrorCode.TEAM_NOT_FOUND));
        
        Messages message = Messages.builder()
            .contents(messagesRequestDto.getContent())
            .users(user)
            .chatRooms(chatRoom)
            .build();
        
        messagesRepository.save(message);
        
        MessagesResponseDto savedMessageDto = MessagesResponseDto.builder()
            .id(message.getId())
            .content(message.getContents())
            .sender(user.getName())
            .teamsId(message.getChatRooms().getId())
            .timestamp(message.getCreatedAt().toString())
            .username(message.getUsers().getName())
            .build();
        String messageJson = objectMapper.writeValueAsString(savedMessageDto);
        redisTemplate.convertAndSend(channelTopic.getTopic(), messageJson);
    }
    
    
    @Transactional
    public synchronized void addUser(ChatRoomsRequestDto chatRoomsRequestDto, SimpMessageHeaderAccessor headerAccessor)
        throws JsonProcessingException {
        ChatRoomsResponseDto joinMessage = ChatRoomsResponseDto.builder()
            .type(chatRoomsRequestDto.getType())
            .sender(chatRoomsRequestDto.getSender())
            .teamsId(chatRoomsRequestDto.getTeamsId())
            .content(chatRoomsRequestDto.getUsername() + "님이 입장하셨습니다.")
            .timestamp(LocalDateTime.now().toString())
            .build();
        redisTemplate.convertAndSend(channelTopic.getTopic(), objectMapper.writeValueAsString(joinMessage));
    }
    
    @Transactional(readOnly = true)
    public List<MessagesResponseDto> getChatMessages(Long teamsId) {
        List<Messages> messages = messagesRepository.findByChatRoomsOrderByCreatedAtAsc(teamsId);
        return messages.stream().map(message -> MessagesResponseDto.builder()
            .id(message.getId())
            .sender(message.getUsers().getName())
            .content(message.getContents())
            .teamsId(message.getChatRooms().getId())
            .timestamp(message.getCreatedAt().toString())
            .username(message.getUsers().getName())
            .build()).collect(Collectors.toList());
    }
}
