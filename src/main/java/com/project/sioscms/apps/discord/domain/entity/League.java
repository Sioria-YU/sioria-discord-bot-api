package com.project.sioscms.apps.discord.domain.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.project.sioscms.apps.attach.domain.entity.AttachFileGroup;
import com.project.sioscms.apps.discord.domain.dto.LeagueDto;
import com.project.sioscms.apps.discord.mapper.LeagueMapper;
import com.project.sioscms.common.domain.entity.CommonEntityWithIdAndDate;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

@Entity
@Getter
@Setter
public class League extends CommonEntityWithIdAndDate {
    @NotNull
    @Comment("리그명")
    private String leagueName;

    @NotNull
    @Comment("리그 알림 제목")
    private String title;

    @Comment("리그 알림 설명")
    @Column(length = 2000)
    private String description;

    @Comment("리그 알림 색상")
    private String color;

    @Comment("리그 시작일")
    @Convert(converter = Jsr310JpaConverters.LocalDateConverter.class)
    private LocalDate startDate;

    @Comment("리그 종료일")
    @Convert(converter = Jsr310JpaConverters.LocalDateConverter.class)
    private LocalDate endDate;
    
    @Comment("리그 시간")
    @Convert(converter = Jsr310JpaConverters.LocalTimeConverter.class)
    private LocalTime leagueTime;

    @Comment("게시 채널")
    private String noticeChannelId;

    @Comment("게시 시간")
    @Convert(converter = Jsr310JpaConverters.LocalTimeConverter.class)
    private LocalTime noticeTime;

    @Comment("참여 인원")
    private Long joinMemberLimit;

    //참여 가능 역할[리스트]
    @JsonManagedReference
    @OneToMany(mappedBy = "league", fetch = FetchType.LAZY)
    @ToString.Exclude
    @OrderBy(value = "id asc")
    private Set<LeagueDiscordMention> joinAceptMentions;

    //트랙[리스트]
    @JsonManagedReference
    @OneToMany(mappedBy = "league", fetch = FetchType.LAZY)
    @ToString.Exclude
    @OrderBy(value = "trackDate asc")
    private Set<LeagueTrack> leagueTracks;

    //참여 카테고리(버튼)[리스트]
    @JsonManagedReference
    @OneToMany(mappedBy = "league", fetch = FetchType.LAZY)
    @ToString.Exclude
    @OrderBy(value = "id asc")
    private Set<LeagueButton> leagueButtons;

    @Comment("첨부파일(이미지)")
    @OneToOne
    @OrderBy(value = "id asc")
    private AttachFileGroup attachFileGroup;

    @Comment("삭제여부")
    @ColumnDefault(value = "FALSE")
    private Boolean isDeleted;

    public LeagueDto.Response toResponse() { return LeagueMapper.mapper.toResponse(this); }
}
