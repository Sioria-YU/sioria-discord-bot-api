package com.project.sioscms.apps.discord.domain.dto;

import com.project.sioscms.apps.discord.domain.entity.DiscordUserMension;
import com.project.sioscms.common.domain.dto.CommonSearchDto;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Comment;

import java.util.Set;

public class DiscordMemberDto {
    @Getter
    @Setter
    public static class Request extends CommonSearchDto {
        private Long id;    //고유 아이디
        private String userId;  //아이디
        private String username; //닉네임
        private String globalName;  //닉네임(전체)
        private String nickname;    //닉네임(길드)
        private String discriminator;   //권한
        private String userMension; //가입자 멘션
        private String discordUserMension; //디스코드 멘션
        private Boolean isDeleted;  //삭제여부
    }

    @Getter
    @Setter
    public static class Response{
        private Long id;    //고유 아이디
        private String userId;  //아이디
        private String username; //닉네임
        private String globalName;  //닉네임(전체)
        private String nickname;    //닉네임(길드)
        private String discriminator;   //권한
        private String userMension; //멘션
        private Boolean isDeleted;  //삭제여부
        private Set<DiscordUserMension> discordUserMensionSet;  //역할
    }
}
