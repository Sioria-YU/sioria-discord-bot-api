package com.project.sioscms.apps.discord.service;

import com.project.sioscms.SioscmsApplication;
import com.project.sioscms.apps.discord.domain.dto.DiscordMemberDto;
import com.project.sioscms.apps.discord.domain.dto.DiscordMentionDto;
import com.project.sioscms.apps.discord.domain.entity.*;
import com.project.sioscms.apps.discord.domain.repository.DiscordMemberRepository;
import com.project.sioscms.apps.discord.domain.repository.DiscordMentionRepository;
import com.project.sioscms.apps.discord.domain.repository.DiscordUserMensionRepository;
import com.project.sioscms.apps.discord.domain.repository.ReagueRepository;
import com.project.sioscms.common.utils.jpa.restriction.ChangSolJpaRestriction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.concrete.NewsChannel;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;


import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;


@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DiscordBotApiService {
    @Value("${discord.guild-key}")
    private String GUILD_KEY;

    @Value("${cms.site.uri}")
    private String SITE_URI;

    private final DiscordMemberRepository discordMemberRepository;
    private final DiscordMentionRepository discordMentionRepository;
    private final DiscordUserMensionRepository discordUserMensionRepository;
    private final ReagueRepository reagueRepository;

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

    /**
     * 디스코드 공지 채널 목록을 얻어온다.
     * @return
     */
    public List<Map<String, String>> getNewsChannels(){
        JDA jda = SioscmsApplication.jda;

        Guild guild = jda.getGuildById(GUILD_KEY);
        assert guild != null;

        List<NewsChannel> newsChannelList = guild.getNewsChannels();

        List<Map<String, String>> resultList = new ArrayList<>();

        for (NewsChannel channel : newsChannelList) {
            Map<String, String> newsChannel = new HashMap<>();
            newsChannel.put("id", channel.getId());
            newsChannel.put("name", channel.getName());
            resultList.add(newsChannel);
        }

        return resultList;
    }

    /**
     * 디스코드 길드 가입자들을 불러와 저장한다.
     * @return
     * @throws InterruptedException
     */
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

    /**
     * 디스코드 역할 멘션들을 불러와 저장한다.
     * @return
     */
    @Transactional
    public boolean rolesRefresh(){
        JDA jda = SioscmsApplication.jda;

        Guild guild = jda.getGuildById(GUILD_KEY);
        assert guild != null;

        List<Role> roleList = guild.getRoles();

        if(roleList.size() > 0) {
            for (Role role : roleList) {
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

    /**
     * 디스코드 길드의 역할들을 조회한다.
     * @return
     */
    public List<DiscordMentionDto.Response> getMentions(){
        ChangSolJpaRestriction rs = new ChangSolJpaRestriction();
        return discordMentionRepository.findAll(Sort.by(Sort.Direction.ASC, "roleName")).stream().map(DiscordMention::toResponse).collect(Collectors.toList());
    }

    /**
     * 등록된 리그를 조회하여 오늘 시작하는 트랙 메세지를 푸시함.
     * @param reagueId
     * @return
     */
    public boolean reagueMessagePush(long reagueId){
        //리그 정보 조회
        Reague reague = reagueRepository.findById(reagueId).orElse(null);
        assert reague != null;

        ReagueTrack reagueTrack = reague.getReagueTracks().stream().filter(t -> t.getTrackDate().isEqual(LocalDate.now())).findFirst().orElse(null);
        if(reagueTrack == null){
            return false;
        }

        JDA jda = SioscmsApplication.jda;
        Guild guild = jda.getGuildById(GUILD_KEY);
        assert guild != null;

        //공지 채널을 얻어옴
        NewsChannel newsChannel = guild.getNewsChannelById(reague.getNoticeChannelId());
        assert newsChannel != null;

        MessageEmbed msg = createReagueMessage(reague);

        List<Button> actionButtonList = new ArrayList<>();
        for (ReagueButton reagueButton : reague.getReagueButtons()) {
            if("Primary".equals(reagueButton.getButtonType())){
                actionButtonList.add(Button.primary(String.valueOf(reagueButton.getId()), reagueButton.getButtonName()));
            }else if("Success".equals(reagueButton.getButtonType())){
                actionButtonList.add(Button.success(String.valueOf(reagueButton.getId()), reagueButton.getButtonName()));
            }else if("Secondary".equals(reagueButton.getButtonType())){
                actionButtonList.add(Button.secondary(String.valueOf(reagueButton.getId()), reagueButton.getButtonName()));
            }else{
                actionButtonList.add(Button.danger(String.valueOf(reagueButton.getId()), reagueButton.getButtonName()));
            }
        }

        MessageCreateData msgData = new MessageCreateBuilder()
                .addEmbeds(msg)
                .addContent(reagueTrack.getTrackDate() + " 오늘 리그는 " + reagueTrack.getTrackCode().getCodeLabel() + " 입니다.")
                .addActionRow(actionButtonList)
                .build();

        newsChannel.sendMessage(msgData).queue();
        return true;
    }

    public MessageEmbed createReagueMessage(Reague reague){
        //이벤트를 수정할 새로운 임베디드를 생성
        EmbedBuilder eb = new EmbedBuilder();

        //제목
        eb.setTitle(reague.getTitle());
        //설명
        eb.appendDescription(reague.getDescription());

        //내용 필드 추가
        //inline true 면 세로로 다단, false면 가로로 나뉨
        if(!ObjectUtils.isEmpty(reague.getReagueButtons())){
            for (ReagueButton reagueButton : reague.getReagueButtons()) {
                eb.addField(String.format("%s(0/%d)",reagueButton.getButtonName(), reague.getJoinMemberLimit()), "", true);
            }
        }

        //하단 이미지
        if(!ObjectUtils.isEmpty(reague.getAttachFileGroup())) {
            if(!ObjectUtils.isEmpty(reague.getAttachFileGroup().getAttachFileList())) {
                eb.setImage(SITE_URI + "/api/attach/get-image/" + reague.getAttachFileGroup().getAttachFileList().get(0).getFileName());
            }
        }

        //임베디드 존 좌측 컬러
        Color color;
        if("red".equals(reague.getColor())){
            color = Color.red;
        }else if("blue".equals(reague.getColor())){
            color = Color.blue;
        }else if("yellow".equals(reague.getColor())){
            color = Color.yellow;
        }else if("green".equals(reague.getColor())){
            color = Color.green;
        }else if("white".equals(reague.getColor())){
            color = Color.white;
        }else {
            color = Color.magenta;
        }
        eb.setColor(color);

        //최하단 시간
        eb.setTimestamp(LocalDateTime.now().atZone(ZoneId.of("Asia/Tokyo")));

        //최하단 설명
        eb.setFooter(reague.getId().toString());

        return eb.build();
    }
}
