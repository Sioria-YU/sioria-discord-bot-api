package com.project.sioscms.apps.code.service;

import com.project.sioscms.apps.code.domain.dto.CodeDto;
import com.project.sioscms.apps.code.domain.entity.Code;
import com.project.sioscms.apps.code.domain.entity.CodeGroup;
import com.project.sioscms.apps.code.domain.repository.CodeGroupRepository;
import com.project.sioscms.apps.code.domain.repository.CodeRepository;
import com.project.sioscms.apps.code.mapper.CodeMapper;
import com.project.sioscms.common.utils.jpa.restriction.ChangSolJpaRestriction;
import com.project.sioscms.common.utils.jpa.restriction.ChangSolJpaRestrictionType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CodeService {
    private final CodeRepository codeRepository;
    private final CodeGroupRepository codeGroupRepository;

    /**
     * 코드 목록 조회
     * @param dto CodeDto.Request
     * @return List<CodeDto.Response>
     */
    public List<CodeDto.Response> getCodeList(CodeDto.Request dto){
        ChangSolJpaRestriction restriction = new ChangSolJpaRestriction(ChangSolJpaRestrictionType.AND);
        restriction.equals("isDeleted", false);

        if(dto.getCodeGroupId() != null){
            CodeGroup codeGroup = codeGroupRepository.findByCodeGroupId(dto.getCodeGroupId()).orElse(null);
            if(codeGroup != null) {
                restriction.equals("codeGroup", codeGroup.getCodeGroupId());
            }
        }

        return codeRepository.findAll(restriction.toSpecification(), Sort.by(Sort.Direction.ASC, "orderNum"))
                .stream().map(Code::toResponse).collect(Collectors.toList());
    }

    /**
     * 코드 조회
     * @param codeId String
     * @return CodeDto.Response
     */
    public CodeDto.Response getCode(String codeId){
        if(codeId != null) {
            Code code = codeRepository.findByCodeId(codeId).orElse(null);
            return code != null ? code.toResponse() : null;
        }else{
            return null;
        }
    }

    /**
     * 코드 등록
     * @param dto CodeDto.Request
     * @return CodeDto.Response
     */
    @Transactional
    public CodeDto.Response saveCode(CodeDto.Request dto){
        if(dto != null && dto.getCodeId() != null && dto.getCodeGroupId() != null && dto.getCodeLabel() != null){
            Code entity = CodeMapper.mapper.toEntity(dto);
            entity.setIsDeleted(false);
            entity.setOrderNum(codeRepository.countByCodeGroup_CodeGroupIdAndIsDeleted(dto.getCodeGroupId(), false).intValue());

            CodeGroup codeGroup = codeGroupRepository.findByCodeGroupId(dto.getCodeGroupId()).orElse(null);
            if(codeGroup != null){
                entity.setCodeGroup(codeGroup);
            }else{
                return null;
            }

            codeRepository.save(entity);

            return entity.toResponse();
        }else{
            return null;
        }
    }

    /**
     * 코드 아이디 중복 체크
     * @param codeId String
     * @return Boolean
     */
    public Boolean duplicationCheck(String codeId){
        if(codeId != null && !codeId.isEmpty()){
            Code code = codeRepository.findByCodeId(codeId).orElse(null);
            return code == null;
        }else{
            return false;
        }
    }
}
