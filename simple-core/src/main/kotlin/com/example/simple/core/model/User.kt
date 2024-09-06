package com.example.simple.core.model

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Column

@Entity(name = "users")
data class User(
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY) var id: Long?,
	@Column(name = "email") var email: String
) : Auditable()
