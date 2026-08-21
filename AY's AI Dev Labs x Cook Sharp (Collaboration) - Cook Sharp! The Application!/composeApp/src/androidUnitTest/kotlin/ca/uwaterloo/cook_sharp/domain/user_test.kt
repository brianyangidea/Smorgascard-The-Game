package ca.uwaterloo.cook_sharp.domain

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class user_test {

    @Test
    fun user_initialization_with_default_values() {
        val user = User(
            id = "test_id",
            name = "Test User",
            email = "test@example.com"
        )
        assertEquals("password123", user.password)
        assertEquals(null, user.dietarypreference)
        assertTrue(user.allergies.isEmpty())
    }

    @Test
    fun isAllergicTo_returns_true_when_allergic() {
        val user = User(
            id = "1", name = "N", email = "E",
            allergies = listOf("Peanuts", "Dairy")
        )
        assertTrue(user.isAllergicTo("Peanuts"))
        assertTrue(user.isAllergicTo("dairy"))
    }

    @Test
    fun isAllergicTo_returns_false_when_not_allergic() {
        val user = User(
            id = "1", name = "N", email = "E",
            allergies = listOf("Peanuts")
        )
        assertFalse(user.isAllergicTo("Shellfish"))
        assertFalse(user.isAllergicTo(""))
    }

    @Test
    fun edge_case_empty_allergies() {
        val user = User(id = "1", name = "N", email = "E", allergies = emptyList())
        assertFalse(user.isAllergicTo("Anything"))
    }
}
