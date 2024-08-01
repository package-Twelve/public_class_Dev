package com.sparta.publicclassdev.domain.users.service;


import com.sparta.publicclassdev.domain.communities.dto.CommunitiesResponseDto;
import com.sparta.publicclassdev.domain.communities.entity.Communities;
import com.sparta.publicclassdev.domain.communities.repository.CommunitiesRepository;
import com.sparta.publicclassdev.domain.users.dao.UserRedisDao;
import com.sparta.publicclassdev.domain.users.dto.AuthRequestDto;
import com.sparta.publicclassdev.domain.users.dto.AuthResponseDto;
import com.sparta.publicclassdev.domain.users.dto.PasswordRequestDto;
import com.sparta.publicclassdev.domain.users.dto.PointResponseDto;
import com.sparta.publicclassdev.domain.users.dto.ProfileRequestDto;
import com.sparta.publicclassdev.domain.users.dto.ProfileResponseDto;
import com.sparta.publicclassdev.domain.users.dto.SignupRequestDto;
import com.sparta.publicclassdev.domain.users.dto.SignupResponseDto;
import com.sparta.publicclassdev.domain.users.dto.UpdateProfileResponseDto;
import com.sparta.publicclassdev.domain.users.entity.CalculateTypeEnum;
import com.sparta.publicclassdev.domain.users.entity.RankEnum;
import com.sparta.publicclassdev.domain.users.entity.RoleEnum;
import com.sparta.publicclassdev.domain.users.entity.Users;
import com.sparta.publicclassdev.domain.users.repository.UsersRepository;
import com.sparta.publicclassdev.global.CacheNames;
import com.sparta.publicclassdev.global.exception.CustomException;
import com.sparta.publicclassdev.global.exception.ErrorCode;
import com.sparta.publicclassdev.global.security.JwtUtil;
import io.jsonwebtoken.Claims;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UsersService {
    private final UsersRepository usersRepository;
    private final CommunitiesRepository communitiesRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final UserRedisDao redisDao;

    @Value("${ADMIN_TOKEN}")
    private String ADMIN_TOKEN;
    public SignupResponseDto createUser(SignupRequestDto requestDto) {
        String password = passwordEncoder.encode(requestDto.getPassword());
        RoleEnum role = RoleEnum.USER;
        if(requestDto.isAdmin()) {
            if (!ADMIN_TOKEN.equals(requestDto.getAdminToken())) {
                throw new CustomException(ErrorCode.INCORRECT_MANAGER_KEY);
            }
            role = RoleEnum.ADMIN;
        }
        if(usersRepository.findByEmail(requestDto.getEmail()).isPresent()) {
            throw new CustomException(ErrorCode.USER_NOT_UNIQUE);
        }
        if(usersRepository.findByName(requestDto.getName()).isPresent()) {
            throw new CustomException(ErrorCode.NAME_NOT_UNIQUE);
        }
        Users user = Users.builder()
            .name(requestDto.getName())
            .email(requestDto.getEmail())
            .password(password)
            .point(0)
            .role(role)
            .build();
        usersRepository.save(user);
        return SignupResponseDto.builder()
            .name(user.getName())
            .email(user.getEmail())
            .role(user.getRole())
            .build();
    }
    @Cacheable(cacheNames = CacheNames.USERBYEMAIL, key = "'login'+ #p0.getEmail()")
    public AuthResponseDto login(AuthRequestDto requestDto) {
        String email = requestDto.getEmail();
        String password = requestDto.getPassword();

        Users user = usersRepository.findByEmail(email).orElseThrow(
            () -> new CustomException(ErrorCode.CHECK_EMAIL)
        );
        if((!passwordEncoder.matches(password, user.getPassword()))) {
            throw new CustomException(ErrorCode.INCORRECT_PASSWORD);
        }
        if(user.getRole().equals(RoleEnum.WITHDRAW)) {
            throw new CustomException(ErrorCode.USER_WITHDRAW);
        }
        AuthResponseDto responseDto = new AuthResponseDto(jwtUtil.createAccessToken(user),
            jwtUtil.createRefreshToken(user));
        redisDao.setRefreshToken(email, responseDto.getRefreshToken(), jwtUtil.getREFRESHTOKEN_TIME());
        return responseDto;
    }

    public ProfileResponseDto getProfile(Users user) {
        List<Communities> recentCommunities = communitiesRepository.findPostByUserLimit5(user);
        List<CommunitiesResponseDto> recentResponseDto = recentCommunities.stream().map(
            (communities) -> new CommunitiesResponseDto(communities.getTitle(), communities.getContent(), communities.getCategory()))
            .toList();
        return new ProfileResponseDto(user, recentResponseDto);
    }

    @Transactional
    public UpdateProfileResponseDto updateProfile(Long id, ProfileRequestDto requestDto) {
        Users user = findById(id);
        user.updateUsers(requestDto.getName(), requestDto.getIntro());
        return new UpdateProfileResponseDto(user);
    }

    public Users findById(Long id) {
        return usersRepository.findById(id).orElseThrow(
            () -> new CustomException(ErrorCode.USER_NOT_FOUND)
        );
    }

    @CacheEvict(cacheNames = CacheNames.USERBYEMAIL, key = "'login'+#p1")
    @Transactional
    public void logout(String accessToken, String email) {
        String substringToken = jwtUtil.substringToken(accessToken);
        Long expiration = jwtUtil.getExpiration(substringToken);
        redisDao.setBlackList(substringToken, "logout", expiration);
        if (redisDao.hasKey(email)) {
            redisDao.deleteRefreshToken(email);
        } else {
            throw new CustomException(ErrorCode.USER_LOGOUT);
        }
    }

    @Transactional
    public void withdraw(Long id, AuthRequestDto requestDto) {
        String email = requestDto.getEmail();
        String password = requestDto.getPassword();
        Users user = findById(id);
        if(!email.equals(user.getEmail())) {
            throw new CustomException(ErrorCode.CHECK_EMAIL);
        }
        if((!passwordEncoder.matches(password, user.getPassword()))) {
            throw new CustomException(ErrorCode.INCORRECT_PASSWORD);
        }
        if(user.getRole().equals(RoleEnum.WITHDRAW)) {
            throw new CustomException(ErrorCode.USER_WITHDRAW);
        }
        user.updateRole(RoleEnum.WITHDRAW);
    }
    public AuthResponseDto reissueToken(String refreshToken) {
        String substringToken = jwtUtil.substringToken(refreshToken);
        Claims info = jwtUtil.getUserInfoFromToken(substringToken);
        String email = info.getSubject();
        return reissueTokenWithEmail(email, refreshToken);
    }
    @CachePut(cacheNames = CacheNames.USERBYEMAIL, key = "'login' + #p0")
    public AuthResponseDto reissueTokenWithEmail(String email, String refreshToken) {
        Users user = usersRepository.findByEmail(email).orElseThrow(() ->
            new UsernameNotFoundException("Not Found " + email));
        String storedRefreshToken = redisDao.getRefreshToken(email);
        log.info("Received refresh token: " + refreshToken);
        log.info("Stored refresh token: " + storedRefreshToken);

        if(redisDao.getRefreshToken(email).contains("\"")) {
            storedRefreshToken = storedRefreshToken.replace("\"", "");
        }
        if (!storedRefreshToken.equals(refreshToken)) {
            throw new CustomException(ErrorCode.TOKEN_MISMATCH);
        }

        String newAccessToken = jwtUtil.createAccessToken(user);
        String newRefreshToken = jwtUtil.createRefreshToken(user);

        redisDao.setRefreshToken(email, newRefreshToken, jwtUtil.getREFRESHTOKEN_TIME());

        return new AuthResponseDto(newAccessToken, newRefreshToken);
    }
    public PointResponseDto getPoint(Long id) {
        Users user = findById(id);
        int point = user.getPoint();
        RankEnum rank = RankEnum.getRankByPoints(point);

        return new PointResponseDto(point, rank);
    }

    @Transactional
    public PointResponseDto updatePoint(Long id, int point, CalculateTypeEnum type) {
        Users user = findById(id);
        switch (type) {
            case ADD -> {
                user.updatePoint(user.getPoint() + point);
            }
            case SUBTRACT -> {
                user.updatePoint(user.getPoint() - point);
            }
        }
        int newPoint = user.getPoint();
        return new PointResponseDto(newPoint, RankEnum.getRankByPoints(newPoint));
    }

    @Transactional
    public void updatePassword(Long id, PasswordRequestDto requestDto) {
        Users user = findById(id);
        String password = passwordEncoder.encode(requestDto.getPassword());
        user.updatePassword(password);
    }
}
