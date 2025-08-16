package com.project.sioscms.apps.discord.domain.dto;

import com.project.sioscms.apps.attach.domain.entity.AttachFileGroup;
import com.project.sioscms.apps.discord.domain.entity.LeagueButton;
import com.project.sioscms.apps.discord.domain.entity.LeagueDiscordMention;
import com.project.sioscms.apps.discord.domain.entity.LeagueTrack;
import com.project.sioscms.common.domain.dto.CommonSearchDto;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

public class LeagueDto {

    @Getter
    @Setter
    @ToString
    public static class Track {
        private String name;

        private String id;

        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate date;
    }

    @Getter
    @Setter
    @ToString
    public static class RequestLeagueButton {
        private String id;
        private String name;
        private String type;
    }

    @Getter
    @Setter
    public static class Request extends CommonSearchDto{
        //시작일, 종료일은 searchDto에 있음
        private Long id;    //고유 아이디
        private String leagueName;  //리그명
        private String title;   //리그 알림 제목
        private String description; //리그 알림 설명
        private String color;   //리그 알림 색상

        @DateTimeFormat(pattern = "HH:mm:ss")
        private LocalTime leagueTime;   //리그 시간
        private String noticeChannelId; //게시 채널

        @DateTimeFormat(pattern = "HH:mm:ss")
        private LocalTime noticeTime;   //게시 시간
        private Long joinMemberLimit;   //참여 인원
        private List<String> joinAceptMentionList; //참여 가능 역할[리스트]
        private List<Track> trackList; //트랙[리스트]
        private List<RequestLeagueButton> leagueButtonList; //참여 카테고리(버튼)[리스트]
        private Boolean isDeleted;  //삭제여부
        private Boolean isJoinDisplay; //참가자표시여부
        private Long attachFileGroupId; //첨부파일 그룹id
    }

    @Getter
    @Setter
    public static class Response{
        private Long id;    //고유 아이디
        private String leagueName;  //리그명
        private String title;   //리그 알림 제목
        private String description; //리그 알림 설명
        private String color;   //리그 알림 색상
        private LocalDate startDate;    //리그 시작일
        private LocalDate endDate;  //리그 종료일
        private LocalTime leagueTime;   //리그 시간
        private String noticeChannelId; //게시 채널
        private LocalTime noticeTime;   //게시 시간
        private Long joinMemberLimit;   //참여 인원
        private Set<LeagueDiscordMention> joinAceptMentions; //참여 가능 역할[리스트]
        private Set<LeagueTrack> leagueTracks;   //트랙[리스트]
        private Set<LeagueButton> leagueButtons; //참여 카테고리(버튼)[리스트]
        private AttachFileGroup attachFileGroup;
        private Boolean isDeleted;  //삭제여부
        private Boolean isJoinDisplay; //참가자표시여부
    }
}
