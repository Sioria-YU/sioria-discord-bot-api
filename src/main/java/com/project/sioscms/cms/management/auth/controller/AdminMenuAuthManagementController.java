package com.project.sioscms.cms.management.auth.controller;

import com.project.sioscms.apps.admin.domain.dto.AdminAuthDto;
import com.project.sioscms.apps.admin.domain.dto.AdminMenuAuthDto;
import com.project.sioscms.apps.admin.domain.dto.AdminMenuAuthListDto;
import com.project.sioscms.apps.admin.service.AdminAuthService;
import com.project.sioscms.apps.admin.service.AdminMenuAuthService;
import com.project.sioscms.apps.menu.domain.dto.MenuDto;
import com.project.sioscms.apps.menu.service.MenuService;
import com.project.sioscms.cms.management.auth.service.AdminMenuAuthManagementService;
import com.project.sioscms.common.utils.common.http.HttpUtil;
import com.project.sioscms.common.utils.jpa.page.SiosPage;
import com.project.sioscms.secure.domain.Auth;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/cms/admin-menu-auth")
@RequiredArgsConstructor
public class AdminMenuAuthManagementController {
    private final AdminMenuAuthManagementService adminMenuAuthManagementService;
    private final AdminAuthService adminAuthService;
    private final AdminMenuAuthService adminMenuAuthService;
    private final MenuService menuService;

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

        //선택한 관리자 권한
        AdminAuthDto.Response adminAuth = adminAuthService.getAdminAuth(id);
        mav.addObject("adminAuth", adminAuth);

        //전체 메뉴 목록
//        MenuDto.Request menuDto = new MenuDto.Request();
//        menuDto.setIsRoot(false);
//
//        List<MenuDto.Response> menuList = menuService.getMenuList(menuDto);
//        mav.addObject("menuList", menuList);

        //메뉴 권한
        List<AdminMenuAuthDto.Response> adminMenuAuthList = adminMenuAuthService.getAdminMenuAuthList(adminAuth.getId());
        mav.addObject("adminMenuAuthList", adminMenuAuthList);

        return mav;
    }

    @Auth(role = Auth.Role.ADMIN)
    @RequestMapping("/update")
    public void update (HttpServletResponse response, AdminMenuAuthListDto.Request requestDto){
        String returnMessage = "";

        if(adminMenuAuthManagementService.update(requestDto.getAdminAuthId(), requestDto.getAdminMenuAuthList())) {
            returnMessage = "정상 처리 되었습니다.";
        }else{
            returnMessage = "처리 중 오류가 발생하였습니다.";
        }

        HttpUtil.alertAndRedirect(response, "/cms/admin-menu-auth/list", returnMessage, null);
    }

}
