package com.project.sioscms.discord;

import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;
import net.dv8tion.jda.api.utils.messages.MessageEditData;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
@Slf4j
public class DiscordEventListener extends ListenerAdapter {

    private static final String WELCOME_MESSAGE = " 게시판에 **가입신청** 해주시고 서버규칙 게시판 **필독** 부탁드립니다.";
    private static final String JOIN_BOARD_NAME = "디스코드_가입신청";

    @Override
    public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event) {
        List<TextChannel> channels = event.getJDA().getTextChannelsByName("general", true);
        TextChannel joinedChannel = null;

        for(TextChannel ch : channels){
            if(JOIN_BOARD_NAME.equals(ch.getName())){
                joinedChannel = ch;
                break;
            }
        }

        log.info("channels length ::" + channels.size());
        for(TextChannel ch : channels){
            if(joinedChannel != null) {
                ch.sendMessage(event.getMember().getAsMention() + "님 환영합니다.\n" + joinedChannel.getAsMention() + WELCOME_MESSAGE).queue();
            }else{
                ch.sendMessage(event.getMember().getAsMention() + "님 환영합니다.\n**디스코드_가입신청**" + WELCOME_MESSAGE).queue();
            }
        }
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        /*User user = event.getAuthor();
        TextChannel channel = event.getChannel().asTextChannel();
        Message message = event.getMessage();

        List<TextChannel> channels = event.getJDA().getTextChannelsByName("general", true);
        TextChannel joinedChannel = null;

        for(TextChannel ch : channels){
            if("general".equals(ch.getName())){
                joinedChannel = ch;
                break;
            }
        }
        final List<String> roleList = event.getJDA().getRoles().stream().map(Role::getName).toList();
        final List<Long> userIds = event.getJDA().getUsers().stream().map(User::getIdLong).toList();

        System.out.println("roleList ::: ");
        roleList.forEach(r -> System.out.print(r + ","));

        System.out.println("userIds ::: ");
        userIds.forEach(u -> System.out.print(u + ","));

        if(Objects.requireNonNull(event.getMember()).hasPermission(Permission.MANAGE_ROLES)){
            System.out.println("MANAGE_ROLES");
        }else{
            System.out.println("Non MANAGE_ROLES");
        }

        if(!user.isBot() && message.getContentDisplay().length() > 0){
            if("!!join".equals(message.getContentDisplay())){
                if(joinedChannel != null) {
                    channel.sendMessage(user.getAsMention() + "님 환영합니다.\n" + joinedChannel.getAsMention() + WELCOME_MESSAGE).queue();
                }else {
                    channel.sendMessage(user.getAsMention() + "님 환영합니다.\n**디스코드_가입신청**" + WELCOME_MESSAGE).queue();
                }
            }else if("!!help".equals(message.getContentDisplay())){
                String commendList = "**[명령어 모음]**" + "\n";
                commendList += "========================" + "\n";
                commendList += "1. !!join : 신규 유저 유입시 코멘트 확인" + "\n";
                commendList += "2. !!echo [comment] : 입력한 텍스트 메아리" + "\n";
                commendList += "3. !!button : 버튼테스트" + "\n";
                commendList += "4. !!board : 게시보드 테스트" + "\n";
                commendList += "5.unknown" + "\n";
                commendList += "6.unknown" + "\n";
                commendList += "7.unknown" + "\n";
                commendList += "========================" + "\n";
                channel.sendMessage(commendList).queue();
            }else if(message.getContentDisplay().startsWith("!!echo")){
                channel.sendMessage(user.getAsMention() + " say : " + message.getContentDisplay().replace("!!echo ", "")).queue();
            }else if(message.getContentDisplay().startsWith("!!button")){
                List<Button> buttonList = new ArrayList<>();
                buttonList.add(Button.success("Join", "Join"));
                buttonList.add(Button.danger("Impossible", "Impossible"));
                channel.sendMessageComponents(ActionRow.of(Button.success("Join", "Join"), Button.danger("Impossible", "Impossible"))).queue();
            }else if(message.getContentDisplay().startsWith("!!board")){
                MessageEmbed messageEmbed = getEmbedContent(event);
                MessageCreateData msgData = new MessageCreateBuilder()
                        .addEmbeds(messageEmbed)
                        .addActionRow(Button.success("Join", "참가"), Button.danger("Impossible", "불참"))
                        .build();

                channel.sendMessage(msgData).queue();
//                channel.sendMessageEmbeds(eb.build()).queue();
//                channel.sendMessageComponents(ActionRow.of(Button.success("Join", "참가"), Button.danger("Impossible", "불참"))).queue();
            }
        }*/
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        if(event.getButton().getId().equals("Join")){
//            event.getChannel().asTextChannel().sendMessage("참가 버튼 클릭").queue();

            /*
            버튼 클릭에 따라 참여자에 변동 값을 저장하고
            변동된 참여 정보를 embedContent 데이터에 반영해야함
            현재 getEmbedContent는 최초 데이터를 생성하기 위해 만든 것이고
            updateEmbedContent로 수정될 내용들을 고쳐야함.
            todo:
                1. JPA Data Model 생성
                2. 해당 모델에 데이터 매킹
                3. content 수정 로직 구현
             */
            MessageEmbed messageEmbed = getEmbedContent(event);

            MessageEditData messageEditData = new MessageEditBuilder()
                    .setEmbeds(messageEmbed)
                    .setActionRow(Button.success("Join", "참가"), Button.danger("Impossible", "불참"))
                    .build();

            //이벤트의 임베디드를 수정함
//            event.replyEmbeds(eb.build()).queue();
            //이벤트의 메세지를 수정함
            event.editMessage(messageEditData).queue();
        }else if(event.getButton().getId().equals("Impossible")){
            event.getChannel().asTextChannel().sendMessage("불참 버튼 클릭").queue();
        }



//        event.reply("return msg").setEphemeral(true).queue();
    }

    private MessageEmbed getEmbedContent(Event event){
        //이벤트를 수정할 새로운 임베디드를 생성
        EmbedBuilder eb = new EmbedBuilder();
        //제목
        eb.setTitle("Test Reague Notice");
        //설명
        eb.appendDescription("테스트 리그 작성 화면 폼입니다.\n 이 폼은 리그의 봇 알림을 위해 작성하고 있는 테스트입니다.");

        //내용 필드 추가
        //inline true 면 세로로 다단, false면 가로로 나뉨
        eb.addField("참가(0/0)", "sioria\nsioria\nsioria\n", true);
        eb.addField("불참(0/0)", "-", true);

        //하단 이미지
        eb.setImage("https://media.contentapi.ea.com/content/dam/ea/f1/f1-23/common/featured-image/f123-featured-image-16x9.jpg.adapt.crop191x100.1200w.jpg");

        //임베디드 존 좌측 컬러
        eb.setColor(Color.orange);

        //최하단 시간
        eb.setTimestamp(LocalDateTime.now().atZone(ZoneId.of("Asia/Tokyo")));

        //최하단 설명
//        eb.setFooter(event.getUser().getName());

        return eb.build();
    }



}
