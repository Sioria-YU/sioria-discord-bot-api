package com.project.sioscms.apps.discord.domain.repository;

import com.project.sioscms.apps.discord.domain.entity.ReagueDiscordMention;
import com.project.sioscms.common.domain.repository.CommonJpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReagueDiscordMentionRepository extends CommonJpaRepository<ReagueDiscordMention, Long> {
}
