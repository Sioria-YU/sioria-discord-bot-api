package com.project.sioscms.apps.discord.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Slf4j
@Transactional(readOnly = true)
public class DiscordBotApiService {

    @Transactional
    public boolean memberRefresh(){


        return false;
    }
}
