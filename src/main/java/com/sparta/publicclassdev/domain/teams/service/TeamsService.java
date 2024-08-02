package com.sparta.publicclassdev.domain.teams.service;

import com.sparta.publicclassdev.domain.chatrooms.entity.ChatRoomUsers;
import com.sparta.publicclassdev.domain.chatrooms.entity.ChatRooms;
import com.sparta.publicclassdev.domain.chatrooms.repository.ChatRoomUsersRepository;
import com.sparta.publicclassdev.domain.chatrooms.repository.ChatRoomsRepository;
import com.sparta.publicclassdev.domain.coderuns.repository.CodeRunsRepository;
import com.sparta.publicclassdev.domain.teams.dto.TeamResponseDto;
import com.sparta.publicclassdev.domain.teams.entity.TeamUsers;
import com.sparta.publicclassdev.domain.teams.entity.Teams;
import com.sparta.publicclassdev.domain.teams.repository.TeamUsersRepository;
import com.sparta.publicclassdev.domain.teams.repository.TeamsRepository;
import com.sparta.publicclassdev.domain.users.entity.Users;
import com.sparta.publicclassdev.domain.users.repository.UsersRepository;
import com.sparta.publicclassdev.domain.winners.repository.WinnersRepository;
import com.sparta.publicclassdev.global.exception.CustomException;
import com.sparta.publicclassdev.global.exception.ErrorCode;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeamsService {
    
    private final TeamsRepository teamsRepository;
    private final TeamUsersRepository teamUsersRepository;
    private final UsersRepository usersRepository;
    private final ChatRoomsRepository chatRoomsRepository;
    private final ChatRoomUsersRepository chatRoomUsersRepository;
    private final CodeRunsRepository codeRunsRepository;
    private final WinnersRepository winnersRepository;
    private final EntityManager entityManager;
    
    private static final List<String> Modifier = List.of(
        "Agile", "Brave", "Calm", "Daring", "Eager", "Fierce", "Gentle", "Heroic", "Jolly", "Keen"
    );
    
    private static final List<String> Label = List.of(
        "Warriors", "Knights", "Mavericks", "Pioneers", "Rangers", "Samurais", "Titans", "Vikings",
        "Wizards", "Yankees"
    );
    
    private final Random RANDOM = new Random();
    private final ConcurrentLinkedDeque<Users> waitQueue = new ConcurrentLinkedDeque<>();
    private final ReentrantLock lock = new ReentrantLock();
    
    private String randomTeamName() {
        String teamName;
        do {
            String modifier = Modifier.get(RANDOM.nextInt(Modifier.size()));
            String label = Label.get(RANDOM.nextInt(Label.size()));
            teamName = modifier + " " + label;
        } while (teamsRepository.existsByName(teamName));
        return teamName;
    }
    
    @Transactional
    public TeamResponseDto createAndMatchTeam(String email) {
        Users user = validateUserByEmail(email);
        checkUserInTeam(user);
        
        List<Users> waitUser = new ArrayList<>();
        lock.lock();
        try {
            if (!waitQueue.contains(user)) {
                waitQueue.add(user);
            }
            
            for (int i = 0; i < 3 && !waitQueue.isEmpty(); i++) {
                Users waitingUser = waitQueue.poll();
                if (waitingUser != null && !teamUsersRepository.existsByUsers(waitingUser)) {
                    waitUser.add(waitingUser);
                }
            }
        } finally {
            lock.unlock();
        }
        
        if (waitUser.isEmpty()) {
            throw new CustomException(ErrorCode.NO_USERS_IN_WAITQUEUE);
        }
        
        Collections.shuffle(waitUser);
        
        Teams teams = Teams.builder()
            .name(randomTeamName())
            .build();
        teamsRepository.save(teams);
        
        ChatRooms chatRooms = ChatRooms.builder()
            .teams(teams)
            .build();
        chatRoomsRepository.save(chatRooms);
        
        List<Users> teamMembers = new ArrayList<>();
        for (Users waitingUser : waitUser) {
            if (!teamUsersRepository.existsByUsers(waitingUser)) {
                teamMembers.add(waitingUser);
                
                TeamUsers teamUsers = TeamUsers.builder()
                    .teams(teams)
                    .users(waitingUser)
                    .build();
                teamUsersRepository.save(teamUsers);
                
                ChatRoomUsers chatRoomUsers = ChatRoomUsers.builder()
                    .chatRooms(chatRooms)
                    .users(waitingUser)
                    .build();
                chatRoomUsersRepository.save(chatRoomUsers);
            }
        }
        return new TeamResponseDto(teams, teamMembers);
    }
    
    private void checkUserInTeam(Users users) {
        if (teamUsersRepository.existsByUsers(users)) {
            throw new CustomException(ErrorCode.USER_ALREADY_TEAM);
        }
    }
    
    @Transactional(readOnly = true)
    public TeamResponseDto getTeamByUserEmail(String email) {
        Users users = validateUserByEmail(email);
        
        List<TeamUsers> teamUsersList = teamUsersRepository.findByUsers(users);
        if (teamUsersList.isEmpty()) {
            throw new CustomException(ErrorCode.USER_NOT_TEAM);
        }
        
        TeamUsers teamUser = teamUsersList.get(0);
        Teams teams = teamUser.getTeams();
        
        List<Users> teamMembers = teams.getTeamUsers().stream()
            .map(TeamUsers::getUsers)
            .collect(Collectors.toList());
        
        return new TeamResponseDto(teams, teamMembers);
    }
    
    private Users validateUserByEmail(String email) {
        return usersRepository.findByEmail(email)
            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }
    
    @Transactional(readOnly = true)
    public TeamResponseDto getTeamById(Long teamsId) {
        Teams teams = teamsRepository.findById(teamsId)
            .orElseThrow(() -> new CustomException(ErrorCode.TEAM_NOT_FOUND));
        
        List<Users> teamMembers = teams.getTeamUsers().stream()
            .map(TeamUsers::getUsers)
            .collect(Collectors.toList());
        
        return new TeamResponseDto(teams, teamMembers);
    }
    
    @Transactional
    public void deleteAllTeams() {
        entityManager.createNativeQuery("SET FOREIGN_KEY_CHECKS = 0").executeUpdate();
        
        List<Teams> allTeams = teamsRepository.findAll();
        for (Teams teams : allTeams) {
            chatRoomsRepository.deleteAllByTeamsId(teams.getId());
            codeRunsRepository.deleteAllByTeams(teams);
            teamUsersRepository.deleteAllByTeams(teams);
            winnersRepository.deleteAllByTeams(teams);
            teamsRepository.delete(teams);
        }
        
        resetAutoIncrementColumns();
        
        entityManager.createNativeQuery("SET FOREIGN_KEY_CHECKS = 1").executeUpdate();
    }
    
    @Transactional
    public void resetAutoIncrementColumns() {
        entityManager.createNativeQuery("ALTER TABLE teams AUTO_INCREMENT = 1").executeUpdate();
        entityManager.createNativeQuery("ALTER TABLE team_users AUTO_INCREMENT = 1").executeUpdate();
        entityManager.createNativeQuery("ALTER TABLE chatrooms AUTO_INCREMENT = 1").executeUpdate();
        entityManager.createNativeQuery("ALTER TABLE coderuns AUTO_INCREMENT = 1").executeUpdate();
        entityManager.createNativeQuery("ALTER TABLE chatroomusers AUTO_INCREMENT = 1").executeUpdate();
        entityManager.close();
    }
}
