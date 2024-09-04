package com.project.sioscms.cms.management.discord.controller;

import com.project.sioscms.secure.domain.Auth;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("/cms/discord/info")
public class DiscordInfoManagementController {

    @Auth(role = Auth.Role.ADMIN)
    @RequestMapping("/manage")
    public String discordInfoMain(){
        return "cms/discord/infoManage";
    }
}
