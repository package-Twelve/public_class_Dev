package com.sparta.publicclassdev.domain.chatrooms.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.publicclassdev.domain.chatrooms.dto.ChatRoomsRequestDto;
import com.sparta.publicclassdev.domain.chatrooms.dto.MessagesRequestDto;
import com.sparta.publicclassdev.domain.chatrooms.dto.MessagesResponseDto;
import com.sparta.publicclassdev.domain.chatrooms.entity.ChatRooms;
import com.sparta.publicclassdev.domain.chatrooms.entity.Messages;
import com.sparta.publicclassdev.domain.chatrooms.repository.ChatRoomsRepository;
import com.sparta.publicclassdev.domain.chatrooms.repository.MessagesRepository;
import com.sparta.publicclassdev.domain.users.entity.RoleEnum;
import com.sparta.publicclassdev.domain.users.entity.Users;
import com.sparta.publicclassdev.domain.users.repository.UsersRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class ChatRoomsServiceTest {
    
    @Autowired
    private ChatRoomsService chatRoomsService;
    
    @Autowired
    private UsersRepository usersRepository;
    
    @Autowired
    private ChatRoomsRepository chatRoomsRepository;
    
    @Autowired
    private MessagesRepository messagesRepository;
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private ChannelTopic channelTopic;
    
    private Users user;
    private ChatRooms chatRoom;
    
    @BeforeEach
    void setUp() {
        user = usersRepository.save(Users.builder()
            .name("testuser1")
            .email("testuser1@email.com")
            .password("password")
            .role(RoleEnum.ADMIN)
            .point(0)
            .build());
        
        chatRoom = chatRoomsRepository.save(ChatRooms.builder().build());
    }
    
    @Test
    @DisplayName("메시지 전송 테스트")
    void sendMessage() throws JsonProcessingException {
        MessagesRequestDto requestDto = MessagesRequestDto.builder()
            .content("test")
            .sender(user.getName())
            .teamsId(chatRoom.getId())
            .build();
        
        chatRoomsService.sendMessage(requestDto);
        
        Messages savedMessage = messagesRepository.findAll().get(0);
        
        assertThat(savedMessage).isNotNull();
        assertThat(savedMessage.getUsers().getName()).isEqualTo("testuser1");
        assertThat(savedMessage.getContents()).isEqualTo("test");
    }
    
    @Test
    @DisplayName("사용자 입장 알림 테스트")
    void addUser() throws JsonProcessingException, InterruptedException {
        ChatRoomsRequestDto requestDto = ChatRoomsRequestDto.builder()
            .type("JOIN")
            .sender(user.getName())
            .teamsId(chatRoom.getId())
            .username(user.getName())
            .build();
        
        final String[] receivedMessage = new String[1];
        
        MessageListener listener = new MessageListener() {
            @Override
            public void onMessage(Message message, byte[] pattern) {
                receivedMessage[0] = new String(message.getBody());
            }
        };
        
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(redisTemplate.getConnectionFactory());
        container.addMessageListener(listener, channelTopic);
        container.afterPropertiesSet();
        container.start();
        
        try {
            chatRoomsService.addUser(requestDto, null);
            
            Thread.sleep(1000);
            
            assertNotNull(receivedMessage[0], "Redis 메시지가 수신되어야 합니다.");
            assertThat(receivedMessage[0]).contains(user.getName() + "님이 입장하셨습니다.");
        } finally {
            container.stop();
        }
    }
    
    @Test
    @DisplayName("채팅 메시지 조회 테스트")
    void getChatMessages() throws JsonProcessingException {
        Messages message1 = messagesRepository.save(Messages.builder()
            .contents("test message 1")
            .users(user)
            .chatRooms(chatRoom)
            .build());
        
        Messages message2 = messagesRepository.save(Messages.builder()
            .contents("test message 2")
            .users(user)
            .chatRooms(chatRoom)
            .build());
        
        List<MessagesResponseDto> messages = chatRoomsService.getChatMessages(chatRoom.getId());
        
        assertThat(messages).hasSize(2);
        assertThat(messages.get(0).getContent()).isEqualTo("test message 1");
        assertThat(messages.get(1).getContent()).isEqualTo("test message 2");
    }
    
    @Test
    @DisplayName("Redis 메시지 발행 및 수신 확인 테스트")
    void testRedisMessagePublishAndReceive() throws InterruptedException {
        String messageJson = "{\"type\":\"JOIN\",\"sender\":\"testuser\",\"teamsId\":1,\"content\":\"testuser님이 입장하셨습니다.\",\"timestamp\":\"2024-08-19T10:14:43.905+09:00\"}";
        
        final String[] receivedMessage = new String[1];
        
        MessageListener listener = new MessageListener() {
            @Override
            public void onMessage(Message message, byte[] pattern) {
                receivedMessage[0] = new String(message.getBody());
            }
        };
        
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(redisTemplate.getConnectionFactory());
        container.addMessageListener(listener, channelTopic);
        container.afterPropertiesSet();
        container.start();
        
        try {
            redisTemplate.convertAndSend(channelTopic.getTopic(), messageJson);
            
            Thread.sleep(1000);
            
            assertNotNull(receivedMessage[0], "Redis 메시지가 수신되어야 합니다.");
            assertThat(receivedMessage[0]).isEqualTo(messageJson);
        } finally {
            container.stop();
        }
    }
}