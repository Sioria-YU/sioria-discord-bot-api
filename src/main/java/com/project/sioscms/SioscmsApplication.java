package com.project.sioscms;

import com.project.sioscms.common.ApplicationContextProvider;
import com.project.sioscms.discord.DiscordBotToken;
import com.project.sioscms.discord.DiscordEventListener;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Objects;

@Slf4j
@EnableJpaAuditing
@EnableScheduling
@SpringBootApplication
public class SioscmsApplication{
    private static JDA jda;
    private static DiscordBotToken token;

    public static void setJda(JDA jda){
        SioscmsApplication.jda = jda;
    }

    public static void setToken(DiscordBotToken token){
        SioscmsApplication.token = token;
    }

    public static JDA getJda() {
        if(jda == null){
            ApplicationContext context = ApplicationContextProvider.getApplicationContext();
            try {
                jda = JDABuilder.createDefault(getToken().getToken())
//                    .setActivity(Activity.playing("업그레이드 진행 중..."))
                        .setActivity(Activity.playing("ESK 리그 대기"))
                        .setAutoReconnect(true)
                        .setLargeThreshold(250)
                        .setMemberCachePolicy(MemberCachePolicy.ALL)
                        .enableIntents(GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MEMBERS)
                        .addEventListeners(new DiscordEventListener(context))
                        .build()
                        .awaitReady();

//                Objects.requireNonNull(jda.getGuildById("1104359385909694534")).upsertCommand("일정", "금주 리그 일정을 알려줍니다.").queue();
//                Objects.requireNonNull(jda.getGuildById("1104359385909694534")).updateCommands().queue();
            } catch (InterruptedException e) {
                log.error("jda 생성 오류 : " + e.getMessage());
            }
        }
        return jda;
    }

    public static DiscordBotToken getToken(){
        if(token == null) {
            ApplicationContext context = ApplicationContextProvider.getApplicationContext();
            token = context.getBean(DiscordBotToken.class);
        }
        return token;
    }

    public static void main(String[] args) {
        SpringApplication.run(SioscmsApplication.class, args);
    }

    /**
     * 어플리케이션이 준비되었을 때 발생하는 이벤트로 jda를 초기화한다.
     */
    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        setToken(getToken());
        setJda(getJda());
    }
}
