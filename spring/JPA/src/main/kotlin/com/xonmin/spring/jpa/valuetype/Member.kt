package com.xonmin.spring.jpa.valuetype

import javax.persistence.*

@Entity
data class Member(
    @Id
    @GeneratedValue
    @Column(name = "MEMBER_ID")
    val id: Long,

    @Column(name = "USERNAME")
    val userName: String,

    @Embedded
    val homeAddress: Address,

    // value type collection의 경우에는 @ElementCollection 이 필요하다
    @ElementCollection
    @CollectionTable(
        name = "FAVORITE_FOOD",
        joinColumns = [JoinColumn(name = "MEMBER_ID")]
    )
    @Column(name = "FOOD_NAME")
    val favoriteFoods: Set<String>,

    @ElementCollection
    @CollectionTable(
        name = "ADDRESS",
        joinColumns = [JoinColumn(name = "MEMBER_ID")]
    )
    val addressHistory: List<Address>
)
