package com.project.sioscms.apps.discord.domain.repository;

import com.project.sioscms.apps.discord.domain.entity.DiscordPenalty;
import com.project.sioscms.common.domain.repository.CommonJpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DiscordPenaltyRepository extends CommonJpaRepository<DiscordPenalty, Long> {
    long countAllByDiscordMember_IdAndIsDeleted(long discordMemberId, boolean isDeleted);
}
