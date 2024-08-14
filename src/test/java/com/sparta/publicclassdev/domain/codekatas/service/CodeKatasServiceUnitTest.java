package com.sparta.publicclassdev.domain.codekatas.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sparta.publicclassdev.domain.codekatas.dto.CodeKatasRequestDto;
import com.sparta.publicclassdev.domain.codekatas.dto.CodeKatasResponseDto;
import com.sparta.publicclassdev.domain.codekatas.entity.CodeKatas;
import com.sparta.publicclassdev.domain.codekatas.repository.CodeKatasRepository;
import com.sparta.publicclassdev.global.exception.CustomException;
import com.sparta.publicclassdev.global.exception.ErrorCode;
import com.sparta.publicclassdev.global.security.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
public class CodeKatasServiceUnitTest {
    
    @Mock
    private CodeKatasRepository codeKatasRepository;
    
    @Mock
    private JwtUtil jwtUtil;
    
    @Mock
    private HttpServletRequest request;
    
    @InjectMocks
    private CodeKatasService codeKatasService;
    
    private String token;
    private Claims mockClaims;
    
    @BeforeEach
    public void setUp() {
        token = "mockToken";
        
        mockClaims = mock(Claims.class);
        lenient().when(mockClaims.get("auth")).thenReturn("ADMIN");
        lenient().when(jwtUtil.getJwtFromHeader(any(HttpServletRequest.class))).thenReturn(token);
        lenient().when(jwtUtil.getUserInfoFromToken(token)).thenReturn(mockClaims);
    }
    
    @Nested
    class AdminTests {
        
        @DisplayName("코드카타 생성")
        @Test
        public void createCodeKata() {
            CodeKatasRequestDto requestDto = new CodeKatasRequestDto("test", "test codekata");
            CodeKatas codeKatas = CodeKatas.builder()
                .title(requestDto.getTitle())
                .contents(requestDto.getContents())
                .build();
            
            when(codeKatasRepository.save(any(CodeKatas.class))).thenReturn(codeKatas);
            
            CodeKatasResponseDto responseDto = codeKatasService.createCodeKata(request, requestDto);
            
            assertNotNull(responseDto);
            assertEquals(requestDto.getTitle(), responseDto.getTitle());
            verify(codeKatasRepository, times(1)).save(any(CodeKatas.class));
            verify(jwtUtil, times(1)).getJwtFromHeader(any(HttpServletRequest.class));
            verify(jwtUtil, times(1)).getUserInfoFromToken(token);
        }
        
        @DisplayName("코드카타 생성 시 권한오류")
        @Test
        public void createCodeKata_Unauthorize() {
            lenient().when(mockClaims.get("auth")).thenReturn("USER");
            
            CodeKatasRequestDto requestDto = new CodeKatasRequestDto("test", "test codekata");
            
            CustomException exception = assertThrows(CustomException.class, () -> {
                codeKatasService.createCodeKata(request, requestDto);
            });
            
            assertEquals(ErrorCode.NOT_UNAUTHORIZED, exception.getErrorCode());
            verify(codeKatasRepository, never()).save(any(CodeKatas.class));
            verify(jwtUtil, times(1)).getJwtFromHeader(any(HttpServletRequest.class));
            verify(jwtUtil, times(1)).getUserInfoFromToken(token);
        }
        
        @DisplayName("코드카타 단건 조회")
        @Test
        public void getCodeKata() {
            CodeKatas codeKatas = CodeKatas.builder()
                .id(1L)
                .title("test")
                .contents("test codekata")
                .build();
            
            when(codeKatasRepository.findById(1L)).thenReturn(Optional.of(codeKatas));
            
            CodeKatasResponseDto responseDto = codeKatasService.getCodeKata(request, 1L);
            
            assertNotNull(responseDto);
            assertEquals(codeKatas.getTitle(), responseDto.getTitle());
            verify(codeKatasRepository, times(1)).findById(1L);
            verify(jwtUtil, times(1)).getJwtFromHeader(any(HttpServletRequest.class));
            verify(jwtUtil, times(1)).getUserInfoFromToken(token);
        }
        
        @DisplayName("코드카타 단건 조회")
        @Test
        public void getCodeKata_NotFound() {
            when(codeKatasRepository.findById(1L)).thenReturn(Optional.empty());
            
            CustomException exception = assertThrows(CustomException.class, () -> {
                codeKatasService.getCodeKata(request, 1L);
            });
            
            assertEquals(ErrorCode.NOT_FOUND_CODEKATA, exception.getErrorCode());
            verify(codeKatasRepository, times(1)).findById(1L);
            verify(jwtUtil, times(1)).getJwtFromHeader(any(HttpServletRequest.class));
            verify(jwtUtil, times(1)).getUserInfoFromToken(token);
        }
        
        @DisplayName("코드카타 전체 조회")
        @Test
        public void getAllCodeKatas() {
            CodeKatas codeKata1 = CodeKatas.builder()
                .title("test1")
                .contents("test codekata1")
                .markDate(LocalDate.now())
                .build();
            
            CodeKatas codeKata2 = CodeKatas.builder()
                .title("test2")
                .contents("test codekata2")
                .markDate(LocalDate.now())
                .build();
            
            List<CodeKatas> codeKatasList = List.of(codeKata1, codeKata2);
            Page<CodeKatas> codeKatasPage = new PageImpl<>(codeKatasList);
            
            when(codeKatasRepository.findAll(any(Pageable.class))).thenReturn(codeKatasPage);
            
            Page<CodeKatasResponseDto> responseDto = codeKatasService.getAllCodeKatas(request,
                Pageable.unpaged());
            
            assertEquals(2, responseDto.getTotalElements());
            assertEquals("test1", responseDto.getContent().get(0).getTitle());
            assertEquals("test2", responseDto.getContent().get(1).getTitle());
            verify(jwtUtil, times(1)).getJwtFromHeader(any(HttpServletRequest.class));
            verify(jwtUtil, times(1)).getUserInfoFromToken(token);
        }
        
        @DisplayName("코드카타 삭제")
        @Test
        public void deleteCodeKata() {
            CodeKatas codeKatas = CodeKatas.builder()
                .title("test")
                .contents("contents")
                .markDate(LocalDate.now())
                .build();
            
            when(codeKatasRepository.findById(anyLong())).thenReturn(Optional.of(codeKatas));
            
            codeKatasService.deleteCodeKata(request, 1L);
            
            verify(codeKatasRepository, times(1)).delete(codeKatas);
            verify(jwtUtil, times(1)).getJwtFromHeader(any(HttpServletRequest.class));
            verify(jwtUtil, times(1)).getUserInfoFromToken(token);
        }
        
        @DisplayName("코드카타 수정")
        @Test
        public void updateCodeKata() {
            CodeKatas codeKatas = CodeKatas.builder()
                .title("test")
                .contents("contents")
                .markDate(null)
                .build();
            
            CodeKatasRequestDto requestDto = new CodeKatasRequestDto("new test", "new contents");
            
            when(codeKatasRepository.findById(anyLong())).thenReturn(Optional.of(codeKatas));
            
            CodeKatasResponseDto responseDto = codeKatasService.updateCodeKata(request, 1L,
                requestDto);
            
            assertEquals("new test", responseDto.getTitle());
            assertEquals("new contents", responseDto.getContents());
            verify(codeKatasRepository, times(1)).save(codeKatas);
            verify(jwtUtil, times(1)).getJwtFromHeader(any(HttpServletRequest.class));
            verify(jwtUtil, times(1)).getUserInfoFromToken(token);
        }
    }
    
    @Nested
    class PublicTests {
        
        @DisplayName("오늘의 코드카타 지정")
        @Test
        public void getTodayCodeKata() {
            LocalDate today = LocalDate.now();
            CodeKatas codeKatas = CodeKatas.builder()
                .title("Today")
                .contents("Today contents")
                .markDate(today)
                .build();
            
            when(codeKatasRepository.findByMarkDate(eq(today))).thenReturn(List.of(codeKatas));
            
            CodeKatasResponseDto responseDto = codeKatasService.getTodayCodeKata();
            
            assertNotNull(responseDto);
            assertEquals("Today", responseDto.getTitle());
            assertEquals("Today contents", responseDto.getContents());
            verify(codeKatasRepository, times(1)).findByMarkDate(eq(today));
        }
        
        @DisplayName("코드카타 랜덤 지정")
        @Test
        public void CreatesRandomKata() {
            LocalDate today = LocalDate.now();
            when(codeKatasRepository.findByMarkDate(eq(today))).thenReturn(List.of());
            
            CodeKatas defaultKata = CodeKatas.builder()
                .title("기본 코드카타")
                .contents("기본 코드카타입니다. 코드카타를 추가해주세요")
                .markDate(null)
                .build();
            
            when(codeKatasRepository.findByMarkDateIsNull()).thenReturn(List.of(defaultKata));
            
            lenient().when(codeKatasRepository.save(any(CodeKatas.class))).thenReturn(defaultKata);
            
            CodeKatasResponseDto responseDto = codeKatasService.getTodayCodeKata();
            
            assertNotNull(responseDto);
            assertEquals("기본 코드카타", responseDto.getTitle());
            assertEquals("기본 코드카타입니다. 코드카타를 추가해주세요", responseDto.getContents());
            verify(codeKatasRepository, times(1)).findByMarkDate(eq(today));
            verify(codeKatasRepository, times(1)).findByMarkDateIsNull();
            verify(codeKatasRepository, times(1)).save(any(CodeKatas.class));
        }
    }
}
