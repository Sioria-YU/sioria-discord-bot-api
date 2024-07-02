package com.project.sioscms.apps.discord.domain.repository;

import com.project.sioscms.apps.discord.domain.entity.LeagueDiscordMention;
import com.project.sioscms.common.domain.repository.CommonJpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LeagueDiscordMentionRepository extends CommonJpaRepository<LeagueDiscordMention, Long> {
}
