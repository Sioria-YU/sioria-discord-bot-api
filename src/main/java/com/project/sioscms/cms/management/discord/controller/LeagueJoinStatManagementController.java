package com.project.sioscms.cms.management.discord.controller;

import com.project.sioscms.apps.discord.domain.dto.LeagueDto;
import com.project.sioscms.secure.domain.Auth;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/cms/discord/league-join-stat")
public class LeagueJoinStatManagementController {

    @Auth(role = Auth.Role.ADMIN)
    @RequestMapping("/list")
    public ModelAndView leagueJoinStatList(LeagueDto.Request requestDto){
        ModelAndView mav = new ModelAndView("cms/discord/leagueJoinStatList");

//        SiosPage<LeagueDto.Response> siosPage = leagueManagementService.getLeagues(requestDto);

//        if(siosPage != null && !siosPage.isEmpty()){
//            mav.addObject("resultList", siosPage.getContents());
//            mav.addObject("pageInfo", siosPage.getPageInfo());
//        }

        return mav;
    }

    @Auth(role = Auth.Role.ADMIN)
    @RequestMapping("/view/{leagueTrackId}")
    public ModelAndView leagueJoinStatView(@PathVariable("leagueTrackId") Long leagueTrackId){
        ModelAndView mav = new ModelAndView("cms/discord/leagueJoinStatView");
        return mav;
    }
}
