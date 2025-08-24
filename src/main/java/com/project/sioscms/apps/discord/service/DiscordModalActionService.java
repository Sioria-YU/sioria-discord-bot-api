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

import java.util.Locale;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DiscordModalActionService {

    private final LeagueService leagueService;
    private final DiscordMessageService discordMessageService;
    private final DiscordDirectMessageService discordDirectMessageService;

    private final LeagueTrackRepository leagueTrackRepository;
    private final LeagueTrackMemberRepository leagueTrackMemberRepository;
    private final DiscordMemberRepository discordMemberRepository;

    private final String JOIN_CHANNEL_ID = "1409015599845343333";
    private final String CHANGE_NICK_NAME_ID = "1409015665758699621";

    @Transactional
    public void modalInteraction(@NotNull ModalInteractionEvent event){
        if(event.getModalId().contains("joinModal")){ //가입신청
            userJoinModalEvent(event);
        }else if(event.getModalId().contains("changeNickName")){ //닉네임변경
            changeNickNameModalEvent(event);
        }else{ //리그 참여취소
            leagueJoinCancelModalEvent(event);
        }
    }

    //region 참여취소
    @Transactional
    public void leagueJoinCancelModalEvent(ModalInteractionEvent event){
        String modalId = event.getModalId();
        String messageId = modalId.split("\\|")[0];
        String reagueTrackId = modalId.split("\\|")[1];

        LeagueTrack leagueTrack = leagueTrackRepository.findById(Long.parseLong(reagueTrackId)).orElse(null);
        if(leagueTrack == null){
            event.reply("리그 정보가 존재하지 않습니다.\n관리자에게 문의해 주세요").setEphemeral(true).queue();
            return;
        }

        DiscordMember discordMember = discordMemberRepository.findByUserId(Objects.requireNonNull(event.getMember()).getUser().getId()).orElse(null);
        if(discordMember == null ){
            event.reply("멤버 정보가 존재하지 않습니다.\n관리자에게 문의해 주세요").setEphemeral(true).queue();
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
            event.reply(nickName + "님 **[참여취소]** 입력 값이 잘못되었습니다.").setEphemeral(true).queue();
            return;
        }

        //공지 채널
        NewsChannel channel = SioscmsApplication.getJda().getNewsChannelById(event.getChannelIdLong());
        assert channel != null;

        //기존 참가자 취소처리
        LeagueTrackMember legueTrackMember = leagueTrackMemberRepository.findByDiscordMember_UserIdAndLeagueTrack_Id(discordMember.getUserId(), leagueTrack.getId()).orElse(null);
        if(legueTrackMember == null){
            event.reply(nickName + "님 리그 참가 정보가 존재하지 않습니다.").queue();
            return;
        }

        String buttonLabel = legueTrackMember.getLeagueButton().getButtonName();
        leagueTrackMemberRepository.delete(legueTrackMember);
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
        //setEphemeral : true: 신청자에게만 보였다 사라짐, false: 전체가 다 보임
        String finalNickName = nickName;
        event.deferReply(true).queue(hook -> {
            // DM 전송
            event.getUser().openPrivateChannel()
                    .flatMap(pc -> pc.sendMessage(finalNickName + "님 **[" + buttonLabel + "]** 취소 처리 되었습니다."))
                    .queue(
                            ok -> hook.editOriginal("DM을 확인해주세요.").queue(),
                            err -> hook.editOriginal("DM을 보낼 수 없습니다. (사용자가 서버 DM을 차단했을 수 있습니다)").queue()
                    );
        });
        discordDirectMessageService.userLeagueJoinStatMessageSend("[취소]" + nickName + " **" + buttonLabel + "** 취소");
    }
    //endregion 참여취소

    //region 가입선청
    public void userJoinModalEvent(ModalInteractionEvent event){
        String joinNote = Objects.requireNonNull(event.getValue("JoinNoteField")).getAsString();
        String nickName = Objects.requireNonNull(event.getValue("NickNameField")).getAsString();
        String platForm = Objects.requireNonNull(event.getValue("PlatFormField")).getAsString();
        String inviteId = Objects.requireNonNull(event.getValue("InviteIdField")).getAsString();

        String koreanRegex = ".*[가-힣]+.*";
        if(nickName.matches(koreanRegex)){
            event.reply("한글이 포함된 닉네임은 사용할 수 없습니다.").setEphemeral(true).queue();
        }

        //JOIN_CHANNEL_ID
        String body = "가입사유 : " + joinNote + "\n" +
                "닉네임 : " + nickName + "\n" +
                "플랫폼 : " + platForm + "\n";
        if(platForm.toLowerCase(Locale.ROOT).contains("steam") || platForm.contains("스팀")){
            body += "스팀친추코드 : " + inviteId;
        }else{
            body += "EA ID : "  + inviteId;
        }

        String footer = event.getUser().getId() + "|" + nickName;
        event.deferReply(true).queue(hook -> {
            // DM 전송
            event.getUser().openPrivateChannel()
                    .flatMap(pc -> pc.sendMessage("가입 신청 되었습니다. 승인이 완료되면 **드라이버**태그가 부여됩니다."))
                    .queue(
                            ok -> hook.editOriginal("승인이 완료되면 **드라이버**태그가 부여됩니다.").queue(),
                            err -> hook.editOriginal("DM을 보낼 수 없습니다. (사용자가 서버 DM을 차단했을 수 있습니다)").queue()
                    );
        });
        discordDirectMessageService.channelEmbedMessageSend("join", JOIN_CHANNEL_ID, "가입신청", body, footer);
    }
    //endregion 가입신청

    //region 닉네임변경
    public void changeNickNameModalEvent(ModalInteractionEvent event){

    }
    //endregion 닉네임변경

}
