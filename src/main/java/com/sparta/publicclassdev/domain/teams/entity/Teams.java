package com.sparta.publicclassdev.domain.teams.entity;

import com.sparta.publicclassdev.domain.chatrooms.entity.ChatRooms;
import com.sparta.publicclassdev.domain.coderuns.entity.CodeRuns;
import com.sparta.publicclassdev.domain.winners.entity.Winners;
import com.sparta.publicclassdev.global.entity.Timestamped;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "teams")
@Getter
@NoArgsConstructor
public class Teams extends Timestamped {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    
    @OneToMany(mappedBy = "teams", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<TeamUsers> teamUsers;
    
    @OneToMany(mappedBy = "teams", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<ChatRooms> chatRooms;
    
    @OneToMany(mappedBy = "teams", cascade = CascadeType.ALL)
    private List<Winners> winners;
    
    @OneToMany(mappedBy = "teams", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<CodeRuns> codeRuns;
    
    @Builder
    public Teams(String name) {
        this.name = name;
    }
    
    public void addTeamUser(TeamUsers teamUser) {
        this.teamUsers.add(teamUser);
    }
    
    public void setTeamUsers(List<TeamUsers> teamUsers) {
        this.teamUsers = teamUsers;
    }
}
