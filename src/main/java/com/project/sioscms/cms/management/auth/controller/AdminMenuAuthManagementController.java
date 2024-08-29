package com.project.sioscms.cms.management.auth.controller;

import com.project.sioscms.apps.admin.domain.dto.AdminAuthDto;
import com.project.sioscms.apps.admin.domain.dto.AdminMenuAuthDto;
import com.project.sioscms.cms.management.auth.service.AdminMenuAuthManagementService;
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
@RequestMapping("/cms/admin-menu-auth")
@RequiredArgsConstructor
public class AdminMenuAuthManagementController {
    private final AdminMenuAuthManagementService adminMenuAuthManagementService;

    @Auth(role = Auth.Role.ADMIN)
    @RequestMapping("/list")
    public ModelAndView list(AdminMenuAuthDto.Request requestDto){
        ModelAndView mav = new ModelAndView("cms/admin/menu/auth/list");

        SiosPage<AdminAuthDto.Response> siosPage = adminMenuAuthManagementService.getAdminAuthList(requestDto);

        if(siosPage != null && !siosPage.isEmpty()){
            mav.addObject("resultList", siosPage.getContents());
            mav.addObject("pageInfo", siosPage.getPageInfo());
        }

        return mav;
    }

    @Auth(role = Auth.Role.ADMIN)
    @RequestMapping("/view/{id}")
    public ModelAndView view(@PathVariable("id") Long id){
        ModelAndView mav = new ModelAndView("cms/admin/menu/auth/view");
        return mav;
    }

}
