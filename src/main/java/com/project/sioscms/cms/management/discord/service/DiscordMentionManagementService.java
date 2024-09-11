package com.project.sioscms.cms.management.discord.service;

import com.project.sioscms.apps.discord.domain.dto.DiscordMentionDto;
import com.project.sioscms.apps.discord.domain.entity.DiscordMention;
import com.project.sioscms.apps.discord.domain.repository.DiscordMentionRepository;
import com.project.sioscms.common.utils.jpa.page.SiosPage;
import com.project.sioscms.common.utils.jpa.restriction.ChangSolJpaRestriction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class DiscordMentionManagementService {
    private final DiscordMentionRepository discordMentionRepository;

    /**
     * 디스코드 멘션 목록 조회
     * @param requestDto
     * @return
     */
    public SiosPage<DiscordMentionDto.Response> getMentionList(DiscordMentionDto.Request requestDto){
        ChangSolJpaRestriction restriction = new ChangSolJpaRestriction();
        if(!ObjectUtils.isEmpty(requestDto.getRoleName())) {
            restriction.equals("roleName", requestDto.getRoleName());
        }

        return new SiosPage<>(discordMentionRepository.findAll(restriction.toSpecification()
                        , requestDto.toPageableWithSortedByKey("roleName", Sort.Direction.ASC))
                .map(DiscordMention::toResponse));

    }

    public DiscordMentionDto.Response getMention(long id){
        DiscordMention discordMention = discordMentionRepository.findById(id).orElse(null);
        return discordMention == null? null : discordMention.toResponse();
    }
}
