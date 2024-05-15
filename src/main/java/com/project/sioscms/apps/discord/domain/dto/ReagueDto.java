package com.project.sioscms.apps.discord.domain.dto;

import com.project.sioscms.apps.discord.domain.entity.ReagueButton;
import com.project.sioscms.apps.discord.domain.entity.ReagueDiscordMention;
import com.project.sioscms.apps.discord.domain.entity.ReagueTrack;
import com.project.sioscms.common.domain.dto.CommonSearchDto;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

public class ReagueDto {

    @Getter
    @Setter
    public static class Request extends CommonSearchDto{
        //시작일, 종료일은 searchDto에 있음
        private Long id;    //고유 아이디
        private String reagueName;  //리그명
        private String title;   //리그 알림 제목
        private String description; //리그 알림 설명
        private String color;   //리그 알림 색상
        private LocalTime reagueTime;   //리그 시간

        private String noticeChannelId; //게시 채널
        private LocalTime noticeTime;   //게시 시간
        private Long joinMemberLimit;   //참여 인원

        private Boolean isDeleted;  //삭제여부
        private Long attachFileGroupId; //첨부파일 그룹id

        //TODO: 아래 3개 항목 어떻게 받아올지 정의해야함
        //참여 가능 역할[리스트]
        //트랙[리스트]
        //참여 카테고리(버튼)[리스트]

    }

    @Getter
    @Setter
    public static class Response{
        private Long id;    //고유 아이디
        private String reagueName;  //리그명
        private String title;   //리그 알림 제목
        private String description; //리그 알림 설명
        private String color;   //리그 알림 색상
        private LocalDate startDate;    //리그 시작일
        private LocalDate endDate;  //리그 종료일
        private LocalTime reagueTime;   //리그 시간

        private String noticeChannelId; //게시 채널
        private LocalTime noticeTime;   //게시 시간
        private Long joinMemberLimit;   //참여 인원

        private Set<ReagueDiscordMention> joinAceptMentions; //참여 가능 역할[리스트]
        private Set<ReagueTrack> reagueTracks;   //트랙[리스트]
        private Set<ReagueButton> reagueButtons; //참여 카테고리(버튼)[리스트]

        private Boolean isDeleted;  //삭제여부
    }
}
