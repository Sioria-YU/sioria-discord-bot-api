package com.project.sioscms.apps.admin.mapper;

import com.project.sioscms.apps.admin.domain.dto.AdminMenuAuthDto;
import com.project.sioscms.apps.admin.domain.entity.AdminMenuAuth;
import com.project.sioscms.common.mapper.CommonEntityMapper;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AdminMenuAuthMapper extends CommonEntityMapper<AdminMenuAuth, AdminMenuAuthDto.Request, AdminMenuAuthDto.Response> {
    AdminMenuAuthMapper mapper = Mappers.getMapper(AdminMenuAuthMapper.class);
}
