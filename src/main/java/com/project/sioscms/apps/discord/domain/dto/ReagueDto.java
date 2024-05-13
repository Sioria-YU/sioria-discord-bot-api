package com.project.sioscms.apps.discord.domain.dto;

import com.project.sioscms.common.domain.dto.CommonSearchDto;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

public class ReagueDto {

    @Getter
    @Setter
    public static class Request extends CommonSearchDto{
        private Long id;    //고유 아이디
        private String reagueName;  //리그명
        private String title;   //리그 알림 제목
        private String description; //리그 알림 설명
        private String color;   //리그 알림 색상
        private LocalTime reagueTime;   //리그 시간
        private Boolean isDeleted;  //삭제여부
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
        private Boolean isDeleted;  //삭제여부
    }
}
