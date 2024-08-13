package com.project.sioscms.apps.discord.domain.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.project.sioscms.common.domain.entity.CommonEntityWithId;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Comment;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
@Getter
@Setter
public class LeagueButton extends CommonEntityWithId {
    @Comment("버튼/카테고리명")
    private String buttonName;

    @Comment("버튼타입(색상)")
    private String buttonType;

    @Comment("리그아이디")
    @ManyToOne
    @JsonBackReference
    private League league;
}
