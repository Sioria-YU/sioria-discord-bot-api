package com.project.sioscms.apps.discord.controller;

import com.project.sioscms.apps.discord.service.DiscordMentionService;
import com.project.sioscms.secure.domain.Auth;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/cms/api/discord/mention")
@RequiredArgsConstructor
public class DiscordMentionController {
    private final DiscordMentionService discordMentionService;

    @Auth(role = Auth.Role.ADMIN)
    @DeleteMapping("multi-delete")
    public ResponseEntity<Boolean> multiDelete(long mentionId, @RequestParam("ids[]") List<Long> ids){
        boolean flag = true;
        flag = discordMentionService.multiDeleteDiscordMention(mentionId, ids);
        return ResponseEntity.ok(flag);
    }
}
