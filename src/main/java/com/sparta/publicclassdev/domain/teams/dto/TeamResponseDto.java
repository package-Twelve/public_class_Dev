package com.sparta.publicclassdev.domain.teams.dto;

import com.sparta.publicclassdev.domain.teams.entity.Teams;
import com.sparta.publicclassdev.domain.users.entity.Users;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TeamResponseDto {
    private Long id;
    private String name;
    private List<String> teamMembers;
    
    @Builder
    public TeamResponseDto(Teams teams, List<Users> teamMembers) {
        this.id = teams.getId();
        this.name = teams.getName();
        this.teamMembers = teamMembers.stream()
            .filter(Objects::nonNull)
            .map(Users::getName)
            .collect(Collectors.toList());
    }
}
