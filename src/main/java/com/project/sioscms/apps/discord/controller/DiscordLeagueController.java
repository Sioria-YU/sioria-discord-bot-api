package com.project.sioscms.apps.discord.controller;

import com.project.sioscms.apps.discord.domain.dto.LeagueDto;
import com.project.sioscms.apps.discord.service.LeagueService;
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
@RequestMapping("/cms/api/discord/league")
public class DiscordLeagueController {

    private final LeagueService leagueService;

    @Auth(role = Auth.Role.ADMIN)
    @GetMapping("/list")
    public ResponseEntity<List<LeagueDto.Response>> getLeagueList(@RequestParam("leagueName") final String leagueName){
        return ResponseEntity.ok(leagueService.getLeagueList(leagueName));
    }
}
