package com.sparta.publicclassdev.domain.chatrooms.entity;

import com.sparta.publicclassdev.domain.teams.entity.Teams;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "chatrooms")
@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class ChatRooms {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "teams_id")
    private Teams teams;
    
    @OneToMany(mappedBy = "chatRooms", orphanRemoval = true)
    private List<ChatRoomUsers> chatRoomUsers;
    
    @OneToMany(mappedBy = "chatRooms", orphanRemoval = true)
    private List<Messages> messages;
}
