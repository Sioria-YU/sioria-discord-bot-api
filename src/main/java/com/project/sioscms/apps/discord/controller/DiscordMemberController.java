package com.project.sioscms.apps.discord.controller;

import com.project.sioscms.apps.discord.domain.dto.DiscordMemberDto;
import com.project.sioscms.apps.discord.service.DiscordMemberService;
import com.project.sioscms.secure.domain.Auth;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/cms/api/discord/member")
public class DiscordMemberController {
    private final DiscordMemberService discordMemberService;

    @Auth(role = Auth.Role.ADMIN)
    @GetMapping("/list")
    public ResponseEntity<List<DiscordMemberDto.Response>> getDiscordMemberList(@RequestParam("username") final String username){
        return ResponseEntity.ok(discordMemberService.getDiscordMemberList(username));
    }
}
