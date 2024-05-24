package com.project.sioscms.apps.discord.domain.repository;

import com.project.sioscms.apps.discord.domain.entity.RegueTrackMember;
import com.project.sioscms.common.domain.repository.CommonJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RegueTrackMemberRepository extends CommonJpaRepository<RegueTrackMember, Long> {
    Optional<RegueTrackMember> findByDiscordMember_UserIdAndReagueTrack_Id(String userId, long trackId);
    List<RegueTrackMember> findAllByReagueTrack_IdAndJoinType(long reagueTrackId, String joinType);
    long countByReagueTrack_IdAndJoinType(long reagueTrackId, String joinType);
}
