package com.sparta.publicclassdev.domain.coderuns.runner;

import com.sparta.publicclassdev.global.exception.CustomException;
import com.sparta.publicclassdev.global.exception.ErrorCode;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

public class JavaCodeRunner implements CodeRunner {
    
    @Override
    public String runCode(String code) {
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
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            throw new CustomException(ErrorCode.INVALID_REQUEST);
        }
        
        Long startTime = System.currentTimeMillis();
        
        StringBuilder stringBuilder = new StringBuilder();
        try {
            ProcessBuilder runBuilder = new ProcessBuilder("java", "-cp", file.getParent(),
                className);
            runBuilder.redirectErrorStream(true);
            Process runProcess = runBuilder.start();
            BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(runProcess.getInputStream()));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
            throw new CustomException(ErrorCode.INVALID_REQUEST);
        }
        
        Long endTime = System.currentTimeMillis();
        Long responseTime = endTime - startTime;
        
        file.delete();
        new File(file.getAbsolutePath().replace(".java", ".class")).delete();
        
        stringBuilder.append("Execution time: ").append(responseTime).append(" ms");
        
        return stringBuilder.toString();
    }
    
    private String getClassNameFromCode(String code) {
        String[] lines = code.split("\\r?\\n");
        for (String line : lines) {
            if (line.trim().startsWith("public class ")) {
                Integer start = line.indexOf("public class ") + "public class ".length();
                Integer end = line.indexOf(" ", start);
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
