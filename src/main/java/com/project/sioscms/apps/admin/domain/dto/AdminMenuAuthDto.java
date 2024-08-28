package com.project.sioscms.apps.admin.domain.dto;

import com.project.sioscms.apps.account.domain.entity.Account;
import com.project.sioscms.apps.admin.domain.entity.AdminAuth;
import com.project.sioscms.apps.menu.domain.entity.Menu;
import com.project.sioscms.common.domain.dto.CommonSearchDto;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

public class AdminMenuAuthDto {

    @Getter
    @Setter
    public static class Request extends CommonSearchDto {
        private Long id;
        private AdminAuth adminAuth;
        private Menu menu;
        private Boolean isSelect;
        private Boolean isInsert;
        private Boolean isUpdate;
        private Boolean isDelete;
    }

    @Getter
    @Setter
    public static class Response{
        private Long id;
        private AdminAuth adminAuth;
        private Menu menu;
        private Boolean isSelect;
        private Boolean isInsert;
        private Boolean isUpdate;
        private Boolean isDelete;

        private Account createdBy;
        private Account updatedBy;
        private LocalDateTime createdDateTime;
        private LocalDateTime updatedDateTime;
    }
}
