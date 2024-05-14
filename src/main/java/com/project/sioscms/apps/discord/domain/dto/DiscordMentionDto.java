package com.project.sioscms.apps.discord.domain.dto;

import com.project.sioscms.apps.discord.domain.entity.DiscordUserMension;
import com.project.sioscms.common.domain.dto.CommonSearchDto;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Comment;

import javax.persistence.OneToMany;
import java.util.Set;

public class DiscordMentionDto {
    @Getter
    @Setter
    public static class Request extends CommonSearchDto {
        private Long id;    //고유 아이디
        private String roleId;  //권한 아이디
        private String roleName;    //권한명
        private String mention; //멘션
    }

    @Getter
    @Setter
    public static class Response{
        private Long id;    //고유 아이디
        private String roleId;  //권한 아이디
        private String roleName;    //권한명
        private String mention; //멘션
    }
}
