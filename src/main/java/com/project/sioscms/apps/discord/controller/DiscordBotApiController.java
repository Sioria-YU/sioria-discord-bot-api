package com.project.sioscms.apps.discord.controller;

import com.project.sioscms.apps.discord.service.DiscordBotApiService;
import com.project.sioscms.cms.management.discord.service.ReagueManagementService;
import com.project.sioscms.secure.domain.Auth;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/discord")
public class DiscordBotApiController {

    private final DiscordBotApiService discordBotApiService;
    private final ReagueManagementService reagueManagementService;

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

    @Auth(role = Auth.Role.ADMIN)
    @DeleteMapping("/multi-delete")
    public ResponseEntity<Boolean> reagueMultiDelete(@RequestParam("ids[]") List<Long> ids){
        int deleteCount = 0;
        if(ids != null && ids.size() > 0){
            for (long id : ids) {
                if(reagueManagementService.delete(id))
                    deleteCount++;
            }
        }

        if(deleteCount > 0){
            return ResponseEntity.ok(true);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
        }
    }
}
