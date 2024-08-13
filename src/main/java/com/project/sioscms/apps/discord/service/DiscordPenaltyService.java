package com.project.sioscms.apps.discord.service;

import com.project.sioscms.apps.code.domain.entity.Code;
import com.project.sioscms.apps.code.domain.repository.CodeRepository;
import com.project.sioscms.apps.discord.domain.dto.DiscordPenaltyDto;
import com.project.sioscms.apps.discord.domain.entity.DiscordMember;
import com.project.sioscms.apps.discord.domain.entity.DiscordPenalty;
import com.project.sioscms.apps.discord.domain.entity.League;
import com.project.sioscms.apps.discord.domain.repository.DiscordMemberRepository;
import com.project.sioscms.apps.discord.domain.repository.DiscordPenaltyRepository;
import com.project.sioscms.apps.discord.domain.repository.LeagueRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DiscordPenaltyService {
    private final DiscordPenaltyRepository discordPenaltyRepository;
    private final LeagueRepository leagueRepository;
    private final DiscordMemberRepository discordMemberRepository;
    private final CodeRepository codeRepository;

    public DiscordPenaltyDto.Response getDiscordPenalty(long id){
        DiscordPenalty discordPenalty = discordPenaltyRepository.findById(id).orElse(null);
        if(discordPenalty != null){
            return discordPenalty.toResponse();
        }else {
            return null;
        }
    }

    @Transactional
    public DiscordPenaltyDto.Response save(DiscordPenaltyDto.Request requestDto){
        if(ObjectUtils.isEmpty(requestDto.getLeagueId())
        || ObjectUtils.isEmpty(requestDto.getDiscordMemberId())
        || ObjectUtils.isEmpty(requestDto.getPenaltyTypeCodeId())){
            return null;
        }

        League league = leagueRepository.findById(requestDto.getLeagueId()).orElse(null);
        DiscordMember discordMember = discordMemberRepository.findById(requestDto.getDiscordMemberId()).orElse(null);
        Code code = codeRepository.findByCodeGroup_CodeGroupIdAndCodeId("PENALTY_TYPE_CD", requestDto.getPenaltyTypeCodeId()).orElse(null);
        if (league == null || discordMember == null || code == null){
            return null;
        }

        long frequency = discordPenaltyRepository.countAllByDiscordMember_IdAndIsDeleted(discordMember.getId(), false);
        frequency += 1;


        DiscordPenalty entity  = new DiscordPenalty();
        entity.setIsDeleted(false);
        entity.setPenaltyNote(requestDto.getPenaltyNote());
        entity.setApplyDate(requestDto.getApplyDate());
        entity.setLeague(league);
        entity.setDiscordMember(discordMember);
        entity.setPenaltyTypeCode(code);
        entity.setFrequency(frequency);
        discordPenaltyRepository.save(entity);

        return entity.toResponse();
    }

    @Transactional
    public DiscordPenaltyDto.Response update(DiscordPenaltyDto.Request requestDto){
        if(ObjectUtils.isEmpty(requestDto.getId())
                || ObjectUtils.isEmpty(requestDto.getLeagueId())
                || ObjectUtils.isEmpty(requestDto.getDiscordMemberId())
                || ObjectUtils.isEmpty(requestDto.getPenaltyTypeCodeId())){
            return null;
        }

        DiscordPenalty entity  = discordPenaltyRepository.findById(requestDto.getId()).orElse(null);
        if(entity == null){
            return null;
        }

        League league = leagueRepository.findById(requestDto.getLeagueId()).orElse(null);
        DiscordMember discordMember = discordMemberRepository.findById(requestDto.getDiscordMemberId()).orElse(null);
        Code code = codeRepository.findByCodeGroup_CodeGroupIdAndCodeId("PENALTY_TYPE_CD", requestDto.getPenaltyTypeCodeId()).orElse(null);
        if (league == null || discordMember == null || code == null){
            return null;
        }

        entity.setPenaltyNote(requestDto.getPenaltyNote());
        entity.setApplyDate(requestDto.getApplyDate());
        entity.setLeague(league);
        entity.setDiscordMember(discordMember);
        entity.setPenaltyTypeCode(code);
        entity.setFrequency(requestDto.getFrequency());

        discordPenaltyRepository.flush();

        return entity.toResponse();
    }

    @Transactional
    public boolean delete(long id){
        if(ObjectUtils.isEmpty(id)){
            return false;
        }

        DiscordPenalty entity  = discordPenaltyRepository.findById(id).orElse(null);
        if(entity != null){
            entity.setIsDeleted(true);
            discordPenaltyRepository.flush();
            return true;
        }else{
            return false;
        }
    }
}
