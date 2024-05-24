package com.project.sioscms;

import com.project.sioscms.discord.DiscordBotToken;
import com.project.sioscms.discord.DiscordEventListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class SioscmsApplication {
    public static JDA jda;

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(SioscmsApplication.class, args);

        //jda init
        DiscordBotToken token = context.getBean(DiscordBotToken.class);

        jda = JDABuilder.createDefault(token.getToken())
                .setActivity(Activity.playing("ESK 리그 대기"))
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .enableIntents(GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MEMBERS)
                .addEventListeners(new DiscordEventListener(context))
                .build();
    }

}
