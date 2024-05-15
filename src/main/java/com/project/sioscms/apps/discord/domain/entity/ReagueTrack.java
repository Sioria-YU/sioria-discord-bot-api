package com.project.sioscms.apps.discord.domain.entity;

import com.project.sioscms.apps.code.domain.entity.Code;
import com.project.sioscms.common.domain.entity.CommonEntityWithId;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Comment;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.time.LocalDate;

@Entity
@Getter
@Setter
public class ReagueTrack extends CommonEntityWithId {

    @Comment("트랙 코드아이디")
    @ManyToOne
    private Code trackCode;

    @Comment("리그 아이디")
    @ManyToOne
    private Reague reague;

    @Comment("경기일")
    @Convert(converter = Jsr310JpaConverters.LocalDateConverter.class)
    private LocalDate trackDate;
}
