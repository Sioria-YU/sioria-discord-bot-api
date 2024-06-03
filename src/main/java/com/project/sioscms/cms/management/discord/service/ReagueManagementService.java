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
import java.util.List;
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
    private final ReagueTrackMemberRepository reagueTrackMemberRepository;
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
        if (!ObjectUtils.isEmpty(requestDto.getReagueName())) {
            rs.like("reagueName", "%" + requestDto.getReagueName() + "%");
        }

        if(!ObjectUtils.isEmpty(requestDto.getStartDate()) && !ObjectUtils.isEmpty(requestDto.getEndDate())){
            rs.greaterThanEquals("startDate", requestDto.getStartDate());
            rs.lessThanEquals("endDate", requestDto.getEndDate());
        }else {
            if (!ObjectUtils.isEmpty(requestDto.getStartDate())) {
                rs.greaterThanEquals("startDate", requestDto.getStartDate());
            }

            if (!ObjectUtils.isEmpty(requestDto.getEndDate())) {
                rs.lessThanEquals("endDate", requestDto.getEndDate());
            }
        }

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
                Code code = codeRepository.findByCodeGroup_CodeGroupIdAndCodeId("TRACK", track.getName()).orElse(null);

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

    @Transactional
    public ReagueDto.Response update(ReagueDto.Request requestDto){
        Reague entity =  reagueRepository.findById(requestDto.getId()).orElse(null);

        if(entity != null){
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

            //참여 가능 역할[리스트]
            //기존 역할 삭제 후 재등록
            reagueDiscordMentionRepository.deleteAll(entity.getJoinAceptMentions());
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
            //기존 트랙 삭제 후 재등록 -> 참가자와 오류 발생으로 인해 찾아서 수정으로 바꿔야될듯함
            if(!ObjectUtils.isEmpty(requestDto.getTrackList())) {
                Set<ReagueTrack> reagueTracks = reagueTrackRepository.findAllByReague_Id(requestDto.getId());

                //기존 트랙 중 삭제된게 있다면 우선 제거
                if(!ObjectUtils.isEmpty(reagueTracks)) {
                    //바뀐 데이터 중에 현재 데이터가 없다면 삭제처리
                    if(requestDto.getTrackList().stream().anyMatch(v -> !ObjectUtils.isEmpty(v.getId()))) {
                        List<ReagueTrack> removeTrackList = reagueTracks.stream()
                                .filter(v -> requestDto.getTrackList().stream().filter(r -> !ObjectUtils.isEmpty(r.getId()))
                                        .noneMatch(r -> v.getId().equals(Long.parseLong(r.getId())))).toList();
                        if (!ObjectUtils.isEmpty(removeTrackList)) {
                            //해당 트랙에 참여신청한 모든 카테고리 삭제처리
                            for (ReagueTrack reagueTrack : removeTrackList) {
                                List<ReagueTrackMember> reagueTrackMemberList = reagueTrackMemberRepository.findAllByReagueTrack_Id(reagueTrack.getId());
                                reagueTrackMemberRepository.deleteAll(reagueTrackMemberList);
                                reagueTracks.remove(reagueTrack);
                                reagueTrackRepository.delete(reagueTrack);
                            }
                        }
                    }
                }

                for (ReagueDto.Track track : requestDto.getTrackList()) {
                    Code code = codeRepository.findByCodeGroup_CodeGroupIdAndCodeId("TRACK", track.getName()).orElse(null);

                    if(code != null) {
                        //중복 트랙을 허용할 때, 엔티티구조 변경될 수 있음
                        if(!ObjectUtils.isEmpty(track.getId())){
                            ReagueTrack reagueTrack = reagueTracks.stream().filter(r -> r.getId().equals(Long.parseLong(track.getId()))).findFirst().orElse(null);
                            
                            //null일 수 없지만 확인
                            if(reagueTrack != null){
                                reagueTrack.setTrackCode(code);
                                reagueTrack.setTrackDate(track.getDate());
                            }
                        }else {
                            ReagueTrack reagueTrack = new ReagueTrack();
                            reagueTrack.setTrackCode(code);
                            reagueTrack.setReague(entity);
                            reagueTrack.setTrackDate(track.getDate());
                            reagueTrackRepository.save(reagueTrack);
                            reagueTracks.add(reagueTrack);
                        }
                    }
                }

                entity.setReagueTracks(reagueTracks);
            }

            //참여 카테고리(버튼)[리스트]
            //기존 버튼 삭제 후 재등록
            if(!ObjectUtils.isEmpty(requestDto.getReagueButtonList())) {
                Set<ReagueButton> reagueButtons = reagueButtonRepository.findAllByReague_Id(requestDto.getId());
                if(reagueButtons != null && reagueButtons.size() > 0){
                    //기존 버튼들 중에 없어진 버튼 삭제처리
                    if(requestDto.getReagueButtonList().stream().anyMatch(v -> !ObjectUtils.isEmpty(v.getId()))) {
                        List<ReagueButton> deleteButtons = reagueButtons.stream().filter(v -> requestDto.getReagueButtonList().stream()
                                        .filter(r -> !ObjectUtils.isEmpty(r.getId())).noneMatch(r -> v.getId().equals(Long.parseLong(r.getId()))))
                                .toList();
                        if (!ObjectUtils.isEmpty(deleteButtons)) {
                            deleteButtons.forEach(reagueButtons::remove);
                            reagueButtonRepository.deleteAll(deleteButtons);
                        }
                    }

                    for (ReagueDto.RequestReagueButton requestReagueButton : requestDto.getReagueButtonList()) {
                        //기존 등록 버튼일 경우
                        if(!ObjectUtils.isEmpty(requestReagueButton.getId())){
                            ReagueButton reagueButton = reagueButtonRepository.findById(Long.parseLong(requestReagueButton.getId())).orElse(null);
                            if(reagueButton != null){
                                reagueButton.setButtonName(requestReagueButton.getName());
                                reagueButton.setButtonType(requestReagueButton.getType());
                                reagueButtonRepository.save(reagueButton);
                            }
                        }//신규 등록 버튼일 경우
                        else{
                            ReagueButton reagueButton = new ReagueButton();
                            reagueButton.setButtonName(requestReagueButton.getName());
                            reagueButton.setButtonType(requestReagueButton.getType());
                            reagueButton.setReague(entity);
                            reagueButtonRepository.save(reagueButton);
                            reagueButtons.add(reagueButton);
                        }


                    }
                }else {
                    reagueButtons = new HashSet<>();
                    for (ReagueDto.RequestReagueButton requestReagueButton : requestDto.getReagueButtonList()) {
                        ReagueButton reagueButton = new ReagueButton();
                        reagueButton.setButtonName(requestReagueButton.getName());
                        reagueButton.setButtonType(requestReagueButton.getType());
                        reagueButton.setReague(entity);
                        reagueButtonRepository.save(reagueButton);
                        reagueButtons.add(reagueButton);
                    }
                }
                entity.setReagueButtons(reagueButtons);
            }

            reagueRepository.flush();
            return entity.toResponse();
        }
        return null;
    }

    @Transactional
    public boolean delete(long id){
        Reague reague = reagueRepository.findById(id).orElse(null);
        if(reague == null){
            return false;
        }else{
            reague.setIsDeleted(true);
            return true;
        }
    }
}
