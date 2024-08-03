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
    
    @Builder
    public ChatRoomsDto(MessageType type, String content, String sender, Long teamsId, LocalDateTime timestamp) {
        this.type = type;
        this.content = content;
        this.sender = sender;
        this.teamsId = teamsId;
        this.timestamp = timestamp;
    }
    
    public ChatRoomsDto withTimestamp(LocalDateTime timestamp) {
        return ChatRoomsDto.builder()
            .type(this.type)
            .content(this.content)
            .sender(this.sender)
            .teamsId(this.teamsId)
            .timestamp(timestamp)
            .build();
    }
}
