package com.example.simple.core.security

import io.kotest.core.spec.style.DescribeSpec
import org.apache.commons.lang3.StringUtils.EMPTY
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class UserContextSpec : DescribeSpec() {
	init {
		this.describe("user context") {
			context("set") {
				beforeEach {
					UserContext.userName = "A"
				}

				it("returns username of user context") {
					expectThat(UserContext.userName).isEqualTo("A")
				}
			}

			context("remove") {
				beforeEach {
					UserContext.removeUserName()
				}

				it("returns empty string") {
					expectThat(UserContext.userName).isEqualTo(EMPTY)
				}
			}
		}
	}
}
