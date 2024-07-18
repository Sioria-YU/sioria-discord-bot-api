package com.project.sioscms.apps.discord.domain.repository;

import com.project.sioscms.apps.discord.domain.entity.LeagueDiscordMention;
import com.project.sioscms.common.domain.repository.CommonJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LeagueDiscordMentionRepository extends CommonJpaRepository<LeagueDiscordMention, Long> {
    List<LeagueDiscordMention> findAllByLeague_Id(long leagueId);
}
