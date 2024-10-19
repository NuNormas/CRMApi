package com.example.demo.Entities

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "sellers")
data class Seller(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    val name: String,

    @Column(name = "contact_info")
    val contactInfo: String,

    @Column(name = "registration_date")
    val registrationDate: LocalDateTime = LocalDateTime.now()
)