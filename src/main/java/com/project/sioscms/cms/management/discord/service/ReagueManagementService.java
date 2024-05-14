package com.project.sioscms.cms.management.discord.service;

import com.project.sioscms.apps.attach.domain.repository.AttachFileGroupRepository;
import com.project.sioscms.apps.discord.domain.dto.ReagueDto;
import com.project.sioscms.apps.discord.domain.entity.Reague;
import com.project.sioscms.apps.discord.domain.repository.ReagueRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReagueManagementService {
    private final ReagueRepository reagueRepository;
    private final AttachFileGroupRepository attachFileGroupRepository;
    /**
     * 새로운 리그 저장
     * @param requestDto
     * @return
     */
    @Transactional
    public ReagueDto.Response save(ReagueDto.Request requestDto){
        Reague entity = new Reague();
        entity.setReagueName(requestDto.getReagueName());
        entity.setTitle(requestDto.getTitle());
        entity.setColor(requestDto.getColor());
        entity.setDescription(requestDto.getDescription());
        entity.setStartDate(requestDto.getStartDate());
        entity.setEndDate(requestDto.getEndDate());
        entity.setReagueTime(requestDto.getReagueTime());
        entity.setIsDeleted(false);

        if(requestDto.getAttachFileGroupId() != null){
            attachFileGroupRepository.findById(requestDto.getAttachFileGroupId()).ifPresent(entity::setAttachFileGroup);
        }

        reagueRepository.save(entity);

        return entity.toResponse();
    }
}
