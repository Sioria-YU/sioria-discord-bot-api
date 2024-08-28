package com.project.sioscms.apps.admin.domain.entity;

import com.project.sioscms.apps.admin.domain.dto.AdminAuthDto;
import com.project.sioscms.apps.admin.mapper.AdminAuthMapper;
import com.project.sioscms.common.domain.entity.CommonEntityWithIdAndDate;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;

import javax.persistence.Entity;

@Getter
@Setter
@Entity
public class AdminAuth extends CommonEntityWithIdAndDate {

    @Comment("권한명")
    private String name;

    @Comment("설명")
    private String notice;

    @ColumnDefault(value = "FALSE")
    @Comment("삭제여부")
    private Boolean isDeleted;

    public AdminAuthDto.Response toResponse(){
        return AdminAuthMapper.mapper.toResponse(this);
    }
}
