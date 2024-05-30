package com.project.sioscms.apps.discord.domain.repository;

import com.project.sioscms.apps.discord.domain.entity.ReagueTrack;
import com.project.sioscms.common.domain.repository.CommonJpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Repository
public interface ReagueTrackRepository extends CommonJpaRepository<ReagueTrack, Long> {
    Set<ReagueTrack> findAllByReague_Id(Long reagueId);
    List<ReagueTrack> findAllByTrackDateAndReague_IsDeleted(LocalDate trackDate, Boolean reagueIsDeleted);
    Long countAllByTrackDateAndReague_IsDeleted(LocalDate trackDate, Boolean reagueIsDeleted);
}
