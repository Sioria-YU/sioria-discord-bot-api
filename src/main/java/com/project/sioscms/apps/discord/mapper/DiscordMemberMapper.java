package com.project.sioscms.apps.discord.mapper;

import com.project.sioscms.apps.discord.domain.dto.DiscordMemberDto;
import com.project.sioscms.apps.discord.domain.entity.DiscordMember;
import com.project.sioscms.common.mapper.CommonEntityMapper;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface DiscordMemberMapper extends CommonEntityMapper<DiscordMember, DiscordMemberDto.Request, DiscordMemberDto.Response> {
    DiscordMemberMapper mapper = Mappers.getMapper(DiscordMemberMapper.class);
}
