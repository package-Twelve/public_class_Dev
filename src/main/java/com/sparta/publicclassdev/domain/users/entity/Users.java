package com.sparta.publicclassdev.domain.users.entity;

import com.sparta.publicclassdev.domain.chatrooms.entity.ChatRoomUsers;
import com.sparta.publicclassdev.domain.chatrooms.entity.Messages;
import com.sparta.publicclassdev.domain.teams.entity.TeamUsers;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    private String intro;

    @Column(nullable = false)
    private String password;
    private int point;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private RoleEnum role;

    @OneToMany(mappedBy = "users")
    private List<TeamUsers> teamUsers;

    @OneToMany(mappedBy = "users")
    private List<ChatRoomUsers> chatRoomUsers;

    @OneToMany(mappedBy = "users")
    private List<Messages> messages;

    @Builder
    public Users(String name, String email, String password, int point, RoleEnum role) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.point = point;
        this.role = role;
    }

    public void updateUsers(String name, String intro) {
        this.name = name;
        this.intro = intro;
    }

    public void updatePassword(String password) {
        this.password = password;
    }

    public void updateRole(RoleEnum role) {
        this.role = role;
    }

    public void updatePoint(int point) {
        this.point = point;
    }
}
