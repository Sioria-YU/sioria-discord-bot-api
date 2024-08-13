package com.project.sioscms.apps.discord.domain.dto;

import com.project.sioscms.apps.code.domain.entity.Code;
import com.project.sioscms.apps.discord.domain.entity.DiscordMember;
import com.project.sioscms.apps.discord.domain.entity.League;
import com.project.sioscms.common.domain.dto.CommonSearchDto;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class DiscordPenaltyDto {
    @Getter
    @Setter
    public static class Request extends CommonSearchDto {
        private Long id;    //고유 아이디
        private Long leagueId; //리그 아이디
        private Long discordMemberId; //디스코드 멤버 아이디
        private String penaltyTypeCodeId; //페널티 구분 코드 아이디
        private String penaltyNote; //페널티 사유
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate applyDate; //적용일자
        private Long frequency; //회수
        private Boolean isDeleted;  //삭제여부

        //검색영역
        private String leagueName;
        private String username;
    }

    @Getter
    @Setter
    public static class Response{
        private Long id;    //고유 아이디
        private League league; //리그
        private DiscordMember discordMember; //디스코드멤버
        private Code penaltyTypeCode; //페널티 구분 코드
        private String penaltyNote; //페널티 사유
        private LocalDate applyDate; //적용일자
        private Long frequency; //회수
        private Boolean isDeleted; //삭제여부
        private LocalDateTime createdDateTime; //작성일
        private LocalDateTime updatedDateTime; //수정일
    }
}
