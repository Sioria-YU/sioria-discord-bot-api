package com.project.sioscms.apps.discord.service;

import com.project.sioscms.SioscmsApplication;
import com.project.sioscms.apps.discord.domain.dto.DiscordMentionDto;
import com.project.sioscms.apps.discord.domain.entity.DiscordMember;
import com.project.sioscms.apps.discord.domain.entity.DiscordMention;
import com.project.sioscms.apps.discord.domain.entity.DiscordUserMension;
import com.project.sioscms.apps.discord.domain.entity.LeagueTrack;
import com.project.sioscms.apps.discord.domain.repository.DiscordMemberRepository;
import com.project.sioscms.apps.discord.domain.repository.DiscordMentionRepository;
import com.project.sioscms.apps.discord.domain.repository.DiscordUserMensionRepository;
import com.project.sioscms.apps.discord.domain.repository.LeagueTrackRepository;
import com.project.sioscms.common.utils.jpa.restriction.ChangSolJpaRestriction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.NewsChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.util.*;
import java.util.stream.Collectors;


@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DiscordBotApiService {
    @Value("${discord.guild-key}")
    private String GUILD_KEY;

    private final LeagueService leagueService;

    private final DiscordMemberRepository discordMemberRepository;
    private final DiscordMentionRepository discordMentionRepository;
    private final DiscordUserMensionRepository discordUserMensionRepository;
    private final LeagueTrackRepository leagueTrackRepository;

    /**
     * JDA API를 불러온다
     * @return
     */
    private JDA getJDA() {
        return SioscmsApplication.getJda();
    }

    //region 디스코드 공지 채널 목록
    /**
     * 디스코드 공지 채널 목록을 얻어온다.
     *
     * @return
     */
    public List<Map<String, String>> getNewsChannels() {
        Guild guild = getJDA().getGuildById(GUILD_KEY);
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
    //endregion 디스코드 공지 채널 목록

    //region 디스코드 길드 가입자 동기화
    /**
     * 디스코드 길드 가입자들을 불러와 저장한다.
     *
     * @return
     * @throws InterruptedException
     */
    @Transactional
    public boolean memberRefresh() throws InterruptedException {
        JDA jda = getJDA();

        Guild guild = jda.getGuildById(GUILD_KEY);
        assert guild != null;

        List<Member> memberList = new ArrayList<>();
        guild.loadMembers().onSuccess(memberList::addAll);
        //3초 대기
        Thread.sleep(3000);

        //디스코드 전체 멤버 목록을 가져온다.
        if (memberList.size() > 0) {
            for (Member member : memberList) {
                if (discordMemberRepository.countByUserId(member.getUser().getId()) < 1) {
                    User user = member.getUser();
                    DiscordMember newMember = new DiscordMember();
                    newMember.setUserId(user.getId());
                    newMember.setUsername(user.getName());
                    newMember.setGlobalName(user.getGlobalName());
                    newMember.setNickname(member.getNickname());
                    newMember.setUserMension(user.getAsMention());
                    newMember.setIsDeleted(false);

                    //길드 멤버 별명 동기화
                    convertToGuildMention(guild, newMember, member);

                    //멤버 권한 저장
                    if (member.getRoles() != null && member.getRoles().size() > 0) {
                        Set<DiscordUserMension> discordUserMensionSet = new HashSet<>();
                        for (Role role : member.getRoles()) {
                            DiscordMention mention = discordMentionRepository.findByRoleId(role.getId()).orElse(null);
                            if (mention != null) {
                                DiscordUserMension discordUserMension = new DiscordUserMension();
                                discordUserMension.setDiscordMention(mention);
                                discordUserMension.setDiscordMember(newMember);
                                discordUserMensionRepository.save((discordUserMension));
                                discordUserMensionSet.add(discordUserMension);
                            }
                        }
                        if (discordUserMensionSet.size() > 0) {
                            newMember.setDiscordUserMensionSet(discordUserMensionSet);
                        }
                    }
                    discordMemberRepository.save(newMember);
                }//기존 회원 정보 최신화
                else {
                    DiscordMember discordMember = discordMemberRepository.findByUserId(member.getUser().getId()).orElse(null);
                    if (discordMember == null) {
                        continue;
                    }

                    User user = member.getUser();
                    discordMember.setUserId(user.getId());
                    discordMember.setUsername(user.getName());
                    discordMember.setNickname(member.getNickname());
                    discordMember.setGlobalName(user.getGlobalName());
                    discordMember.setUserMension(user.getAsMention());
                    discordMember.setIsDeleted(false);

                    //길드 멤버 별명 동기화
                    convertToGuildMention(guild, discordMember, member);

                    //멤버 권한 저장
                    if (member.getRoles() != null && member.getRoles().size() > 0) {
                        Set<DiscordUserMension> discordUserMensionSet = discordUserMensionRepository.findAllByDiscordMember_Id(discordMember.getId());
                        for (Role role : member.getRoles()) {
                            DiscordMention mention = discordMentionRepository.findByRoleId(role.getId()).orElse(null);
                            if (mention != null) {
                                if (!ObjectUtils.isEmpty(discordUserMensionSet)) {
                                    //추가 권한을 부여했을 경우
                                    if (discordUserMensionSet.stream().noneMatch(v -> v.getDiscordMention().getId().equals(mention.getId()))) {
                                        DiscordUserMension discordUserMension = new DiscordUserMension();
                                        discordUserMension.setDiscordMention(mention);
                                        discordUserMension.setDiscordMember(discordMember);
                                        discordUserMensionRepository.save((discordUserMension));
                                        discordUserMensionSet.add(discordUserMension);
                                    }
                                } else {
                                    DiscordUserMension discordUserMension = new DiscordUserMension();
                                    discordUserMension.setDiscordMention(mention);
                                    discordUserMension.setDiscordMember(discordMember);
                                    discordUserMensionRepository.save((discordUserMension));
                                    discordUserMensionSet.add(discordUserMension);
                                }
                            }
                        }

                        //기존 권한 중에 삭제된게 있다면 삭제처리
                        List<DiscordUserMension> nonmatchMension = discordUserMensionSet.stream().filter(v -> member.getRoles().stream().noneMatch(r -> r.getId().equals(v.getDiscordMention().getRoleId()))).toList();
                        if (nonmatchMension != null && nonmatchMension.size() > 0) {
                            discordUserMensionRepository.deleteAll(nonmatchMension);
                        }

                        if (discordUserMensionSet.size() > 0) {
                            discordMember.setDiscordUserMensionSet(discordUserMensionSet);
                        }
                    }
                }
            }
            return true;
        } else {
            return false;
        }
    }
    //endregion 디스코드 길드 가입자 동기화

    //region 디스코드 역할 멘션 동기화
    /**
     * 디스코드 역할 멘션들을 불러와 저장한다.
     *
     * @return
     */
    @Transactional
    public boolean rolesRefresh() {
        JDA jda = getJDA();

        Guild guild = jda.getGuildById(GUILD_KEY);
        assert guild != null;

        List<Role> roleList = guild.getRoles();

        if (roleList.size() > 0) {
            for (Role role : roleList) {
                DiscordMention mention = discordMentionRepository.findByRoleId(role.getId()).orElse(null);
                if (mention == null) {
                    mention = new DiscordMention();
                    mention.setRoleId(role.getId());
                    mention.setRoleName(role.getName());
                    mention.setMention(role.getAsMention());
                    discordMentionRepository.save(mention);
                }
            }
            return true;
        } else {
            return false;
        }
    }
    //endregion 디스코드 역할 멘션 동기화

    //region 디스코드 별명 동기화(길드 별명이 없을 경우 강제로 주입)
    @Transactional
    public void convertToGuildMention(Guild guild, DiscordMember discordMember, Member member){
        if(ObjectUtils.isEmpty(discordMember.getNickname())){
            String nickName = ObjectUtils.isEmpty(member.getUser().getGlobalName())? member.getUser().getName() : member.getUser().getGlobalName();
            discordMember.setNickname(nickName);
            guild.modifyNickname(member, nickName).queue();
        }
    }

    //endregion

    //region 디스코드 길드 역할 조회
    /**
     * 디스코드 길드의 역할들을 조회한다.
     *
     * @return
     */
    public List<DiscordMentionDto.Response> getMentions() {
        ChangSolJpaRestriction rs = new ChangSolJpaRestriction();
        return discordMentionRepository.findAll(Sort.by(Sort.Direction.ASC, "roleName")).stream().map(DiscordMention::toResponse).collect(Collectors.toList());
    }
    //endregion 디스코드 길드 역할 조회

    //region 등록된 리그를 조회하여 오늘 시작하는 트랙 메세지 푸시
    /**
     * 등록된 리그를 조회하여 오늘 시작하는 트랙 메세지를 푸시함.
     *
     * @param leagueId
     * @return
     */
    public boolean leagueMessagePush(long leagueId) {
        return leagueService.leagueMessagePush(leagueId);
    }
    //endregion 등록된 리그를 조회하여 오늘 시작하는 트랙 메세지 푸시

    //region 오늘 시작되는 리그가 있는지 카운트 조회
    /**
     * 오늘 시작되는 리그가 있는지 카운트를 조회한다.
     *
     * @return
     */
    public long countLeagueTrackStartToday() {
        return leagueTrackRepository.countAllByTrackDateAndLeague_IsDeleted(LocalDate.now(), false);
    }
    //endregion 오늘 시작되는 리그가 있는지 카운트 조회

    //region 오늘 시작되는 리그트랙 목록 조회
    /**
     * 오늘 시작되는 리그트랙 목록을 조회한다.
     *
     * @return
     */
    public List<LeagueTrack> getLeagueTrackStartToday() {
        return leagueTrackRepository.findAllByTrackDateAndLeague_IsDeleted(LocalDate.now(), false);
    }
    //endregion 오늘 시작되는 리그트랙 목록 조회

    //region 슬래시 커맨드 이벤트
    /**
     * 슬래시 커맨드 이벤트
     * @param event
     */
    public void slashCommandInteraction(@NotNull SlashCommandInteractionEvent event){
        if(event.getName().contains("일정")){
            slashWeekdayScheduleEvent(event);
        }else{
            System.out.println("eventName ::: " + event.getName());
        }

    }
    //endregion 슬래시 커맨드 이벤트

    //region 슬래시 커맨드 일정 이벤트
    public void slashWeekdayScheduleEvent(SlashCommandInteractionEvent event){
        LocalDate today = LocalDate.now();
//        int day = today.get(ChronoField.DAY_OF_WEEK);
//        LocalDate start = today.minusDays(day-1);
        LocalDate end;

        if(!ObjectUtils.isEmpty(event.getOption("days")) && !ObjectUtils.isEmpty(event.getOption("days").getAsInt()) && event.getOption("days").getAsInt() > 0){
            int plusDays = event.getOption("days").getAsInt();
            end = today.plusDays(plusDays);
        }else{
            end = today.plusDays(7);
        }

        List<LeagueTrack> leagueTracks = leagueTrackRepository.findAllByTrackDateBetweenOrderByTrackDateAsc(today, end);
        leagueTracks = leagueTracks.stream()
                .filter(t -> today.isBefore(t.getLeague().getEndDate()) || today.isEqual(t.getLeague().getEndDate()))
                .collect(Collectors.toList());

        StringBuilder out = new StringBuilder();
        if(leagueTracks != null){
            for (LeagueTrack leagueTrack : leagueTracks) {
                out.append("**[").append(leagueTrack.getTrackDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))).append("]** ");
                out.append(leagueTrack.getLeague().getLeagueName()).append(" - ");
                out.append(leagueTrack.getTrackCode().getCodeLabel());
                out.append("(").append(leagueTrack.getLeague().getLeagueTime()).append(")\n");
            }
        }

        event.reply(out.toString()).queue();
    }
    //endregion 슬래시 커맨드 일정 이벤트

}
