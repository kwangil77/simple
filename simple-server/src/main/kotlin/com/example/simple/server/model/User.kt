package com.example.simple.server.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table(name = "users")
data class User(
	@Id var id: Long,
	@Column("email") var email: String
) : Auditable()
