package com.sparta.publicclassdev.domain.coderuns.runner;

import com.sparta.publicclassdev.global.exception.CustomException;
import com.sparta.publicclassdev.global.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JavaCodeRunnerTest {
    
    private CodeRunner codeRunner;
    
    @BeforeEach
    void setUp() {
        codeRunner = new JavaCodeRunner();
    }
    
    @Test
    @DisplayName("유효한 Java 코드 테스트")
    void runValidJavaCode() {
        String code = "public class Test { public static void main(String[] args) { System.out.println(\"Hello, World!\"); } }";
        String output = codeRunner.runCode(code);
        assertTrue(output.contains("Hello, World!"), "결과값 : 'Hello, World!'");
    }
    
    @Test
    @DisplayName("잘못된 코드는 CustomException 테스트")
    void runInvalidJavaCode() {
        String code = "public class Test { public static void main(String[] args) { System.out.println(Hello, World!); } }";
        CustomException thrown = assertThrows(CustomException.class, () -> codeRunner.runCode(code));
        assertEquals(ErrorCode.INVALID_REQUEST, thrown.getErrorCode(), "INVALID_REQUEST 에러 발생");
    }
    
    @Test
    @DisplayName("허용되지 않는 작업이 포함된 코드는 테스트")
    void runCodeWithDisallowedOperations() {
        String code = "import java.io.*; public class Test { public static void main(String[] args) { System.exit(1); } }";
        CustomException thrown = assertThrows(CustomException.class, () -> codeRunner.runCode(code));
        assertEquals(ErrorCode.INVALID_CODE, thrown.getErrorCode(), "INVALID_CODE 에러 발생");
    }
}
