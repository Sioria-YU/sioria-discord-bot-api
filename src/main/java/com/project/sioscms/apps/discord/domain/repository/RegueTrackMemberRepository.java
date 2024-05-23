package com.project.sioscms.apps.discord.domain.repository;

import com.project.sioscms.apps.discord.domain.entity.RegueTrackMember;
import com.project.sioscms.common.domain.repository.CommonJpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RegueTrackMemberRepository extends CommonJpaRepository<RegueTrackMember, Long> {
}
