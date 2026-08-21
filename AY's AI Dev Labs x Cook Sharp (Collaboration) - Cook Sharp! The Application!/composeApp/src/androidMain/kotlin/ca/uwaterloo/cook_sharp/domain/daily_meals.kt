package ca.uwaterloo.cook_sharp.domain

import java.time.LocalDate

data class DailyMeals(
    val date : LocalDate,
    val meals: List<Meal> = emptyList()
)

