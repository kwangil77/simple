package com.example.simple.server.model

import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedBy
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.relational.core.mapping.Column
import java.time.Instant

abstract class Auditable(
	@Column("creator") @CreatedBy var createdBy: String? = null,
	@Column("createdDate") @CreatedDate var createdDate: Instant? = null,
	@Column("lastModifier") @LastModifiedBy var lastModifiedBy: String? = null,
	@Column("lastModifiedDate") @LastModifiedDate var lastModifiedDate: Instant? = null
)
