package com.project.sioscms.apps.log.controller;

import com.project.sioscms.apps.log.service.ExceptionLogService;
import com.project.sioscms.common.utils.common.http.HttpUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@ControllerAdvice
@RequiredArgsConstructor
public class ExceptionLogControllerAdvice {

    private final ExceptionLogService exceptionLogService;

    @ExceptionHandler(Exception.class)
    public void RuntimeException(HttpServletResponse response, HttpServletRequest request, Exception e){
        //log saved
        exceptionLogService.save(request, e);
        HttpUtil.alertAndRedirect(response, "/error", null, null);
    }
}
