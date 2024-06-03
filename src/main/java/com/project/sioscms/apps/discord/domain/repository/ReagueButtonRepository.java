package com.project.sioscms.apps.discord.domain.repository;

import com.project.sioscms.apps.discord.domain.entity.ReagueButton;
import com.project.sioscms.common.domain.repository.CommonJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface ReagueButtonRepository extends CommonJpaRepository<ReagueButton, Long> {
    Set<ReagueButton> findAllByReague_Id(Long id);
}
