package com.project.sioscms.apps.discord.domain.repository;

import com.project.sioscms.apps.discord.domain.entity.ReagueTrackWait;
import com.project.sioscms.common.domain.repository.CommonJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReagueTrackWaitRepository extends CommonJpaRepository<ReagueTrackWait, Long> {
    long countByReagueTrack_Id(long reagueTrackId);
    long countByReagueTrack_IdAndReagueButton_Id(long reagueTrackId, long reagueButtonId);
    List<ReagueTrackWait> findAllByReagueTrack_IdAndReagueButton_IdOrderByCreatedDateTimeAsc(long reagueTrackId, long reagueButtonId);
    Optional<ReagueTrackWait> findByReagueTrack_IdAndDiscordMember_Id(long reagueTrackId, long discordMemberId);
    Optional<ReagueTrackWait> findByReagueTrack_IdAndReagueButton_IdAndDiscordMember_Id(long reagueTrackId, long reagueButtonId, long discordMemberId);
    Optional<ReagueTrackWait> findTop1ByReagueTrack_IdAndReagueButton_IdOrderByCreatedDateTimeAsc(long reagueTrackId, long reagueButtonId);
}
