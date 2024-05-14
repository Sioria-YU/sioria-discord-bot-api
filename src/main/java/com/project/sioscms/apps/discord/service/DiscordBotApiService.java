package com.project.sioscms.apps.discord.service;

import com.project.sioscms.SioscmsApplication;
import com.project.sioscms.apps.contents.domain.entity.Contents;
import com.project.sioscms.apps.discord.domain.dto.DiscordMemberDto;
import com.project.sioscms.apps.discord.domain.entity.DiscordMember;
import com.project.sioscms.apps.discord.domain.entity.DiscordMention;
import com.project.sioscms.apps.discord.domain.entity.DiscordUserMension;
import com.project.sioscms.apps.discord.domain.repository.DiscordMemberRepository;
import com.project.sioscms.apps.discord.domain.repository.DiscordMentionRepository;
import com.project.sioscms.apps.discord.domain.repository.DiscordUserMensionRepository;
import com.project.sioscms.common.utils.jpa.restriction.ChangSolJpaRestriction;
import com.project.sioscms.discord.DiscordBotToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DiscordBotApiService {
    @Value("${discord.guild-key}")
    private String GUILD_KEY;

    private final DiscordMemberRepository discordMemberRepository;
    private final DiscordMentionRepository discordMentionRepository;
    private final DiscordUserMensionRepository discordUserMensionRepository;

    /**
     * 디스코드 길드 가입자 목록 조회
     * @return
     */
    public List<DiscordMemberDto.Response> getDiscordMembers(DiscordMemberDto.Request requestDto){
        ChangSolJpaRestriction restriction = new ChangSolJpaRestriction();
        restriction.equals("isDeleted", false);
        return discordMemberRepository.findAll(restriction.toSpecification(), Sort.by(Sort.Direction.DESC, "username"))
                .stream().map(DiscordMember::toResponse).collect(Collectors.toList());
    }

    @Transactional
    public boolean memberRefresh() throws InterruptedException {
        JDA jda = SioscmsApplication.jda;

        Guild guild = jda.getGuildById(GUILD_KEY);
        assert guild != null;

        List<Member> memberList = new ArrayList<>();
        guild.loadMembers().onSuccess(memberList::addAll);
        //3초 대기
        Thread.sleep(3000);

        //디스코드 전체 멤버 목록을 가져온다.
        if(memberList.size() > 0){
            for (Member member : memberList) {
                if(discordMemberRepository.countByUserId(member.getUser().getId()) < 1){
                    User user = member.getUser();
                    DiscordMember newMember = new DiscordMember();
                    newMember.setUserId(user.getId());
                    newMember.setUsername(user.getName());
                    newMember.setGlobalName(user.getGlobalName());
                    newMember.setUserMension(user.getAsMention());
                    newMember.setIsDeleted(false);

                    //멤버 권한 저장
                    if(member.getRoles() != null && member.getRoles().size() > 0) {
                        Set<DiscordUserMension> discordUserMensionSet = new HashSet<>();
                        for (Role role : member.getRoles()) {
                            DiscordMention mention = discordMentionRepository.findByRoleId(role.getId()).orElse(null);
                            if(mention != null){
                                DiscordUserMension discordUserMension = new DiscordUserMension();
                                discordUserMension.setDiscordMention(mention);
                                discordUserMension.setDiscordMember(newMember);
                                discordUserMensionRepository.save((discordUserMension));
                                discordUserMensionSet.add(discordUserMension);
                            }
                        }
                        if(discordUserMensionSet.size() > 0) {
                            newMember.setDiscordUserMensionSet(discordUserMensionSet);
                        }
                    }
                    discordMemberRepository.save(newMember);
                }
            }
            return true;
        }else {
            return false;
        }
    }

    @Transactional
    public boolean rolesRefresh(){
        JDA jda = SioscmsApplication.jda;

        Guild guild = jda.getGuildById(GUILD_KEY);
        assert guild != null;

        List<Role> roleList = guild.getRoles();

        log.info("ESK guild roles out!!!!!!!!!!!!!!!!!!!!!!!!!!");
        if(roleList.size() > 0) {
            for (Role role : roleList) {
//                log.info("Role Name : {}  / Mentions : {} / Id : {}", role.getName(), role.getAsMention(), role.getId());
                DiscordMention mention = new DiscordMention();
                mention.setRoleId(role.getId());
                mention.setRoleName(role.getName());
                mention.setMention(role.getAsMention());
                discordMentionRepository.save(mention);
            }
            return true;
        }else{
            return false;
        }
    }
}
