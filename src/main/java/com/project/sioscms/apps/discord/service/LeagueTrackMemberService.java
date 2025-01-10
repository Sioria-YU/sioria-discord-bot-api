package com.project.sioscms.apps.discord.service;

import com.project.sioscms.apps.code.domain.entity.Code;
import com.project.sioscms.apps.code.domain.repository.CodeRepository;
import com.project.sioscms.apps.discord.domain.entity.DiscordMember;
import com.project.sioscms.apps.discord.domain.entity.LeagueButton;
import com.project.sioscms.apps.discord.domain.entity.LeagueTrack;
import com.project.sioscms.apps.discord.domain.entity.LeagueTrackMember;
import com.project.sioscms.apps.discord.domain.repository.DiscordMemberRepository;
import com.project.sioscms.apps.discord.domain.repository.LeagueButtonRepository;
import com.project.sioscms.apps.discord.domain.repository.LeagueTrackMemberRepository;
import com.project.sioscms.apps.discord.domain.repository.LeagueTrackRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LeagueTrackMemberService {
    private final LeagueTrackMemberRepository leagueTrackMemberRepository;
    private final LeagueButtonRepository leagueButtonRepository;
    private final LeagueTrackRepository leagueTrackRepository;
    private final DiscordMemberRepository discordMemberRepository;
    private final CodeRepository codeRepository;


    @Transactional
    public boolean deleteLeagueTrackMember(Long id) {
        try {
            LeagueTrackMember leagueTrackMember = leagueTrackMemberRepository.findById(id).orElseThrow(NullPointerException::new);
            leagueTrackMemberRepository.delete(leagueTrackMember);
            leagueTrackMemberRepository.flush();
            return true;
        } catch (NullPointerException e) {
            log.error(e.getMessage());
            return false;
        }
    }

    @Transactional
    public boolean saveLeagueTrackMember(Long trackId, Long buttonId, Long memberId) {
        try {
            if(leagueTrackMemberRepository.findByDiscordMember_IdAndLeagueTrack_Id(memberId, trackId).orElse(null) != null){
                return false;
            }

            LeagueTrackMember entity = new LeagueTrackMember();
            LeagueTrack leagueTrack = leagueTrackRepository.findById(trackId).orElseThrow(NullPointerException::new);
            LeagueButton leagueButton = leagueButtonRepository.findById(buttonId).orElseThrow(NullPointerException::new);
            DiscordMember discordMember = discordMemberRepository.findById(memberId).orElseThrow(NullPointerException::new);

            Code joinType = codeRepository.findByCodeGroup_CodeGroupIdAndCodeId("LEAGUE_JOIN_TYPE_CD", "1").orElseThrow(NullPointerException::new);

            entity.setLeagueTrack(leagueTrack);
            entity.setLeagueButton(leagueButton);
            entity.setDiscordMember(discordMember);
            entity.setJoinType(joinType);
            entity.setScore(0L);
            leagueTrackMemberRepository.save(entity);
            return true;
        } catch (NullPointerException e) {
            log.error(e.getMessage());
            return false;
        }
    }
}
