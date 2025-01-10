package com.project.sioscms.apps.discord.controller;

import com.project.sioscms.apps.discord.service.LeagueTrackMemberService;
import com.project.sioscms.secure.domain.Auth;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/cms/api/discord/league-track-member")
@RequiredArgsConstructor
public class DiscordLeagueTrackMemberController {
    private final LeagueTrackMemberService leagueTrackMemberService;

    @Auth(role = Auth.Role.ADMIN)
    @PostMapping("/delete")
    public ResponseEntity<Boolean> deleteLeagueTrackMember(@RequestParam(value = "leagueTrackMemberId") Long leagueTrackMemberId){
        return ResponseEntity.ok(leagueTrackMemberService.deleteLeagueTrackMember(leagueTrackMemberId));
    }

    @Auth(role = Auth.Role.ADMIN)
    @PostMapping("/add-member")
    public ResponseEntity<Boolean> addLeagueTrackMember(@RequestParam(value = "trackId") Long trackId, @RequestParam(value = "buttonId") Long buttonId, @RequestParam(value = "memberId") Long memberId){
        return ResponseEntity.ok(leagueTrackMemberService.saveLeagueTrackMember(trackId, buttonId, memberId));
    }
}
