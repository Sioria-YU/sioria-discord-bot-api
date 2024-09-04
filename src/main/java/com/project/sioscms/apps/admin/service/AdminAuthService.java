package com.project.sioscms.apps.admin.service;

import com.project.sioscms.apps.admin.domain.dto.AdminAuthDto;
import com.project.sioscms.apps.admin.domain.entity.AdminAuth;
import com.project.sioscms.apps.admin.domain.repository.AdminAuthRepository;
import lombok.NonNull;
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
public class AdminAuthService {
    private final AdminAuthRepository adminAuthRepository;

    public List<AdminAuthDto.Response> getAdminAuthList(){
        return adminAuthRepository.findAll(Sort.by(Sort.Direction.ASC, "id")).stream().map(AdminAuth::toResponse).toList();
    }

    public AdminAuthDto.Response getAdminAuth(@NonNull Long id){
        AdminAuth adminAuth = adminAuthRepository.findById(id).orElse(null);
        return  adminAuth == null? null : adminAuth.toResponse();
    }

}
