package com.project.sioscms.apps.discord.domain.dto;

import com.project.sioscms.apps.code.domain.entity.Code;
import com.project.sioscms.apps.discord.domain.entity.League;
import com.project.sioscms.common.domain.dto.CommonSearchDto;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

public class LeagueTrackDto {

    @Getter
    @Setter
    public static class Request extends CommonSearchDto {
        private Long id;
        private String leagueName;  //리그명

        private List<Long> trackMemberIds;
        private List<Long> scores;
        private List<String> joinTypes;
    }

    @Getter
    @Setter
    public static class Response{
        private Long id;
        private Code trackCode;
        private League league;
        @DateTimeFormat(pattern = "HH:mm:ss")
        private LocalDate trackDate;
        private Boolean isColsed;
    }
}
