package com.project.sioscms.apps.admin.mapper;

import com.project.sioscms.apps.admin.domain.dto.AdminAuthDto;
import com.project.sioscms.apps.admin.domain.entity.AdminAuth;
import com.project.sioscms.common.mapper.CommonEntityMapper;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AdminAuthMapper extends CommonEntityMapper<AdminAuth, AdminAuthDto.Request, AdminAuthDto.Response> {
    AdminAuthMapper mapper = Mappers.getMapper(AdminAuthMapper.class);
}
