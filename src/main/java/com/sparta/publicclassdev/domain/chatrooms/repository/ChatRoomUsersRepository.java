package com.sparta.publicclassdev.domain.chatrooms.repository;

import com.sparta.publicclassdev.domain.chatrooms.entity.ChatRoomUsers;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomUsersRepository extends JpaRepository<ChatRoomUsers, Long> {

}
