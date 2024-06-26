package com.project.sioscms.scheduler.discord;

import com.project.sioscms.apps.discord.domain.entity.LeagueTrack;
import com.project.sioscms.apps.discord.service.DiscordBotApiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DricordScheduler {

    private final DiscordBotApiService discordBotApiService;

    /**
     * 리그 알림 푸시 스케줄
     * 매 10분 마다 체크하여 전송
     * @throws Exception
     */
    @Scheduled(cron = "0 */10 * * * *")
    public void discordLeaguePushTask() throws Exception{
        log.info("리그 알림 푸시 스케줄러 시작 : ", LocalDateTime.now());
        long leagueCnt = discordBotApiService.countLeagueTrackStartToday();
        log.info("leagueCnt ::: " + leagueCnt);

        if(leagueCnt > 0){
            List<LeagueTrack> leagueTrackList = discordBotApiService.getLeagueTrackStartToday();

            //현재시간
            LocalDateTime now = LocalDateTime.now();
            LocalDate nowDay = now.toLocalDate();
            LocalTime nowTime = now.toLocalTime();

            log.info("Scheduler Times ::: " + now);

            for (LeagueTrack leagueTrack : leagueTrackList) {
                //오늘이 리그 시작,종료일과 같거나, 그 사이인 경우
                if((nowDay.isEqual(leagueTrack.getLeague().getStartDate()) || nowDay.isEqual(leagueTrack.getLeague().getEndDate()))
                    || (nowDay.isAfter(leagueTrack.getLeague().getStartDate()) && nowDay.isBefore(leagueTrack.getLeague().getEndDate()))) {
                    
                    //현재 시간(시,분)이 알림 시간과 같을 경우 푸시 메세지 발송
                    if (nowTime.getHour() == leagueTrack.getLeague().getNoticeTime().getHour()
                            && nowTime.getMinute() == leagueTrack.getLeague().getNoticeTime().getMinute()) {
                        discordBotApiService.leagueMessagePush(leagueTrack.getLeague().getId());
                    }
                }
            }
            log.info("리그 알림 푸시 스케줄러 종료 : ", LocalDateTime.now());
        }
    }

    /**
     * 가입자 동기화
     * 매일 0시 0분 0초에 실행
     * @throws Exception
     */
    @Scheduled(cron = "0 0 0 * * *")
    public void updateDiscordMemberTask() throws Exception{
        log.info("가입자 정보 동기화 스케줄러 시작 : ", LocalDateTime.now());
        discordBotApiService.memberRefresh();
        log.info("가입자 정보 동기화 스케줄러 종료 : ", LocalDateTime.now());
    }
}
