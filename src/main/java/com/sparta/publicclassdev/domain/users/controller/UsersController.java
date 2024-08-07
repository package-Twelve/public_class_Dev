package com.sparta.publicclassdev.domain.users.controller;

import com.sparta.publicclassdev.domain.users.dto.AuthRequestDto;
import com.sparta.publicclassdev.domain.users.dto.AuthResponseDto;
import com.sparta.publicclassdev.domain.users.dto.PasswordRequestDto;
import com.sparta.publicclassdev.domain.users.dto.PointRequestDto;
import com.sparta.publicclassdev.domain.users.dto.PointResponseDto;
import com.sparta.publicclassdev.domain.users.dto.ProfileRequestDto;
import com.sparta.publicclassdev.domain.users.dto.ProfileResponseDto;
import com.sparta.publicclassdev.domain.users.dto.ReissueTokenRequestDto;
import com.sparta.publicclassdev.domain.users.dto.SignupRequestDto;
import com.sparta.publicclassdev.domain.users.dto.SignupResponseDto;
import com.sparta.publicclassdev.domain.users.dto.UpdateProfileResponseDto;
import com.sparta.publicclassdev.domain.users.service.UsersService;
import com.sparta.publicclassdev.global.dto.DataResponse;
import com.sparta.publicclassdev.global.dto.MessageResponse;
import com.sparta.publicclassdev.global.security.JwtUtil;
import com.sparta.publicclassdev.global.security.UserDetailsImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UsersController {
    private final UsersService usersService;
    @PostMapping("/signup")
    public ResponseEntity<DataResponse<SignupResponseDto>> createUser(@Valid @RequestBody SignupRequestDto requestDto) {
        SignupResponseDto responseDto = usersService.createUser(requestDto);
        DataResponse<SignupResponseDto> response = new DataResponse<>(HttpStatus.CREATED.value(), "회원가입 성공", responseDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<DataResponse<AuthResponseDto>> login(@Valid @RequestBody AuthRequestDto requestDto) {
        AuthResponseDto responseDto = usersService.login(requestDto);
        DataResponse<AuthResponseDto> response = new DataResponse<>(HttpStatus.OK.value(), "로그인 성공", responseDto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @GetMapping("/profiles")
    public ResponseEntity<DataResponse<ProfileResponseDto>> getProfile(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        ProfileResponseDto responseDto = usersService.getProfile(userDetails.getUser());
        DataResponse<ProfileResponseDto> response = new DataResponse<>(HttpStatus.OK.value(), "프로필 조회 완료", responseDto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @PatchMapping("/profiles")
    public ResponseEntity<DataResponse<UpdateProfileResponseDto>> updateProfile(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @Valid @RequestBody ProfileRequestDto requestDto) {
        UpdateProfileResponseDto responseDto = usersService.updateProfile(userDetails.getUser().getId(), requestDto);
        DataResponse<UpdateProfileResponseDto> response = new DataResponse<>(HttpStatus.OK.value(), "프로필 수정 완료", responseDto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @PatchMapping("/profiles/passwords")
    public ResponseEntity<MessageResponse> updatePassword(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @Valid @RequestBody PasswordRequestDto requestDto) {
        usersService.updatePassword(userDetails.getUser().getId(), requestDto);
        MessageResponse messageResponse = new MessageResponse(HttpStatus.OK.value(), "비밀번호 초기화 완료");
        return new ResponseEntity<>(messageResponse, HttpStatus.OK);
    }
    @PostMapping("/logout")
    public ResponseEntity<MessageResponse> logout(@AuthenticationPrincipal UserDetailsImpl userDetails, HttpServletRequest request) {
        String accessToken = request.getHeader(JwtUtil.AUTHORIZATION_HEADER);
        usersService.logout(accessToken, userDetails.getUser().getEmail());
        MessageResponse messageResponse = new MessageResponse(HttpStatus.OK.value(), "로그아웃");
        return new ResponseEntity<>(messageResponse, HttpStatus.OK);
    }
    @PostMapping("/withdraw")
    public ResponseEntity<MessageResponse> withdraw(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody AuthRequestDto requestDto, HttpServletRequest request) {
        usersService.withdraw(userDetails.getUser().getId(), requestDto);
        logout(userDetails, request);
        MessageResponse messageResponse = new MessageResponse(HttpStatus.OK.value(), "회원탈퇴 성공");
        return new ResponseEntity<>(messageResponse, HttpStatus.OK);
    }
    @PostMapping("/reissue-token")
    public ResponseEntity<DataResponse<AuthResponseDto>> reissueToken(@RequestBody ReissueTokenRequestDto requestDto) {
        AuthResponseDto responseDto = usersService.reissueToken(requestDto.getRefreshToken());
        DataResponse<AuthResponseDto> response = new DataResponse<>(HttpStatus.OK.value(), "토큰 재발급 성공", responseDto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @GetMapping("/points")
    public ResponseEntity<DataResponse<PointResponseDto>> getPoints(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        PointResponseDto responseDto = usersService.getPoint(userDetails.getUser().getId());
        DataResponse<PointResponseDto> response = new DataResponse<>(HttpStatus.OK.value(), "포인트 조회 성공", responseDto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @PatchMapping("/points")
    public ResponseEntity<DataResponse<PointResponseDto>> updatePoints(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody PointRequestDto requestDto) {
        PointResponseDto responseDto = usersService.updatePoint(userDetails.getUser().getId(), requestDto.getPoint(), requestDto.getType());
        DataResponse<PointResponseDto> response = new DataResponse<>(HttpStatus.OK.value(), "포인트 수정 성공", responseDto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
