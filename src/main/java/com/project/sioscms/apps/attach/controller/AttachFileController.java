package com.project.sioscms.apps.attach.controller;

import com.project.sioscms.apps.attach.service.AttachFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/attach")
@RequiredArgsConstructor
public class AttachFileController {

    private final AttachFileService attachFileService;

    @GetMapping("/encrypt-test")
    public ResponseEntity<String> encryptTest(@RequestParam("inputText") String inputText) throws Exception {
        return ResponseEntity.ok(attachFileService.encryptTest(inputText));
    }

    @GetMapping("/decrypt-test")
    public ResponseEntity<String> decryptTest(@RequestParam("inputText") String inputText) throws Exception {
        return ResponseEntity.ok(attachFileService.decryptTest(inputText));
    }

    @PostMapping("/upload")
    public ResponseEntity upload(@RequestPart MultipartFile file) throws Exception {
        return ResponseEntity.ok(attachFileService.upload(file));
    }
}
