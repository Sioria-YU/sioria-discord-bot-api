package com.project.sioscms.apps.discord.domain.repository;

import com.project.sioscms.apps.discord.domain.entity.LeagueButton;
import com.project.sioscms.common.domain.repository.CommonJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface LeagueButtonRepository extends CommonJpaRepository<LeagueButton, Long> {
    Set<LeagueButton> findAllByLeague_Id(Long id);
}
