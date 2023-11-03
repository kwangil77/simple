package com.example.simple.core.model

import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.LastModifiedBy
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import jakarta.persistence.Column
import jakarta.persistence.EntityListeners
import jakarta.persistence.MappedSuperclass
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import java.time.Instant

@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
abstract class Auditable(
	@Column(name = "creator", updatable = false) @CreatedBy var createdBy: String? = null,
	@Column(name = "createdDate", updatable = false) @CreatedDate var createdDate: Instant? = null,
	@Column(name = "lastModifier") @LastModifiedBy var lastModifiedBy: String? = null,
	@Column(name = "lastModifiedDate") @LastModifiedDate var lastModifiedDate: Instant? = null
)
