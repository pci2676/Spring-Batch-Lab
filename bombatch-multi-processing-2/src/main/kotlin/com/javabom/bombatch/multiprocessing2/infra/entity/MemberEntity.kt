package com.javabom.bombatch.multiprocessing2.infra.entity

import javax.persistence.*

@Entity
@Table(
    name = "member",
    uniqueConstraints = [
        UniqueConstraint(name = "uk_member_1", columnNames = ["member_number"])
    ]
)
class MemberEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    @Column(name = "member_number")
    val memberNumber: String,
)