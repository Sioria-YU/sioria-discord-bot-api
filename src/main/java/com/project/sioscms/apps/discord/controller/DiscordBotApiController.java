package com.project.sioscms.apps.discord.controller;

import com.project.sioscms.apps.discord.service.DiscordBotApiService;
import com.project.sioscms.secure.domain.Auth;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/discord")
public class DiscordBotApiController {

    private final DiscordBotApiService discordBotApiService;

    @Auth(role = Auth.Role.ADMIN)
    @GetMapping("/member-refresh")
    public ResponseEntity<Boolean> memberRefresh() throws InterruptedException {
        return ResponseEntity.ok(discordBotApiService.memberRefresh());
    }

    @Auth(role = Auth.Role.ADMIN)
    @GetMapping("/roles-refresh")
    public ResponseEntity<Boolean> rolesRefresh(){
        return ResponseEntity.ok(discordBotApiService.rolesRefresh());
    }

    @Auth(role = Auth.Role.ADMIN)
    @GetMapping("/reague-msg-push")
    public ResponseEntity<Boolean> reagueMessagePush(@RequestParam(name = "reagueId") long reagueId){
        return ResponseEntity.ok(discordBotApiService.reagueMessagePush(reagueId));
    }
}
