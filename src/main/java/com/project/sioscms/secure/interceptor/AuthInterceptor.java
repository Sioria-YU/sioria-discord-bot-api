package com.project.sioscms.secure.interceptor;

import com.project.sioscms.apps.account.domain.entity.Account;
import com.project.sioscms.apps.admin.domain.dto.AdminMenuAuthDto;
import com.project.sioscms.apps.admin.domain.entity.AdminMenuAuth;
import com.project.sioscms.apps.admin.service.AdminMenuAuthService;
import com.project.sioscms.apps.menu.service.MenuService;
import com.project.sioscms.secure.domain.Auth;
import com.project.sioscms.secure.domain.UserAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.security.auth.message.AuthException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Component
public class AuthInterceptor implements HandlerInterceptor {
    private final ApplicationContext context;

    @Autowired
    public AuthInterceptor(ApplicationContext context){
        this.context = context;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if(!(handler instanceof HandlerMethod)) {
            return true;
        }
        HandlerMethod handlerMethod = (HandlerMethod) handler;

        Auth auth = handlerMethod.getMethodAnnotation(Auth.class);
        if(auth == null){
            return true;
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication.getPrincipal().equals("anonymousUser")){
            throw new AuthException("로그인 정보가 없습니다.");
        }

        UserAccount userAccount = (UserAccount) authentication.getPrincipal();

        //관리자 권한 체크
        if(auth.role().compareTo(Auth.Role.ADMIN) == 0){
            if(userAccount == null){
                throw new AuthException("로그인 정보가 없습니다.");
            }else if(userAccount.getAccount().getRole().compareTo(Account.Role_Type.ADMIN) < 0){
                throw new AccessDeniedException("접근 권한이 없습니다.");
            }else {
                //메뉴 접근 권한 체크
                List<AdminMenuAuthDto.Response> adminMenuAuthList = userAccount.getAdminMenuAuthList();
                List<AdminMenuAuthDto.Response> filters = adminMenuAuthList.stream().filter(m -> m.getMenu().getMenuLink().contains(request.getRequestURI())).toList();

                if(filters != null && filters.size() > 0){
                    boolean checked = false;
                    for (AdminMenuAuthDto.Response filter : filters) {
                        if(filter.getIsSelect()){
                            checked = true;
                            break;
                        }
                    }
                    if(!checked){
                        throw new AccessDeniedException("접근 권한이 없습니다.");
                    }
                }else{
                    //메뉴가 맞는지 체크
                    MenuService menuService = context.getBean(MenuService.class);
                    if(!menuService.getMenuListByMenuLink(request.getRequestURI()).isEmpty()) {
                        throw new AccessDeniedException("접근 권한이 없습니다.");
                    }
                }
            }
        }

        //사용자 권한 체크
        if(auth.role().compareTo(Auth.Role.USER) == 0){
            //사용자 권한은 로그인만 했다면 이용할 수 있다.
            if(userAccount == null){
                throw new AuthException("로그인 정보가 없습니다.");
            }
        }

        return true;
    }
}
