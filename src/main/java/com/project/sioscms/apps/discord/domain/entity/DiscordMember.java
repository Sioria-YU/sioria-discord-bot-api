package com.project.sioscms.apps.discord.domain.entity;

import com.project.sioscms.apps.discord.domain.dto.DiscordMemberDto;
import com.project.sioscms.apps.discord.mapper.DiscordMemberMapper;
import com.project.sioscms.common.domain.entity.CommonEntityWithIdAndDate;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;
import org.jetbrains.annotations.NotNull;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
@Getter
@Setter
public class DiscordMember extends CommonEntityWithIdAndDate {
    @Comment("아이디")
    @Column(unique=true)
    @NotNull
    private String userId;

    @Comment("닉네임")
    @NotNull
    private String username;

    @Comment("닉네임(전체)")
    private String globalName;

    @Comment("역할")
    private String discriminator;

    @Comment("삭제여부")
    @ColumnDefault(value = "FALSE")
    private Boolean isDeleted;

    public DiscordMemberDto.Response toResponse() { return DiscordMemberMapper.mapper.toResponse(this); }
}
