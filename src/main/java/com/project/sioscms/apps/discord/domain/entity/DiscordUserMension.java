package com.project.sioscms.apps.discord.domain.entity;

import com.project.sioscms.common.domain.entity.CommonEntityWithId;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
@Getter
@Setter
public class DiscordUserMension extends CommonEntityWithId {

    @ManyToOne(cascade = CascadeType.PERSIST)
    private DiscordMember discordMember;

    @ManyToOne(cascade = CascadeType.PERSIST)
    private DiscordMention discordMention;
}
