package com.sparta.publicclassdev.domain.teams.dto;

import com.sparta.publicclassdev.domain.teams.entity.Teams;
import com.sparta.publicclassdev.domain.users.entity.Users;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;

@Getter
public class TeamResponseDto {

    private Long id;
    private String teamsName;
    private List<String> teamMembers;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public TeamResponseDto(Teams teams, List<Users> users) {

        this.id = teams.getId();
        this.teamsName = teams.getName();
        this.teamMembers = users.stream()
            .map(Users::getName)
            .collect(Collectors.toList());
        this.createdAt = teams.getCreatedAt();
        this.modifiedAt = teams.getModifiedAt();
    }
}
