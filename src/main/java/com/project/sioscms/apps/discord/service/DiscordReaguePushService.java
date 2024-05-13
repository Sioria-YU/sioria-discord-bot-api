package com.project.sioscms.apps.discord.service;

import com.project.sioscms.SioscmsApplication;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.springframework.stereotype.Service;

@Service
public class DiscordReaguePushService {

    public void push(){

        JDA jda = SioscmsApplication.jda;

        TextChannel textChannel = jda.getGuildById("935914503214862396").getTextChannelById("935914503214862399");
        textChannel.sendMessage("service 메세지 테스트").queue();
    }
}
