package com.project.sioscms.discord;

import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.thread.member.ThreadMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class DiscordEventListener extends ListenerAdapter {

    private static final String WELCOME_MESSAGE = "디스코드_가입신청 게시판에 가입신청 해주시고 서버규칙 게시판 필독 부탁드립니다";

    @Override
    public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event) {
        List<TextChannel> channels = event.getJDA().getTextChannelsByName("general", true);
        for(TextChannel ch : channels){
            ch.sendMessage(event.getMember().getAsMention() + WELCOME_MESSAGE).queue();
        }
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        User user = event.getAuthor();
        TextChannel channel = event.getChannel().asTextChannel();
        Message message = event.getMessage();

        if(!user.isBot() && message.getContentDisplay().length() > 0){
            if("join".equals(message.getContentDisplay())){
                channel.sendMessage(user.getName() + WELCOME_MESSAGE).queue();
            }
//            channel.sendMessage("메아리 - " + message.getContentDisplay()).queue();
        }
    }
}
