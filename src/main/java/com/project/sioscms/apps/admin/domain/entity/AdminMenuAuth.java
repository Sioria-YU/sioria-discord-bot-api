package com.project.sioscms.apps.admin.domain.entity;

import com.project.sioscms.apps.admin.domain.dto.AdminMenuAuthDto;
import com.project.sioscms.apps.admin.mapper.AdminMenuAuthMapper;
import com.project.sioscms.apps.menu.domain.entity.Menu;
import com.project.sioscms.common.domain.entity.CommonEntityWithIdAndDate;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Getter
@Setter
@Entity
public class AdminMenuAuth extends CommonEntityWithIdAndDate {
    @Comment("권한 아이디")
    @ManyToOne
    private AdminAuth adminAuth;

    @Comment("메뉴 아이디")
    @ManyToOne
    private Menu menu;

    @ColumnDefault(value = "TRUE")
    @Comment("조회여부")
    private Boolean isSelect;

    @ColumnDefault(value = "TRUE")
    @Comment("등록여부")
    private Boolean isInsert;

    @ColumnDefault(value = "TRUE")
    @Comment("수정여부")
    private Boolean isUpdate;

    @ColumnDefault(value = "TRUE")
    @Comment("삭제여부")
    private Boolean isDelete;

    public AdminMenuAuthDto.Response toResponse() { return AdminMenuAuthMapper.mapper.toResponse(this);
    }
}
