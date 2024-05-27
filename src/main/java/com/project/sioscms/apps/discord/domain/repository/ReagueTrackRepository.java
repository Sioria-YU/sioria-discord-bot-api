package com.project.sioscms.apps.discord.domain.repository;

import com.project.sioscms.apps.discord.domain.entity.ReagueTrack;
import com.project.sioscms.common.domain.repository.CommonJpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReagueTrackRepository extends CommonJpaRepository<ReagueTrack, Long> {
    List<ReagueTrack> findAllByTrackDate(LocalDate trackDate);
    Long countAllByTrackDate(LocalDate trackDate);
}
