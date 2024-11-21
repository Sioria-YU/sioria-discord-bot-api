package com.project.sioscms.apps.discord.service;

import com.project.sioscms.SioscmsApplication;
import com.project.sioscms.apps.discord.domain.dto.DiscordMemberDto;
import com.project.sioscms.apps.discord.domain.entity.DiscordMember;
import com.project.sioscms.apps.discord.domain.repository.DiscordMemberRepository;
import com.project.sioscms.common.utils.jpa.restriction.ChangSolJpaRestriction;
import com.project.sioscms.common.utils.jpa.restriction.ChangSolJpaRestrictionType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DiscordMemberService {
    @Value("${discord.guild-key}")
    private String GUILD_KEY;

    private JDA getJDA() {
        return SioscmsApplication.getJda();
    }

    private final DiscordMemberRepository discordMemberRepository;

    /**
     * 디스코드 가입자 목록 조회
     * @param username
     * @return
     */
    public List<DiscordMemberDto.Response> getDiscordMemberList(String username){
        ChangSolJpaRestriction rs = new ChangSolJpaRestriction();
        rs.equals("isDeleted", false);

        if(!ObjectUtils.isEmpty(username)){
            ChangSolJpaRestriction rs2 = new ChangSolJpaRestriction(ChangSolJpaRestrictionType.OR);
            rs2.iLike("username", "%" + username + "%");
            rs2.iLike("nickname", "%" + username + "%");
            rs2.iLike("globalName", "%" + username + "%");
            rs.addChild(rs2);
        }

        return discordMemberRepository.findAll(rs.toSpecification(), Sort.by(Sort.Direction.ASC, "username"))
                .stream().map(DiscordMember::toResponse).toList();
    }

    /**
     * 디스코드 가입자 조회
     * @param id
     * @return
     */
    public DiscordMemberDto.Response getDiscordMember(long id){
        DiscordMember discordMember = discordMemberRepository.findById(id).orElse(null);
        if(discordMember != null){
            return discordMember.toResponse();
        }
        return null;
    }

    /**
     * 디스코드 가입자 닉네임 동기화
     * @return
     */
    @Transactional
    public boolean refreshMemberNickname(List<Member> memberList){
        try {
            Guild guild = getJDA().getGuildById(GUILD_KEY);
            assert guild != null;
            if(ObjectUtils.isEmpty(memberList)) {
                memberList = new ArrayList<>();
                guild.loadMembers().onSuccess(memberList::addAll);
                //3초 대기
                Thread.sleep(3000);
            }

            for (Member member : memberList) {
                DiscordMember discordMember = discordMemberRepository.findByUserId(member.getUser().getId()).orElse(null);
                if (discordMember == null) {
                    continue;
                }

                //관리자, 봇 등 제외
                if(member.getRoles().stream().noneMatch(r -> {
                    if(r.getId().equals("1125386665574273024") //관리자
                        || r.getId().equals("1174337274419351622") //리그운영진
                        || r.getId().equals("1174337274419351622") //BOT
                        || r.getId().equals("1174337158551707710") //Apollo
                        || r.getId().equals("1182681528745087076") //Dyno
                        || r.getId().equals("1239563081617641544") //siobot
                        || r.getId().equals("1250328630538797080") //치직
                    ) {
                        return true;
                    }
                    return false;
                    }
                )){
                    //길드 멤버 별명 동기화
                    convertToGuildMention(guild, discordMember, member);
                    User user = member.getUser();
                    discordMember.setUserId(user.getId());
                    discordMember.setUsername(user.getName());
                    discordMember.setGlobalName(user.getGlobalName());
                    discordMember.setUserMension(user.getAsMention());
                }
            }
            return true;
        }catch (Exception e){
            log.error(e.getMessage());
        }
        return false;
    }

    @Transactional
    public void updateDiscordMemberTags(){

    }

    @Transactional
    public boolean deleteDiscordMemberTag(long id){
        try {
            if (!ObjectUtils.isEmpty(id)) {
                discordMemberRepository.deleteById(id);
                return true;
            }else {
                return false;
            }
        }catch (Exception e){
            log.error(e.getMessage());
            return false;
        }
    }

    @Transactional
    public void convertToGuildMention(Guild guild, DiscordMember discordMember, Member member){
        //길드 닉네임이 없을 경우 -> 1순위 전체 닉네임, 2순위 아이디로 세팅함
        if(ObjectUtils.isEmpty(member.getNickname())){
            String nickName = ObjectUtils.isEmpty(member.getUser().getGlobalName())? member.getUser().getName() : member.getUser().getGlobalName();
            discordMember.setNickname(nickName);
            guild.modifyNickname(member, nickName).queue(); //디스코드 서버에 변경처리
        }//길드 닉네임과 서버에 저장된 닉네임이 다를 경우 업데이트
        else if(!ObjectUtils.isEmpty(member.getNickname()) && !member.getNickname().equals(discordMember.getNickname())){
            //변경 로그 남김
            log.info("Nickname changed ::: " + discordMember.getNickname() + " -> " + member.getNickname());
            discordMember.setNickname(member.getNickname());
        }
    }
}
