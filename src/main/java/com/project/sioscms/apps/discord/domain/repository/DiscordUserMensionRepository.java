package com.project.sioscms.apps.discord.domain.repository;

import com.project.sioscms.apps.discord.domain.entity.DiscordMember;
import com.project.sioscms.apps.discord.domain.entity.DiscordUserMension;
import com.project.sioscms.common.domain.repository.CommonJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface DiscordUserMensionRepository extends CommonJpaRepository<DiscordUserMension, Long> {
    Set<DiscordUserMension> findAllByDiscordMember_Id(Long id);
    List<DiscordUserMension> findAllByDiscordMention_RoleId(String roleId);
    void deleteAllByDiscordMention_IdAndDiscordMemberIn(Long discordMentionId, List<DiscordMember> discordMemberList);
}
