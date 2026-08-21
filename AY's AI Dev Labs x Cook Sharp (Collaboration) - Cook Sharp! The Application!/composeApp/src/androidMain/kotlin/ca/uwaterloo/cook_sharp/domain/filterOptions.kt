package ca.uwaterloo.cook_sharp.domain

enum class MealTypeAPI(val label: String) {
    MAIN_COURSE("Main Course"),
    SIDE_DISH("Side Dish"),
    DESSERT("Dessert"),
    APPETIZER("Appetizer"),
    SALAD("Salad"),
    BREAD("Bread"),
    BREAKFAST("Breakfast"),
    SOUP("Soup"),
    BEVERAGE("Beverage"),
    SAUCE("Sauce"),
    MARINADE("Marinade"),
    FINGERFOOD("Fingerfood"),
    SNACK("Snack"),
    DRINK("Drink")
}

enum class DietType(val label: String) {
    GLUTEN_FREE("Gluten Free"),
    KETOGENIC("Ketogenic"),
    VEGETARIAN("Vegetarian"),
    LACTO_VEGETARIAN("Lacto-Vegetarian"),
    OVO_VEGETARIAN("Ovo-Vegetarian"),
    VEGAN("Vegan"),
    PESCETARIAN("Pescetarian"),
    PALEO("Paleo"),
    PRIMAL("Primal"),
    LOW_FODMAP("Low FODMAP"),
    WHOLE30("Whole30")
}

enum class CuisineType(val label: String) {
    AFRICAN("African"),
    ASIAN("Asian"),
    AMERICAN("American"),
    BRITISH("British"),
    CAJUN("Cajun"),
    CARIBBEAN("Caribbean"),
    CHINESE("Chinese"),
    EASTERN_EUROPEAN("Eastern European"),
    EUROPEAN("European"),
    FRENCH("French"),
    GERMAN("German"),
    GREEK("Greek"),
    INDIAN("Indian"),
    IRISH("Irish"),
    ITALIAN("Italian"),
    JAPANESE("Japanese"),
    JEWISH("Jewish"),
    KOREAN("Korean"),
    LATIN_AMERICAN("Latin American"),
    MEDITERRANEAN("Mediterranean"),
    MEXICAN("Mexican"),
    MIDDLE_EASTERN("Middle Eastern"),
    NORDIC("Nordic"),
    SOUTHERN("Southern"),
    SPANISH("Spanish"),
    THAI("Thai"),
    VIETNAMESE("Vietnamese")
}