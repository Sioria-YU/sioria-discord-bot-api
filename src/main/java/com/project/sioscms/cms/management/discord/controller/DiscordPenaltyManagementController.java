package com.project.sioscms.cms.management.discord.controller;

import com.project.sioscms.apps.code.domain.dto.CodeDto;
import com.project.sioscms.apps.code.service.CodeService;
import com.project.sioscms.apps.discord.domain.dto.DiscordPenaltyDto;
import com.project.sioscms.cms.management.discord.service.DiscordPenaltyManagementService;
import com.project.sioscms.common.utils.jpa.page.SiosPage;
import com.project.sioscms.secure.domain.Auth;
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
    private final DiscordPenaltyManagementService discordPenaltyManagementService;
    private final CodeService codeService;

    @Auth(role = Auth.Role.ADMIN)
    @RequestMapping("/list")
    public ModelAndView list(DiscordPenaltyDto.Request requestDto){
        ModelAndView mav = new ModelAndView("cms/discord/penaltyList");

        CodeDto.Request param = new CodeDto.Request();
        param.setCodeGroupId("PENALTY_TYPE_CD");

        SiosPage<DiscordPenaltyDto.Response> siosPage = discordPenaltyManagementService.getList(requestDto);
        if(siosPage != null && !siosPage.isEmpty()){
            mav.addObject("resultList", siosPage.getContents());
            mav.addObject("pageInfo", siosPage.getPageInfo());
        }

        mav.addObject("penaltyTypeCdList", codeService.getCodeList(param));

        return mav;
    }

}
