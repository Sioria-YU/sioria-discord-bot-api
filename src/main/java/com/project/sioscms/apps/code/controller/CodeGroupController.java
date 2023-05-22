package com.project.sioscms.apps.code.controller;

import com.project.sioscms.apps.code.domain.dto.CodeGroupDto;
import com.project.sioscms.apps.code.service.CodeGroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/cms/api/code-group")
@RequiredArgsConstructor
public class CodeGroupController {
    private final CodeGroupService codeGroupService;

    @GetMapping("/list")
    public ResponseEntity<List<CodeGroupDto.Response>> getCodeGroupList(CodeGroupDto.Request dto){
        return ResponseEntity.ok(codeGroupService.getCodeGroupList(dto));
    }

    @GetMapping("/{codeGroupId}")
    public ResponseEntity<CodeGroupDto.Response> getCodeGroup(@PathVariable("codeGroupId") String codeGroupId){
        return ResponseEntity.ok(codeGroupService.getCodeGroup(codeGroupId));
    }
}