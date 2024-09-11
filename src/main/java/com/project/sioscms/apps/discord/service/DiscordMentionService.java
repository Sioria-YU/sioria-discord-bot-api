package com.project.sioscms.apps.discord.service;

import com.project.sioscms.SioscmsApplication;
import com.project.sioscms.apps.discord.domain.entity.DiscordMember;
import com.project.sioscms.apps.discord.domain.entity.DiscordMention;
import com.project.sioscms.apps.discord.domain.repository.DiscordMemberRepository;
import com.project.sioscms.apps.discord.domain.repository.DiscordMentionRepository;
import com.project.sioscms.apps.discord.domain.repository.DiscordUserMensionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DiscordMentionService {
    @Value("${discord.guild-key}")
    private String GUILD_KEY;

    private JDA getJDA() {
        return SioscmsApplication.getJda();
    }

    private final DiscordMemberRepository discordMemberRepository;
    private final DiscordMentionRepository discordMentionRepository;
    private final DiscordUserMensionRepository discordUserMensionRepository;

    @Transactional
    public Boolean multiDeleteDiscordMention(long mentionId, List<Long> ids){
        DiscordMention discordMention = discordMentionRepository.findById(mentionId).orElse(null);
        assert discordMention != null;

        List<DiscordMember> discordMemberList = discordMemberRepository.findAllById(ids);
        assert ObjectUtils.isEmpty(discordMemberList);

        try {
            //db 삭제처리
            discordUserMensionRepository.deleteAllByDiscordMention_IdAndDiscordMemberIn(discordMention.getId(), discordMemberList);

            //discord 멘션 제거
            JDA jda = getJDA();

            Guild guild = jda.getGuildById(GUILD_KEY);
            assert guild != null;

            List<Member> memberList = new ArrayList<>();
            guild.loadMembers().onSuccess(memberList::addAll);
            //3초 대기(api 로드 시간)
            Thread.sleep(3000);
            if (memberList.size() > 0) {
                List<Member> updatedMemberList = memberList.stream().filter(m -> discordMemberList.stream().anyMatch(v -> v.getUserId().equals(m.getUser().getId()))).toList();

                for (Member member : updatedMemberList) {
                    List<Role> roles = member.getRoles();
                    List<Role> afterRoles = roles.stream().filter(r -> !r.getId().equals(discordMention.getRoleId())).toList();
                    guild.modifyMemberRoles(member, afterRoles).queue();
                }
            }
        } catch (InterruptedException e) {
            log.error(e.getMessage());
            return false;
        }

        return true;
    }

}
