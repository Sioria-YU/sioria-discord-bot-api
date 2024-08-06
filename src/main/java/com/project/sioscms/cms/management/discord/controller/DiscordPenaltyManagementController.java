package com.project.sioscms.cms.management.discord.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/cms/discord/penalty")
public class DiscordPenaltyManagementController {

    @RequestMapping("/list")
    public ModelAndView list(){
        ModelAndView mav = new ModelAndView("cms/discord/penaltyList");

        return mav;
    }

}
