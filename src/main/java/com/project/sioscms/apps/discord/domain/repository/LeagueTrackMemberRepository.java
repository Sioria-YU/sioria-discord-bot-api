package com.project.sioscms.apps.discord.domain.repository;

import com.project.sioscms.apps.discord.domain.entity.LeagueTrackMember;
import com.project.sioscms.common.domain.repository.CommonJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LeagueTrackMemberRepository extends CommonJpaRepository<LeagueTrackMember, Long> {
    Optional<LeagueTrackMember> findByDiscordMember_UserIdAndLeagueTrack_Id(String userId, long trackId);
//    List<LeagueTrackMember> findAllByLeagueTrack_IdAndJoinType(long leagueTrackId, String joinType);
//    long countByLeagueTrack_IdAndJoinType(long leagueTrackId, String joinType);
    List<LeagueTrackMember> findAllByLeagueTrack_Id(long leagueTrackId);
    List<LeagueTrackMember> findAllByLeagueTrack_IdAndLeagueButton_IdOrderByCreatedDateTimeAscUpdatedDateTimeAsc(long leagueTrackId, long buttonId);
    List<LeagueTrackMember> findAllByLeagueButton_Id(long leagueButtonId);
    long countByLeagueTrack_IdAndLeagueButton_Id(long leagueTrackId, long buttonId);
}
