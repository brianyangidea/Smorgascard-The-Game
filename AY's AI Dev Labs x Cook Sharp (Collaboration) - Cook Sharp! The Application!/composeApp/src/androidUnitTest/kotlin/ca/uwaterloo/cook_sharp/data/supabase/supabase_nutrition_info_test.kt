package ca.uwaterloo.cook_sharp.data.supabase

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNull

class supabase_nutrition_info_test {

    @Test
    fun create_supabase_nutrition_info_row_with_all_fields() {
        val info = SupabaseNutritionInfoRow(
            id = 1L,
            recipe_id = 100L,
            calories = 500.0,
            protein = 30.0,
            carbs = 60.0,
            fat = 20.0,
            fiber = 10.0,
            sugar = 15.0,
            sodium = 300.0,
            saturated_fat = 5.0,
            cholesterol = 50.0,
            potassium = 400.0
        )

        assertEquals(1L, info.id)
        assertEquals(100L, info.recipe_id)
        assertEquals(500.0, info.calories)
        assertEquals(30.0, info.protein)
        assertEquals(60.0, info.carbs)
        assertEquals(20.0, info.fat)
        assertEquals(10.0, info.fiber)
        assertEquals(15.0, info.sugar)
        assertEquals(300.0, info.sodium)
        assertEquals(5.0, info.saturated_fat)
        assertEquals(50.0, info.cholesterol)
        assertEquals(400.0, info.potassium)
    }

    @Test
    fun create_supabase_nutrition_info_row_with_default_values() {
        val info = SupabaseNutritionInfoRow(
            id = 2L,
            recipe_id = 200L
        )

        assertEquals(2L, info.id)
        assertEquals(200L, info.recipe_id)
        assertEquals(0.0, info.calories)
        assertEquals(0.0, info.protein)
        assertEquals(0.0, info.carbs)
        assertEquals(0.0, info.fat)
        assertEquals(0.0, info.fiber)
        assertEquals(0.0, info.sugar)
        assertEquals(0.0, info.sodium)
        assertNull(info.saturated_fat)
        assertNull(info.cholesterol)
        assertNull(info.potassium)
    }

    @Test
    fun create_supabase_nutrition_info_row_with_optional_fields_null() {
        val info = SupabaseNutritionInfoRow(
            id = 3L,
            recipe_id = 300L,
            calories = 400.0,
            protein = 25.0,
            carbs = 50.0,
            fat = 15.0,
            fiber = 8.0,
            sugar = 12.0,
            sodium = 250.0
        )

        assertEquals(3L, info.id)
        assertEquals(300L, info.recipe_id)
        assertNull(info.saturated_fat)
        assertNull(info.cholesterol)
        assertNull(info.potassium)
    }

    @Test
    fun two_supabase_nutrition_info_rows_with_same_data_are_equal() {
        val info1 = SupabaseNutritionInfoRow(
            id = 1L,
            recipe_id = 100L,
            calories = 500.0,
            protein = 30.0,
            carbs = 60.0,
            fat = 20.0,
            fiber = 10.0,
            sugar = 15.0,
            sodium = 300.0
        )
        val info2 = SupabaseNutritionInfoRow(
            id = 1L,
            recipe_id = 100L,
            calories = 500.0,
            protein = 30.0,
            carbs = 60.0,
            fat = 20.0,
            fiber = 10.0,
            sugar = 15.0,
            sodium = 300.0
        )

        assertEquals(info1, info2)
    }

    @Test
    fun two_supabase_nutrition_info_rows_with_different_ids_are_not_equal() {
        val info1 = SupabaseNutritionInfoRow(
            id = 1L,
            recipe_id = 100L,
            calories = 500.0,
            protein = 30.0,
            carbs = 60.0,
            fat = 20.0,
            fiber = 10.0,
            sugar = 15.0,
            sodium = 300.0
        )
        val info2 = SupabaseNutritionInfoRow(
            id = 2L,
            recipe_id = 100L,
            calories = 500.0,
            protein = 30.0,
            carbs = 60.0,
            fat = 20.0,
            fiber = 10.0,
            sugar = 15.0,
            sodium = 300.0
        )

        assertNotEquals(info1, info2)
    }

    @Test
    fun create_supabase_nutrition_info_insert_with_required_fields() {
        val insert = SupabaseNutritionInfoInsert(
            recipe_id = 500L,
            calories = 600.0,
            protein = 35.0,
            carbs = 70.0,
            fat = 25.0
        )

        assertEquals(500L, insert.recipe_id)
        assertEquals(600.0, insert.calories)
        assertEquals(35.0, insert.protein)
        assertEquals(70.0, insert.carbs)
        assertEquals(25.0, insert.fat)
        assertEquals(0.0, insert.fiber)
        assertEquals(0.0, insert.sugar)
        assertEquals(0.0, insert.sodium)
        assertNull(insert.saturated_fat)
        assertNull(insert.cholesterol)
        assertNull(insert.potassium)
    }

    @Test
    fun create_supabase_nutrition_info_insert_with_all_fields() {
        val insert = SupabaseNutritionInfoInsert(
            recipe_id = 600L,
            calories = 700.0,
            protein = 40.0,
            carbs = 80.0,
            fat = 30.0,
            fiber = 12.0,
            sugar = 18.0,
            sodium = 350.0,
            saturated_fat = 8.0,
            cholesterol = 60.0,
            potassium = 500.0
        )

        assertEquals(600L, insert.recipe_id)
        assertEquals(700.0, insert.calories)
        assertEquals(40.0, insert.protein)
        assertEquals(80.0, insert.carbs)
        assertEquals(30.0, insert.fat)
        assertEquals(12.0, insert.fiber)
        assertEquals(18.0, insert.sugar)
        assertEquals(350.0, insert.sodium)
        assertEquals(8.0, insert.saturated_fat)
        assertEquals(60.0, insert.cholesterol)
        assertEquals(500.0, insert.potassium)
    }

    @Test
    fun two_supabase_nutrition_info_inserts_with_same_data_are_equal() {
        val insert1 = SupabaseNutritionInfoInsert(
            recipe_id = 500L,
            calories = 600.0,
            protein = 35.0,
            carbs = 70.0,
            fat = 25.0
        )
        val insert2 = SupabaseNutritionInfoInsert(
            recipe_id = 500L,
            calories = 600.0,
            protein = 35.0,
            carbs = 70.0,
            fat = 25.0
        )

        assertEquals(insert1, insert2)
    }

    @Test
    fun two_supabase_nutrition_info_inserts_with_different_recipe_ids_are_not_equal() {
        val insert1 = SupabaseNutritionInfoInsert(
            recipe_id = 500L,
            calories = 600.0,
            protein = 35.0,
            carbs = 70.0,
            fat = 25.0
        )
        val insert2 = SupabaseNutritionInfoInsert(
            recipe_id = 501L,
            calories = 600.0,
            protein = 35.0,
            carbs = 70.0,
            fat = 25.0
        )

        assertNotEquals(insert1, insert2)
    }
}
