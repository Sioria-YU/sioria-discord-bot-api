package com.project.sioscms.cms.management.auth.service;

import com.project.sioscms.apps.admin.domain.dto.AdminAuthDto;
import com.project.sioscms.apps.admin.domain.entity.AdminAuth;
import com.project.sioscms.apps.admin.domain.repository.AdminAuthRepository;
import com.project.sioscms.common.utils.jpa.page.SiosPage;
import com.project.sioscms.common.utils.jpa.restriction.ChangSolJpaRestriction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminAuthManagementService {
    private final AdminAuthRepository adminAuthRepository;

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
