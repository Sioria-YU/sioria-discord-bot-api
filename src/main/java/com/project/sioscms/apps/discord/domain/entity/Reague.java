package com.project.sioscms.apps.discord.domain.entity;

import com.project.sioscms.common.domain.entity.CommonEntityWithIdAndDate;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Comment;

import javax.persistence.Entity;
import javax.validation.constraints.NotNull;

@Entity
@Getter
@Setter
public class Reague extends CommonEntityWithIdAndDate {
    @NotNull
    @Comment("리그명")
    private String reagueName;

    @NotNull
    @Comment("리그 알림 제목")
    private String title;

    @Comment("리그 알림 설명")
    private String description;

    @Comment("리그 알림 색상")
    private String color;

}
