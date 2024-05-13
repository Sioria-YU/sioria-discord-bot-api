package com.project.sioscms.cms.management.discord.service;

import com.project.sioscms.apps.discord.domain.dto.DiscordMemberDto;
import com.project.sioscms.apps.discord.domain.entity.DiscordMember;
import com.project.sioscms.apps.discord.domain.repository.DiscordMemberRepository;
import com.project.sioscms.common.utils.jpa.page.SiosPage;
import com.project.sioscms.common.utils.jpa.restriction.ChangSolJpaRestriction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DiscordManagementService {
    private final DiscordMemberRepository discordMemberRepository;

    public SiosPage<DiscordMemberDto.Response> getDiscordMembers(DiscordMemberDto.Request requestDto){
        ChangSolJpaRestriction rs = new ChangSolJpaRestriction();
        rs.equals("isDeleted", false);

        if(!ObjectUtils.isEmpty(requestDto.getUsername())){
            rs.equals("username", requestDto.getUsername());
        }

        return new SiosPage<>(discordMemberRepository.findAll(rs.toSpecification()
                , requestDto.toPageableWithSortedByKey("username", Sort.Direction.ASC)).map(DiscordMember::toResponse)
                , requestDto.getPageSize());
    }
}
