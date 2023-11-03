package com.example.simple.api.controller

import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document
import com.epages.restdocs.apispec.ResourceDocumentation.parameterWithName
import com.epages.restdocs.apispec.ResourceDocumentation.resource
import com.epages.restdocs.apispec.ResourceSnippetParameters
import com.epages.restdocs.apispec.Schema
import com.fasterxml.jackson.databind.ObjectMapper
import com.example.simple.core.model.User
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.spring.SpringExtension
import org.hamcrest.Matchers
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.Instant
import java.util.function.Function

@AutoConfigureMockMvc
@AutoConfigureRestDocs
@ActiveProfiles("local")
@SpringBootTest
class UserControllerSpec @Autowired constructor(
	private val mockMvc: MockMvc,
	private val objectMapper: ObjectMapper
) : DescribeSpec() {
	override fun extensions() = listOf(SpringExtension)

	init {
		this.describe("users") {
			context("get users") {
				it("returns users") {
					mockMvc.perform(
						RestDocumentationRequestBuilders
							.get("/users")
							.accept(MediaType.APPLICATION_JSON)
					)
						.andExpect(status().isOk)
						.andExpect(jsonPath("[0].createdBy", Matchers.notNullValue()))
						.andExpect(jsonPath("[0].createdDate", Matchers.notNullValue()))
						.andExpect(jsonPath("[0].lastModifiedBy", Matchers.notNullValue()))
						.andExpect(jsonPath("[0].lastModifiedDate", Matchers.notNullValue()))
						.andExpect(jsonPath("[0].id", Matchers.notNullValue()))
						.andExpect(jsonPath("[0].email", Matchers.notNullValue()))
						.andDo(
							document("get-users", null, null, false, false, null, null, Function.identity(),
								resource(
									ResourceSnippetParameters.builder()
										.responseSchema(Schema("User"))
										.responseFields(listOf(
											fieldWithPath("[].createdBy").description(""),
											fieldWithPath("[].createdDate").description(""),
											fieldWithPath("[].lastModifiedBy").description(""),
											fieldWithPath("[].lastModifiedDate").description(""),
											fieldWithPath("[].id").description(""),
											fieldWithPath("[].email").description("")
										))
										.build()
								)
							)
						)
				}
			}
			context("get user") {
				it("returns user") {
					mockMvc.perform(
						RestDocumentationRequestBuilders
							.get("/users/{id}", 1L)
							.accept(MediaType.APPLICATION_JSON)
					)
						.andExpect(status().isOk)
						.andExpect(jsonPath("createdBy", Matchers.notNullValue()))
						.andExpect(jsonPath("createdDate", Matchers.notNullValue()))
						.andExpect(jsonPath("lastModifiedBy", Matchers.notNullValue()))
						.andExpect(jsonPath("lastModifiedDate", Matchers.notNullValue()))
						.andExpect(jsonPath("id", Matchers.notNullValue()))
						.andExpect(jsonPath("email", Matchers.notNullValue()))
						.andDo(
							document("get-user", null, null, false, false, null, null, Function.identity(),
								resource(
									ResourceSnippetParameters.builder()
										.pathParameters(
											parameterWithName("id").description("")
										)
										.responseSchema(Schema("User"))
										.responseFields(fields)
										.build()
								)
							)
						)
				}
			}
			context("create user") {
				val user = objectMapper.readValue("""{
                    "email": "devops@example.com"
                }""", User::class.java)

				beforeEach {
					user.createdBy = "System"
					user.createdDate = now
					user.lastModifiedBy = "System"
					user.lastModifiedDate = now
				}
				it("returns user") {
					mockMvc.perform(
						RestDocumentationRequestBuilders.post("/users")
							.contentType(MediaType.APPLICATION_JSON)
							.content(objectMapper.writeValueAsString(user))
					)
						.andExpect(status().isOk)
						.andExpect(jsonPath("createdBy", Matchers.notNullValue()))
						.andExpect(jsonPath("createdDate", Matchers.notNullValue()))
						.andExpect(jsonPath("lastModifiedBy", Matchers.notNullValue()))
						.andExpect(jsonPath("lastModifiedDate", Matchers.notNullValue()))
						.andExpect(jsonPath("id", Matchers.notNullValue()))
						.andExpect(jsonPath("email", Matchers.notNullValue()))
						.andDo(
							document("create-user", null, null, false, false, null, null, Function.identity(),
								resource(
									ResourceSnippetParameters.builder()
										.requestSchema(Schema("User"))
										.requestFields(fields)
										.responseSchema(Schema("User"))
										.responseFields(fields)
										.build()
								)
							)
						)
				}
			}
		}
	}
	companion object {
		private val now: Instant = Instant.now()
		private val fields = listOf(
			fieldWithPath("createdBy").description(""),
			fieldWithPath("createdDate").description(""),
			fieldWithPath("lastModifiedBy").description(""),
			fieldWithPath("lastModifiedDate").description(""),
			fieldWithPath("id").description(""),
			fieldWithPath("email").description("")
		)
	}
}