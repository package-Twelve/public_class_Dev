package com.sparta.publicclassdev.domain.chatrooms.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.sparta.publicclassdev.domain.chatrooms.entity.ChatRooms;
import com.sparta.publicclassdev.domain.chatrooms.entity.Messages;
import com.sparta.publicclassdev.domain.users.entity.RoleEnum;
import com.sparta.publicclassdev.domain.users.entity.Users;
import com.sparta.publicclassdev.domain.users.repository.UsersRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

@DataJpaTest
class MessagesRepositoryTest {
    
    @Autowired
    private MessagesRepository messagesRepository;
    
    @Autowired
    private ChatRoomsRepository chatRoomsRepository;
    
    @Autowired
    private UsersRepository usersRepository;
    
    private ChatRooms chatRoom;
    private Users user;
    
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
    @DisplayName("특정 채팅방의 메시지 조회 테스트")
    void findByChatRooms_Id() {
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
        
        List<Messages> messages = messagesRepository.findByChatRooms_Id(chatRoom.getId());
        
        assertThat(messages).hasSize(2);
        assertThat(messages.get(0).getContents()).isEqualTo("test message 1");
        assertThat(messages.get(1).getContents()).isEqualTo("test message 2");
    }
    
    @Test
    @DisplayName("빈 채팅방의 메시지 조회 테스트")
    void findByChatRooms_Id_NoMessages() {
        Long nonExistentRoomId = 999L;
        
        List<Messages> messages = messagesRepository.findByChatRooms_Id(nonExistentRoomId);
        
        assertThat(messages).isEmpty();
    }
}
