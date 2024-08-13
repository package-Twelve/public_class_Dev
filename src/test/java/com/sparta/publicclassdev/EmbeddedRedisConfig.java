package com.sparta.publicclassdev;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.util.StringUtils;
import redis.embedded.RedisServer;

@Profile("test")
@Configuration
public class EmbeddedRedisConfig {
    @Value("${spring.data.redis.port}")
    int port;
    private RedisServer redisServer;

//    public EmbeddedRedisConfig() throws IOException {
//        this.redisServer = RedisServer.builder()
//            .port(this.port)
//            .setting("maxmemory 128M")
//            .build();
//    }

    @PostConstruct
    public void startRedis() throws IOException {
        if(!isRedisRunning()) {
            this.redisServer = RedisServer.builder()
                .port(this.port)
                .setting("maxmemory 128M")
                .build();
            this.redisServer.start();
        }
    }

    @PreDestroy
    public void stopRedis() {
        this.redisServer.stop();
    }
    private boolean isRedisRunning() throws IOException {
        return isRunning(executeGrepProcessCommand(port));
    }
    private Process executeGrepProcessCommand(int redisPort) throws IOException {
        String command = String.format("netstat -an | findstr LISTENING | findstr :%d", redisPort);
        String[] shell = {"cmd.exe", "/c", command};

        return Runtime.getRuntime().exec(shell);

    }
    private boolean isRunning(Process process) {
        String line;
        StringBuilder pidInfo = new StringBuilder();

        try (BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            while ((line = input.readLine()) != null) {
                pidInfo.append(line);
            }
        } catch (Exception e) {
            //throw new RuntimeException();
        }
        return StringUtils.hasText(pidInfo.toString());
    }
}