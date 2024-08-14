package com.sparta.publicclassdev.domain.chatrooms.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MessagesRequestDto {
    
    private String content;
    private String sender;
    private Long teamsId;
    private String timestamp;
    private String username;
    
    @Builder
    public MessagesRequestDto(String content, String sender, Long teamsId, String timestamp, String username) {
        this.content = content;
        this.sender = sender;
        this.teamsId = teamsId;
        this.timestamp = timestamp;
        this.username = username;
    }
}
