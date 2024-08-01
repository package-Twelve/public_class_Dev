package com.sparta.publicclassdev.domain.teams.service;

import com.sparta.publicclassdev.domain.chatrooms.entity.ChatRoomUsers;
import com.sparta.publicclassdev.domain.chatrooms.entity.ChatRooms;
import com.sparta.publicclassdev.domain.chatrooms.entity.Messages;
import com.sparta.publicclassdev.domain.chatrooms.repository.ChatRoomUsersRepository;
import com.sparta.publicclassdev.domain.chatrooms.repository.ChatRoomsRepository;
import com.sparta.publicclassdev.domain.chatrooms.repository.MessagesRepository;
import com.sparta.publicclassdev.domain.coderuns.entity.CodeRuns;
import com.sparta.publicclassdev.domain.coderuns.repository.CodeRunsRepository;
import com.sparta.publicclassdev.domain.teams.dto.TeamResponseDto;
import com.sparta.publicclassdev.domain.teams.entity.TeamUsers;
import com.sparta.publicclassdev.domain.teams.entity.Teams;
import com.sparta.publicclassdev.domain.teams.repository.TeamUsersRepository;
import com.sparta.publicclassdev.domain.teams.repository.TeamsRepository;
import com.sparta.publicclassdev.domain.users.entity.Users;
import com.sparta.publicclassdev.domain.users.repository.UsersRepository;
import com.sparta.publicclassdev.domain.winners.entity.Winners;
import com.sparta.publicclassdev.domain.winners.repository.WinnersRepository;
import com.sparta.publicclassdev.global.exception.CustomException;
import com.sparta.publicclassdev.global.exception.ErrorCode;
import jakarta.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public TeamResponseDto teamMatch(Users users) {
        waitQueue.add(users);
        return createTeam(users);
    }
    
    @Transactional
    public TeamResponseDto createTeam(Users currentUser) {
        boolean isUserInTeam = teamUsersRepository.existsByUsers(currentUser);
        if (isUserInTeam) {
            throw new CustomException(ErrorCode.USER_NOT_TEAM);
        }
        
        List<Users> waitUser = new ArrayList<>();
        for (int i = 0; i < 3 && !waitQueue.isEmpty(); i++) {
            Users user = waitQueue.poll();
            if (!teamUsersRepository.existsByUsers(user)) {
                waitUser.add(user);
            }
        }
        waitUser.add(currentUser);
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
        for (Users user : waitUser) {
            if (!teamUsersRepository.existsByUsers(user)) {
                teamMembers.add(user);
                
                TeamUsers teamUsers = TeamUsers.builder()
                    .teams(teams)
                    .users(user)
                    .build();
                teamUsersRepository.save(teamUsers);
                
                ChatRoomUsers chatRoomUsers = ChatRoomUsers.builder()
                    .chatRooms(chatRooms)
                    .users(user)
                    .build();
                chatRoomUsersRepository.save(chatRoomUsers);
            }
        }
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
    
    @Transactional(readOnly = true)
    public TeamResponseDto getTeamByUserId(Long usersId) {
        Users users = validateUser(usersId);
        
        TeamUsers teamUser = teamUsersRepository.findByUsers(users)
            .orElseThrow(() -> new CustomException(ErrorCode.TEAM_NOT_FOUND));
        
        Teams teams = teamUser.getTeams();
        
        List<Users> teamMembers = teams.getTeamUsers().stream()
            .map(TeamUsers::getUsers)
            .collect(Collectors.toList());
        
        return new TeamResponseDto(teams, teamMembers);
    }
    
    private Users validateUser(Long userId) {
        return usersRepository.findById(userId)
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
    public void resetAutoIncrementColumns() {
        entityManager.createNativeQuery("ALTER TABLE teams AUTO_INCREMENT = 1").executeUpdate();
        entityManager.createNativeQuery("ALTER TABLE team_users AUTO_INCREMENT = 1").executeUpdate();
        entityManager.createNativeQuery("ALTER TABLE chatrooms AUTO_INCREMENT = 1").executeUpdate();
        entityManager.createNativeQuery("ALTER TABLE coderuns AUTO_INCREMENT = 1").executeUpdate();
        entityManager.createNativeQuery("ALTER TABLE chatroomusers AUTO_INCREMENT = 1").executeUpdate();
        entityManager.close();
    }
}
