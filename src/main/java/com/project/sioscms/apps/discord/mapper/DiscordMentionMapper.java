package com.project.sioscms.apps.discord.mapper;

import com.project.sioscms.apps.discord.domain.dto.DiscordMentionDto;
import com.project.sioscms.apps.discord.domain.entity.DiscordMention;
import com.project.sioscms.common.mapper.CommonEntityMapper;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface DiscordMentionMapper extends CommonEntityMapper<DiscordMention, DiscordMentionDto.Request, DiscordMentionDto.Response> {
    DiscordMentionMapper mapper = Mappers.getMapper(DiscordMentionMapper.class);

}