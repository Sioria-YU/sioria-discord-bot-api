package com.project.sioscms.apps.discord.service;

import com.project.sioscms.apps.discord.domain.entity.*;
import com.project.sioscms.apps.discord.domain.repository.DiscordMemberRepository;
import com.project.sioscms.apps.discord.domain.repository.LeagueTrackMemberRepository;
import com.project.sioscms.apps.discord.domain.repository.LeagueTrackRepository;
import com.project.sioscms.apps.discord.domain.repository.LeagueTrackWaitRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DiscordButtonActionService {
    private final LeagueService leagueService;
    private final DiscordMessageService discordMessageService;
    private final DiscordDirectMessageService discordDirectMessageService;

    private final DiscordMemberRepository discordMemberRepository;
    private final LeagueTrackRepository leagueTrackRepository;
    private final LeagueTrackMemberRepository leagueTrackMemberRepository;
    private final LeagueTrackWaitRepository leagueTrackWaitRepository;


    @Transactional
    public void buttonInteraction(@NotNull ButtonInteractionEvent event) {
        if ("league-refresh".equals(event.getButton().getId())) {
            if (leagueManagerAuthCheck(Objects.requireNonNull(event.getMember()).getUser().getId())) {
                leagueMessageRefresh(event);
            }
        } else if ("league-close".equals(event.getButton().getId())) {
            if (leagueManagerAuthCheck(Objects.requireNonNull(event.getMember()).getUser().getId())) {
                leagueCloseAction(event);
            }
        } else {
            leagueButtonEvent(event);
        }
    }

    //region 리그 버튼 이벤트 처리
    /**
     * 리그 버튼 이벤트 처리
     * @param event
     */
    public void leagueButtonEvent(@NotNull ButtonInteractionEvent event){
        //이벤트 액션에따라 참여 목록을 저장 or 삭제한다.
        //리그트랙 번호는 임베디드 푸터에서 얻어와 불러온다.
        MessageEmbed embed = event.getMessage().getEmbeds().get(0); //임베디드는 1개만 생성함.

        //리그정보를 불러온다.
        assert Objects.requireNonNull(embed.getFooter()).getText() != null;
        long leagueTrackId = Long.parseLong(embed.getFooter().getText());

        LeagueTrack leagueTrack = leagueTrackRepository.findById(leagueTrackId).orElse(null);
        assert leagueTrack != null;

        //마감됐다면
        if (leagueTrack.getIsColsed()) {
            discordDirectMessageService.userDmSendByUserId(leagueTrack.getLeague().getLeagueName() + " 리그는 마감됐습니다.", Objects.requireNonNull(event.getMember()).getUser().getId());
            return;
        }

        League league = leagueTrack.getLeague();
        DiscordMember discordMember = discordMemberRepository.findByUserId(Objects.requireNonNull(event.getMember()).getUser().getId()).orElse(null);

        //멤버 등록이 안된 경우
        if (discordMember == null) {
            log.error("discordMember is not found!!!");
            //관리자에게 문의 메세지 전송
            discordDirectMessageService.userDmSendByUserId("시스템에 멤버로 등록되지 않았습니다. 관리자에게 문의해주세요.", Objects.requireNonNull(event.getMember()).getUser().getId());
            return;
        }

        //출전불가 멘션 체크
        //출전정지 태그(1366660976648261633)가 포함되어 있으면 리그 권한 체크 없이 창여 불가
        boolean isPenalty = discordMember.getDiscordUserMensionSet().stream().anyMatch(m -> m.getDiscordMention().getRoleId().equals("1366660976648261633"));
        log.info("isPenalty :::> " + isPenalty);
        if(isPenalty) {
            discordDirectMessageService.userDmSendByUserId("출전 정지 상태에서는 참여할 수 없습니다.", Objects.requireNonNull(event.getMember()).getUser().getId());
            return;
        }

        //리그 참여자 권한 체크
        boolean isJoinAuth = false;
        for (DiscordUserMension discordUserMension : discordMember.getDiscordUserMensionSet()) {
            if (league.getJoinAceptMentions().stream().anyMatch(m -> m.getDiscordMention().equals(discordUserMension.getDiscordMention()))) {
                isJoinAuth = true;
                break;
            }
        }

        //참여 대상 권한이 아닐 경우 DM발송
        if (!isJoinAuth) {
            discordDirectMessageService.userDmSendByUserId(league.getLeagueName() + " 참여 대상이 아닙니다.", Objects.requireNonNull(event.getMember()).getUser().getId());
            return;
        }

        //리그 참여 처리
        boolean isSuccess = leagueJoinProc(event, leagueTrack, discordMember);

        //메세지 처리
        if(isSuccess) {
            editMessageSend(event, leagueTrack, leagueTrack.getIsColsed());
        }
    }
    //endregion 리그 버튼 이벤트 처리

    //region 수정 메세지 생성 및 전송처리
    /**
     * embed 수정 메세지 생성 및 전송처리
     *
     * @param event
     * @param leagueTrack
     * @param isClosed
     */
    public void editMessageSend(ButtonInteractionEvent event, LeagueTrack leagueTrack, boolean isClosed) {
        League league = leagueTrack.getLeague();

        MessageEmbed embed = event.getMessage().getEmbeds().get(0);

        //메세지 수정 발송
        event.editMessage(discordMessageService.editLeagueMessage(leagueTrack, isClosed, embed)).queue();
    }
    //endregion 수정 메세지 생성 및 전송처리

    //region 버튼 클릭한 유저 운영진, 리그운영진 권한 체크

    /**
     * 운영진, 리그운영진 권한 체크
     * @param userId
     * @return
     */
    public boolean leagueManagerAuthCheck(@NotNull String userId) {
        DiscordMember discordMember = discordMemberRepository.findByUserId(userId).orElse(null);
        assert discordMember != null;
        return discordMember.getDiscordUserMensionSet().stream().anyMatch(r -> "1125386665574273024".equals(r.getDiscordMention().getRoleId()) || "1125983811797270548".equals(r.getDiscordMention().getRoleId()));
    }
    //endregion 운영진, 리그운영진 권한 체크

    //region 리그 메세지 새로고침

    /**
     * 리그 메세지 새로고침
     * @param event
     */
    public void leagueMessageRefresh(ButtonInteractionEvent event) {
        MessageEmbed embed = event.getMessage().getEmbeds().get(0); //임베디드는 1개만 생성함.

        //리그정보를 불러온다.
        assert Objects.requireNonNull(embed.getFooter()).getText() != null;
        long leagueTrackId = Long.parseLong(embed.getFooter().getText());

        LeagueTrack leagueTrack = leagueTrackRepository.findById(leagueTrackId).orElse(null);
        assert leagueTrack != null;

        editMessageSend(event, leagueTrack, leagueTrack.getIsColsed());
    }
    //endregion 리그 메세지 새로고침

    //region 리그 참가 신청 마감/마감해제
    /**
     * 리그 참가 신청 마감/마감해제
     * @param event
     */
    @Transactional
    public void leagueCloseAction(ButtonInteractionEvent event) {
        MessageEmbed embed = event.getMessage().getEmbeds().get(0); //임베디드는 1개만 생성함.

        //리그정보를 불러온다.
        assert Objects.requireNonNull(embed.getFooter()).getText() != null;
        long leagueTrackId = Long.parseLong(embed.getFooter().getText());

        LeagueTrack leagueTrack = leagueTrackRepository.findById(leagueTrackId).orElse(null);
        assert leagueTrack != null;

        if (leagueTrack.getIsColsed()) {
            leagueTrack.setIsColsed(false);
        } else {
            leagueTrack.setIsColsed(true);
        }

        editMessageSend(event, leagueTrack, leagueTrack.getIsColsed());
    }
    //endregion 리그 참가 신청 마감/마감해제

    //region 리그 참여 처리 로직
    /**
     * 리그 참여 처리 로직
     * @param event
     * @param leagueTrack
     * @param discordMember
     * @return
     */
    @Transactional
    public boolean leagueJoinProc(ButtonInteractionEvent event, LeagueTrack leagueTrack, DiscordMember discordMember) {
        LeagueTrackMember legueTrackMember = leagueTrackMemberRepository.findByDiscordMember_UserIdAndLeagueTrack_Id(discordMember.getUserId(), leagueTrack.getId()).orElse(null);
        League league = leagueTrack.getLeague();

        //현재 참여가 안된 경우 참여 등록처리
        LeagueButton joinButton = league.getLeagueButtons().stream().filter(v -> Objects.equals(event.getButton().getId(), v.getId().toString())).findFirst().orElse(null);
        if (joinButton == null) {
            log.error("LeagueButton is not found!!!");
            discordDirectMessageService.userDmSendByUserId("처리 오류가 발생하였습니다. 관리자에게 문의해주세요.", Objects.requireNonNull(event.getMember()).getUser().getId());
            event.deferReply(true).queue();
            return false;
        }

        //신규 참여자 or 대기열참여자
        if (legueTrackMember == null) {
            //대기열 확인
            LeagueTrackWait leagueTrackWait = leagueTrackWaitRepository.findByLeagueTrack_IdAndDiscordMember_Id(leagueTrack.getId(), discordMember.getId()).orElse(null);

            //신규 참여자일 경우
            if(leagueTrackWait == null) {
                long joinCnt = leagueTrackMemberRepository.countByLeagueTrack_IdAndLeagueButton_Id(leagueTrack.getId(), joinButton.getId());
                if (joinCnt >= league.getJoinMemberLimit()) {
                    leagueService.appendLeagueTrackWait(discordMember, leagueTrack, joinButton);
                    editMessageSend(event, leagueTrack, leagueTrack.getIsColsed());
                    discordDirectMessageService.userDmSendByUserId("참가 신청 되었습니다. 현재 참가자가 많아 대기열로 배정되었습니다.", Objects.requireNonNull(event.getMember()).getUser().getId());
                    //관리자 메세지 로그
                    discordDirectMessageService.userLeagueJoinStatMessageSend(
                            "[대기열]" + getNickName(Objects.requireNonNull(event.getMember()).getUser().getId())
                                    + " **" + joinButton.getButtonName() + "**");
                    return false;
                }

                LeagueTrackMember newLegueTrackMember = new LeagueTrackMember();
                newLegueTrackMember.setLeagueButton(joinButton);
                newLegueTrackMember.setLeagueTrack(leagueTrack);
                newLegueTrackMember.setDiscordMember(discordMember);
                leagueTrackMemberRepository.save(newLegueTrackMember);
                discordDirectMessageService.userDmSendByUserId(getNickName(Objects.requireNonNull(event.getMember()).getUser().getId())
                        + "님 **" + league.getLeagueName() + " " + joinButton.getButtonName() + "** 신청 신청되었습니다.", Objects.requireNonNull(event.getMember()).getUser().getId());
                //관리자 메세지 로그
                discordDirectMessageService.userLeagueJoinStatMessageSend(
                        "[참가]" + getNickName(Objects.requireNonNull(event.getMember()).getUser().getId())
                                + " **" + joinButton.getButtonName() + "**");
            }//대기열 참여자일 경우
            else{
                //동일한 카테고리일 경우 삭제 처리
                if(Objects.equals(joinButton, leagueTrackWait.getLeagueButton())){
                    leagueTrackWaitRepository.delete(leagueTrackWait);
                    discordDirectMessageService.userDmSendByUserId(getNickName(Objects.requireNonNull(event.getMember()).getUser().getId())
                            + "님 **" + league.getLeagueName() + " " + joinButton.getButtonName() + "** 대기열 취소 되었습니다.", Objects.requireNonNull(event.getMember()).getUser().getId());
                    if(!league.getIsJoinDisplay()) {
                        discordDirectMessageService.userLeagueJoinStatMessageSend(
                                "[대기열-취소]" + getNickName(Objects.requireNonNull(event.getMember()).getUser().getId())
                                        + " **" + joinButton.getButtonName() + "** 취소");
                    }
                }//다른 카테고리에서 변경한 경우
                else{
                    //참여가능 인원 확인
                    long joinCnt = leagueTrackMemberRepository.countByLeagueTrack_IdAndLeagueButton_Id(leagueTrack.getId(), joinButton.getId());
                    //참여 가능 인원 초과한 경우 대기열 변경 등록
                    if (joinCnt >= league.getJoinMemberLimit()) {
                        leagueTrackWaitRepository.delete(leagueTrackWait);
                        leagueTrackWaitRepository.flush();
                        leagueService.appendLeagueTrackWait(discordMember, leagueTrack, joinButton);
                        discordDirectMessageService.userDmSendByUserId(getNickName(Objects.requireNonNull(event.getMember()).getUser().getId())
                                + "님 **" + league.getLeagueName() + " " + joinButton.getButtonName() + "** 대기열 변경 신청되었습니다.", Objects.requireNonNull(event.getMember()).getUser().getId());
                        //관리자 메세지 로그
                        discordDirectMessageService.userLeagueJoinStatMessageSend(
                                "[대기열]" + getNickName(Objects.requireNonNull(event.getMember()).getUser().getId())
                                        + " **" + joinButton.getButtonName() + "**");
                    }//참여가능할 경우 대기열에서 제거하고 참여자로 등록
                    else{
                        LeagueTrackMember newLegueTrackMember = new LeagueTrackMember();
                        newLegueTrackMember.setLeagueButton(joinButton);
                        newLegueTrackMember.setLeagueTrack(leagueTrack);
                        newLegueTrackMember.setDiscordMember(discordMember);
                        leagueTrackMemberRepository.save(newLegueTrackMember);
                        leagueTrackWaitRepository.delete(leagueTrackWait);
                        discordDirectMessageService.userDmSendByUserId(getNickName(Objects.requireNonNull(event.getMember()).getUser().getId())
                                + "님 **" + league.getLeagueName() + " " + joinButton.getButtonName() + "** 신청 되었습니다.", Objects.requireNonNull(event.getMember()).getUser().getId());
                        //관리자 메세지 로그
                        discordDirectMessageService.userLeagueJoinStatMessageSend(
                                "[참가]" + getNickName(Objects.requireNonNull(event.getMember()).getUser().getId())
                                        + " **" + joinButton.getButtonName() + "**");
                    }
                }
            }
        }//현재 참여중인 경우 삭제처리
        else {
            //현재 참여 타입과 동일한 타입으로 눌렀을 경우 참여 취소 처리
            if (joinButton.getId().equals(legueTrackMember.getLeagueButton().getId())) {
                //취소확인 모달 팝업
                event.replyModal(createJoinCancelModal(event.getMessageId() + "|" + leagueTrack.getId())).queue();
                return false;
            }//현재 참여 타입과 다른 타입으로 신청한 경우 변경처리
            else {
                //참여인원 제한 체크
                long joinCnt = leagueTrackMemberRepository.countByLeagueTrack_IdAndLeagueButton_Id(leagueTrack.getId(), joinButton.getId());
                if (joinCnt >= league.getJoinMemberLimit()) {
                    leagueTrackMemberRepository.delete(legueTrackMember);
                    leagueService.appendLeagueTrackWait(discordMember, leagueTrack, joinButton);

                    //카테고리가 변경되었기 때문에 전체 카테고리 대기열을 순회하여 대기열처리해줌.
                    for (LeagueButton leagueButton : leagueTrack.getLeague().getLeagueButtons()) {
                        leagueService.changeLeagueTrackWaiterToLeagueTrackMember(leagueTrack, leagueButton);
                    }

                    editMessageSend(event, leagueTrack, leagueTrack.getIsColsed());
                    discordDirectMessageService.userDmSendByUserId(getNickName(Objects.requireNonNull(event.getMember()).getUser().getId())
                            + "님 **" + league.getLeagueName() + " " + joinButton.getButtonName() + "** 대기열 변경 신청 되었습니다."
                            , Objects.requireNonNull(event.getMember()).getUser().getId());
                    //관리자 메세지 로그
                    discordDirectMessageService.userLeagueJoinStatMessageSend(
                            "[대기열]" + getNickName(Objects.requireNonNull(event.getMember()).getUser().getId())
                                    + " **" + joinButton.getButtonName() + "**");
                    return false;
                } else {
                    legueTrackMember.setLeagueButton(joinButton);
                    discordDirectMessageService.userDmSendByUserId(getNickName(Objects.requireNonNull(event.getMember()).getUser().getId())
                            + "님 **" + league.getLeagueName() + " " + joinButton.getButtonName() + "** 신청 되었습니다.", Objects.requireNonNull(event.getMember()).getUser().getId());
                    //관리자 메세지 로그
                    discordDirectMessageService.userLeagueJoinStatMessageSend(
                            "[참가]" + getNickName(Objects.requireNonNull(event.getMember()).getUser().getId())
                                    + " **" + joinButton.getButtonName() + "**");
                    //카테고리가 변경되었기 때문에 전체 카테고리 대기열을 순회하여 대기열처리해줌.
                    for (LeagueButton leagueButton : leagueTrack.getLeague().getLeagueButtons()) {
                        leagueService.changeLeagueTrackWaiterToLeagueTrackMember(leagueTrack, leagueButton);
                    }
                }
            }
        }
        return true;
    }
    //endregion 리그 참여 처리 로직

    //region 참여 취소 모달 생성
    /**
     * 참여 취소 모달 생성
     * @param messageId
     * @return
     */
    public Modal createJoinCancelModal(String messageId){
        String fieldName = "참여취소";
        TextInput joinCancelField = TextInput.create("joinCancelField", fieldName, TextInputStyle.SHORT)
                .setPlaceholder(fieldName)
                .setMinLength(4)
                .setMaxLength(4)
                .setValue(fieldName)
                .setRequired(true)
                .build();

        return Modal.create(messageId, fieldName)
                .addActionRow(joinCancelField)
                .build();
    }
    //endregion 참여 취소 모달 생성

    public String getNickName(String userId){
        DiscordMember discordMember = discordMemberRepository.findByUserId(userId).orElse(null);
        if(discordMember == null ){
            return "";
        }

        String nickName = "";
        if(!ObjectUtils.isEmpty(discordMember.getNickname())){
            nickName = discordMember.getNickname();
        }else if(!ObjectUtils.isEmpty(discordMember.getGlobalName())){
            nickName = discordMember.getGlobalName();
        }else{
            nickName = discordMember.getUsername();
        }
        return nickName;
    }
}
