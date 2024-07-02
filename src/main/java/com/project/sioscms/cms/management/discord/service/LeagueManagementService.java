package com.project.sioscms.cms.management.discord.service;

import com.project.sioscms.apps.attach.domain.repository.AttachFileGroupRepository;
import com.project.sioscms.apps.code.domain.entity.Code;
import com.project.sioscms.apps.code.domain.repository.CodeRepository;
import com.project.sioscms.apps.discord.domain.dto.LeagueDto;
import com.project.sioscms.apps.discord.domain.entity.*;
import com.project.sioscms.apps.discord.domain.repository.*;
import com.project.sioscms.common.utils.jpa.page.SiosPage;
import com.project.sioscms.common.utils.jpa.restriction.ChangSolJpaRestriction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LeagueManagementService {
    private final LeagueRepository leagueRepository;
    private final AttachFileGroupRepository attachFileGroupRepository;
    private final DiscordMentionRepository discordMentionRepository;
    private final LeagueDiscordMentionRepository leagueDiscordMentionRepository;
    private final LeagueTrackRepository leagueTrackRepository;
    private final LeagueTrackMemberRepository leagueTrackMemberRepository;
    private final CodeRepository codeRepository;
    private final LeagueButtonRepository leagueButtonRepository;

    /**
     * 리그 목록 조회
     * @param requestDto
     * @return
     */
    public SiosPage<LeagueDto.Response> getLeagues(LeagueDto.Request requestDto){
        ChangSolJpaRestriction rs = new ChangSolJpaRestriction();
        rs.equals("isDeleted", false);
        if (!ObjectUtils.isEmpty(requestDto.getLeagueName())) {
            rs.like("leagueName", "%" + requestDto.getLeagueName() + "%");
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

        //정렬 변경 : 1.종료일 내림차순, 2.시작일 오름차순, 3.등록일 내림차순 
        List<Sort.Order> orders = new ArrayList<>();
        Sort.Order order1 = new Sort.Order(Sort.Direction.DESC, "endDate");
        Sort.Order order2 = new Sort.Order(Sort.Direction.ASC, "startDate");
        Sort.Order order3 = new Sort.Order(Sort.Direction.DESC, "createdDateTime");
        orders.add(order1);
        orders.add(order2);
        orders.add(order3);

        return new SiosPage<>(leagueRepository.findAll(rs.toSpecification()
                        , PageRequest.of(requestDto.getPageNumber()
                                , requestDto.getPageOffset()
                                , Sort.by(orders)))
                .map(League::toResponse)
                , requestDto.getPageSize());
    }

    /**
     * 리그 상세 조회
     * @param id
     * @return
     */
    public LeagueDto.Response getLeague(Long id){
        return Objects.requireNonNull(leagueRepository.findById(id).orElse(null)).toResponse();
    }

    /**
     * 새로운 리그 저장
     * @param requestDto
     * @return
     */
    @Transactional
    public LeagueDto.Response save(LeagueDto.Request requestDto){
        League entity = new League();
        entity.setLeagueName(requestDto.getLeagueName());
        entity.setTitle(requestDto.getTitle());
        entity.setDescription(requestDto.getDescription());
        entity.setColor(requestDto.getColor());
        entity.setStartDate(requestDto.getStartDate());
        entity.setEndDate(requestDto.getEndDate());
        entity.setLeagueTime(requestDto.getLeagueTime());
        entity.setNoticeChannelId(requestDto.getNoticeChannelId());
        entity.setNoticeTime(requestDto.getNoticeTime());
        entity.setJoinMemberLimit(requestDto.getJoinMemberLimit());
        entity.setIsDeleted(false);

        //참여 가능 역할[리스트]
        if(!ObjectUtils.isEmpty(requestDto.getJoinAceptMentionList())) {
            Set<LeagueDiscordMention> joinAceptMentions = new HashSet<>();
            for (String joinAceptMention : requestDto.getJoinAceptMentionList()) {
                DiscordMention discordMention = discordMentionRepository.findByRoleId(joinAceptMention).orElse(null);
                if(discordMention != null) {
                    LeagueDiscordMention leagueDiscordMention = new LeagueDiscordMention();
                    leagueDiscordMention.setLeague(entity);
                    leagueDiscordMention.setDiscordMention(discordMention);
                    leagueDiscordMentionRepository.save(leagueDiscordMention);
                    joinAceptMentions.add(leagueDiscordMention);
                }
            }
            entity.setJoinAceptMentions(joinAceptMentions);
        }

        //트랙[리스트]
        if(!ObjectUtils.isEmpty(requestDto.getTrackList())) {
            Set<LeagueTrack> leagueTracks = new HashSet<>();
            for (LeagueDto.Track track : requestDto.getTrackList()) {
                Code code = codeRepository.findByCodeGroup_CodeGroupIdAndCodeId("TRACK", track.getName()).orElse(null);

                if(code != null) {
                    LeagueTrack leagueTrack = new LeagueTrack();
                    leagueTrack.setTrackCode(code);
                    leagueTrack.setLeague(entity);
                    leagueTrack.setTrackDate(track.getDate());
                    leagueTrack.setIsColsed(false);
                    leagueTrackRepository.save(leagueTrack);
                    leagueTracks.add(leagueTrack);
                }
            }
            entity.setLeagueTracks(leagueTracks);
        }

        //참여 카테고리(버튼)[리스트]
        if(!ObjectUtils.isEmpty(requestDto.getLeagueButtonList())) {
            Set<LeagueButton> leagueButtons = new HashSet<>();
            for (LeagueDto.RequestLeagueButton requestLeagueButton : requestDto.getLeagueButtonList()) {
                LeagueButton leagueButton = new LeagueButton();
                leagueButton.setButtonName(requestLeagueButton.getName());
                leagueButton.setButtonType(requestLeagueButton.getType());
                leagueButton.setLeague(entity);
                leagueButtonRepository.save(leagueButton);
                leagueButtons.add(leagueButton);
            }
            entity.setLeagueButtons(leagueButtons);
        }

        if(requestDto.getAttachFileGroupId() != null){
            attachFileGroupRepository.findById(requestDto.getAttachFileGroupId()).ifPresent(entity::setAttachFileGroup);
        }
        leagueRepository.save(entity);
        return entity.toResponse();
    }

    @Transactional
    public LeagueDto.Response update(LeagueDto.Request requestDto){
        League entity =  leagueRepository.findById(requestDto.getId()).orElse(null);

        if(entity != null){
            entity.setLeagueName(requestDto.getLeagueName());
            entity.setTitle(requestDto.getTitle());
            entity.setDescription(requestDto.getDescription());
            entity.setColor(requestDto.getColor());
            entity.setStartDate(requestDto.getStartDate());
            entity.setEndDate(requestDto.getEndDate());
            entity.setLeagueTime(requestDto.getLeagueTime());
            entity.setNoticeChannelId(requestDto.getNoticeChannelId());
            entity.setNoticeTime(requestDto.getNoticeTime());
            entity.setJoinMemberLimit(requestDto.getJoinMemberLimit());

            //참여 가능 역할[리스트]
            //기존 역할 삭제 후 재등록
            leagueDiscordMentionRepository.deleteAll(entity.getJoinAceptMentions());
            if(!ObjectUtils.isEmpty(requestDto.getJoinAceptMentionList())) {
                Set<LeagueDiscordMention> joinAceptMentions = new HashSet<>();
                for (String joinAceptMention : requestDto.getJoinAceptMentionList()) {
                    DiscordMention discordMention = discordMentionRepository.findByRoleId(joinAceptMention).orElse(null);
                    if(discordMention != null) {
                        LeagueDiscordMention leagueDiscordMention = new LeagueDiscordMention();
                        leagueDiscordMention.setLeague(entity);
                        leagueDiscordMention.setDiscordMention(discordMention);
                        leagueDiscordMentionRepository.save(leagueDiscordMention);
                        joinAceptMentions.add(leagueDiscordMention);
                    }
                }
                entity.setJoinAceptMentions(joinAceptMentions);
            }

            //트랙[리스트]
            //기존 트랙 삭제 후 재등록 -> 참가자와 오류 발생으로 인해 찾아서 수정으로 바꿔야될듯함
            if(!ObjectUtils.isEmpty(requestDto.getTrackList())) {
                Set<LeagueTrack> leagueTracks = leagueTrackRepository.findAllByLeague_Id(requestDto.getId());

                //기존 트랙 중 삭제된게 있다면 우선 제거
                if(!ObjectUtils.isEmpty(leagueTracks)) {
                    //바뀐 데이터 중에 현재 데이터가 없다면 삭제처리
                    if(requestDto.getTrackList().stream().anyMatch(v -> !ObjectUtils.isEmpty(v.getId()))) {
                        List<LeagueTrack> removeTrackList = leagueTracks.stream()
                                .filter(v -> requestDto.getTrackList().stream().filter(r -> !ObjectUtils.isEmpty(r.getId()))
                                        .noneMatch(r -> v.getId().equals(Long.parseLong(r.getId())))).toList();
                        if (!ObjectUtils.isEmpty(removeTrackList)) {
                            //해당 트랙에 참여신청한 모든 카테고리 삭제처리
                            for (LeagueTrack leagueTrack : removeTrackList) {
                                List<LeagueTrackMember> leagueTrackMemberList = leagueTrackMemberRepository.findAllByLeagueTrack_Id(leagueTrack.getId());
                                leagueTrackMemberRepository.deleteAll(leagueTrackMemberList);
                                leagueTracks.remove(leagueTrack);
                                leagueTrackRepository.delete(leagueTrack);
                            }
                        }
                    }
                }

                for (LeagueDto.Track track : requestDto.getTrackList()) {
                    Code code = codeRepository.findByCodeGroup_CodeGroupIdAndCodeId("TRACK", track.getName()).orElse(null);

                    if(code != null) {
                        if(!ObjectUtils.isEmpty(track.getId())){
                            LeagueTrack leagueTrack = leagueTracks.stream().filter(r -> r.getId().equals(Long.parseLong(track.getId()))).findFirst().orElse(null);
                            //null일 수 없지만 확인
                            if(leagueTrack != null){
                                leagueTrack.setTrackCode(code);
                                leagueTrack.setTrackDate(track.getDate());
                            }
                        }else {
                            LeagueTrack leagueTrack = new LeagueTrack();
                            leagueTrack.setTrackCode(code);
                            leagueTrack.setLeague(entity);
                            leagueTrack.setTrackDate(track.getDate());
                            leagueTrack.setIsColsed(false);
                            leagueTrackRepository.save(leagueTrack);
                            leagueTracks.add(leagueTrack);
                        }
                    }
                }

                entity.setLeagueTracks(leagueTracks);
            }

            //참여 카테고리(버튼)[리스트]
            //기존 버튼 삭제 후 재등록
            if(!ObjectUtils.isEmpty(requestDto.getLeagueButtonList())) {
                Set<LeagueButton> leagueButtons = leagueButtonRepository.findAllByLeague_Id(requestDto.getId());
                if(leagueButtons != null && leagueButtons.size() > 0){
                    //기존 버튼들 중에 없어진 버튼 삭제처리
                    if(requestDto.getLeagueButtonList().stream().anyMatch(v -> !ObjectUtils.isEmpty(v.getId()))) {
                        List<LeagueButton> deleteButtons = leagueButtons.stream().filter(v -> requestDto.getLeagueButtonList().stream()
                                        .filter(r -> !ObjectUtils.isEmpty(r.getId())).noneMatch(r -> v.getId().equals(Long.parseLong(r.getId()))))
                                .toList();
                        if (!ObjectUtils.isEmpty(deleteButtons)) {
                            deleteButtons.forEach(leagueButtons::remove);

                            //삭제되는 카테고리의 참여자들 삭제처리
                            for (LeagueButton deleteButton : deleteButtons) {
                                List<LeagueTrackMember> removeTrackMember = leagueTrackMemberRepository.findAllByLeagueButton_Id(deleteButton.getId());
                                if(!ObjectUtils.isEmpty(removeTrackMember)){
                                    leagueTrackMemberRepository.deleteAll(removeTrackMember);
                                }
                            }

                            leagueButtonRepository.deleteAll(deleteButtons);
                        }
                    }

                    for (LeagueDto.RequestLeagueButton requestLeagueButton : requestDto.getLeagueButtonList()) {
                        //기존 등록 버튼일 경우
                        if(!ObjectUtils.isEmpty(requestLeagueButton.getId())){
                            LeagueButton leagueButton = leagueButtonRepository.findById(Long.parseLong(requestLeagueButton.getId())).orElse(null);
                            if(leagueButton != null){
                                leagueButton.setButtonName(requestLeagueButton.getName());
                                leagueButton.setButtonType(requestLeagueButton.getType());
                                leagueButtonRepository.save(leagueButton);
                            }
                        }//신규 등록 버튼일 경우
                        else{
                            LeagueButton leagueButton = new LeagueButton();
                            leagueButton.setButtonName(requestLeagueButton.getName());
                            leagueButton.setButtonType(requestLeagueButton.getType());
                            leagueButton.setLeague(entity);
                            leagueButtonRepository.save(leagueButton);
                            leagueButtons.add(leagueButton);
                        }


                    }
                }else {
                    leagueButtons = new HashSet<>();
                    for (LeagueDto.RequestLeagueButton requestLeagueButton : requestDto.getLeagueButtonList()) {
                        LeagueButton leagueButton = new LeagueButton();
                        leagueButton.setButtonName(requestLeagueButton.getName());
                        leagueButton.setButtonType(requestLeagueButton.getType());
                        leagueButton.setLeague(entity);
                        leagueButtonRepository.save(leagueButton);
                        leagueButtons.add(leagueButton);
                    }
                }
                entity.setLeagueButtons(leagueButtons);
            }

            leagueRepository.flush();
            return entity.toResponse();
        }
        return null;
    }

    @Transactional
    public boolean delete(long id){
        League league = leagueRepository.findById(id).orElse(null);
        if(league == null){
            return false;
        }else{
            league.setIsDeleted(true);
            return true;
        }
    }
}
