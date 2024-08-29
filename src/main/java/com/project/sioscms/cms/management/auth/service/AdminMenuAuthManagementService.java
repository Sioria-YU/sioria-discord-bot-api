package com.project.sioscms.cms.management.auth.service;

import com.project.sioscms.apps.admin.domain.dto.AdminAuthDto;
import com.project.sioscms.apps.admin.domain.dto.AdminMenuAuthDto;
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
public class AdminMenuAuthManagementService {
    private final AdminAuthRepository adminAuthRepository;

    public SiosPage<AdminAuthDto.Response> getAdminAuthList(AdminMenuAuthDto.Request requestDto){
        ChangSolJpaRestriction restriction = new ChangSolJpaRestriction();
        restriction.equals("isDeleted", false);

        return new SiosPage<>(adminAuthRepository.findAll(restriction.toSpecification()
                        , requestDto.toPageableWithSortedByCreatedDateTime(Sort.Direction.DESC))
                .map(AdminAuth::toResponse)
                , requestDto.getPageSize());
    }


}
