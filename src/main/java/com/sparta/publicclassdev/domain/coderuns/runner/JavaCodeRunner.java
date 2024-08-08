package com.sparta.publicclassdev.domain.coderuns.runner;

import com.sparta.publicclassdev.global.exception.CustomException;
import com.sparta.publicclassdev.global.exception.ErrorCode;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

public class JavaCodeRunner implements CodeRunner {
    
    @Override
    public String runCode(String code) {
        validateCode(code);
        
        String className = getClassNameFromCode(code);
        if (className == null) {
            throw new CustomException(ErrorCode.INVALID_REQUEST);
        }
        String classFileName = className + ".java";
        
        File file = new File(System.getProperty("java.io.tmpdir"), classFileName);
        try (FileWriter fileWriter = new FileWriter(file)) {
            fileWriter.write(code);
        } catch (IOException e) {
            e.printStackTrace();
            throw new CustomException(ErrorCode.INVALID_REQUEST);
        }
        
        try {
            ProcessBuilder compileBuilder = new ProcessBuilder("javac", file.getAbsolutePath());
            compileBuilder.redirectErrorStream(true);
            Process compileProcess = compileBuilder.start();
            compileProcess.waitFor();
            
            if (compileProcess.exitValue() != 0) {
                throw new CustomException(ErrorCode.INVALID_REQUEST);
            }
            
            ProcessBuilder runBuilder = new ProcessBuilder("java", "-cp", file.getParent(), className);
            runBuilder.redirectErrorStream(true);
            Process runProcess = runBuilder.start();
            
            if (!runProcess.waitFor(5, TimeUnit.SECONDS)) {
                runProcess.destroy();
                throw new CustomException(ErrorCode.TIMEOUT);
            }
            
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(runProcess.getInputStream()));
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
            bufferedReader.close();
            
            file.delete();
            new File(file.getAbsolutePath().replace(".java", ".class")).delete();
            
            return stringBuilder.toString();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            throw new CustomException(ErrorCode.INVALID_REQUEST);
        }
    }
    
    private void validateCode(String code) {
        if (code.contains("System.exit") || code.contains("Runtime.getRuntime().exec") || code.contains("java.io.File")) {
            throw new CustomException(ErrorCode.INVALID_CODE);
        }
    }
    
    private String getClassNameFromCode(String code) {
        String[] lines = code.split("\\r?\\n");
        for (String line : lines) {
            if (line.trim().startsWith("public class ")) {
                int start = line.indexOf("public class ") + "public class ".length();
                int end = line.indexOf(" ", start);
                if (end == -1) {
                    end = line.indexOf("{", start);
                }
                if (end != -1) {
                    return line.substring(start, end).trim();
                }
            }
        }
        return null;
    }
}
