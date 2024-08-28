package com.project.sioscms.cms.management.auth.controller;

import com.project.sioscms.apps.admin.domain.dto.AdminMenuAuthDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Slf4j
@Controller
@RequestMapping("/cms/admin-menu-auth")
public class AdminMenuAuthManagementController {

    @RequestMapping("/list")
    public ModelAndView list(AdminMenuAuthDto.Request requestDto){
        ModelAndView mav = new ModelAndView("cms/admin/menu/auth/list");
        return mav;
    }

}
