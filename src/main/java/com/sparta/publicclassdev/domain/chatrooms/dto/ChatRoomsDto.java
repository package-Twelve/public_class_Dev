package com.sparta.publicclassdev.domain.chatrooms.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChatRoomsDto {
    
    public enum MessageType {
        CHAT, JOIN, LEAVE
    }
    
    private MessageType type;
    private String content;
    private String sender;
    private Long teamsId;
    private LocalDateTime timestamp;
    private String username;
    
    @Builder
    public ChatRoomsDto(MessageType type, String content, String sender, Long teamsId, LocalDateTime timestamp, String username) {
        this.type = type;
        this.content = content;
        this.sender = sender;
        this.teamsId = teamsId;
        this.timestamp = timestamp;
        this.username = username;
    }
    
    public ChatRoomsDto withTimestamp(LocalDateTime timestamp) {
        return ChatRoomsDto.builder()
            .type(this.type)
            .content(this.content)
            .sender(this.sender)
            .teamsId(this.teamsId)
            .timestamp(timestamp)
            .username(this.username)
            .build();
    }
}
