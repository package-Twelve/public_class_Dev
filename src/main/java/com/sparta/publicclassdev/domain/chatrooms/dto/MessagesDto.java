package com.sparta.publicclassdev.domain.chatrooms.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MessagesDto {
    
    private Long id;
    private String content;
    private String sender;
    private Long teamsId;
    private LocalDateTime timestamp;
    
    @Builder
    public MessagesDto(Long id, String content, String sender, Long teamsId, LocalDateTime timestamp) {
        this.id = id;
        this.content = content;
        this.sender = sender;
        this.teamsId = teamsId;
        this.timestamp = timestamp;
    }
}
