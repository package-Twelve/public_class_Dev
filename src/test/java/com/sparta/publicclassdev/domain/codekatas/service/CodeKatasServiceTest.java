package com.sparta.publicclassdev.domain.codekatas.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.sparta.publicclassdev.domain.codekatas.dto.CodeKatasRequestDto;
import com.sparta.publicclassdev.domain.codekatas.dto.CodeKatasResponseDto;
import com.sparta.publicclassdev.domain.codekatas.entity.CodeKatas;
import com.sparta.publicclassdev.domain.codekatas.repository.CodeKatasRepository;
import com.sparta.publicclassdev.domain.users.entity.RoleEnum;
import com.sparta.publicclassdev.domain.users.entity.Users;
import com.sparta.publicclassdev.domain.users.repository.UsersRepository;
import com.sparta.publicclassdev.global.security.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class CodeKatasServiceTest {
    
    @Autowired
    private CodeKatasService codeKatasService;
    
    @Autowired
    private CodeKatasRepository codeKatasRepository;
    
    @Autowired
    private UsersRepository usersRepository;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    private Users adminUser;
    private HttpServletRequest mockRequest;
    
    @BeforeEach
    void setUp() {
        usersRepository.deleteAll();
        codeKatasRepository.deleteAll();
        
        adminUser = createUser();
        usersRepository.save(adminUser);
        
        mockRequest = Mockito.mock(HttpServletRequest.class);
        Mockito.when(mockRequest.getHeader("Authorization"))
            .thenReturn(jwtUtil.createAccessToken(adminUser));
    }
    
    private Users createUser() {
        Users user = Users.builder()
            .name("testuser")
            .email("testuser@email.com")
            .password(new BCryptPasswordEncoder().encode("password"))
            .role(RoleEnum.ADMIN)
            .point(0)
            .build();
        
        ReflectionTestUtils.setField(user, "id", 1L);
        return user;
    }
    
    @Test
    @DisplayName("코드카타 생성 테스트")
    void testCreateCodeKata() {
        CodeKatasRequestDto requestDto = new CodeKatasRequestDto("testkata", "test contents");
        
        CodeKatasResponseDto responseDto = codeKatasService.createCodeKata(mockRequest, requestDto);
        
        assertThat(responseDto).isNotNull();
        assertEquals("testkata", responseDto.getTitle());
        assertEquals("test contents", responseDto.getContents());
        assertEquals(1, codeKatasRepository.count());
    }
    
    @Test
    @DisplayName("코드카타 단건 조회 테스트")
    void testGetCodeKata() {
        CodeKatas codeKata = codeKatasRepository.save(CodeKatas.builder()
            .title("testkata")
            .contents("test contents")
            .markDate(null)
            .build());
        
        CodeKatasResponseDto responseDto = codeKatasService.getCodeKata(mockRequest,
            codeKata.getId());
        
        assertThat(responseDto).isNotNull();
        assertEquals("testkata", responseDto.getTitle());
        assertEquals("test contents", responseDto.getContents());
    }
    
    @Test
    @DisplayName("코드카타 전체 조회 테스트")
    void testGetAllCodeKatas() {
        codeKatasRepository.save(CodeKatas.builder()
            .title("kata1")
            .contents("contents1")
            .markDate(null)
            .build());
        
        codeKatasRepository.save(CodeKatas.builder()
            .title("kata2")
            .contents("contents2")
            .markDate(null)
            .build());
        
        List<CodeKatasResponseDto> responseList = codeKatasService.getAllCodeKatas(mockRequest,
            Pageable.unpaged()).getContent();
        
        assertThat(responseList).hasSize(2);
        assertThat(responseList.get(0).getTitle()).isEqualTo("kata1");
        assertThat(responseList.get(1).getTitle()).isEqualTo("kata2");
    }
    
    @Test
    @DisplayName("코드카타 삭제 테스트")
    void testDeleteCodeKata() {
        CodeKatas codeKata = codeKatasRepository.save(CodeKatas.builder()
            .title("testkata")
            .contents("test contents")
            .markDate(null)
            .build());
        
        codeKatasService.deleteCodeKata(mockRequest, codeKata.getId());
        
        assertThat(codeKatasRepository.findById(codeKata.getId())).isEmpty();
    }
    
    @Test
    @DisplayName("코드카타 수정 테스트")
    void testUpdateCodeKata() {
        CodeKatas codeKata = codeKatasRepository.save(CodeKatas.builder()
            .title("oldtitle")
            .contents("old contents")
            .markDate(null)
            .build());
        
        CodeKatasRequestDto updateRequestDto = new CodeKatasRequestDto("newtitle", "new contents");
        
        CodeKatasResponseDto responseDto = codeKatasService.updateCodeKata(mockRequest,
            codeKata.getId(), updateRequestDto);
        
        assertThat(responseDto).isNotNull();
        assertEquals("newtitle", responseDto.getTitle());
        assertEquals("new contents", responseDto.getContents());
    }
    
    @Test
    @DisplayName("오늘의 코드카타 조회 테스트")
    void testGetTodayCodeKata() {
        CodeKatas codeKata = codeKatasRepository.save(CodeKatas.builder()
            .title("testkata")
            .contents("test contents")
            .markDate(LocalDate.now())
            .build());
        
        CodeKatasResponseDto responseDto = codeKatasService.getTodayCodeKata();
        
        assertThat(responseDto).isNotNull();
        assertEquals("testkata", responseDto.getTitle());
        assertEquals("test contents", responseDto.getContents());
    }
    
    @Test
    @DisplayName("랜덤 코드카타 생성 테스트")
    void testCreateRandomCodeKata() {
        CodeKatas codeKata1 = codeKatasRepository.save(CodeKatas.builder()
            .title("kata1")
            .contents("contents1")
            .markDate(null)
            .build());
        
        CodeKatas codeKata2 = codeKatasRepository.save(CodeKatas.builder()
            .title("kata2")
            .contents("contents2")
            .markDate(null)
            .build());
        
        CodeKatasResponseDto responseDto = codeKatasService.createRandomCodeKata();
        
        assertThat(responseDto).isNotNull();
        assertThat(responseDto.getTitle()).isIn("kata1", "kata2");
    }
}
