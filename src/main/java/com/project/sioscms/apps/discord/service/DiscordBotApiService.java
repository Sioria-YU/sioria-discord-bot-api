package com.project.sioscms.apps.discord.service;

import com.project.sioscms.SioscmsApplication;
import com.project.sioscms.apps.contents.domain.entity.Contents;
import com.project.sioscms.apps.discord.domain.dto.DiscordMemberDto;
import com.project.sioscms.apps.discord.domain.entity.DiscordMember;
import com.project.sioscms.apps.discord.domain.repository.DiscordMemberRepository;
import com.project.sioscms.common.utils.jpa.restriction.ChangSolJpaRestriction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DiscordBotApiService {

    private final DiscordMemberRepository discordMemberRepository;

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

        Guild guild = jda.getGuildById("1104359385909694534");
        List<Member> memberList = new ArrayList<>();
        assert guild != null;
        guild.loadMembers().onSuccess(memberList::addAll);
        //3초 대기
        Thread.sleep(3000);

        log.info("총 멤버 수 : {}", guild.getMemberCount());

        //디스코드 전체 멤버 목록을 가져온다.
        if(memberList.size() > 0){
            for (Member member : memberList) {
                if(discordMemberRepository.countByUserId(member.getUser().getId()) < 1){
                    User user = member.getUser();
                    DiscordMember newMember = new DiscordMember();
                    newMember.setUserId(user.getId());
                    newMember.setUsername(user.getName());
                    newMember.setGlobalName(user.getGlobalName());
                    newMember.setDiscriminator(user.getAsMention());
                    newMember.setIsDeleted(false);
                    discordMemberRepository.save(newMember);
                }
            }
//            memberList.forEach(m -> log.info("멤버 : " + m.getUser().getId() + "[" + m.getUser().getName() + "]" ));
            return true;
        }else {
            return false;
        }
    }
}
