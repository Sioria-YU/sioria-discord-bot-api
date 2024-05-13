package com.project.sioscms.cms.management.discord.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/cms/discord")
public class DiscordManagementConroller {

    @RequestMapping("/member-list")
    public ModelAndView memberList(){
        ModelAndView mav = new ModelAndView("cms/discord/memberList");

        return mav;
    }
}
