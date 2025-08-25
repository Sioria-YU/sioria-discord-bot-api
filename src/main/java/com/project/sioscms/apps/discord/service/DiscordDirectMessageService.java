package com.project.sioscms.apps.discord.service;

import com.project.sioscms.SioscmsApplication;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.concrete.NewsChannel;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class DiscordDirectMessageService {

    @Value("${discord.guild-key}")
    private String GUILD_KEY;

    //region 버튼 이벤트에서 특정 유저에게 dm 발송
    /**
     * 특정 유저에게 dm 알림
     *
     * @param text
     * @param userId
     */
    public void userDmSendByUserId(String text, String userId) {
        if (ObjectUtils.isEmpty(text) || ObjectUtils.isEmpty(userId)) {
            return;
        }

        Objects.requireNonNull(Objects.requireNonNull(SioscmsApplication.getJda().getGuildById(GUILD_KEY))
                        .getMemberById(userId))
                .getUser()
                .openPrivateChannel()
                .queue(
                        channel -> {
                            channel.sendMessage(text).queue();
                        });
    }
    //endregion  버튼 이벤트에서 특정 유저에게 dm 발송

    //region 관리자 참여신청 알림 전용으로 메세지 발송
    public void userLeagueJoinStatMessageSend(String text) {
        if (ObjectUtils.isEmpty(text)) {
            return;
        }

        Objects.requireNonNull(Objects.requireNonNull(SioscmsApplication.getJda().getGuildById(GUILD_KEY))
                        .getChannelById(NewsChannel.class,"1406132814554075386")).sendMessage(text).queue();
    }
    //endregion

    //region 특정 채널에 알림 메세지 발송
    public void channelMessageSend(String channelId, String text) {
        if (ObjectUtils.isEmpty(channelId) || ObjectUtils.isEmpty(text)) {
            return;
        }

        Objects.requireNonNull(Objects.requireNonNull(SioscmsApplication.getJda().getGuildById(GUILD_KEY))
                .getChannelById(NewsChannel.class, channelId)).sendMessage(text).queue();
    }
    //endregion

    //region 특정 채널에 승인/거부 임베딩 메세지 발송
    public void channelEmbedMessageSend(String eventName, String channelId, String header, String title, String body, String footer) {
        if (ObjectUtils.isEmpty(eventName) || ObjectUtils.isEmpty(channelId) || ObjectUtils.isEmpty(title) || ObjectUtils.isEmpty(body)) {
            return;
        }
        EmbedBuilder embedBuilder = new EmbedBuilder();

        //제목
        embedBuilder.setTitle(title);

        //설명
        embedBuilder.appendDescription(body);
        //시간
        embedBuilder.setTimestamp(LocalDateTime.now().atZone(ZoneId.of("Asia/Tokyo")));
        embedBuilder.setFooter(footer);

        List<Button> actionButtonList = new ArrayList<>();
        actionButtonList.add(Button.success(eventName + "-confirm", "승인"));
        actionButtonList.add(Button.danger(eventName + "-reject", "거부"));

        NewsChannel newsChannel = Objects.requireNonNull(SioscmsApplication.getJda().getGuildById(GUILD_KEY)).getNewsChannelById(channelId);
        newsChannel.sendMessage(new MessageCreateBuilder()
                        .addContent(header)
                        .addEmbeds(embedBuilder.build())
                        .addActionRow(actionButtonList)
                        .build())
                .queue();
    }
    //endregion
}
