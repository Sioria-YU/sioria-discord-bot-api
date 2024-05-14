package com.project.sioscms.apps.discord.domain.repository;

import com.project.sioscms.apps.discord.domain.entity.DiscordUserMension;
import com.project.sioscms.common.domain.repository.CommonJpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DiscordUserMensionRepository extends CommonJpaRepository<DiscordUserMension, Long> {
}
