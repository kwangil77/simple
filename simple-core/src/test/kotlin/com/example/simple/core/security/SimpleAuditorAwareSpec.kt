package com.example.simple.core.security

import com.example.simple.core.security.SimpleAuditorAware.Companion.DEFAULT_SYSTEM_USER
import io.kotest.core.spec.style.DescribeSpec
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import java.util.Optional

class SimpleAuditorAwareSpec : DescribeSpec() {
	private val simpleAuditorAware = SimpleAuditorAware()

	init {
		this.describe("current auditor") {
			context("set") {
				lateinit var result : Optional<String>

				beforeEach {
					UserContext.userName = "A"
					result = simpleAuditorAware.currentAuditor
				}

				it("returns username of user context") {
					expectThat(result.get()).isEqualTo("A")
				}
			}

			context("remove") {
				lateinit var result : Optional<String>

				beforeEach {
					UserContext.removeUserName()
					result = simpleAuditorAware.currentAuditor
				}

				it("returns default system user") {
					expectThat(result.get()).isEqualTo(DEFAULT_SYSTEM_USER)
				}
			}
		}
	}
}
