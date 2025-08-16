package com.project.sioscms.apps.discord.domain.repository;

import com.project.sioscms.apps.discord.domain.entity.LeagueTrack;
import com.project.sioscms.common.domain.repository.CommonJpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Repository
public interface LeagueTrackRepository extends CommonJpaRepository<LeagueTrack, Long> {
    Set<LeagueTrack> findAllByLeague_Id(Long leagueId);
    Set<LeagueTrack> findAllByLeague_IdOrderByTrackDateAsc(Long leagueId);
    List<LeagueTrack> findAllByTrackDateAndLeague_IsDeleted(LocalDate trackDate, Boolean leagueIsDeleted);
    List<LeagueTrack> findAllByTrackDateBetweenAndLeague_IsDeletedOrderByTrackDateAsc(LocalDate startDate, LocalDate endDate, boolean isDeleted);
    Long countAllByTrackDateAndLeague_IsDeleted(LocalDate trackDate, Boolean leagueIsDeleted);
}
