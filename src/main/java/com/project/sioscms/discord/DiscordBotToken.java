package com.project.sioscms.discord;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DiscordBotToken {

    @Value("${discord.token}")
    private String token;

    public String getToken(){
        return this.token;
    }
}
