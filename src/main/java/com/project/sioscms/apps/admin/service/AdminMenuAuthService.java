package com.project.sioscms.apps.admin.service;

import com.project.sioscms.apps.admin.domain.dto.AdminMenuAuthDto;
import com.project.sioscms.apps.admin.domain.entity.AdminMenuAuth;
import com.project.sioscms.apps.admin.domain.repository.AdminMenuAuthRepository;
import com.project.sioscms.common.utils.jpa.restriction.ChangSolJpaRestriction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminMenuAuthService {
    private final AdminMenuAuthRepository adminMenuAuthRepository;

    public List<AdminMenuAuthDto.Response> getAdminMenuAuthList(long adminAuthId){
        ChangSolJpaRestriction restriction = new ChangSolJpaRestriction();
        restriction.equals("adminAuth.id", adminAuthId);
        restriction.equals("menu.isRoot", false);
        restriction.equals("menu.isDeleted", false);

        return adminMenuAuthRepository.findAll(restriction.toSpecification(), Sort.by(Sort.Direction.ASC, "menu.orderNum"))
                .stream().map(AdminMenuAuth::toResponse).toList();
    }
}
