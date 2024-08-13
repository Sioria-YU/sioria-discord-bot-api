package com.project.sioscms.apps.discord.service;

import com.project.sioscms.SioscmsApplication;
import com.project.sioscms.apps.discord.domain.dto.LeagueDto;
import com.project.sioscms.apps.discord.domain.entity.*;
import com.project.sioscms.apps.discord.domain.repository.LeagueRepository;
import com.project.sioscms.apps.discord.domain.repository.LeagueTrackMemberRepository;
import com.project.sioscms.apps.discord.domain.repository.LeagueTrackWaitRepository;
import com.project.sioscms.common.utils.jpa.restriction.ChangSolJpaRestriction;
import com.project.sioscms.common.utils.jpa.restriction.ChangSolJpaRestrictionType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.channel.concrete.NewsChannel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LeagueService {

    @Value("${discord.guild-key}")
    private String GUILD_KEY;

    private final LeagueRepository leagueRepository;
    private final LeagueTrackWaitRepository leagueTrackWaitRepository;
    private final LeagueTrackMemberRepository leagueTrackMemberRepository;

    private final DiscordMessageService discordMessageService;
    private final DiscordDirectMessageService discordDirectMessageService;

    //region 리그 목록 조회

    /**
     * 리그 목록 조회
     * @param leagueName
     * @return
     */
    public List<LeagueDto.Response> getLeagueList(final String leagueName){
        ChangSolJpaRestriction rs = new ChangSolJpaRestriction();
        rs.equals("isDeleted", false);

        if(!ObjectUtils.isEmpty(leagueName)){
            ChangSolJpaRestriction rs2 = new ChangSolJpaRestriction(ChangSolJpaRestrictionType.OR);
            rs2.iLike("leagueName", "%" + leagueName + "%");
            rs.addChild(rs2);
        }

        return leagueRepository.findAll(rs.toSpecification(), Sort.by(Sort.Direction.DESC, "startDate", "endDate", "leagueName"))
                .stream().map(League::toResponse).toList();
    }
    //endregion

    //region 등록된 리그를 조회하여 오늘 시작하는 트랙 메세지 푸시
    /**
     * 등록된 리그를 조회하여 오늘 시작하는 트랙 메세지를 푸시함.
     *
     * @param leagueId
     * @return
     */
    public boolean leagueMessagePush(long leagueId) {
        if(ObjectUtils.isEmpty(leagueId)){
            return false;
        }

        //리그 정보 조회
        League league = leagueRepository.findById(leagueId).orElse(null);
        assert league != null;

        LeagueTrack leagueTrack = league.getLeagueTracks().stream().filter(t -> t.getTrackDate().isEqual(LocalDate.now())).findFirst().orElse(null);
        if (leagueTrack == null) {
            return false;
        }

        //공지 채널을 얻어옴
        NewsChannel newsChannel = Objects.requireNonNull(SioscmsApplication.getJda().getGuildById(GUILD_KEY)).getNewsChannelById(league.getNoticeChannelId());
        assert newsChannel != null;

        newsChannel.sendMessage(discordMessageService.createLeaguePushMessage(league, leagueTrack)).queue();
        return true;
    }
    //endregion 등록된 리그를 조회하여 오늘 시작하는 트랙 메세지 푸시

    //region 참석 대기자 취소 시 대기열 변경처리
    /**
     * 참석 대기자 취소 시 대기열 변경처리
     * @param leagueTrack
     * @param leagueButton
     */
    @Transactional
    public void changeLeagueTrackWaiterToLeagueTrackMember(LeagueTrack leagueTrack, LeagueButton leagueButton) {
        //대기자 존재 유무 확인
        boolean isWaiting = leagueTrackWaitRepository.countByLeagueTrack_IdAndLeagueButton_Id(leagueTrack.getId(), leagueButton.getId()) > 0;

        //대기자가 있을 경우
        if (isWaiting) {
            //현재 참여자 카운트 확인(동시 작동시 초과되는거 방지)
            long joinCnt = leagueTrackMemberRepository.countByLeagueTrack_IdAndLeagueButton_Id(leagueTrack.getId(), leagueButton.getId());
            if(joinCnt >= leagueTrack.getLeague().getJoinMemberLimit()){
                return;
            }

            //대기자에서 제거
            LeagueTrackWait leagueTrackWait = leagueTrackWaitRepository.findTop1ByLeagueTrack_IdAndLeagueButton_IdOrderByCreatedDateTimeAsc(leagueTrack.getId(), leagueButton.getId()).orElse(null);
            if (leagueTrackWait != null) {
                //대기열 참석자에게 개인DM으로 알림
                String dmText = "참석자 취소로 " + leagueTrackWait.getDiscordMember().getUserMension() + " 님 참석으로 전환되었습니다.";
                discordDirectMessageService.userDmSendByUserId(dmText, leagueTrackWait.getDiscordMember().getUserId());

                //참여자로 추가
                LeagueTrackMember newRegueTrackMember = new LeagueTrackMember();
                newRegueTrackMember.setLeagueButton(leagueButton);
                newRegueTrackMember.setLeagueTrack(leagueTrack);
                newRegueTrackMember.setDiscordMember(leagueTrackWait.getDiscordMember());
                leagueTrackMemberRepository.save(newRegueTrackMember);

                //대기열에서 삭제
                leagueTrackWaitRepository.delete(leagueTrackWait);
                leagueTrackWaitRepository.flush();
            }else{
                log.error("대기자 처리 중 오류 발생!!!");
            }
        }
    }
    //endregion 참석 대기자 취소 시 대기열 변경처리

    //region 리그 대기자 추가처리
    /**
     * 리그 대기자 추가처리
     *
     * @param discordMember
     * @param leagueTrack
     * @param leagueButton
     */
    @Transactional
    public void appendLeagueTrackWait(DiscordMember discordMember, LeagueTrack leagueTrack, LeagueButton leagueButton) {
        LeagueTrackWait leagueTrackWait = new LeagueTrackWait();
        leagueTrackWait.setDiscordMember(discordMember);
        leagueTrackWait.setLeagueTrack(leagueTrack);
        leagueTrackWait.setLeagueButton(leagueButton);
        leagueTrackWaitRepository.save(leagueTrackWait);
    }
    //endregion 리그 대기자 추가처리

}
