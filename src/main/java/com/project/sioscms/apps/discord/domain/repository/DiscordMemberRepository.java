package com.project.sioscms.apps.discord.domain.repository;

import com.project.sioscms.apps.discord.domain.entity.DiscordMember;
import com.project.sioscms.common.domain.repository.CommonJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DiscordMemberRepository extends CommonJpaRepository<DiscordMember, Long> {
    Optional<DiscordMember> findByUserId(String userId);
    long countByUserId(String userId);
}
