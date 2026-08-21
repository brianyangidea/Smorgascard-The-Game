package ca.uwaterloo.cook_sharp.data.mock

import ca.uwaterloo.cook_sharp.domain.Recipe
import ca.uwaterloo.cook_sharp.domain.Ingredient
import ca.uwaterloo.cook_sharp.domain.RecipeSource
import ca.uwaterloo.cook_sharp.domain.MealType
import ca.uwaterloo.cook_sharp.domain.NutritionInfo
import ca.uwaterloo.cook_sharp.domain.RecipeInstruction
object MockRecipes {
    val garlickyKale = Recipe(
        id = 644387L,
        createdByUserId = null,
        title = "Garlicky Kale",
        readyInMinutes = 45,
        difficulty = "Easy",
        image = "https://img.spoonacular.com/recipes/644387-556x370.jpg",
        servings = 2,
        cuisineType = "",

        isVegetarian = true,
        isVegan = true,
        isGlutenFree = true,
        isDairyFree = true,
        isLowFodmap = false,
        isPescatarian = false,
        isKetogenic = false,
        isPaleo = true,
        isWhole30 = true,

        source = RecipeSource.API,
        localId = "644387",
        remoteId = 644387L,

        nutritionInfo = NutritionInfo(
            id = 21L,
            recipeId = 644387L,
            calories = 169.87,
            protein = 5.0,
            carbs = 5.0,
            fat = 5.0,
            fiber = 0.0,
            sugar = 0.0,
            sodium = 0.0,
            saturatedFat = null,
            cholesterol = null,
            potassium = null
        ),

        ingredients = listOf(
            Ingredient(
                id = 2069L,
                recipeId = 644387L,
                name = "balsamic vinegar",
                amount = 3.0,
                unit = "tablespoons",
                originalName = "balsamic vinegar"
            ),
            Ingredient(
                id = 11215L,
                recipeId = 644387L,
                name = "garlic",
                amount = 1.0,
                unit = "clove",
                originalName = "garlic, minced"
            ),
            Ingredient(
                id = 11233L,
                recipeId = 644387L,
                name = "curly kale",
                amount = 1.0,
                unit = "bunch",
                originalName = "curly kale, stems removed and chopped"
            ),
            Ingredient(
                id = 4053L,
                recipeId = 644387L,
                name = "olive oil",
                amount = 2.0,
                unit = "servings",
                originalName = "Olive oil"
            )
        ),

        instructions = listOf(
            RecipeInstruction(
                id = 1L,
                recipeId = 644387L,
                stepNumber = 1,
                instruction = "Heat the olive oil in a large pot over medium heat."
            ),
            RecipeInstruction(
                id = 2L,
                recipeId = 644387L,
                stepNumber = 2,
                instruction = "Add the kale and cover. Stir occasionally until the volume of the kale is reduced by half."
            ),
            RecipeInstruction(
                id = 3L,
                recipeId = 644387L,
                stepNumber = 3,
                instruction = "Uncover. Add garlic and balsamic vinegar; cook ~30 seconds, mixing well."
            ),
            RecipeInstruction(
                id = 4L,
                recipeId = 644387L,
                stepNumber = 4,
                instruction = "Serve hot."
            )
        ),

        mealTypes = listOf(MealType.LUNCH, MealType.DINNER),
    )

    val redLentilSoup = Recipe(
        id = 715415L,
        createdByUserId = null,
        title = "Red Lentil Soup with Chicken and Turnips",
        readyInMinutes = 55,
        difficulty = "Medium",
        image = "https://img.spoonacular.com/recipes/715415-556x370.jpg",
        servings = 8,
        cuisineType = "",

        isVegetarian = false,
        isVegan = false,
        isGlutenFree = true,
        isDairyFree = true,
        isLowFodmap = false,
        isPescatarian = false,
        isKetogenic = false,
        isPaleo = false,
        isWhole30 = false,

        source = RecipeSource.API,
        localId = "715415",
        remoteId = 715415L,

        nutritionInfo = NutritionInfo(
            id = 23L,
            recipeId = 715415L,
            calories = 477.24,
            protein = 5.0,
            carbs = 5.0,
            fat = 5.0,
            fiber = 0.0,
            sugar = 0.0,
            sodium = 0.0,
            saturatedFat = null,
            cholesterol = null,
            potassium = null
        ),

        ingredients = listOf(
            Ingredient(
                id = 9037L,
                recipeId = 715415L,
                name = "avocado",
                amount = 8.0,
                unit = "servings",
                originalName = "additional toppings: diced avocado, micro greens, chopped basil"
            ),
            Ingredient(
                id = 11124L,
                recipeId = 715415L,
                name = "carrots",
                amount = 3.0,
                unit = "medium",
                originalName = "3 medium carrots, peeled and diced"
            ),
            Ingredient(
                id = 10111143L,
                recipeId = 715415L,
                name = "celery stalks",
                amount = 3.0,
                unit = "",
                originalName = "3 celery stalks, diced"
            ),
            Ingredient(
                id = 5064L,
                recipeId = 715415L,
                name = "chicken breast",
                amount = 2.0,
                unit = "cups",
                originalName = "2 cups fully-cooked chicken breast, shredded (may be omitted for a vegetarian version)"
            ),
            Ingredient(
                id = 10311297L,
                recipeId = 715415L,
                name = "flat leaf parsley",
                amount = 0.5,
                unit = "cup",
                originalName = "½ cup flat leaf Italian parsley, chopped (plus extra for garnish)"
            ),
            Ingredient(
                id = 11215L,
                recipeId = 715415L,
                name = "garlic",
                amount = 6.0,
                unit = "cloves",
                originalName = "6 cloves of garlic, finely minced"
            ),
            Ingredient(
                id = 4053L,
                recipeId = 715415L,
                name = "olive oil",
                amount = 2.0,
                unit = "tablespoons",
                originalName = "2 tablespoons olive oil"
            ),
            Ingredient(
                id = 10011693L,
                recipeId = 715415L,
                name = "plum tomatoes",
                amount = 28.0,
                unit = "ounce",
                originalName = "28 ounce-can plum tomatoes, drained and rinsed, chopped"
            ),
            Ingredient(
                id = 10016069L,
                recipeId = 715415L,
                name = "red lentils",
                amount = 2.0,
                unit = "cups",
                originalName = "2 cups dried red lentils, rinsed"
            ),
            Ingredient(
                id = 1102047L,
                recipeId = 715415L,
                name = "salt and black pepper",
                amount = 1.0,
                unit = "to taste",
                originalName = "salt and black pepper, to taste"
            ),
            Ingredient(
                id = 11564L,
                recipeId = 715415L,
                name = "turnip",
                amount = 1.0,
                unit = "large",
                originalName = "1 large turnip, peeled and diced"
            ),
            Ingredient(
                id = 6615L,
                recipeId = 715415L,
                name = "vegetable stock",
                amount = 8.0,
                unit = "cups",
                originalName = "8 cups vegetable stock"
            ),
            Ingredient(
                id = 10511282L,
                recipeId = 715415L,
                name = "yellow onion",
                amount = 1.0,
                unit = "medium",
                originalName = "1 medium yellow onion, diced"
            )
        ),

        instructions = listOf(
            RecipeInstruction(
                id = 1L,
                recipeId = 715415L,
                stepNumber = 1,
                instruction = "Heat the olive oil over medium heat in a large dutch oven or soup pot."
            ),
            RecipeInstruction(
                id = 2L,
                recipeId = 715415L,
                stepNumber = 2,
                instruction = "Add the onion, carrots, and celery; cook 8–10 minutes until tender, stirring occasionally."
            ),
            RecipeInstruction(
                id = 3L,
                recipeId = 715415L,
                stepNumber = 3,
                instruction = "Add garlic and cook 2 minutes until fragrant. Season with salt and black pepper."
            ),
            RecipeInstruction(
                id = 4L,
                recipeId = 715415L,
                stepNumber = 4,
                instruction = "Add tomatoes, turnip, and red lentils. Stir in vegetable stock and bring to a boil; reduce to a simmer and cook until turnips are tender and lentils are cooked (about 20 minutes)."
            ),
            RecipeInstruction(
                id = 5L,
                recipeId = 715415L,
                stepNumber = 5,
                instruction = "Add chicken breast and parsley; cook 5 minutes. Adjust seasoning and serve, garnished with parsley (and optional toppings)."
            )
        ),

        mealTypes = listOf(MealType.LUNCH, MealType.DINNER)
    )

    val asparagusAndPeaSoup = Recipe(
        id = 716406L,
        createdByUserId = null,
        title = "Asparagus and Pea Soup: Real Convenience Food",
        readyInMinutes = 20,
        difficulty = "Easy",
        image = "https://img.spoonacular.com/recipes/716406-556x370.jpg",
        servings = 2,
        cuisineType = "",

        isVegetarian = true,
        isVegan = true,
        isGlutenFree = true,
        isDairyFree = true,
        isLowFodmap = false,
        isPescatarian = false,
        isKetogenic = false,
        isPaleo = true,
        isWhole30 = false,

        source = RecipeSource.API,
        localId = "716406",
        remoteId = 716406L,

        nutritionInfo = NutritionInfo(
            id = 24L,
            recipeId = 716406L,
            calories = 217.43,
            protein = 5.0,
            carbs = 5.0,
            fat = 5.0,
            fiber = 0.0,
            sugar = 0.0,
            sodium = 0.0,
            saturatedFat = null,
            cholesterol = null,
            potassium = null
        ),

        ingredients = listOf(
            Ingredient(
                id = 11011L,
                recipeId = 716406L,
                name = "asparagus",
                amount = 1.0,
                unit = "bag",
                originalName = "1 bag of frozen organic asparagus (preferably thawed)"
            ),
            Ingredient(
                id = 1034053L,
                recipeId = 716406L,
                name = "evoo",
                amount = 1.0,
                unit = "T",
                originalName = "1T EVOO (extra virgin olive oil)"
            ),
            Ingredient(
                id = 11215L,
                recipeId = 716406L,
                name = "garlic",
                amount = 2.0,
                unit = "cloves",
                originalName = "a couple of garlic cloves"
            ),
            Ingredient(
                id = 11282L,
                recipeId = 716406L,
                name = "onion",
                amount = 0.5,
                unit = "",
                originalName = "1/2 onion"
            ),
            Ingredient(
                id = 11304L,
                recipeId = 716406L,
                name = "peas",
                amount = 2.0,
                unit = "c",
                originalName = "2-3c of frozen organic peas"
            ),
            Ingredient(
                id = 99253L,
                recipeId = 716406L,
                name = "vegetable broth",
                amount = 1.0,
                unit = "box",
                originalName = "1 box low-sodium vegetable broth"
            )
        ),

        instructions = listOf(
            RecipeInstruction(
                id = 1L,
                recipeId = 716406L,
                stepNumber = 1,
                instruction = "Chop the garlic and onions."
            ),
            RecipeInstruction(
                id = 2L,
                recipeId = 716406L,
                stepNumber = 2,
                instruction = "Saute the onions in the EVOO; add the garlic after a couple of minutes and cook until the onions are translucent."
            ),
            RecipeInstruction(
                id = 3L,
                recipeId = 716406L,
                stepNumber = 3,
                instruction = "Add the asparagus and cover with the broth. Season with salt and pepper (and red pepper flakes if using). Simmer until asparagus is tender, then turn off heat and puree with an immersion blender."
            ),
            RecipeInstruction(
                id = 4L,
                recipeId = 716406L,
                stepNumber = 4,
                instruction = "Add peas and puree until smooth; add more until it reaches the thickness you like."
            ),
            RecipeInstruction(
                id = 5L,
                recipeId = 716406L,
                stepNumber = 5,
                instruction = "Top with chives and a small dollop of creme fraiche, sour cream, or greek yogurt."
            )
        ),

        mealTypes = listOf(MealType.SNACK)
    )

    val rusticPastaWithChunkyVegetables = Recipe(
        id = 1096282L,
        createdByUserId = null,
        title = "Rustic Pasta with Chunky Vegetables",
        readyInMinutes = 75,
        difficulty = "Hard",
        image = "https://img.spoonacular.com/recipes/1096282-556x370.jpg",
        servings = 4,
        cuisineType = "",

        isVegetarian = true,
        isVegan = false,
        isGlutenFree = false,
        isDairyFree = false,
        isLowFodmap = false,
        isPescatarian = false,
        isKetogenic = false,
        isPaleo = false,
        isWhole30 = false,

        source = RecipeSource.API,
        localId = "1096282",
        remoteId = 1096282L,

        nutritionInfo = NutritionInfo(
            id = 25L,
            recipeId = 1096282L,
            calories = 766.97,
            protein = 5.0,
            carbs = 5.0,
            fat = 5.0,
            fiber = 0.0,
            sugar = 0.0,
            sodium = 0.0,
            saturatedFat = null,
            cholesterol = null,
            potassium = null
        ),

        ingredients = listOf(
            Ingredient(
                id = 11477L,
                recipeId = 1096282L,
                name = "zucchini",
                amount = 1.0,
                unit = "medium",
                originalName = "1 medium zucchini, cut into chunks"
            ),
            Ingredient(
                id = 11124L,
                recipeId = 1096282L,
                name = "carrots",
                amount = 2.0,
                unit = "",
                originalName = "2 carrots, peeled and cut into chunks"
            ),
            Ingredient(
                id = 10011282L,
                recipeId = 1096282L,
                name = "onion",
                amount = 1.0,
                unit = "small",
                originalName = "1 small red onion, cut into 1/8th"
            ),
            Ingredient(
                id = 11266L,
                recipeId = 1096282L,
                name = "cremini mushrooms",
                amount = 8.0,
                unit = "ounces",
                originalName = "8 ounces cremini mushrooms, halved"
            ),
            Ingredient(
                id = 11011L,
                recipeId = 1096282L,
                name = "asparagus spears",
                amount = 12.0,
                unit = "ounces",
                originalName = "12 ounces asparagus spears, ends trimmed and cut into 1-inch pieces"
            ),
            Ingredient(
                id = 1034053L,
                recipeId = 1096282L,
                name = "extra virgin olive oil",
                amount = 0.33333334,
                unit = "cup",
                originalName = "1/3 cup extra virgin olive oil"
            ),
            Ingredient(
                id = 2069L,
                recipeId = 1096282L,
                name = "balsamic vinegar",
                amount = 1.0,
                unit = "tablespoon",
                originalName = "1 tablespoon balsamic vinegar"
            ),
            Ingredient(
                id = 9152L,
                recipeId = 1096282L,
                name = "lemon juice",
                amount = 1.0,
                unit = "teaspoon",
                originalName = "1 teaspoon fresh lemon juice"
            ),
            Ingredient(
                id = 1012049L,
                recipeId = 1096282L,
                name = "thyme",
                amount = 1.0,
                unit = "teaspoon",
                originalName = "1 teaspoon fresh minced thyme"
            ),
            Ingredient(
                id = 2063L,
                recipeId = 1096282L,
                name = "rosemary",
                amount = 1.0,
                unit = "tablespoon",
                originalName = "1 tablespoon fresh minced rosemary"
            ),
            Ingredient(
                id = 2047L,
                recipeId = 1096282L,
                name = "salt",
                amount = 0.25,
                unit = "teaspoon",
                originalName = "¼ teaspoon salt"
            ),
            Ingredient(
                id = 1002030L,
                recipeId = 1096282L,
                name = "pepper",
                amount = 0.25,
                unit = "teaspoon",
                originalName = "¼ teaspoon freshly ground black pepper"
            ),
            Ingredient(
                id = 1034053L,
                recipeId = 1096282L,
                name = "extra virgin olive oil",
                amount = 2.0,
                unit = "tablespoons",
                originalName = "2 tablespoons extra virgin olive oil"
            ),
            Ingredient(
                id = 11677L,
                recipeId = 1096282L,
                name = "shallots",
                amount = 2.0,
                unit = "",
                originalName = "2 shallots, minced"
            ),
            Ingredient(
                id = 11216L,
                recipeId = 1096282L,
                name = "ginger",
                amount = 3.0,
                unit = "tablespoons",
                originalName = "3 tablespoons minced ginger"
            ),
            Ingredient(
                id = 11215L,
                recipeId = 1096282L,
                name = "garlic",
                amount = 4.0,
                unit = "cloves",
                originalName = "4 cloves garlic, minced"
            ),
            Ingredient(
                id = 11693L,
                recipeId = 1096282L,
                name = "tomatoes",
                amount = 56.0,
                unit = "ounce",
                originalName = "2 28-ounce cans crushed tomatoes"
            ),
            Ingredient(
                id = 2037L,
                recipeId = 1096282L,
                name = "saffron threads",
                amount = 0.25,
                unit = "teaspoon",
                originalName = "¼ teaspoon saffron threads"
            ),
            Ingredient(
                id = 93820L,
                recipeId = 1096282L,
                name = "mascarpone cheese",
                amount = 3.0,
                unit = "tablespoons",
                originalName = "3 tablespoons mascarpone cheese"
            ),
            Ingredient(
                id = 99182L,
                recipeId = 1096282L,
                name = "penne",
                amount = 12.0,
                unit = "ounce",
                originalName = "12 ounce box whole wheat penne"
            )
        ),

        instructions = listOf(
            RecipeInstruction(
                id = 1L,
                recipeId = 1096282L,
                stepNumber = 1,
                instruction = "Heat oven to 400°F."
            ),
            RecipeInstruction(
                id = 2L,
                recipeId = 1096282L,
                stepNumber = 2,
                instruction = "Place vegetable pieces in a large bowl."
            ),
            RecipeInstruction(
                id = 3L,
                recipeId = 1096282L,
                stepNumber = 3,
                instruction = "Whisk olive oil, herbs, salt, pepper, vinegar, and lemon juice; pour over vegetables and toss to coat."
            ),
            RecipeInstruction(
                id = 4L,
                recipeId = 1096282L,
                stepNumber = 4,
                instruction = "Spread on a baking sheet and roast 30–40 minutes until vegetables are cooked through; set aside."
            ),
            RecipeInstruction(
                id = 5L,
                recipeId = 1096282L,
                stepNumber = 5,
                instruction = "Heat olive oil in a large pot over medium heat."
            ),
            RecipeInstruction(
                id = 6L,
                recipeId = 1096282L,
                stepNumber = 6,
                instruction = "Add shallots and ginger; sauté until shallots are soft (about 3 minutes)."
            ),
            RecipeInstruction(
                id = 7L,
                recipeId = 1096282L,
                stepNumber = 7,
                instruction = "Meanwhile, boil salted water and cook pasta according to package directions."
            ),
            RecipeInstruction(
                id = 8L,
                recipeId = 1096282L,
                stepNumber = 8,
                instruction = "Add garlic; sauté 1 minute. Stir in tomatoes and saffron; simmer 20 minutes."
            ),
            RecipeInstruction(
                id = 9L,
                recipeId = 1096282L,
                stepNumber = 9,
                instruction = "Stir in mascarpone cheese."
            ),
            RecipeInstruction(
                id = 10L,
                recipeId = 1096282L,
                stepNumber = 10,
                instruction = "Add roasted vegetables (plus herbs/liquid from the baking sheet) to the sauce; heat through and adjust salt/pepper."
            ),
            RecipeInstruction(
                id = 11L,
                recipeId = 1096282L,
                stepNumber = 11,
                instruction = "Toss cooked pasta with the sauce and serve."
            )
        ),

        mealTypes = listOf(MealType.LUNCH, MealType.DINNER)
    )

    val pestoZucchiniPastaWhole30 = Recipe(
        id = 1096227L,
        createdByUserId = null,
        title = "Pesto Zucchini Pasta (Whole 30 Approved)",
        readyInMinutes = 35,
        difficulty = "Medium",
        image = "https://img.spoonacular.com/recipes/1096227-556x370.jpg",
        servings = 4,
        cuisineType = "",

        isVegetarian = true,
        isVegan = true,
        isGlutenFree = true,
        isDairyFree = true,
        isLowFodmap = false,
        isPescatarian = false,
        isKetogenic = false,
        isPaleo = true,
        isWhole30 = true,

        source = RecipeSource.API,
        localId = "1096227",
        remoteId = 1096227L,

        nutritionInfo = NutritionInfo(
            id = 26L,
            recipeId = 1096227L,
            calories = 188.51,
            protein = 5.0,
            carbs = 5.0,
            fat = 5.0,
            fiber = 0.0,
            sugar = 0.0,
            sodium = 0.0,
            saturatedFat = null,
            cholesterol = null,
            potassium = null
        ),

        ingredients = listOf(
            Ingredient(
                id = 11477L,
                recipeId = 1096227L,
                name = "zucchini spiralized",
                amount = 3.0,
                unit = "large",
                originalName = "3 large zucchini spiralized"
            ),
            Ingredient(
                id = 2044L,
                recipeId = 1096227L,
                name = "basil",
                amount = 3.0,
                unit = "cups",
                originalName = "3 cups fresh basil"
            ),
            Ingredient(
                id = 12147L,
                recipeId = 1096227L,
                name = "pine nuts",
                amount = 0.25,
                unit = "cup",
                originalName = "¼ cup pine nuts"
            ),
            Ingredient(
                id = 10211215L,
                recipeId = 1096227L,
                name = "garlic cloves",
                amount = 3.0,
                unit = "",
                originalName = "3 garlic cloves"
            ),
            Ingredient(
                id = 1034053L,
                recipeId = 1096227L,
                name = "olive oil",
                amount = 0.5,
                unit = "cup",
                originalName = "½ cup extra-virgin olive oil"
            ),
            Ingredient(
                id = 2047L,
                recipeId = 1096227L,
                name = "salt",
                amount = 0.5,
                unit = "teaspoon",
                originalName = "½ teaspoon salt"
            ),
            Ingredient(
                id = 1002030L,
                recipeId = 1096227L,
                name = "pepper",
                amount = 0.5,
                unit = "teaspoon",
                originalName = "½ teaspoon pepper"
            ),
            Ingredient(
                id = 4047L,
                recipeId = 1096227L,
                name = "coconut oil",
                amount = 1.0,
                unit = "tablespoon",
                originalName = "1 tablespoon coconut oil"
            ),
            Ingredient(
                id = 11529L,
                recipeId = 1096227L,
                name = "tomato",
                amount = 1.0,
                unit = "",
                originalName = "1 tomato diced"
            )
        ),

        instructions = listOf(
            RecipeInstruction(
                id = 1L,
                recipeId = 1096227L,
                stepNumber = 1,
                instruction = "Place zoodles on paper towels, sprinkle with sea salt, and let sit 20 minutes to draw out moisture. Pat dry."
            ),
            RecipeInstruction(
                id = 2L,
                recipeId = 1096227L,
                stepNumber = 2,
                instruction = "Make pesto: blend basil, pine nuts, garlic, olive oil, salt, and pepper in a food processor until smooth (about 2 minutes)."
            ),
            RecipeInstruction(
                id = 3L,
                recipeId = 1096227L,
                stepNumber = 3,
                instruction = "Warm coconut oil in a saucepan over medium heat."
            ),
            RecipeInstruction(
                id = 4L,
                recipeId = 1096227L,
                stepNumber = 4,
                instruction = "Add zoodles, ~1/2 cup pesto, and diced tomato. Cook about 5 minutes, stirring occasionally, until warmed through."
            ),
            RecipeInstruction(
                id = 5L,
                recipeId = 1096227L,
                stepNumber = 5,
                instruction = "Garnish with extra pine nuts and save remaining pesto for later."
            )
        ),

        mealTypes = listOf(MealType.SNACK)
    )

    val caramelPeanutFudgeCake = Recipe(
        id = 637016L,
        createdByUserId = null,
        title = "Caramel Peanut Fudge Cake",
        readyInMinutes = 45,
        difficulty = "Medium",
        image = "https://img.spoonacular.com/recipes/637016-556x370.jpg",
        servings = 10,
        cuisineType = "",

        isVegetarian = true,
        isVegan = false,
        isGlutenFree = true,
        isDairyFree = false,
        isLowFodmap = false,
        isPescatarian = false,
        isKetogenic = false,
        isPaleo = false,
        isWhole30 = false,

        source = RecipeSource.API,
        localId = "637016",
        remoteId = 637016L,

        nutritionInfo = NutritionInfo(
            id = 27L,
            recipeId = 637016L,
            calories = 459.09,
            protein = 5.0,
            carbs = 5.0,
            fat = 5.0,
            fiber = 0.0,
            sugar = 0.0,
            sodium = 0.0,
            saturatedFat = null,
            cholesterol = null,
            potassium = null
        ),

        ingredients = listOf(
            Ingredient(
                id = 12061L,
                recipeId = 637016L,
                name = "almonds",
                amount = 100.0,
                unit = "g",
                originalName = "100g almonds, finely ground"
            ),
            Ingredient(
                id = 19165L,
                recipeId = 637016L,
                name = "cocoa",
                amount = 25.0,
                unit = "g",
                originalName = "25g cocoa"
            ),
            Ingredient(
                id = 19165L,
                recipeId = 637016L,
                name = "chocolate (70% cocoa)",
                amount = 300.0,
                unit = "g",
                originalName = "300g chocolate of at least 70% cocoa parts"
            ),
            Ingredient(
                id = 1053L,
                recipeId = 637016L,
                name = "cream",
                amount = 500.0,
                unit = "ml",
                originalName = "500ml cream"
            ),
            Ingredient(
                id = 1124L,
                recipeId = 637016L,
                name = "eggs",
                amount = 6.0,
                unit = "",
                originalName = "6 eggs, egg yolks separated from the egg whites"
            ),
            Ingredient(
                id = 16091L,
                recipeId = 637016L,
                name = "peanuts",
                amount = 150.0,
                unit = "g",
                originalName = "150g peanuts, coarsely chopped"
            ),
            Ingredient(
                id = 11114037L,
                recipeId = 637016L,
                name = "rum",
                amount = 1.0,
                unit = "Tbsp",
                originalName = "1 Tbs rum"
            ),
            Ingredient(
                id = 19335L,
                recipeId = 637016L,
                name = "sugar",
                amount = 150.0,
                unit = "g",
                originalName = "150g sugar"
            ),
            Ingredient(
                id = 14412L,
                recipeId = 637016L,
                name = "water",
                amount = 2.0,
                unit = "Tbsp",
                originalName = "2 Tbs water"
            )
        ),

        instructions = listOf(
            RecipeInstruction(
                id = 1L,
                recipeId = 637016L,
                stepNumber = 1,
                instruction = "Sponge: Beat egg yolks with sugar 3–4 minutes until pale and doubled in volume."
            ),
            RecipeInstruction(
                id = 2L,
                recipeId = 637016L,
                stepNumber = 2,
                instruction = "Whisk egg whites to soft peaks, then gently fold into the yolk mixture."
            ),
            RecipeInstruction(
                id = 3L,
                recipeId = 637016L,
                stepNumber = 3,
                instruction = "Gently stir in ground almonds and cocoa."
            ),
            RecipeInstruction(
                id = 4L,
                recipeId = 637016L,
                stepNumber = 4,
                instruction = "Prepare a 20 cm round cake pan (butter + line with parchment). Pour in batter and bake at 180°C about 20 minutes, until a toothpick comes out clean. Cool completely, then split into 2 layers."
            ),
            RecipeInstruction(
                id = 5L,
                recipeId = 637016L,
                stepNumber = 5,
                instruction = "Caramel cream: Heat sugar + water until dissolved and boiling; continue cooking (no stirring) until golden amber. Remove from heat and let cool slightly."
            ),
            RecipeInstruction(
                id = 6L,
                recipeId = 637016L,
                stepNumber = 6,
                instruction = "Whip cream, gradually mix in caramel syrup. Stir in chopped peanuts. Let thicken to room temp."
            ),
            RecipeInstruction(
                id = 7L,
                recipeId = 637016L,
                stepNumber = 7,
                instruction = "Ganache: Bring cream just to a boil and pour over chocolate. Rest 10 minutes, then stir gently until smooth. Chill ~1 hour, then beat 2–3 minutes until fluffy (don’t overbeat)."
            ),
            RecipeInstruction(
                id = 8L,
                recipeId = 637016L,
                stepNumber = 8,
                instruction = "Assemble: Sprinkle each cake layer with rum + water syrup. Spread caramel cream on bottom layer, add top layer (wet side down), then frost with ganache."
            )
        ),

        mealTypes = emptyList()
    )

    val rosemaryWalnutBread = Recipe(
        id = 658813L,
        createdByUserId = null,
        title = "Rosemary Walnut Bread",
        readyInMinutes = 45,
        difficulty = "Medium",
        image = "https://img.spoonacular.com/recipes/658813-556x370.jpg",
        servings = 16,
        cuisineType = "",

        isVegetarian = true,
        isVegan = false,
        isGlutenFree = false,
        isDairyFree = false,
        isLowFodmap = false,
        isPescatarian = false,
        isKetogenic = false,
        isPaleo = false,
        isWhole30 = false,

        source = RecipeSource.API,
        localId = "658813",
        remoteId = 658813L,

        nutritionInfo = NutritionInfo(
            id = 28L,
            recipeId = 658813L,
            calories = 308.32,
            protein = 5.0,
            carbs = 5.0,
            fat = 5.0,
            fiber = 0.0,
            sugar = 0.0,
            sodium = 0.0,
            saturatedFat = null,
            cholesterol = null,
            potassium = null
        ),

        ingredients = listOf(
            Ingredient(
                id = 1017L,
                recipeId = 658813L,
                name = "cream cheese",
                amount = 4.0,
                unit = "ounces",
                originalName = "4 ounces cream cheese"
            ),
            Ingredient(
                id = 18375L,
                recipeId = 658813L,
                name = "yeast",
                amount = 2.0,
                unit = "packages",
                originalName = "2 packages dry yeast"
            ),
            Ingredient(
                id = 1123L,
                recipeId = 658813L,
                name = "eggs",
                amount = 3.0,
                unit = "",
                originalName = "3 eggs"
            ),
            Ingredient(
                id = 20081L,
                recipeId = 658813L,
                name = "flour",
                amount = 5.0,
                unit = "cups",
                originalName = "5 cups flour"
            ),
            Ingredient(
                id = 19296L,
                recipeId = 658813L,
                name = "honey",
                amount = 2.0,
                unit = "tablespoons",
                originalName = "2 tablespoons honey"
            ),
            Ingredient(
                id = 9156L,
                recipeId = 658813L,
                name = "lemon zest",
                amount = 2.0,
                unit = "tablespoons",
                originalName = "2 tablespoons lemon zest"
            ),
            Ingredient(
                id = 1077L,
                recipeId = 658813L,
                name = "milk",
                amount = 1.5,
                unit = "cups",
                originalName = "1 1/2 cups milk (100 to 110 degrees)"
            ),
            Ingredient(
                id = 4053L,
                recipeId = 658813L,
                name = "olive oil",
                amount = 1.0,
                unit = "tablespoon",
                originalName = "1 tablespoon olive oil"
            ),
            Ingredient(
                id = 2036L,
                recipeId = 658813L,
                name = "rosemary",
                amount = 3.0,
                unit = "tablespoons",
                originalName = "3 tablespoons chopped rosemary"
            ),
            Ingredient(
                id = 2047L,
                recipeId = 658813L,
                name = "salt",
                amount = 1.0,
                unit = "to taste",
                originalName = "Salt to taste"
            ),
            Ingredient(
                id = 12155L,
                recipeId = 658813L,
                name = "walnuts",
                amount = 2.0,
                unit = "cups",
                originalName = "2 cups walnuts"
            )
        ),

        instructions = listOf(
            RecipeInstruction(
                id = 1L,
                recipeId = 658813L,
                stepNumber = 1,
                instruction = "Combine first four ingredients, add yeast, and let stand for 5 minutes."
            ),
            RecipeInstruction(
                id = 2L,
                recipeId = 658813L,
                stepNumber = 2,
                instruction = "Stir in two cups flour, cover with plastic, and place in a warm spot (85 degrees) for 15 minutes."
            ),
            RecipeInstruction(
                id = 3L,
                recipeId = 658813L,
                stepNumber = 3,
                instruction = "Add rest of flour, nuts, lemon, rosemary, and 2 eggs."
            ),
            RecipeInstruction(
                id = 4L,
                recipeId = 658813L,
                stepNumber = 4,
                instruction = "Mix in bowl until it sticks together, then turn onto floured surface and knead for 10 minutes."
            ),
            RecipeInstruction(
                id = 5L,
                recipeId = 658813L,
                stepNumber = 5,
                instruction = "Rub top with olive oil and place in oiled bowl."
            ),
            RecipeInstruction(
                id = 6L,
                recipeId = 658813L,
                stepNumber = 6,
                instruction = "Cover with damp cloth, and let rise for 1 hour."
            ),
            RecipeInstruction(
                id = 7L,
                recipeId = 658813L,
                stepNumber = 7,
                instruction = "Return dough to floured surface and form into two leaf-shaped loaves."
            ),
            RecipeInstruction(
                id = 8L,
                recipeId = 658813L,
                stepNumber = 8,
                instruction = "Make three diagonal slashes about 1/2 inch deep."
            ),
            RecipeInstruction(
                id = 9L,
                recipeId = 658813L,
                stepNumber = 9,
                instruction = "Brush top with egg, and let rise for 30 minutes."
            ),
            RecipeInstruction(
                id = 10L,
                recipeId = 658813L,
                stepNumber = 10,
                instruction = "Bake in 375 degree oven for 40 minutes."
            ),
            RecipeInstruction(
                id = 11L,
                recipeId = 658813L,
                stepNumber = 11,
                instruction = "Let stand twenty minutes before slicing."
            )
        ),

        mealTypes = emptyList()
    )

    val romaTomatoBruschetta = Recipe(
        id = 658753L,
        createdByUserId = null,
        title = "Roma Tomato Bruschetta",
        readyInMinutes = 45,
        difficulty = "Easy",
        image = "https://img.spoonacular.com/recipes/658753-556x370.jpg",
        servings = 4,
        cuisineType = "",

        isVegetarian = true,
        isVegan = true,
        isGlutenFree = false,
        isDairyFree = true,
        isLowFodmap = false,
        isPescatarian = false,
        isKetogenic = false,
        isPaleo = false,
        isWhole30 = false,

        source = RecipeSource.API,
        localId = "658753",
        remoteId = 658753L,

        nutritionInfo = NutritionInfo(
            id = 29L,
            recipeId = 658753L,
            calories = 445.71,
            protein = 5.0,
            carbs = 5.0,
            fat = 5.0,
            fiber = 0.0,
            sugar = 0.0,
            sodium = 0.0,
            saturatedFat = null,
            cholesterol = null,
            potassium = null
        ),

        ingredients = listOf(
            Ingredient(
                id = 2069L,
                recipeId = 658753L,
                name = "balsamic vinegar",
                amount = 2.0,
                unit = "tablespoons",
                originalName = "2 tablespoons balsamic vinegar"
            ),
            Ingredient(
                id = 1034053L,
                recipeId = 658753L,
                name = "extra virgin olive oil",
                amount = 4.0,
                unit = "tablespoons",
                originalName = "4 tablespoons extra virgin olive oil"
            ),
            Ingredient(
                id = 18029L,
                recipeId = 658753L,
                name = "bread",
                amount = 1.0,
                unit = "loaf",
                originalName = "1 loaf french bread"
            ),
            Ingredient(
                id = 2044L,
                recipeId = 658753L,
                name = "basil",
                amount = 10.0,
                unit = "leaves",
                originalName = "10 leaves fresh basil"
            ),
            Ingredient(
                id = 11215L,
                recipeId = 658753L,
                name = "garlic",
                amount = 1.0,
                unit = "clove",
                originalName = "1 clove garlic, minced"
            ),
            Ingredient(
                id = 10211821L,
                recipeId = 658753L,
                name = "bell pepper",
                amount = 4.0,
                unit = "servings",
                originalName = "Pepper to taste"
            ),
            Ingredient(
                id = 2047L,
                recipeId = 658753L,
                name = "salt",
                amount = 4.0,
                unit = "servings",
                originalName = "Salt to taste"
            ),
            Ingredient(
                id = 11529L,
                recipeId = 658753L,
                name = "tomatoes",
                amount = 4.0,
                unit = "medium",
                originalName = "4 mediums tomatoes, roma"
            )
        ),

        instructions = listOf(
            RecipeInstruction(
                id = 1L,
                recipeId = 658753L,
                stepNumber = 1,
                instruction = "Slice the bread on a bias about 1/2 inch thick (about 10–12 slices)."
            ),
            RecipeInstruction(
                id = 2L,
                recipeId = 658753L,
                stepNumber = 2,
                instruction = "Mix 2 tablespoons of extra virgin olive oil with salt and pepper (about 1/3 teaspoon each, or to taste)."
            ),
            RecipeInstruction(
                id = 3L,
                recipeId = 658753L,
                stepNumber = 3,
                instruction = "Brush the mixture on both sides of the bread slices."
            ),
            RecipeInstruction(
                id = 4L,
                recipeId = 658753L,
                stepNumber = 4,
                instruction = "Bake on a sheet pan in a 400°F preheated oven for about 3–4 minutes until golden; flip and crisp the other side too."
            ),
            RecipeInstruction(
                id = 5L,
                recipeId = 658753L,
                stepNumber = 5,
                instruction = "While hot, rub a garlic clove on the toasted bread (crostini)."
            ),
            RecipeInstruction(
                id = 6L,
                recipeId = 658753L,
                stepNumber = 6,
                instruction = "Chop the roma tomatoes."
            ),
            RecipeInstruction(
                id = 7L,
                recipeId = 658753L,
                stepNumber = 7,
                instruction = "Chiffonade the basil and add to the tomatoes."
            ),
            RecipeInstruction(
                id = 8L,
                recipeId = 658753L,
                stepNumber = 8,
                instruction = "Add salt, pepper, olive oil, and balsamic vinegar; let it rest a few minutes."
            ),
            RecipeInstruction(
                id = 9L,
                recipeId = 658753L,
                stepNumber = 9,
                instruction = "Top each crostini generously with the tomato mixture and serve."
            )
        ),

        mealTypes = listOf(MealType.SNACK)
    )

    val radishSnapPeaQuinoaSalad = Recipe(
        id = 657719L,
        createdByUserId = null,
        title = "Radish & Snap Pea Quinoa Salad",
        readyInMinutes = 45,
        difficulty = "Easy",
        image = "https://img.spoonacular.com/recipes/657719-556x370.jpg",
        servings = 4,
        cuisineType = "",

        isVegetarian = true,
        isVegan = false,
        isGlutenFree = true,
        isDairyFree = false,
        isLowFodmap = false,
        isPescatarian = false,
        isKetogenic = false,
        isPaleo = false,
        isWhole30 = false,

        source = RecipeSource.API,
        localId = "657719",
        remoteId = 657719L,

        nutritionInfo = NutritionInfo(
            id = 30L,
            recipeId = 657719L,
            calories = 262.13,
            protein = 5.0,
            carbs = 5.0,
            fat = 5.0,
            fiber = 0.0,
            sugar = 0.0,
            sodium = 0.0,
            saturatedFat = null,
            cholesterol = null,
            potassium = null
        ),

        ingredients = listOf(
            Ingredient(
                id = 20035L,
                recipeId = 657719L,
                name = "quinoa",
                amount = 1.0,
                unit = "cup",
                originalName = "1 cup quinoa, rinsed"
            ),
            Ingredient(
                id = 14412L,
                recipeId = 657719L,
                name = "water",
                amount = 2.0,
                unit = "cups",
                originalName = "2 cups water"
            ),
            Ingredient(
                id = 10011300L,
                recipeId = 657719L,
                name = "snap peas",
                amount = 1.0,
                unit = "pint",
                originalName = "1 pint snap peas, cut in half (about 2 cups total)"
            ),
            Ingredient(
                id = 11429L,
                recipeId = 657719L,
                name = "radishes",
                amount = 7.0,
                unit = "medium",
                originalName = "7 or 8 medium radishes, sliced"
            ),
            Ingredient(
                id = 10211297L,
                recipeId = 657719L,
                name = "flat leaf parsley",
                amount = 1.0,
                unit = "large handful",
                originalName = "large handful of flat leaf parsley, minced"
            ),
            Ingredient(
                id = 1001116L,
                recipeId = 657719L,
                name = "yogurt",
                amount = 0.25,
                unit = "cup",
                originalName = "1/4 cup plain yogurt (cultured coconut milk is my favorite option, but any plain yogurt will work)"
            ),
            Ingredient(
                id = 12698L,
                recipeId = 657719L,
                name = "tahini",
                amount = 2.0,
                unit = "tbsp",
                originalName = "2 tbsp tahini"
            ),
            Ingredient(
                id = 9152L,
                recipeId = 657719L,
                name = "juice of lemon",
                amount = 0.5,
                unit = "",
                originalName = "juice of 1/2 a lemon"
            ),
            Ingredient(
                id = 1012047L,
                recipeId = 657719L,
                name = "sea salt",
                amount = 4.0,
                unit = "servings",
                originalName = "sea salt to taste"
            ),
            Ingredient(
                id = 14412L,
                recipeId = 657719L,
                name = "water",
                amount = 4.0,
                unit = "servings",
                originalName = "water as needed"
            )
        ),

        instructions = listOf(
            RecipeInstruction(
                id = 1L,
                recipeId = 657719L,
                stepNumber = 1,
                instruction = "Make the quinoa: Combine the quinoa and water in a saucepan and bring to a boil. Reduce heat to a simmer, stir, cover, and cook 10–15 minutes until water is absorbed."
            ),
            RecipeInstruction(
                id = 2L,
                recipeId = 657719L,
                stepNumber = 2,
                instruction = "Remove from heat, let sit 5 minutes, then fluff with a fork. Cool before assembling the salad (quinoa can be made ahead and refrigerated)."
            ),
            RecipeInstruction(
                id = 3L,
                recipeId = 657719L,
                stepNumber = 3,
                instruction = "Make the dressing: Whisk yogurt, tahini, lemon juice, and a pinch of sea salt until smooth. Thin with a bit of water if too thick."
            ),
            RecipeInstruction(
                id = 4L,
                recipeId = 657719L,
                stepNumber = 4,
                instruction = "Make the salad: Combine quinoa, snap peas, radishes, and parsley. Add about half the dressing and toss."
            ),
            RecipeInstruction(
                id = 5L,
                recipeId = 657719L,
                stepNumber = 5,
                instruction = "Taste and add more dressing or sea salt if desired. Serve at room temperature or chilled."
            )
        ),

        mealTypes = listOf(MealType.SNACK)
    )

    val doughnuts = Recipe(
        id = 716276L,
        createdByUserId = null,
        title = "Doughnuts",
        readyInMinutes = 45,
        difficulty = "Medium",
        image = "https://img.spoonacular.com/recipes/716276-556x370.jpg",
        servings = 2,
        cuisineType = "",

        isVegetarian = true,
        isVegan = false,
        isGlutenFree = false,
        isDairyFree = false,
        isLowFodmap = false,
        isPescatarian = false,
        isKetogenic = false,
        isPaleo = false,
        isWhole30 = false,

        source = RecipeSource.API,
        localId = "716276",
        remoteId = 716276L,

        nutritionInfo = NutritionInfo(
            id = 31L,
            recipeId = 716276L,
            calories = 430.24,
            protein = 5.0,
            carbs = 5.0,
            fat = 5.0,
            fiber = 0.0,
            sugar = 0.0,
            sodium = 0.0,
            saturatedFat = null,
            cholesterol = null,
            potassium = null
        ),

        ingredients = listOf(
            Ingredient(
                id = 20081L,
                recipeId = 716276L,
                name = "flour",
                amount = 1.5,
                unit = "cups",
                originalName = "1.5 cups of flour"
            ),
            Ingredient(
                id = 19296L,
                recipeId = 716276L,
                name = "honey",
                amount = 30.0,
                unit = "ml",
                originalName = "30 ml honey"
            ),
            Ingredient(
                id = 1090L,
                recipeId = 716276L,
                name = "powdered milk",
                amount = 1.0,
                unit = "tablespoon",
                originalName = "1 tablespoon of powdered milk"
            ),
            Ingredient(
                id = 2047L,
                recipeId = 716276L,
                name = "salt",
                amount = 0.5,
                unit = "teaspoon",
                originalName = "1/2 teaspoon salt"
            ),
            Ingredient(
                id = 14412L,
                recipeId = 716276L,
                name = "warm water",
                amount = 150.0,
                unit = "ml",
                originalName = "150 ml warm water"
            ),
            Ingredient(
                id = 18375L,
                recipeId = 716276L,
                name = "yeast",
                amount = 1.0,
                unit = "teaspoon",
                originalName = "1 teaspoon yeast"
            )
        ),

        instructions = listOf(
            RecipeInstruction(
                id = 1L,
                recipeId = 716276L,
                stepNumber = 1,
                instruction = "Mix warm water with yeast and honey, whisk, and let sit 15 minutes until foamy."
            ),
            RecipeInstruction(
                id = 2L,
                recipeId = 716276L,
                stepNumber = 2,
                instruction = "Mix flour, salt, and powdered milk. Pour in yeast mixture and knead until elastic and not sticky."
            ),
            RecipeInstruction(
                id = 3L,
                recipeId = 716276L,
                stepNumber = 3,
                instruction = "Cover and let dough rise until doubled (about 1–2 hours)."
            ),
            RecipeInstruction(
                id = 4L,
                recipeId = 716276L,
                stepNumber = 4,
                instruction = "Roll out dough (not too thin), cut circles and holes for doughnuts, and let rise again 45 minutes."
            ),
            RecipeInstruction(
                id = 5L,
                recipeId = 716276L,
                stepNumber = 5,
                instruction = "Heat oil and fry doughnuts until browned on both sides."
            ),
            RecipeInstruction(
                id = 6L,
                recipeId = 716276L,
                stepNumber = 6,
                instruction = "Optional glaze: Mix powdered sugar with milk and vanilla; drizzle on doughnuts. Add sprinkles if desired."
            )
        ),

        mealTypes = listOf(MealType.BREAKFAST)
    )

    val userCreatedMasalaOats = Recipe(
        id = 999001L,
        createdByUserId = "sueflay999",
        title = "Masala Oats",
        readyInMinutes = 10,
        difficulty = "Easy",
        image = null,
        servings = 1,
        cuisineType = "Indian",

        isVegetarian = true,
        isVegan = false,
        isGlutenFree = false,
        isDairyFree = true,
        isLowFodmap = false,
        isPescatarian = false,
        isKetogenic = false,
        isPaleo = false,
        isWhole30 = false,

        source = RecipeSource.USER,
        localId = "999001",
        remoteId = null,

        nutritionInfo = NutritionInfo(
            id = 32L,
            recipeId = 999001L,
            calories = 320.0,
            protein = 5.0,
            carbs = 5.0,
            fat = 5.0,
            fiber = 0.0,
            sugar = 0.0,
            sodium = 0.0,
            saturatedFat = null,
            cholesterol = null,
            potassium = null
        ),

        ingredients = listOf(
            Ingredient(
                id = 1L,
                recipeId = 999001L,
                name = "rolled oats",
                amount = 0.5,
                unit = "cup",
                originalName = "rolled oats"
            ),
            Ingredient(
                id = 2L,
                recipeId = 999001L,
                name = "water",
                amount = 1.25,
                unit = "cup",
                originalName = "water"
            ),
            Ingredient(
                id = 3L,
                recipeId = 999001L,
                name = "frozen peas",
                amount = 0.25,
                unit = "cup",
                originalName = "frozen peas"
            ),
            Ingredient(
                id = 4L,
                recipeId = 999001L,
                name = "garam masala",
                amount = 0.5,
                unit = "tsp",
                originalName = "garam masala"
            ),
            Ingredient(
                id = 5L,
                recipeId = 999001L,
                name = "salt",
                amount = 0.25,
                unit = "tsp",
                originalName = "salt"
            )
        ),

        instructions = listOf(
            RecipeInstruction(
                id = 1L,
                recipeId = 999001L,
                stepNumber = 1,
                instruction = "Bring water to a boil."
            ),
            RecipeInstruction(
                id = 2L,
                recipeId = 999001L,
                stepNumber = 2,
                instruction = "Add oats and cook for 4–5 minutes, stirring."
            ),
            RecipeInstruction(
                id = 3L,
                recipeId = 999001L,
                stepNumber = 3,
                instruction = "Stir in peas and spices; cook 2 more minutes."
            ),
            RecipeInstruction(
                id = 4L,
                recipeId = 999001L,
                stepNumber = 4,
                instruction = "Serve hot."
            )
        ),

        mealTypes = listOf(MealType.BREAKFAST)
    )

    val recipes = listOf(garlickyKale, redLentilSoup, asparagusAndPeaSoup,rusticPastaWithChunkyVegetables, pestoZucchiniPastaWhole30,
                         caramelPeanutFudgeCake, rosemaryWalnutBread, romaTomatoBruschetta, radishSnapPeaQuinoaSalad, doughnuts, userCreatedMasalaOats)
}