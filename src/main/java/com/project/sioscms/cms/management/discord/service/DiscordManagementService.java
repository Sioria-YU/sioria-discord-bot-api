package com.project.sioscms.cms.management.discord.service;

import com.project.sioscms.apps.discord.domain.dto.DiscordMemberDto;
import com.project.sioscms.apps.discord.domain.entity.DiscordMember;
import com.project.sioscms.apps.discord.domain.entity.DiscordUserMension;
import com.project.sioscms.apps.discord.domain.repository.DiscordMemberRepository;
import com.project.sioscms.apps.discord.domain.repository.DiscordUserMensionRepository;
import com.project.sioscms.common.utils.jpa.page.SiosPage;
import com.project.sioscms.common.utils.jpa.restriction.ChangSolJpaRestriction;
import com.project.sioscms.common.utils.jpa.restriction.ChangSolJpaRestrictionType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import javax.persistence.criteria.JoinType;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DiscordManagementService {
    @Value("${discord.guild-key}")
    private String GUILD_KEY;

    private final DiscordMemberRepository discordMemberRepository;
    private final DiscordUserMensionRepository discordUserMensionRepository;

    public SiosPage<DiscordMemberDto.Response> getDiscordMembers(DiscordMemberDto.Request requestDto){
        ChangSolJpaRestriction rs = new ChangSolJpaRestriction();
        rs.equals("isDeleted", false);

        if(!ObjectUtils.isEmpty(requestDto.getUsername())){
            ChangSolJpaRestriction rs2 = new ChangSolJpaRestriction(ChangSolJpaRestrictionType.OR);
            rs2.iLike("username", "%" + requestDto.getUsername().toUpperCase() + "%");
            rs2.iLike("globalName", "%" + requestDto.getUsername().toUpperCase() + "%");
            rs.addChild(rs2);
        }

        if(!ObjectUtils.isEmpty(requestDto.getDiscordUserMension())){
            List<DiscordUserMension> discordUserMensions = discordUserMensionRepository.findAllByDiscordMention_RoleId(requestDto.getDiscordUserMension());
            if(discordUserMensions != null && discordUserMensions.size() > 0) {
                List<Long> ids = discordUserMensions.stream().map(v -> v.getDiscordMember().getId()).toList();
                rs.in("id", ids);
            }else{
                //결과값 없앰
                return null;
            }
        }

        return new SiosPage<>(discordMemberRepository.findAll(rs.toSpecification()
                        , requestDto.toPageableWithSortedByKey("username", Sort.Direction.ASC)).map(DiscordMember::toResponse)
                , requestDto.getPageSize());
    }
}
