package com.project.sioscms.apps.admin.domain.repository;

import com.project.sioscms.apps.admin.domain.entity.AdminAuth;
import com.project.sioscms.common.domain.repository.CommonJpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminAuthRepository extends CommonJpaRepository<AdminAuth, Long> {
}
