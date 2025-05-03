package com.project.sioscms.apps.discord.mapper;

import com.project.sioscms.apps.discord.domain.dto.LeagueTrackDto;
import com.project.sioscms.apps.discord.domain.entity.LeagueTrack;
import com.project.sioscms.common.mapper.CommonEntityMapper;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface LeagueTrackMapper extends CommonEntityMapper<LeagueTrack, LeagueTrackDto.Request, LeagueTrackDto.Response> {
    LeagueTrackMapper mapper = Mappers.getMapper(LeagueTrackMapper.class);
}
