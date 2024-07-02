package com.project.sioscms.apps.discord.controller;

import com.project.sioscms.apps.discord.service.DiscordLeaguePushService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/discord/message")
public class DiscordMessageController {
    private final DiscordLeaguePushService discordLeaguePushService;

    @RequestMapping("/message")
    public String messagePage(){
        return "cms/discord/message";
    }

    @PostMapping("/send-msg")
    public ResponseEntity<String> sendMessage(){
        discordLeaguePushService.push();
        return ResponseEntity.ok("ok");
    }
}
