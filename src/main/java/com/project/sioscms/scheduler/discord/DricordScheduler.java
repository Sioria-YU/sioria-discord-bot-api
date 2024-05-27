package com.project.sioscms.scheduler.discord;

import com.project.sioscms.apps.discord.domain.entity.ReagueTrack;
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

    @Scheduled(cron = "0 */10 * * * *")
    public void discordReaguePushTask() throws Exception{
        long reagueCnt = discordBotApiService.countReagueTrackStartToday();
        log.info("reagueCnt ::: " + reagueCnt);

        if(reagueCnt > 0){
            List<ReagueTrack> reagueTrackList = discordBotApiService.getReagueTrackStartToday();

            //현재시간
            LocalDateTime now = LocalDateTime.now();
            LocalDate nowDay = now.toLocalDate();
            LocalTime nowTime = now.toLocalTime();

            log.info("Scheduler Times ::: " + now);

            for (ReagueTrack reagueTrack : reagueTrackList) {
                //오늘이 리그 시작,종료일과 같거나, 그 사이인 경우
                if((nowDay.isEqual(reagueTrack.getReague().getStartDate()) || nowDay.isEqual(reagueTrack.getReague().getEndDate()))
                    || (nowDay.isAfter(reagueTrack.getReague().getStartDate()) && nowDay.isBefore(reagueTrack.getReague().getEndDate()))) {
                    
                    //현재 시간(시,분)이 알림 시간과 같을 경우 푸시 메세지 발송
                    if (nowTime.getHour() == reagueTrack.getReague().getNoticeTime().getHour()
                            && nowTime.getMinute() == reagueTrack.getReague().getNoticeTime().getMinute()) {
                        discordBotApiService.reagueMessagePush(reagueTrack.getReague().getId());
                    }
                }
            }

        }
    }
}
