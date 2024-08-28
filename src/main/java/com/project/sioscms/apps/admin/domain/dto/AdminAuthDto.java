package com.project.sioscms.apps.admin.domain.dto;

import com.project.sioscms.apps.account.domain.entity.Account;
import com.project.sioscms.common.domain.dto.CommonSearchDto;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

public class AdminAuthDto {
    @Getter
    @Setter
    public static class Request extends CommonSearchDto {
        private Long id;
        private String name;
        private String notice;
        private Boolean isDeleted;
    }

    @Getter
    @Setter
    public static class Response{
        private Long id;
        private String name;
        private String notice;
        private Boolean isDeleted;

        private Account createdBy;
        private Account updatedBy;
        private LocalDateTime createdDateTime;
        private LocalDateTime updatedDateTime;
    }
}
