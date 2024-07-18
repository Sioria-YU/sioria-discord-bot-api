package com.project.sioscms.apps.discord.service;

import com.project.sioscms.SioscmsApplication;
import com.project.sioscms.apps.discord.domain.entity.DiscordMember;
import com.project.sioscms.apps.discord.domain.entity.LeagueButton;
import com.project.sioscms.apps.discord.domain.entity.LeagueTrack;
import com.project.sioscms.apps.discord.domain.entity.LeagueTrackMember;
import com.project.sioscms.apps.discord.domain.repository.DiscordMemberRepository;
import com.project.sioscms.apps.discord.domain.repository.LeagueTrackMemberRepository;
import com.project.sioscms.apps.discord.domain.repository.LeagueTrackRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.NewsChannel;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DiscordModalActionService {

    private final LeagueService leagueService;
    private final DiscordMessageService discordMessageService;

    private final LeagueTrackRepository leagueTrackRepository;
    private final LeagueTrackMemberRepository leagueTrackMemberRepository;
    private final DiscordMemberRepository discordMemberRepository;

    @Transactional
    public void modalInteraction(@NotNull ModalInteractionEvent event){
        String modalId = event.getModalId();
        String messageId = modalId.split("\\|")[0];
        String reagueTrackId = modalId.split("\\|")[1];

        LeagueTrack leagueTrack = leagueTrackRepository.findById(Long.parseLong(reagueTrackId)).orElse(null);
        if(leagueTrack == null){
            event.reply("리그 정보가 존재하지 않습니다.\n관리자에게 문의해 주세요").queue();
            return;
        }

        DiscordMember discordMember = discordMemberRepository.findByUserId(Objects.requireNonNull(event.getMember()).getUser().getId()).orElse(null);
        if(discordMember == null ){
            event.reply("멤버 정보가 존재하지 않습니다.\n관리자에게 문의해 주세요").queue();
            return;
        }

        String nickName = "";
        if(!ObjectUtils.isEmpty(discordMember.getNickname())){
            nickName = discordMember.getNickname();
        }else if(!ObjectUtils.isEmpty(discordMember.getGlobalName())){
            nickName = discordMember.getGlobalName();
        }else{
            nickName = discordMember.getUsername();
        }

        //입력값 검증
        if(!"참여취소".equals(Objects.requireNonNull(event.getValue("joinCancelField")).getAsString())){
            event.reply(nickName + "님 **[참여취소]** 입력 값이 잘못되었습니다.").queue();
            return;
        }

        //공지 채널
        NewsChannel channel = SioscmsApplication.getJda().getNewsChannelById(event.getChannelIdLong());
        assert channel != null;

        //기존 참가자 취소처리
        LeagueTrackMember regueTrackMember = leagueTrackMemberRepository.findByDiscordMember_UserIdAndLeagueTrack_Id(discordMember.getUserId(), leagueTrack.getId()).orElse(null);
        if(regueTrackMember == null){
            event.reply(nickName + "님 리그 참가 정보가 존재하지 않습니다.").queue();
            return;
        }

        String buttonLabel = regueTrackMember.getLeagueButton().getButtonName();
        leagueTrackMemberRepository.delete(regueTrackMember);
        leagueTrackMemberRepository.flush();

        //대기자가 있을 경우 참여자로 변경 처리
        //카테고리가 변경되었기 때문에 전체 카테고리 대기열을 순회하여 대기열처리해줌.
        for (LeagueButton leagueButton : leagueTrack.getLeague().getLeagueButtons()) {
            leagueService.changeLeagueTrackWaiterToLeagueTrackMember(leagueTrack, leagueButton);
        }

        //메세지 찾아와서 변경하기
        channel.retrieveMessageById(messageId).queue((message) -> {
            MessageEmbed embed = message.getEmbeds().get(0);

            //변경사항 반영
            channel.editMessageById(messageId,discordMessageService.editLeagueMessage(leagueTrack, false, embed)).queue();
        });

        //처리 응답(생략 불가능)
        event.reply(nickName + "님 **" + buttonLabel + "** 취소 처리 되었습니다.").queue();
    }


}
