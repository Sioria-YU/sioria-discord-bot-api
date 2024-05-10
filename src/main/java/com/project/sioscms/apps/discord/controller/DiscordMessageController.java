package com.project.sioscms.apps.discord.controller;

import com.project.sioscms.apps.discord.service.DiscordReaguePushService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/cms/Discord")
public class DiscordMessageController {
    private final DiscordReaguePushService discordReaguePushService;

    @RequestMapping("/message")
    public String messagePage(){
        return "cms/discord/message";
    }

    @PostMapping("/send-msg")
    public ResponseEntity<String> sendMessage(){
        discordReaguePushService.push();
        return ResponseEntity.ok("ok");
    }


}
