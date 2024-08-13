package com.project.sioscms.apps.discord.mapper;

import com.project.sioscms.apps.discord.domain.dto.DiscordPenaltyDto;
import com.project.sioscms.apps.discord.domain.entity.DiscordPenalty;
import com.project.sioscms.common.mapper.CommonEntityMapper;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface DiscordPenaltyMapper extends CommonEntityMapper<DiscordPenalty, DiscordPenaltyDto.Request, DiscordPenaltyDto.Response> {
    DiscordPenaltyMapper mapper = Mappers.getMapper(DiscordPenaltyMapper.class);
}
