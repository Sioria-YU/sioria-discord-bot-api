package com.project.sioscms.apps.discord.service;

import com.project.sioscms.SioscmsApplication;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

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
}
