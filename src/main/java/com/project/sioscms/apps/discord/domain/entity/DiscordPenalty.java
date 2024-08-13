package com.project.sioscms.apps.discord.domain.entity;

import com.project.sioscms.apps.code.domain.entity.Code;
import com.project.sioscms.apps.discord.domain.dto.DiscordPenaltyDto;
import com.project.sioscms.apps.discord.mapper.DiscordPenaltyMapper;
import com.project.sioscms.common.domain.entity.CommonEntityWithIdAndDate;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.time.LocalDate;

@Entity
@Getter
@Setter
public class DiscordPenalty extends CommonEntityWithIdAndDate {

    @Comment("리그 아이디")
    @ManyToOne
    private League league;

    @Comment("디스코드 멤버 아이디")
    @ManyToOne
    private DiscordMember discordMember;

    @Comment("페널티 구분 코드 아이디")
    @ManyToOne
    private Code penaltyTypeCode;

    @Comment("페널티 사유")
    private String penaltyNote;

    @Comment("적용일자")
    @Convert(converter = Jsr310JpaConverters.LocalDateConverter.class)
    private LocalDate applyDate;

    @Comment("회수")
    private Long frequency;

    @Comment("삭제여부")
    @ColumnDefault(value = "FALSE")
    private Boolean isDeleted;

    public DiscordPenaltyDto.Response toResponse(){
        return DiscordPenaltyMapper.mapper.toResponse(this);
    }
}
