package com.project.sioscms.apps.code.domain.entity;

import com.project.sioscms.apps.code.domain.dto.CodeDto;
import com.project.sioscms.apps.code.mapper.CodeMapper;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Code {

    @Id
    @Comment("코드 아이디")
    private String codeId;

    @ManyToOne
    @Comment("코드그룹 아이디")
    private CodeGroup codeGroup;

    @Comment("코드 명")
    private String codeLabel;

    @Comment("옵션1")
    private String option1;

    @Comment("옵션2")
    private String option2;

    @Comment("옵션3")
    private String option3;

    @Comment("옵션4")
    private String option4;

    @Comment("코드 순번")
    private Integer orderNum;

    @Comment("삭제여부")
    @ColumnDefault(value = "FALSE")
    private Boolean isDeleted = false;

    @Comment("등록자 pk")
    @CreatedBy
    private Long createdBy;

    @Comment("수정자 pk")
    @LastModifiedBy
    private Long updatedBy;

    @Comment("등록일시")
    @CreatedDate
    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    @Column(updatable = false)
    private LocalDateTime createdDateTime = LocalDateTime.now();

    @Comment("수정일시")
    @LastModifiedDate
    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    private LocalDateTime updatedDateTime = LocalDateTime.now();

    public CodeDto.Response toResponse() { return CodeMapper.mapper.toResponse(this); }
}
