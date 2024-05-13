package com.project.sioscms.apps.discord.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/discord")
public class DiscordBotApiController {

    @GetMapping("/member-refresh")
    public ResponseEntity<Boolean> memberRefresh(){
        return ResponseEntity.ok(false);
    }
}
