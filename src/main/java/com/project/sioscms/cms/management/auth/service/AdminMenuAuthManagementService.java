package com.project.sioscms.cms.management.auth.service;

import com.project.sioscms.apps.admin.domain.dto.AdminAuthDto;
import com.project.sioscms.apps.admin.domain.dto.AdminMenuAuthDto;
import com.project.sioscms.apps.admin.domain.dto.AdminMenuAuthListDto;
import com.project.sioscms.apps.admin.domain.entity.AdminAuth;
import com.project.sioscms.apps.admin.domain.entity.AdminMenuAuth;
import com.project.sioscms.apps.admin.domain.repository.AdminAuthRepository;
import com.project.sioscms.apps.admin.domain.repository.AdminMenuAuthRepository;
import com.project.sioscms.common.utils.jpa.page.SiosPage;
import com.project.sioscms.common.utils.jpa.restriction.ChangSolJpaRestriction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminMenuAuthManagementService {
    private final AdminAuthRepository adminAuthRepository;
    private final AdminMenuAuthRepository adminMenuAuthRepository;

    public SiosPage<AdminAuthDto.Response> getAdminAuthList(AdminMenuAuthDto.Request requestDto){
        ChangSolJpaRestriction restriction = new ChangSolJpaRestriction();
        restriction.equals("isDeleted", false);

        return new SiosPage<>(adminAuthRepository.findAll(restriction.toSpecification()
                        , requestDto.toPageableWithSortedByCreatedDateTime(Sort.Direction.DESC))
                .map(AdminAuth::toResponse)
                , requestDto.getPageSize());
    }

    @Transactional
    public boolean update(long adminAuthId, List<AdminMenuAuthDto.Request> requestList){
        if(ObjectUtils.isEmpty(adminAuthId) || ObjectUtils.isEmpty(requestList)){
            return false;
        }

        try {
            for (AdminMenuAuthDto.Request request : requestList) {
                AdminMenuAuth adminMenuAuth = adminMenuAuthRepository.findById(request.getId()).orElseThrow(NullPointerException::new);
                adminMenuAuth.setIsSelect(request.getIsSelect());
                adminMenuAuth.setIsInsert(request.getIsInsert());
                adminMenuAuth.setIsUpdate(request.getIsUpdate());
                adminMenuAuth.setIsDelete(request.getIsDelete());
            }
            return true;
        }catch (NullPointerException ne){
            log.error(ne.getMessage());
            return false;
        }
    }


}
