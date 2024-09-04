package com.project.sioscms.cms.management.discord.controller;

import com.project.sioscms.apps.discord.domain.dto.DiscordMemberDto;
import com.project.sioscms.apps.discord.service.DiscordBotApiService;
import com.project.sioscms.cms.management.discord.service.DiscordManagementService;
import com.project.sioscms.common.utils.jpa.page.SiosPage;
import com.project.sioscms.secure.domain.Auth;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/cms/discord")
public class DiscordManagementConroller {
    private final DiscordManagementService discordManagementService;
    private final DiscordBotApiService discordBotApiService;

    @Auth(role = Auth.Role.ADMIN)
    @RequestMapping("/member-list")
    public ModelAndView memberList(DiscordMemberDto.Request requestDto){
        ModelAndView mav = new ModelAndView("cms/discord/memberList");
        SiosPage<DiscordMemberDto.Response> siosPage = discordManagementService.getDiscordMembers(requestDto);

        if(siosPage != null && !siosPage.isEmpty()){
            mav.addObject("resultList", siosPage.getContents());
            mav.addObject("pageInfo", siosPage.getPageInfo());
        }

        mav.addObject("discordMentionList", discordBotApiService.getMentions());

        return mav;
    }
}
