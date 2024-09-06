package com.example.simple.api.model

import com.example.simple.core.model.User
import java.time.ZonedDateTime

class UserRequest(
    val id: Long?,
    val email: String,
    val createdBy: String?,
    val createdDate: ZonedDateTime?,
    val lastModifiedBy: String?,
    val lastModifiedDate : ZonedDateTime?
) {
    fun convertUser() : User {
        val user = User(id, email)
        user.createdBy = createdBy
        user.createdDate = createdDate?.toInstant()
        user.lastModifiedBy = lastModifiedBy
        user.lastModifiedDate = lastModifiedDate?.toInstant()
        return user
    }
}