package com.project.sioscms.cms.management.discord.service;

import com.project.sioscms.apps.attach.domain.repository.AttachFileGroupRepository;
import com.project.sioscms.apps.code.domain.entity.Code;
import com.project.sioscms.apps.code.domain.repository.CodeRepository;
import com.project.sioscms.apps.discord.domain.dto.ReagueDto;
import com.project.sioscms.apps.discord.domain.entity.*;
import com.project.sioscms.apps.discord.domain.repository.*;
import com.project.sioscms.common.utils.jpa.page.SiosPage;
import com.project.sioscms.common.utils.jpa.restriction.ChangSolJpaRestriction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReagueManagementService {
    private final ReagueRepository reagueRepository;
    private final AttachFileGroupRepository attachFileGroupRepository;
    private final DiscordMentionRepository discordMentionRepository;
    private final ReagueDiscordMentionRepository reagueDiscordMentionRepository;
    private final ReagueTrackRepository reagueTrackRepository;
    private final CodeRepository codeRepository;
    private final ReagueButtonRepository reagueButtonRepository;

    /**
     * 리그 목록 조회
     * @param requestDto
     * @return
     */
    public SiosPage<ReagueDto.Response> getReagues(ReagueDto.Request requestDto){
        ChangSolJpaRestriction rs = new ChangSolJpaRestriction();
        rs.equals("isDeleted", false);

        return new SiosPage<>(reagueRepository.findAll(rs.toSpecification(), requestDto.toPageableWithSortedByCreatedDateTime(Sort.Direction.DESC))
                .map(Reague::toResponse)
                , requestDto.getPageSize());
    }

    /**
     * 리그 상세 조회
     * @param id
     * @return
     */
    public ReagueDto.Response getReague(Long id){
        return Objects.requireNonNull(reagueRepository.findById(id).orElse(null)).toResponse();
    }

    /**
     * 새로운 리그 저장
     * @param requestDto
     * @return
     */
    @Transactional
    public ReagueDto.Response save(ReagueDto.Request requestDto){
        Reague entity = new Reague();
        entity.setReagueName(requestDto.getReagueName());
        entity.setTitle(requestDto.getTitle());
        entity.setDescription(requestDto.getDescription());
        entity.setColor(requestDto.getColor());
        entity.setStartDate(requestDto.getStartDate());
        entity.setEndDate(requestDto.getEndDate());
        entity.setReagueTime(requestDto.getReagueTime());
        entity.setNoticeChannelId(requestDto.getNoticeChannelId());
        entity.setNoticeTime(requestDto.getNoticeTime());
        entity.setJoinMemberLimit(requestDto.getJoinMemberLimit());
        entity.setIsDeleted(false);

        //참여 가능 역할[리스트]
        if(!ObjectUtils.isEmpty(requestDto.getJoinAceptMentionList())) {
            Set<ReagueDiscordMention> joinAceptMentions = new HashSet<>();
            for (String joinAceptMention : requestDto.getJoinAceptMentionList()) {
                DiscordMention discordMention = discordMentionRepository.findByRoleId(joinAceptMention).orElse(null);
                if(discordMention != null) {
                    ReagueDiscordMention reagueDiscordMention = new ReagueDiscordMention();
                    reagueDiscordMention.setReague(entity);
                    reagueDiscordMention.setDiscordMention(discordMention);
                    reagueDiscordMentionRepository.save(reagueDiscordMention);
                    joinAceptMentions.add(reagueDiscordMention);
                }
            }
            entity.setJoinAceptMentions(joinAceptMentions);
        }

        //트랙[리스트]
        if(!ObjectUtils.isEmpty(requestDto.getTrackList())) {
            Set<ReagueTrack> reagueTracks = new HashSet<>();
            for (ReagueDto.Track track : requestDto.getTrackList()) {
                Code code = codeRepository.findByCodeGroup_CodeGroupIdAndCodeLabel("TRACK", track.getName()).orElse(null);

                if(code != null) {
                    ReagueTrack reagueTrack = new ReagueTrack();
                    reagueTrack.setTrackCode(code);
                    reagueTrack.setReague(entity);
                    reagueTrack.setTrackDate(track.getDate());
                    reagueTrackRepository.save(reagueTrack);
                    reagueTracks.add(reagueTrack);
                }
            }
            entity.setReagueTracks(reagueTracks);
        }

        //참여 카테고리(버튼)[리스트]
        if(!ObjectUtils.isEmpty(requestDto.getReagueButtonList())) {
            Set<ReagueButton> reagueButtons = new HashSet<>();
            for (ReagueDto.RequestReagueButton requestReagueButton : requestDto.getReagueButtonList()) {
                ReagueButton reagueButton = new ReagueButton();
                reagueButton.setButtonName(requestReagueButton.getName());
                reagueButton.setButtonType(requestReagueButton.getType());
                reagueButton.setReague(entity);
                reagueButtonRepository.save(reagueButton);
                reagueButtons.add(reagueButton);
            }
            entity.setReagueButtons(reagueButtons);
        }

        if(requestDto.getAttachFileGroupId() != null){
            attachFileGroupRepository.findById(requestDto.getAttachFileGroupId()).ifPresent(entity::setAttachFileGroup);
        }
        reagueRepository.save(entity);
        return entity.toResponse();
    }
}
