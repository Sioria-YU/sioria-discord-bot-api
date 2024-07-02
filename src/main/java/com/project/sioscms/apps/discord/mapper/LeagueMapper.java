package com.project.sioscms.apps.discord.mapper;

import com.project.sioscms.apps.discord.domain.dto.LeagueDto;
import com.project.sioscms.apps.discord.domain.entity.League;
import com.project.sioscms.common.mapper.CommonEntityMapper;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface LeagueMapper extends CommonEntityMapper<League, LeagueDto.Request, LeagueDto.Response> {
    LeagueMapper mapper = Mappers.getMapper(LeagueMapper.class);
}
