package com.project.sioscms.apps.discord.domain.entity;

import com.project.sioscms.apps.code.domain.entity.Code;
import com.project.sioscms.common.domain.entity.CommonEntityWithIdAndDate;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
@Getter
@Setter
public class LeagueTrackMember extends CommonEntityWithIdAndDate {
    @Comment("디스코드 가입자")
    @ManyToOne
    private DiscordMember discordMember;

    @Comment("리그 트랙")
    @ManyToOne
    private LeagueTrack leagueTrack;

    @Comment("리그 버튼")
    @ManyToOne
    private LeagueButton leagueButton;

    @Comment("참여 구분(LEAGUE_JOIN_TYPE_CD)")
    @ManyToOne
    private Code joinType;

    @Comment("점수")
    @ColumnDefault(value = "0")
    private Long score;
}
