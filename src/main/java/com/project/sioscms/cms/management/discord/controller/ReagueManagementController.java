package com.project.sioscms.cms.management.discord.controller;

import com.project.sioscms.apps.attach.domain.dto.AttachFileGroupDto;
import com.project.sioscms.apps.attach.service.AttachFileService;
import com.project.sioscms.apps.code.domain.dto.CodeDto;
import com.project.sioscms.apps.code.service.CodeService;
import com.project.sioscms.apps.discord.domain.dto.ReagueDto;
import com.project.sioscms.apps.discord.service.DiscordBotApiService;
import com.project.sioscms.cms.management.discord.service.ReagueManagementService;
import com.project.sioscms.common.utils.common.http.HttpUtil;
import com.project.sioscms.common.utils.jpa.page.SiosPage;
import com.project.sioscms.secure.domain.Auth;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/cms/discord/reague")
public class ReagueManagementController {

    private final AttachFileService attachFileService;
    private final ReagueManagementService reagueManagementService;
    private final DiscordBotApiService discordBotApiService;
    private final CodeService codeService;

    @Auth(role = Auth.Role.ADMIN)
    @RequestMapping("/list")
    public ModelAndView reagueList(ReagueDto.Request requestDto){
        ModelAndView mav = new ModelAndView("cms/discord/reagueList");

        SiosPage<ReagueDto.Response> siosPage = reagueManagementService.getReagues(requestDto);

        if(siosPage != null && !siosPage.isEmpty()){
            mav.addObject("resultList", siosPage.getContents());
            mav.addObject("pageInfo", siosPage.getPageInfo());
        }

        return mav;
    }

    @Auth(role = Auth.Role.ADMIN)
    @RequestMapping("/view/{id}")
    public ModelAndView reagueView(@PathVariable Long id){
        ModelAndView mav = new ModelAndView("cms/discord/reagueRegist");

        ReagueDto.Response dto = reagueManagementService.getReague(id);

        if(dto != null){
            mav.addObject("result", dto);
        }

        CodeDto.Request param = new CodeDto.Request();
        param.setCodeGroupId("TRACK");

        mav.addObject("tackCodeList", codeService.getCodeList(param));
        mav.addObject("discordMentionLise", discordBotApiService.getMentions());
        mav.addObject("newsChannelList", discordBotApiService.getNewsChannels());

        return mav;
    }

    @Auth(role = Auth.Role.ADMIN)
    @RequestMapping("/regist")
    public ModelAndView reagueRegist(){
        ModelAndView mav = new ModelAndView("cms/discord/reagueRegist");
        CodeDto.Request param = new CodeDto.Request();
        param.setCodeGroupId("TRACK");

        mav.addObject("tackCodeList", codeService.getCodeList(param));
        mav.addObject("discordMentionLise", discordBotApiService.getMentions());
        mav.addObject("newsChannelList", discordBotApiService.getNewsChannels());
        return mav;
    }

    @Auth(role = Auth.Role.ADMIN)
    @RequestMapping("/save")
    public void reagueSave(HttpServletResponse response, ReagueDto.Request requestDto, @RequestPart List<MultipartFile> files){
        //첨부파일을 등록하여 attachFileGroupId를 requestDto에 set하여 게시판 저장으로 넘긴다.
        //최초 저장이기 때문에 attachFileGroup = null
        AttachFileGroupDto.Response attachFileGroupDto = attachFileService.multiUpload(files, null, "reague");

        if(attachFileGroupDto != null){
            requestDto.setAttachFileGroupId(attachFileGroupDto.getId());
        }
        ReagueDto.Response dto = reagueManagementService.save(requestDto);

        if (dto != null) {
            HttpUtil.alertAndRedirect(response, "/cms/discord/reague/list", "정상 처리되었습니다.", null);
        } else {
            HttpUtil.alertAndRedirect(response, "/cms/discord/reague/list", "처리 실패하였습니다.", null);
        }
    }

    @Auth(role = Auth.Role.ADMIN)
    @RequestMapping("/update")
    public void reagueUpdate(HttpServletResponse response, ReagueDto.Request requestDto, @RequestPart List<MultipartFile> files){
        AttachFileGroupDto.Response attachFileGroupDto = attachFileService.multiUpload(files, requestDto.getAttachFileGroupId(), "reague");

        if(attachFileGroupDto != null){
            requestDto.setAttachFileGroupId(attachFileGroupDto.getId());
        }
        ReagueDto.Response dto = reagueManagementService.update(requestDto);

        if (dto != null) {
            HttpUtil.alertAndRedirect(response, "/cms/discord/reague/list", "정상 처리되었습니다.", null);
        } else {
            HttpUtil.alertAndRedirect(response, "/cms/discord/reague/list", "처리 실패하였습니다.", null);
        }
    }
}
