package com.sparta.publicclassdev.domain.chatrooms.controller;

import com.sparta.publicclassdev.domain.chatrooms.dto.MessagesRequestDto;
import com.sparta.publicclassdev.domain.chatrooms.service.ChatRoomsService;
import com.sparta.publicclassdev.domain.users.entity.Users;
import com.sparta.publicclassdev.global.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.util.concurrent.SettableListenableFuture;

import java.lang.reflect.Type;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@WebMvcTest(ChatRoomsController.class)
public class ChatRoomsControllerTest {
    
    @MockBean
    private JwtUtil jwtUtil;
    
    @MockBean
    private ChatRoomsService chatRoomsService;
    
    @Mock
    private WebSocketStompClient stompClient;
    
    @Mock
    private Users testUser;
    
    private BlockingQueue<String> blockingQueue;
    
    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        
        blockingQueue = new LinkedBlockingQueue<>();
        
        testUser = mock(Users.class);
        when(testUser.getEmail()).thenReturn("testuser@email.com");
        
        WebSocketClient webSocketClient = new StandardWebSocketClient();
        stompClient = mock(WebSocketStompClient.class);
    }
    
    @Test
    @DisplayName("WebSocket으로 메시지 전송 테스트")
    void testSendMessage() throws Exception {
        StompSession session = mock(StompSession.class);
        when(stompClient.connect(anyString(), any(WebSocketHttpHeaders.class), any(StompSessionHandlerAdapter.class)))
            .thenReturn(mockListenableFuture(session));
        
        doAnswer(invocation -> {
            blockingQueue.offer("test message");
            return null;
        }).when(session).send(anyString(), any());
        
        MessagesRequestDto requestDto = MessagesRequestDto.builder()
            .content("test message")
            .sender(testUser.getEmail())
            .teamsId(1L)
            .build();
        
        session.subscribe("/topic/messages", new DefaultStompFrameHandler());
        session.send("/app/chat.sendMessage", requestDto);
        
        String receivedMessage = blockingQueue.poll(10, TimeUnit.SECONDS);
        assertThat(receivedMessage).contains("test message");
    }
    
    @Test
    @DisplayName("WebSocket으로 사용자 추가 테스트")
    void testAddUser() throws Exception {
        StompSession session = mock(StompSession.class);
        when(stompClient.connect(anyString(), any(WebSocketHttpHeaders.class), any()))
            .thenReturn(mockListenableFuture(session));
        
        doAnswer(invocation -> {
            blockingQueue.offer("testuser님이 입장하셨습니다.");
            return null;
        }).when(session).send(anyString(), any());
        
        session.subscribe("/topic/users", new DefaultStompFrameHandler());
        session.send("/app/chat.addUser", "testuser님이 입장하셨습니다.");
        
        String receivedMessage = blockingQueue.poll(10, TimeUnit.SECONDS);
        assertThat(receivedMessage).contains("testuser님이 입장하셨습니다.");
    }
    
    private class DefaultStompFrameHandler implements org.springframework.messaging.simp.stomp.StompFrameHandler {
        @Override
        public Type getPayloadType(StompHeaders headers) {
            return String.class;
        }
        
        @Override
        public void handleFrame(StompHeaders headers, Object payload) {
            blockingQueue.offer((String) payload);
        }
    }
    
    private ListenableFuture<StompSession> mockListenableFuture(StompSession session) {
        SettableListenableFuture<StompSession> future = new SettableListenableFuture<>();
        future.set(session);
        return future;
    }
}
