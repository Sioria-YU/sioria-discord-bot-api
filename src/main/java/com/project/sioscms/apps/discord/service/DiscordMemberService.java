package com.project.sioscms.apps.discord.service;

import com.project.sioscms.apps.discord.domain.dto.DiscordMemberDto;
import com.project.sioscms.apps.discord.domain.entity.DiscordMember;
import com.project.sioscms.apps.discord.domain.repository.DiscordMemberRepository;
import com.project.sioscms.common.utils.jpa.restriction.ChangSolJpaRestriction;
import com.project.sioscms.common.utils.jpa.restriction.ChangSolJpaRestrictionType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DiscordMemberService {
    private final DiscordMemberRepository discordMemberRepository;

    public List<DiscordMemberDto.Response> getDiscordMemberList(String username){
        ChangSolJpaRestriction rs = new ChangSolJpaRestriction();
        rs.equals("isDeleted", false);

        if(!ObjectUtils.isEmpty(username)){
            ChangSolJpaRestriction rs2 = new ChangSolJpaRestriction(ChangSolJpaRestrictionType.OR);
            rs2.iLike("username", "%" + username + "%");
            rs2.iLike("nickname", "%" + username + "%");
            rs2.iLike("globalName", "%" + username + "%");
            rs.addChild(rs2);
        }

        return discordMemberRepository.findAll(rs.toSpecification(), Sort.by(Sort.Direction.ASC, "username"))
                .stream().map(DiscordMember::toResponse).toList();
    }
}
