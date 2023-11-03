package com.example.simple.server.controller

import com.epages.restdocs.apispec.ResourceDocumentation.parameterWithName
import com.epages.restdocs.apispec.ResourceDocumentation.resource
import com.epages.restdocs.apispec.ResourceSnippetParameters
import com.epages.restdocs.apispec.Schema
import com.epages.restdocs.apispec.WebTestClientRestDocumentationWrapper.document
import com.fasterxml.jackson.databind.ObjectMapper
import com.example.simple.server.model.User
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.spring.SpringExtension
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient
import java.time.Instant
import java.util.function.Function

@AutoConfigureWebTestClient
@AutoConfigureRestDocs
@ActiveProfiles("local")
@SpringBootTest
class UserControllerSpec @Autowired constructor(
	private val webTestClient: WebTestClient,
	private val objectMapper: ObjectMapper
) : DescribeSpec() {
	override fun extensions() = listOf(SpringExtension)

	init {
		this.describe("users") {
			context("get users") {
				it("returns users") {
					webTestClient.get().uri("/users")
						.accept(MediaType.APPLICATION_JSON)
						.exchange()
						.expectStatus().isOk
						.expectBody()
						.jsonPath("[0].createdBy").isNotEmpty
						.jsonPath("[0].createdDate").isNotEmpty
						.jsonPath("[0].lastModifiedBy").isNotEmpty
						.jsonPath("[0].lastModifiedDate").isNotEmpty
						.jsonPath("[0].id").isNotEmpty
						.jsonPath("[0].email").isNotEmpty
						.consumeWith(
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
					webTestClient.get().uri("/users/{id}", 1L)
						.accept(MediaType.APPLICATION_JSON)
						.exchange()
						.expectStatus().isOk
						.expectBody()
						.jsonPath("createdBy").isNotEmpty
						.jsonPath("createdDate").isNotEmpty
						.jsonPath("lastModifiedBy").isNotEmpty
						.jsonPath("lastModifiedDate").isNotEmpty
						.jsonPath("id").isNotEmpty
						.jsonPath("email").isNotEmpty
						.consumeWith(
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
					webTestClient.post().uri("/users")
						.contentType(MediaType.APPLICATION_JSON)
						.bodyValue(objectMapper.writeValueAsString(user))
						.exchange()
						.expectStatus().isOk
						.expectBody()
						.jsonPath("createdBy").isNotEmpty
						.jsonPath("createdDate").isNotEmpty
						.jsonPath("lastModifiedBy").isNotEmpty
						.jsonPath("lastModifiedDate").isNotEmpty
						.jsonPath("id").isNotEmpty
						.jsonPath("email").isNotEmpty
						.consumeWith(
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