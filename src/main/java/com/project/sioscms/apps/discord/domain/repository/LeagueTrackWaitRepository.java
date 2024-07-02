package com.project.sioscms.apps.discord.domain.repository;

import com.project.sioscms.apps.discord.domain.entity.LeagueTrackWait;
import com.project.sioscms.common.domain.repository.CommonJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LeagueTrackWaitRepository extends CommonJpaRepository<LeagueTrackWait, Long> {
    long countByLeagueTrack_Id(long leagueTrackId);
    long countByLeagueTrack_IdAndLeagueButton_Id(long leagueTrackId, long leagueButtonId);
    List<LeagueTrackWait> findAllByLeagueTrack_IdAndLeagueButton_IdOrderByCreatedDateTimeAsc(long leagueTrackId, long leagueButtonId);
    Optional<LeagueTrackWait> findByLeagueTrack_IdAndDiscordMember_Id(long leagueTrackId, long discordMemberId);
    Optional<LeagueTrackWait> findByLeagueTrack_IdAndLeagueButton_IdAndDiscordMember_Id(long leagueTrackId, long leagueButtonId, long discordMemberId);
    Optional<LeagueTrackWait> findTop1ByLeagueTrack_IdAndLeagueButton_IdOrderByCreatedDateTimeAsc(long leagueTrackId, long leagueButtonId);
}
