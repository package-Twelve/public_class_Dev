package com.sparta.publicclassdev.domain.chatrooms.service;

import static org.mockito.Mockito.when;

import com.sparta.publicclassdev.domain.chatrooms.entity.ChatRooms;
import com.sparta.publicclassdev.domain.chatrooms.repository.ChatRoomsRepository;
import com.sparta.publicclassdev.domain.chatrooms.repository.MessagesRepository;
import com.sparta.publicclassdev.domain.users.entity.RoleEnum;
import com.sparta.publicclassdev.domain.users.entity.Users;
import com.sparta.publicclassdev.domain.users.repository.UsersRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
public class ChatRoomsServiceTest {

    @Mock
    private SimpMessageSendingOperations operations;

    @Mock
    private UsersRepository usersRepository;

    @Mock
    private ChatRoomsRepository chatRoomsRepository;

    @Mock
    private MessagesRepository messagesRepository;

    @InjectMocks
    private ChatRoomsService chatRoomsService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        Users testUser = Users.builder()
            .name("testUser")
            .email("testUser@email.com")
            .password("password")
            .role(RoleEnum.USER)
            .build();

        ChatRooms testChatRoom = ChatRooms.builder()
            .teams(null)
            .build();

        when(usersRepository.findByName("testUser")).thenReturn(Optional.of(testUser));
        when(chatRoomsRepository.findById(1L)).thenReturn(Optional.of(testChatRoom));
    }

//    @Test
//    public void sendMessageTest() throws JsonProcessingException {
//        ChatRoomsDto chatRoomsDto = ChatRoomsDto.builder()
//            .type(ChatRoomsDto.MessageType.CHAT)
//            .content("Hello")
//            .sender("testUser")
//            .teamsId(1L)
//            .build();
//
//        chatRoomsService.sendMessage(chatRoomsDto);
//
//        verify(messagesRepository, times(1)).save(any(Messages.class));
//        verify(operations, times(1)).convertAndSend(eq("/topic/chatroom/1"), eq(chatRoomsDto));
//    }

//    @Test
//    public void sendMessageUserNotFoundTest() {
//        ChatRoomsDto chatRoomsDto = ChatRoomsDto.builder()
//            .type(ChatRoomsDto.MessageType.CHAT)
//            .content("Hello")
//            .sender("unknown")
//            .teamsId(1L)
//            .build();
//
//        when(usersRepository.findByName("unknown")).thenReturn(Optional.empty());
//
//        assertThatThrownBy(() -> chatRoomsService.sendMessage(chatRoomsDto))
//            .isInstanceOf(CustomException.class)
//            .hasMessageContaining(ErrorCode.USER_NOT_FOUND.getMessage());
//    }
}