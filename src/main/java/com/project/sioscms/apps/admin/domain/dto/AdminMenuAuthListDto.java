package com.project.sioscms.apps.admin.domain.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class AdminMenuAuthListDto {

    @Getter
    @Setter
    public static class Request{
        private Long adminAuthId;
        private List<AdminMenuAuthDto.Request> adminMenuAuthList;
    }
}
