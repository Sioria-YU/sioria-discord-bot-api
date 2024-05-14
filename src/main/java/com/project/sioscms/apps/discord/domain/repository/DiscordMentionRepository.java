package com.project.sioscms.apps.discord.domain.repository;

import com.project.sioscms.apps.discord.domain.entity.DiscordMention;
import com.project.sioscms.common.domain.repository.CommonJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DiscordMentionRepository extends CommonJpaRepository<DiscordMention, Long> {
    Optional<DiscordMention> findByRoleId(String roleId);
}
