package com.project.sioscms.cms.management.auth.controller;

import com.project.sioscms.apps.admin.domain.dto.AdminAuthDto;
import com.project.sioscms.cms.management.auth.service.AdminAuthManagementService;
import com.project.sioscms.common.utils.common.http.HttpUtil;
import com.project.sioscms.common.utils.jpa.page.SiosPage;
import com.project.sioscms.secure.domain.Auth;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;

@Slf4j
@Controller
@RequestMapping("/cms/admin-auth")
@RequiredArgsConstructor
public class AdminAuthManagementController {
    private final AdminAuthManagementService adminAuthManagementService;

    @Auth(role = Auth.Role.ADMIN)
    @RequestMapping("/list")
    public ModelAndView list(AdminAuthDto.Request requestDto){
        ModelAndView mav = new ModelAndView("cms/admin/auth/list");

        SiosPage<AdminAuthDto.Response> siosPage = adminAuthManagementService.getAdminAuthList(requestDto);

        if(siosPage != null && !siosPage.isEmpty()){
            mav.addObject("resultList", siosPage.getContents());
            mav.addObject("pageInfo", siosPage.getPageInfo());
        }

        return mav;
    }

    @Auth(role = Auth.Role.ADMIN)
    @RequestMapping("/save")
    public void save(HttpServletResponse response, AdminAuthDto.Request requestDto){
        String returnMessage = "";
        if(adminAuthManagementService.save(requestDto) != null) {
            returnMessage = "정상 처리 되었습니다.";
        }else{
            returnMessage = "처리 중 오류가 발생하였습니다.";
        }

        HttpUtil.alertAndRedirect(response, "/cms/admin-auth/list", returnMessage, null);
    }

    @Auth(role = Auth.Role.ADMIN)
    @RequestMapping("/update")
    public void update(HttpServletResponse response, AdminAuthDto.Request requestDto){
        String returnMessage = "";

        if(adminAuthManagementService.update(requestDto) != null) {
            returnMessage = "정상 처리 되었습니다.";
        }else{
            returnMessage = "처리 중 오류가 발생하였습니다.";
        }

        HttpUtil.alertAndRedirect(response, "/cms/admin-auth/list", returnMessage, null);
    }
}
