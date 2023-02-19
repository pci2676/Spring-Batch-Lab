package com.javabom.bombatch.multiprocessing2.infra.entity.repository

import com.javabom.bombatch.multiprocessing2.infra.entity.MemberEntity
import org.springframework.data.jpa.repository.JpaRepository

interface MemberEntityRepository : JpaRepository<MemberEntity, Long>