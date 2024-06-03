package com.project.sioscms.apps.discord.domain.repository;

import com.project.sioscms.apps.discord.domain.entity.ReagueTrackMember;
import com.project.sioscms.common.domain.repository.CommonJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReagueTrackMemberRepository extends CommonJpaRepository<ReagueTrackMember, Long> {
    Optional<ReagueTrackMember> findByDiscordMember_UserIdAndReagueTrack_Id(String userId, long trackId);
//    List<ReagueTrackMember> findAllByReagueTrack_IdAndJoinType(long reagueTrackId, String joinType);
//    long countByReagueTrack_IdAndJoinType(long reagueTrackId, String joinType);
    List<ReagueTrackMember> findAllByReagueTrack_Id(long reagueTrackId);
    List<ReagueTrackMember> findAllByReagueTrack_IdAndReagueButton_Id(long reagueTrackId, long buttonId);
    long countByReagueTrack_IdAndReagueButton_Id(long reagueTrackId, long buttonId);
}
