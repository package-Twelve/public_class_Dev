package com.sparta.publicclassdev.domain.teams.entity;

import com.sparta.publicclassdev.domain.chatrooms.entity.ChatRooms;
import com.sparta.publicclassdev.domain.coderuns.entity.CodeRuns;
import com.sparta.publicclassdev.domain.winners.entity.Winners;
import com.sparta.publicclassdev.global.entity.Timestamped;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
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

    @OneToMany(mappedBy = "teams")
    private List<TeamUsers> teamUsers;

    @OneToMany(mappedBy = "teams")
    private List<ChatRooms> chatRooms;

    @OneToMany(mappedBy = "teams")
    private List<Winners> winners;

    @OneToMany(mappedBy = "teams")
    private List<CodeRuns> codeRuns;

    @Builder
    public Teams(String name, List<TeamUsers> teamUsers, List<ChatRooms> chatRooms,
        List<Winners> winners) {
        this.name = name;
        this.teamUsers = teamUsers;
        this.chatRooms = chatRooms;
        this.winners = winners;
    }
}
