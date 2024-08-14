package com.sparta.publicclassdev.domain.chatrooms.repository;

import com.sparta.publicclassdev.domain.chatrooms.entity.Messages;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessagesRepository extends JpaRepository<Messages, Long> {
    List<Messages> findByChatRooms_Id(Long roomsId);
}
