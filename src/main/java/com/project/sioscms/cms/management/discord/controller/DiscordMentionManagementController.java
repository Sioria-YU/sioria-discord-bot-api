package com.project.sioscms.cms.management.discord.controller;

import com.project.sioscms.apps.discord.domain.dto.DiscordMemberDto;
import com.project.sioscms.apps.discord.domain.dto.DiscordMentionDto;
import com.project.sioscms.cms.management.discord.service.DiscordManagementService;
import com.project.sioscms.cms.management.discord.service.DiscordMentionManagementService;
import com.project.sioscms.common.utils.jpa.page.SiosPage;
import com.project.sioscms.secure.domain.Auth;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Slf4j
@Controller
@RequestMapping("/cms/discord/mention")
@RequiredArgsConstructor
public class DiscordMentionManagementController {
    private final DiscordMentionManagementService discordMentionManagementService;
    private final DiscordManagementService discordManagementService;

    @Auth(role = Auth.Role.ADMIN)
    @RequestMapping("/list")
    public ModelAndView list(DiscordMentionDto.Request requestDto){
        ModelAndView mav = new ModelAndView("cms/discord/mentionList");

        SiosPage<DiscordMentionDto.Response> siosPage = discordMentionManagementService.getMentionList(requestDto);

        if(siosPage != null && !siosPage.isEmpty()){
            mav.addObject("resultList", siosPage.getContents());
            mav.addObject("pageInfo", siosPage.getPageInfo());
        }

        return mav;
    }

    @Auth(role = Auth.Role.ADMIN)
    @RequestMapping("/view/{id}")
    public ModelAndView view(@PathVariable("id") long id, DiscordMemberDto.Request requestDto){
        ModelAndView mav = new ModelAndView("cms/discord/mentionView");

        DiscordMentionDto.Response mentionInfo = discordMentionManagementService.getMention(id);
        if(mentionInfo != null){
            mav.addObject("mentionInfo", mentionInfo);
            requestDto.setDiscordUserMension(mentionInfo.getRoleId());
        }

        SiosPage<DiscordMemberDto.Response> siosPage = discordManagementService.getDiscordMembers(requestDto);

        if(siosPage != null && !siosPage.isEmpty()){
            mav.addObject("resultList", siosPage.getContents());
            mav.addObject("pageInfo", siosPage.getPageInfo());
        }

        return mav;
    }

}
