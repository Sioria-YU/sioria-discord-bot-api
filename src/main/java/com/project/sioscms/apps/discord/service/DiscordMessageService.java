package com.project.sioscms.apps.discord.service;

import com.project.sioscms.apps.attach.domain.entity.AttachFile;
import com.project.sioscms.apps.discord.domain.entity.*;
import com.project.sioscms.apps.discord.domain.repository.LeagueDiscordMentionRepository;
import com.project.sioscms.apps.discord.domain.repository.LeagueTrackMemberRepository;
import com.project.sioscms.apps.discord.domain.repository.LeagueTrackWaitRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;
import net.dv8tion.jda.api.utils.messages.MessageEditData;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.awt.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DiscordMessageService {
    @Value("${cms.site.uri}")
    private String SITE_URI;

    private final LeagueTrackMemberRepository leagueTrackMemberRepository;
    private final LeagueTrackWaitRepository leagueTrackWaitRepository;
    private final LeagueDiscordMentionRepository leagueDiscordMentionRepository;

    //region 리그 알림 메세지 생성
    /**
     * 리그 시작 알림 메세지 생성
     * @param league
     * @param leagueTrack
     * @return
     */
    public MessageCreateData createLeaguePushMessage(League league, LeagueTrack leagueTrack){
        MessageEmbed msg = createLeagueEmbedMessage(league, leagueTrack);

        //버튼 세팅
        List<Button> actionButtonList = getLeagueButtons(league);

        return new MessageCreateBuilder()
                .addEmbeds(msg)
                .addContent(getLeagueContentMessage(leagueTrack))
                .addActionRow(actionButtonList)
                .build();
    }
    //endregion 리그 알림 메세지 생성

    //region 임베디드 메세지 생성
    /**
     * 임베디드 메세지 생성
     *
     * @param league
     * @return
     */
    public MessageEmbed createLeagueEmbedMessage(League league, LeagueTrack leagueTrack) {
        //이벤트를 수정할 새로운 임베디드를 생성
        EmbedBuilder embedBuilder = new EmbedBuilder();

        //제목
        embedBuilder.setTitle(league.getTitle());
        //설명
        embedBuilder.appendDescription(league.getDescription());

        //리그 카테고리 추가
        setLeagueCategory(leagueTrack, embedBuilder, false);

        //임베디드 존 좌측 컬러
        embedBuilder.setColor(getEmbedColor(league));

        //최하단 시간
        embedBuilder.setTimestamp(LocalDateTime.now().atZone(ZoneId.of("Asia/Tokyo")));

        //최하단 설명
        embedBuilder.setFooter(leagueTrack.getId().toString());

        //하단 이미지
        setLeagueImage(league, embedBuilder);

        return embedBuilder.build();
    }
    //endregion 임베디드 메세지 생성

    //region 수정 메세지 생성
    /**
     * embed 수정 메새지 생성
     * @param leagueTrack
     * @param isClosed
     * @param embed
     * @return
     */
    @Transactional
    public MessageEditData editLeagueMessage(LeagueTrack leagueTrack, boolean isClosed, MessageEmbed embed){
        League league = leagueTrack.getLeague();

        //이벤트 메세지로부터 임베디드 메세지를 받아와 필스를 수정한다.
        EmbedBuilder embedBuilder = new EmbedBuilder(embed);
        embedBuilder.clearFields();

        //마감일 경우 타이틀에 마감 표기함
        if (isClosed) {
            embedBuilder.setTitle(league.getTitle() + "[마감]");
        } else {
            embedBuilder.setTitle(league.getTitle());
        }

        long leagueTrackId = Long.parseLong(embed.getFooter().getText());

        //리그 카테고리 추가
        setLeagueCategory(leagueTrack, embedBuilder, isClosed);

        //하단 이미지
        setLeagueImage(league, embedBuilder);

        //버튼 세팅
        List<Button> actionButtonList = getLeagueButtons(league);

        //수정 메세지 세팅
        return new MessageEditBuilder()
                .setEmbeds(embedBuilder.build())
                .setActionRow(actionButtonList)
                .setContent(getLeagueContentMessage(leagueTrack))
                .build();
    }
    //endregion 수정 메세지 생성

    //region 리그 버튼 카테고리 목록 생성
    public void setLeagueCategory(LeagueTrack leagueTrack, EmbedBuilder embedBuilder, boolean isClosed){
        League league = leagueTrack.getLeague();

        //inline true 면 세로로 다단, false면 가로로 나뉨
        if (!ObjectUtils.isEmpty(league.getLeagueButtons())) {
            //참여자목록
            for (LeagueButton leagueButton : league.getLeagueButtons()) {
                List<LeagueTrackMember> regueTrackMemberList = leagueTrackMemberRepository.findAllByLeagueTrack_IdAndLeagueButton_IdOrderByCreatedDateTimeAscUpdatedDateTimeAsc(leagueTrack.getId(), leagueButton.getId());

                String joinMembers = "";
                for (LeagueTrackMember trackMember : regueTrackMemberList) {
                    //길드 닉네임이 있다면 1순위, 아니면 전체 닉네임 2순위, 아니면 기본 닉네임(아이디) 3순위
                    String userName = "";
                    if (!ObjectUtils.isEmpty(trackMember.getDiscordMember().getNickname())) {
                        userName = trackMember.getDiscordMember().getNickname();
                    } else if (!ObjectUtils.isEmpty(trackMember.getDiscordMember().getGlobalName())) {
                        userName = trackMember.getDiscordMember().getGlobalName();
                    } else {
                        userName = trackMember.getDiscordMember().getUsername();
                    }

                    if ("".equals(joinMembers)) {
                        joinMembers = userName;
                    } else {
                        joinMembers += "\n" + userName;
                    }
                }
                //표기 여부 허용일 때 또는 마감되었을 때만 참가자 목록을 만든다.
                if(league.getIsJoinDisplay() || isClosed)
                    embedBuilder.addField(String.format("%s(%d/%d)", leagueButton.getButtonName(), regueTrackMemberList.size(), league.getJoinMemberLimit()), joinMembers, true);
            }

            //대기자 존재 유무 확인
            boolean isWaiting = leagueTrackWaitRepository.countByLeagueTrack_Id(leagueTrack.getId()) > 0;
            if(isWaiting) {
                boolean isFirst = true;
                for (LeagueButton leagueButton : league.getLeagueButtons()) {
                    List<LeagueTrackWait> waitList = leagueTrackWaitRepository.findAllByLeagueTrack_IdAndLeagueButton_IdOrderByCreatedDateTimeAsc(leagueTrack.getId(), leagueButton.getId());

                    if(!ObjectUtils.isEmpty(waitList)) {
                        String waitMembers = "";
                        for (LeagueTrackWait leagueTrackWait : waitList) {
                            //길드 닉네임이 있다면 1순위, 아니면 전체 닉네임 2순위, 아니면 기본 닉네임(아이디) 3순위
                            String userName = "";
                            if (!ObjectUtils.isEmpty(leagueTrackWait.getDiscordMember().getNickname())) {
                                userName = leagueTrackWait.getDiscordMember().getNickname();
                            } else if (!ObjectUtils.isEmpty(leagueTrackWait.getDiscordMember().getGlobalName())) {
                                userName = leagueTrackWait.getDiscordMember().getGlobalName();
                            } else {
                                userName = leagueTrackWait.getDiscordMember().getUsername();
                            }

                            if ("".equals(waitMembers)) {
                                waitMembers = userName;
                            } else {
                                waitMembers += "\n" + userName;
                            }
                        }

                        //처음 추가일 경우 세로로 처리
                        if (isFirst) {
                            isFirst = false;
                            embedBuilder.addField(String.format("%s 대기열(%d)", leagueButton.getButtonName(), waitList.size()), waitMembers, false);
                        } else {
                            embedBuilder.addField(String.format("%s 대기열(%d)", leagueButton.getButtonName(), waitList.size()), waitMembers, true);
                        }
                    }
                }
            }
        }
    }
    //endregion 리그 버튼 카테고리 목록 생성

    //region 리그 버튼리스트를 얻어옴
    /**
     * 리그 버튼리스트를 얻어옴
     * @param league
     * @return
     */
    public List<Button> getLeagueButtons(League league){
        List<Button> actionButtonList = new ArrayList<>();
        for (LeagueButton leagueButton : league.getLeagueButtons()) {
            if ("Primary".equals(leagueButton.getButtonType())) {
                actionButtonList.add(Button.primary(String.valueOf(leagueButton.getId()), leagueButton.getButtonName()));
            } else if ("Success".equals(leagueButton.getButtonType())) {
                actionButtonList.add(Button.success(String.valueOf(leagueButton.getId()), leagueButton.getButtonName()));
            } else if ("Secondary".equals(leagueButton.getButtonType())) {
                actionButtonList.add(Button.secondary(String.valueOf(leagueButton.getId()), leagueButton.getButtonName()));
            } else {
                actionButtonList.add(Button.danger(String.valueOf(leagueButton.getId()), leagueButton.getButtonName()));
            }
        }
        //기본 버튼 새팅(새로고침, 마감)
        actionButtonList.add(Button.secondary("league-refresh", "새로고침"));
        actionButtonList.add(Button.danger("league-close", "마감"));

        return actionButtonList;
    }
    //endregion 리그 버튼리스트를 얻어옴

    //region 참여 가능 멘션을 얻어옴
    /**
     * 참여 가능 멘션을 얻어옴
     * @param league
     * @return
     */
    public String getLeagueAlertMensions(League league){
        List<LeagueDiscordMention> joinAceptMentionList = leagueDiscordMentionRepository.findAllByLeague_Id(league.getId());
        String alertMentions = "";
        for (LeagueDiscordMention joinAceptMention : joinAceptMentionList) {
            if ("".equals(alertMentions)) {
                alertMentions = joinAceptMention.getDiscordMention().getMention();
            } else {
                alertMentions += " " + joinAceptMention.getDiscordMention().getMention();
            }
        }

        return alertMentions;
    }
    //endregion 참여 가능 멘션을 얻어옴

    //region 임베디드 메세지 컬러를 얻어옴
    /**
     * 임베디드 메세지 컬러를 얻어옴
     * @param league
     * @return
     */
    public Color getEmbedColor(League league){
        Color color;
        if ("red".equals(league.getColor())) {
            color = Color.red;
        } else if ("blue".equals(league.getColor())) {
            color = Color.blue;
        } else if ("yellow".equals(league.getColor())) {
            color = Color.yellow;
        } else if ("green".equals(league.getColor())) {
            color = Color.green;
        } else if ("white".equals(league.getColor())) {
            color = Color.white;
        } else {
            color = Color.magenta;
        }

        return color;
    }
    //endregion 임베디드 메세지 컬러를 얻어옴

    //region 리그 메세지 이미지 세팅
    /**
     * 리그 메세지 이미지 세팅
     * @param league
     * @param embedBuilder
     */
    public void setLeagueImage(League league, EmbedBuilder embedBuilder){
        if (!ObjectUtils.isEmpty(league.getAttachFileGroup())) {
            if (!ObjectUtils.isEmpty(league.getAttachFileGroup().getAttachFileList())) {
                AttachFile file = league.getAttachFileGroup().getAttachFileList().get(0);
                String filePath = "/api/attach/get-image/";
                embedBuilder.setImage(SITE_URI + filePath + file.getFileName());
            }
        }
    }
    //endregion 리그 메세지 이미지 세팅

    //region 리그 컨텐츠 메세지를 얻어온다.
    /**
     * 리그 컨텐츠 메세지를 얻어온다.
     * @param leagueTrack
     * @return
     */
    public String getLeagueContentMessage(LeagueTrack leagueTrack){
        String content = getLeagueAlertMensions(leagueTrack.getLeague());
        content += "\n**[" + leagueTrack.getTrackDate() + "]** 오늘 리그는 " + leagueTrack.getTrackCode().getCodeLabel() + " 입니다.";
        content += "\n시작 시간은 **" + leagueTrack.getLeague().getLeagueTime() + "**입니다.";
        return content;
    }
    //endregion 리그 컨텐츠 메세지를 얻어온다.
}
