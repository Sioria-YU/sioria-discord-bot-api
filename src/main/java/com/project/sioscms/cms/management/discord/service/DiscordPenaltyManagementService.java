package com.project.sioscms.cms.management.discord.service;

import com.project.sioscms.apps.discord.domain.dto.DiscordPenaltyDto;
import com.project.sioscms.apps.discord.domain.entity.DiscordPenalty;
import com.project.sioscms.apps.discord.domain.repository.DiscordPenaltyRepository;
import com.project.sioscms.common.utils.jpa.page.SiosPage;
import com.project.sioscms.common.utils.jpa.restriction.ChangSolJpaRestriction;
import com.project.sioscms.common.utils.jpa.restriction.ChangSolJpaRestrictionType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DiscordPenaltyManagementService {
    private final DiscordPenaltyRepository discordPenaltyRepository;

    public SiosPage<DiscordPenaltyDto.Response> getList(DiscordPenaltyDto.Request requestDto){
        ChangSolJpaRestriction rs = new ChangSolJpaRestriction();
        rs.equals("isDeleted", false);

        //검색조건
        if (!ObjectUtils.isEmpty(requestDto.getLeagueName())) {
            rs.like("league.leagueName", "%" + requestDto.getLeagueName() + "%");
        }

        if (!ObjectUtils.isEmpty(requestDto.getUsername())) {
            ChangSolJpaRestriction rs2 = new ChangSolJpaRestriction(ChangSolJpaRestrictionType.OR);
            rs2.iLike("discordMember.username", "%" + requestDto.getUsername() + "%");
            rs2.iLike("discordMember.nickname", "%" + requestDto.getUsername() + "%");
            rs2.iLike("discordMember.globalName", "%" + requestDto.getUsername() + "%");
            rs.addChild(rs2);
        }

        if(!ObjectUtils.isEmpty(requestDto.getStartDate()) && !ObjectUtils.isEmpty(requestDto.getEndDate())){
            rs.greaterThanEquals("applyDate", requestDto.getStartDate());
            rs.lessThanEquals("applyDate", requestDto.getEndDate());
        }else {
            if (!ObjectUtils.isEmpty(requestDto.getStartDate())) {
                rs.greaterThanEquals("applyDate", requestDto.getStartDate());
            }

            if (!ObjectUtils.isEmpty(requestDto.getEndDate())) {
                rs.lessThanEquals("applyDate", requestDto.getEndDate());
            }
        }

        return new SiosPage<>(discordPenaltyRepository.findAll(rs.toSpecification(), requestDto.toPageableWithSortedByCreatedDateTime(Sort.Direction.DESC))
                .map(DiscordPenalty::toResponse)
                , requestDto.getPageSize());
    }
}
