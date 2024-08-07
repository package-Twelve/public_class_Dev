package com.sparta.publicclassdev.domain.chatrooms.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MessagesResponseDto {
    
    private Long id;
    private String content;
    private String sender;
    private Long teamsId;
    private String timestamp;
    private String username;
    
    @Builder
    public MessagesResponseDto(Long id, String content, String sender, Long teamsId, String timestamp, String username) {
        this.id = id;
        this.content = content;
        this.sender = sender;
        this.teamsId = teamsId;
        this.timestamp = timestamp;
        this.username = username;
    }
}
