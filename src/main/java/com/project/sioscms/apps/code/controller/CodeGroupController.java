package com.project.sioscms.apps.code.controller;

import com.project.sioscms.apps.code.domain.dto.CodeGroupDto;
import com.project.sioscms.apps.code.service.CodeGroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/save")
    @PostMapping
    public ResponseEntity<CodeGroupDto.Response> saveCodeGroup(CodeGroupDto.Request dto){
        return ResponseEntity.ok(codeGroupService.saveCodeGroup(dto));
    }

    @GetMapping("/update")
    @PutMapping
    public ResponseEntity<CodeGroupDto.Response> updateCodeGroup(CodeGroupDto.Request dto){
        return ResponseEntity.ok(codeGroupService.updateCodeGroup(dto));
    }

    @GetMapping("/delete/{codeGroupId}")
    @PutMapping
    public ResponseEntity<Boolean> deleteCodeGroup(@PathVariable("codeGroupId") String codeGroupId){
        return ResponseEntity.ok(codeGroupService.deleteCodeGroup(codeGroupId));
    }
}
