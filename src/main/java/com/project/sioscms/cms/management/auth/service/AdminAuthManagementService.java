package com.project.sioscms.cms.management.auth.service;

import com.project.sioscms.apps.admin.domain.dto.AdminAuthDto;
import com.project.sioscms.apps.admin.domain.entity.AdminAuth;
import com.project.sioscms.apps.admin.domain.entity.AdminMenuAuth;
import com.project.sioscms.apps.admin.domain.repository.AdminAuthRepository;
import com.project.sioscms.apps.admin.domain.repository.AdminMenuAuthRepository;
import com.project.sioscms.apps.menu.domain.entity.Menu;
import com.project.sioscms.apps.menu.domain.repository.MenuRepository;
import com.project.sioscms.common.utils.jpa.page.SiosPage;
import com.project.sioscms.common.utils.jpa.restriction.ChangSolJpaRestriction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminAuthManagementService {
    private final AdminAuthRepository adminAuthRepository;
    private final MenuRepository menuRepository;
    private final AdminMenuAuthRepository adminMenuAuthRepository;

    public SiosPage<AdminAuthDto.Response> getAdminAuthList(AdminAuthDto.Request requestDto){
        ChangSolJpaRestriction restriction = new ChangSolJpaRestriction();
        restriction.equals("isDeleted", false);

        return new SiosPage<>(adminAuthRepository.findAll(restriction.toSpecification()
                , requestDto.toPageableWithSortedByCreatedDateTime(Sort.Direction.DESC))
                .map(AdminAuth::toResponse)
                , requestDto.getPageSize());
    }

    @Transactional
    public AdminAuthDto.Response save(AdminAuthDto.Request requestDto){
        AdminAuth entity = new AdminAuth();
        entity.setIsDeleted(false);
        entity.setName(requestDto.getName());
        entity.setNotice(requestDto.getNotice());

        adminAuthRepository.save(entity);

        //권한이 추가되면 모든 메뉴에 대한 메뉴권한을 생성해준다.
        Set<Menu> menus = menuRepository.findAllByIsDeletedOrderByOrderNumAsc(false);
        for (Menu menu : menus) {
            AdminMenuAuth adminMenuAuth = new AdminMenuAuth();
            adminMenuAuth.setMenu(menu);
            adminMenuAuth.setAdminAuth(entity);
            adminMenuAuth.setIsSelect(true);
            adminMenuAuth.setIsInsert(true);
            adminMenuAuth.setIsUpdate(true);
            adminMenuAuth.setIsDelete(true);

            adminMenuAuthRepository.save(adminMenuAuth);
        }

        return entity.toResponse();
    }

    @Transactional
    public AdminAuthDto.Response update(AdminAuthDto.Request requestDto){
        AdminAuth entity = adminAuthRepository.findById(requestDto.getId()).orElse(null);
        if(entity == null){
            return null;
        }else{
            entity.setName(requestDto.getName());
            entity.setNotice(requestDto.getNotice());
            return entity.toResponse();
        }
    }
}
