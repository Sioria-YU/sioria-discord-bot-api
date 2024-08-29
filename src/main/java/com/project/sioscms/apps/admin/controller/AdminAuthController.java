package com.project.sioscms.apps.admin.controller;

import com.project.sioscms.apps.admin.domain.dto.AdminAuthDto;
import com.project.sioscms.apps.admin.service.AdminAuthService;
import com.project.sioscms.secure.domain.Auth;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/cms/api/admin-auth")
@RequiredArgsConstructor
public class AdminAuthController {
    private final AdminAuthService adminAuthService;

    @Auth(role = Auth.Role.ADMIN)
    @GetMapping("/{id}")
    public ResponseEntity<AdminAuthDto.Response> getAdminAuth(@PathVariable("id") Long id){
        return ResponseEntity.ok(adminAuthService.getAdminAuth(id));
    }

}
