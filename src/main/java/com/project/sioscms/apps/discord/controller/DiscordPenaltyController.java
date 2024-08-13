package com.project.sioscms.apps.discord.controller;

import com.project.sioscms.apps.discord.domain.dto.DiscordPenaltyDto;
import com.project.sioscms.apps.discord.service.DiscordPenaltyService;
import com.project.sioscms.secure.domain.Auth;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/cms/api/discord/penalty")
public class DiscordPenaltyController {
    private final DiscordPenaltyService discordPenaltyService;

    @Auth(role = Auth.Role.ADMIN)
    @GetMapping("/get/{id}")
    public ResponseEntity<DiscordPenaltyDto.Response> getDiscordPenalty(@PathVariable("id") long id){
        return ResponseEntity.ok(discordPenaltyService.getDiscordPenalty(id));
    }

    @Auth(role = Auth.Role.ADMIN)
    @PostMapping("/save")
    public ResponseEntity<Boolean> save(@NonNull DiscordPenaltyDto.Request requestDto){
        boolean flag = true;
        try{
            DiscordPenaltyDto.Response dto = discordPenaltyService.save(requestDto);
            if(dto == null){
                flag = false;
            }
        }catch (Exception e){
            flag = false;
        }

        return ResponseEntity.ok(flag);
    }

    @Auth(role = Auth.Role.ADMIN)
    @PutMapping("/update")
    public ResponseEntity<Boolean> update(@NonNull DiscordPenaltyDto.Request requestDto){
        boolean flag = true;
        try{
            DiscordPenaltyDto.Response dto = discordPenaltyService.update(requestDto);
            if(dto == null){
                flag = false;
            }
        }catch (Exception e){
            flag = false;
        }

        return ResponseEntity.ok(flag);
    }

    @Auth(role = Auth.Role.ADMIN)
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Boolean> delete(@PathVariable("id") long id){
        return ResponseEntity.ok(discordPenaltyService.delete(id));
    }

    @Auth(role = Auth.Role.ADMIN)
    @DeleteMapping("/multi-delete")
    public ResponseEntity<Boolean> discordPenaltyMultiDelete(@RequestParam("ids[]") List<Long> ids){
        int deleteCount = 0;
        if(ids != null && ids.size() > 0){
            for (long id : ids) {
                if(discordPenaltyService.delete(id))
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
