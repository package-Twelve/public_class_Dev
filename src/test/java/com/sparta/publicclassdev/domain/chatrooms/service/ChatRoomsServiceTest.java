package com.sparta.publicclassdev.domain.chatrooms.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sparta.publicclassdev.domain.chatrooms.dto.MessagesRequestDto;
import com.sparta.publicclassdev.domain.chatrooms.entity.ChatRooms;
import com.sparta.publicclassdev.domain.chatrooms.entity.Messages;
import com.sparta.publicclassdev.domain.chatrooms.repository.ChatRoomsRepository;
import com.sparta.publicclassdev.domain.chatrooms.repository.MessagesRepository;
import com.sparta.publicclassdev.domain.teams.entity.Teams;
import com.sparta.publicclassdev.domain.teams.repository.TeamsRepository;
import com.sparta.publicclassdev.domain.users.entity.RoleEnum;
import com.sparta.publicclassdev.domain.users.entity.Users;
import com.sparta.publicclassdev.domain.users.repository.UsersRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
public class ChatRoomsServiceTest {
    
    @Autowired
    private ChatRoomsService chatRoomsService;
    
    @Autowired
    private UsersRepository usersRepository;
    
    @Autowired
    private TeamsRepository teamsRepository;
    
    @Autowired
    private ChatRoomsRepository chatRoomsRepository;
    
    @Autowired
    private MessagesRepository messagesRepository;
    
    @Test
    @Transactional
    public void testSendMessage() throws JsonProcessingException {
        Users user = Users.builder()
            .name("testuser")
            .email("testuser@example.com")
            .password("password")
            .role(RoleEnum.USER)
            .build();
        user = usersRepository.save(user);
        
        Teams team = Teams.builder()
            .name("testteam")
            .build();
        team = teamsRepository.save(team);
        
        ChatRooms chatRoom = ChatRooms.builder()
            .teams(team)
            .build();
        chatRoom = chatRoomsRepository.save(chatRoom);
        
        MessagesRequestDto messagesRequestDto = MessagesRequestDto.builder()
            .sender(user.getName())
            .content("테스트")
            .teamsId(chatRoom.getId())
            .build();
        
        chatRoomsService.sendMessage(messagesRequestDto);
        
        List<Messages> messages = messagesRepository.findAll();
        assertFalse(messages.isEmpty());
        assertEquals("테스트", messages.get(0).getContents());
    }
    
    @Test
    @Transactional
    public void testSendMessageWithDifferentContents() throws JsonProcessingException {
        Users user = Users.builder()
            .name("testuser")
            .email("testuser@example.com")
            .password("password")
            .role(RoleEnum.USER)
            .build();
        user = usersRepository.save(user);
        
        Teams team = Teams.builder()
            .name("testteam")
            .build();
        team = teamsRepository.save(team);
        
        ChatRooms chatRoom = ChatRooms.builder()
            .teams(team)
            .build();
        chatRoom = chatRoomsRepository.save(chatRoom);
        
        String[] contents = {"테스트 입니다", "무슨 테스트?", "채팅 테스트"};
        
        for (String content : contents) {
            MessagesRequestDto messagesRequestDto = MessagesRequestDto.builder()
                .sender(user.getName())
                .content(content)
                .teamsId(chatRoom.getId())
                .build();
            
            chatRoomsService.sendMessage(messagesRequestDto);
        }
        
        List<Messages> messages = messagesRepository.findAll();
        assertEquals(3, messages.size());
        for (int i = 0; i < contents.length; i++) {
            assertEquals(contents[i], messages.get(i).getContents());
        }
    }
    
    @Test
    @Transactional
    public void testSendMessageByMultipleUsers() throws JsonProcessingException {
        Users user1 = Users.builder()
            .name("testuser1")
            .email("testuser1@example.com")
            .password("password")
            .role(RoleEnum.USER)
            .build();
        user1 = usersRepository.save(user1);
        
        Users user2 = Users.builder()
            .name("testuser2")
            .email("testuser2@example.com")
            .password("password")
            .role(RoleEnum.USER)
            .build();
        user2 = usersRepository.save(user2);
        
        Teams team = Teams.builder()
            .name("testteam")
            .build();
        team = teamsRepository.save(team);
        
        ChatRooms chatRoom = ChatRooms.builder()
            .teams(team)
            .build();
        chatRoom = chatRoomsRepository.save(chatRoom);
        
        MessagesRequestDto messagesRequestDto1 = MessagesRequestDto.builder()
            .sender(user1.getName())
            .content("test1 메시지")
            .teamsId(chatRoom.getId())
            .build();
        chatRoomsService.sendMessage(messagesRequestDto1);
        
        MessagesRequestDto messagesRequestDto2 = MessagesRequestDto.builder()
            .sender(user2.getName())
            .content("test2 메시지")
            .teamsId(chatRoom.getId())
            .build();
        chatRoomsService.sendMessage(messagesRequestDto2);
        
        List<Messages> messages = messagesRepository.findAll();
        assertEquals(2, messages.size());
        assertEquals("test1 메시지", messages.get(0).getContents());
        assertEquals("test2 메시지", messages.get(1).getContents());
    }
    
}


