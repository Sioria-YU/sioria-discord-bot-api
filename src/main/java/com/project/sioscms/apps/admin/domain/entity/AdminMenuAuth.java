package com.project.sioscms.apps.admin.domain.entity;

import com.project.sioscms.apps.menu.domain.entity.Menu;
import com.project.sioscms.common.domain.entity.CommonEntityWithIdAndDate;
import lombok.Getter;
import lombok.Setter;
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

    @Comment("조회여부")
    private Boolean isSelect;

    @Comment("등록여부")
    private Boolean isInsert;

    @Comment("수정여부")
    private Boolean isUpdate;

    @Comment("삭제여부")
    private Boolean isDelete;
}
