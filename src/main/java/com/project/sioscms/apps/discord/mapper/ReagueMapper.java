package com.project.sioscms.apps.discord.mapper;

import com.project.sioscms.apps.discord.domain.dto.ReagueDto;
import com.project.sioscms.apps.discord.domain.entity.Reague;
import com.project.sioscms.common.mapper.CommonEntityMapper;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ReagueMapper extends CommonEntityMapper<Reague, ReagueDto.Request, ReagueDto.Response> {
    ReagueMapper mapper = Mappers.getMapper(ReagueMapper.class);
}
