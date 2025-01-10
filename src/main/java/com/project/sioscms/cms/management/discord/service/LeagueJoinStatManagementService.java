package com.project.sioscms.cms.management.discord.service;

import com.project.sioscms.apps.code.domain.entity.Code;
import com.project.sioscms.apps.code.domain.repository.CodeRepository;
import com.project.sioscms.apps.discord.domain.dto.LeagueTrackDto;
import com.project.sioscms.apps.discord.domain.entity.LeagueTrack;
import com.project.sioscms.apps.discord.domain.entity.LeagueTrackMember;
import com.project.sioscms.apps.discord.domain.repository.LeagueTrackMemberRepository;
import com.project.sioscms.apps.discord.domain.repository.LeagueTrackRepository;
import com.project.sioscms.common.utils.jpa.page.SiosPage;
import com.project.sioscms.common.utils.jpa.restriction.ChangSolJpaRestriction;
import com.project.sioscms.common.utils.jpa.restriction.ChangSolJpaRestrictionType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LeagueJoinStatManagementService {
    private final LeagueTrackRepository leagueTrackRepository;
    private final LeagueTrackMemberRepository leagueTrackMemberRepository;
    private final CodeRepository codeRepository;

    public SiosPage<LeagueTrackDto.Response> getLeagueTrackList(LeagueTrackDto.Request requestDto){
        ChangSolJpaRestriction rs = new ChangSolJpaRestriction(ChangSolJpaRestrictionType.AND);
        rs.lessThanEquals("trackDate", LocalDate.now());

        //검색조건
        if (!ObjectUtils.isEmpty(requestDto.getLeagueName())) {
            rs.like("league.leagueName", "%" + requestDto.getLeagueName() + "%");
        }

        if(!ObjectUtils.isEmpty(requestDto.getStartDate()) && !ObjectUtils.isEmpty(requestDto.getEndDate())){
            rs.between("trackDate", requestDto.getStartDate(), requestDto.getEndDate());
        }else {
            if (!ObjectUtils.isEmpty(requestDto.getStartDate())) {
                rs.greaterThanEquals("trackDate", requestDto.getStartDate());
            }

            if (!ObjectUtils.isEmpty(requestDto.getEndDate())) {
                rs.lessThanEquals("trackDate", requestDto.getEndDate());
            }
        }

        return new SiosPage<>(leagueTrackRepository.findAll(rs.toSpecification()
                        , requestDto.toPageableWithSortedByKey("trackDate", Sort.Direction.DESC))
                .map(LeagueTrack::toResponse)
                , requestDto.getPageSize()
        );
    }

    public LeagueTrackDto.Response getLeagueTrack(Long leagueTrackId){
        LeagueTrack leagueTrack = leagueTrackRepository.findById(leagueTrackId).orElse(null);
        if(leagueTrack != null){
            return leagueTrack.toResponse();
        }else{
            return null;
        }
    }

    public List<LeagueTrackDto.Response> getLeagueTrackListByLeagueId(Long leagueId){
        return leagueTrackRepository.findAllByLeague_IdOrderByTrackDateAsc(leagueId).stream().map(LeagueTrack::toResponse).collect(Collectors.toList());
    }

    public List<LeagueTrackMember> getTrackMembers(Long leagueTrackId){
        return leagueTrackMemberRepository.findAllByLeagueTrack_IdOrderByJoinTypeAscScoreDesc(leagueTrackId);
    }

    @Transactional
    public boolean updateTrackMembers(LeagueTrackDto.Request requestDto){
        if(ObjectUtils.isEmpty(requestDto.getTrackMemberIds()) || ObjectUtils.isEmpty(requestDto.getJoinTypes())
                || ObjectUtils.isEmpty(requestDto.getScores())){
            return false;
        }

        try {
            for (int i = 0; i < requestDto.getTrackMemberIds().size(); i++) {
                LeagueTrackMember leagueTrackMember = leagueTrackMemberRepository.findById(requestDto.getTrackMemberIds().get(i))
                        .orElseThrow(NullPointerException::new);

                Code joinType = codeRepository.findByCodeGroup_CodeGroupIdAndCodeId("LEAGUE_JOIN_TYPE_CD", requestDto.getJoinTypes().get(i))
                        .orElseThrow(NullPointerException::new);
                leagueTrackMember.setJoinType(joinType);
                leagueTrackMember.setScore(requestDto.getScores().get(i));
            }

            return true;
        }catch (Exception e){
            log.error(e.getMessage());
            return false;
        }
    }
}
