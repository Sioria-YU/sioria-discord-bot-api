package com.project.sioscms.cms.management.discord.controller;

import com.project.sioscms.apps.code.domain.dto.CodeDto;
import com.project.sioscms.apps.code.service.CodeGroupService;
import com.project.sioscms.apps.code.service.CodeService;
import com.project.sioscms.apps.discord.domain.dto.LeagueDto;
import com.project.sioscms.apps.discord.domain.dto.LeagueTrackDto;
import com.project.sioscms.cms.management.discord.service.LeagueJoinStatManagementService;
import com.project.sioscms.common.utils.common.http.HttpUtil;
import com.project.sioscms.common.utils.jpa.page.SiosPage;
import com.project.sioscms.secure.domain.Auth;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/cms/discord/league-join-stat")
public class LeagueJoinStatManagementController {
    private final LeagueJoinStatManagementService leagueJoinStatManagementService;
    private final CodeService codeService;

    @Auth(role = Auth.Role.ADMIN)
    @RequestMapping("/list")
    public ModelAndView leagueJoinStatList(LeagueTrackDto.Request requestDto){
        ModelAndView mav = new ModelAndView("cms/discord/leagueJoinStatList");
        SiosPage<LeagueTrackDto.Response> siosPage = leagueJoinStatManagementService.getLeagueTrackList(requestDto);

        if(siosPage != null && !siosPage.isEmpty()){
            mav.addObject("resultList", siosPage.getContents());
            mav.addObject("pageInfo", siosPage.getPageInfo());
        }

        return mav;
    }

    @Auth(role = Auth.Role.ADMIN)
    @RequestMapping("/view/{leagueTrackId}")
    public ModelAndView leagueJoinStatView(@PathVariable("leagueTrackId") Long leagueTrackId){
        ModelAndView mav = new ModelAndView("cms/discord/leagueJoinStatView");

        //현재 트랙 정보
        LeagueTrackDto.Response leagueTrack = leagueJoinStatManagementService.getLeagueTrack(leagueTrackId);
        mav.addObject("leagueTrackInfo", leagueTrack);

        //부모 리그 정보 -> 현재 트랙에 들어있음
        mav.addObject("leagueInfo", leagueTrack.getLeague().toResponse());

        //리그 전체 트랙 목록
        mav.addObject("allTrackInfo", leagueJoinStatManagementService.getLeagueTrackListByLeagueId(leagueTrack.getLeague().getId()));

        //참여자 정보
        mav.addObject("trackMembersInfo", leagueJoinStatManagementService.getTrackMembers(leagueTrackId));

        //참여 구분 코드
        CodeDto.Request dto = new CodeDto.Request();
        dto.setCodeGroupId("LEAGUE_JOIN_TYPE_CD");
        mav.addObject("joinTypeCodeList", codeService.getCodeList(dto));

        return mav;
    }

    @Auth(role = Auth.Role.ADMIN)
    @RequestMapping("/update")
    public void trackMemberUpdate(HttpServletResponse response, LeagueTrackDto.Request requestDto){
        boolean flag = leagueJoinStatManagementService.updateTrackMembers(requestDto);

        if(flag){
            HttpUtil.alertAndRedirect(response, "/cms/discord/league-join-stat/view/"+requestDto.getId(), "정상 처리되었습니다.", null);
        }else{
            HttpUtil.alertAndRedirect(response, "/cms/discord/league-join-stat/view/"+requestDto.getId(), "처리 실패하였습니다.", null);
        }
    }
}
