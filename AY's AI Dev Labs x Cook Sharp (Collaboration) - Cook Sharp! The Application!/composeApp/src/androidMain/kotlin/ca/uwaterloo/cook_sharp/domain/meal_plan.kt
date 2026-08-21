package ca.uwaterloo.cook_sharp.domain

import java.time.LocalDate

data class MealPlan(
    val id : Int,
    val userId : String,
    val weekStartDate : LocalDate,
    val meals : List<DailyMeals> = emptyList()
)