package com.project.sioscms.apps.discord.domain.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.project.sioscms.apps.discord.domain.dto.DiscordMentionDto;
import com.project.sioscms.apps.discord.mapper.DiscordMentionMapper;
import com.project.sioscms.common.domain.entity.CommonEntityWithIdAndDate;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Comment;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.Set;

@Entity
@Getter
@Setter
public class DiscordMention extends CommonEntityWithIdAndDate {
    @Comment("권한 아이디")
    private String roleId;

    @Comment("권한명")
    private String roleName;

    @Comment("멘션")
    private String mention;

    @JsonManagedReference
    @OneToMany(mappedBy = "discordMention")
    @ToString.Exclude
    private Set<DiscordUserMension> discordUserMensionSet;

    public DiscordMentionDto.Response toResponse() { return DiscordMentionMapper.mapper.toResponse(this);
    }
}
