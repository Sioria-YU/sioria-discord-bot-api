package com.project.sioscms.apps.discord.domain.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.project.sioscms.common.domain.entity.CommonEntityWithId;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Comment;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
@Getter
@Setter
public class LeagueDiscordMention extends CommonEntityWithId {
    @Comment("역할 아이디")
    @ManyToOne(cascade = CascadeType.PERSIST)
    @JsonBackReference
    private DiscordMention discordMention;

    @Comment("리그 아이디")
    @ManyToOne(cascade = CascadeType.PERSIST)
    @JsonBackReference
    private League league;
}
