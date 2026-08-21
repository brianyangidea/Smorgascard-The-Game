package ca.uwaterloo.cook_sharp.ui.components
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.background
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ca.uwaterloo.cook_sharp.R
import ca.uwaterloo.cook_sharp.ui.theme.CookSharpTheme
import ca.uwaterloo.cook_sharp.ui.theme.PrimaryButtonBackground
import ca.uwaterloo.cook_sharp.ui.theme.TextMuted
import ca.uwaterloo.cook_sharp.ui.theme.TextPrimary
import ca.uwaterloo.cook_sharp.ui.theme.LikeAccent
import androidx.compose.material3.IconButton
import ca.uwaterloo.cook_sharp.domain.Recipe
import coil.compose.AsyncImage
import coil.request.ImageRequest
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.layout.ContentScale
import ca.uwaterloo.cook_sharp.data.mock.MockRecipeRepository

@Composable
fun Recipe_card(
    recipe: Recipe,
    isLiked: Boolean,
    onClick: (Recipe) -> Unit,
    onToggleLike: (Recipe) -> Unit,
    modifier: Modifier = Modifier,
    borderColor: Color = PrimaryButtonBackground,
    userAllergies: List<String> = emptyList(),
) {
    val shape = RoundedCornerShape(16.dp)
    val matchedAllergies = userAllergies.filter { recipe.containsAllergen(it) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .border(width = 2.dp, color = if (matchedAllergies.isNotEmpty()) Color(0xFFCC0000).copy(alpha = 0.6f) else borderColor, shape = shape)
            .clickable { onClick(recipe) }
    ) {
        if (matchedAllergies.isNotEmpty()) {
            val allergyText = matchedAllergies.joinToString(", ") { it.replaceFirstChar { c -> c.uppercase() } }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFFF6B6B).copy(alpha = 0.12f))
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = Color(0xFFCC0000),
                    modifier = Modifier.size(14.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = "Contains: $allergyText",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFFCC0000)
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
        val imageShape = RoundedCornerShape(12.dp)
        Box(
            modifier = Modifier
                .size(74.dp)
                .clip(imageShape),
            contentAlignment = Alignment.Center
        ) {
            val context = LocalContext.current
                AsyncImage(
                model = recipe.image?.let {
                    ImageRequest.Builder(context)
                        .data(it)
                        .crossfade(true)
                        .build()
                },
                contentDescription = recipe.title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(R.drawable.ramen_bowl),
                error = painterResource(R.drawable.ramen_bowl),
                fallback = painterResource(R.drawable.ramen_bowl)
            )
        }

        Spacer(Modifier.width(14.dp))

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = recipe.title,
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = "Cook Time: ${recipe.readyInMinutes} mins",
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted
            )

            Text(
                text = "Difficulty: ${recipe.difficulty}",
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted
            )

            Text(
                text = "Calories: ${recipe.calories.toInt()} kcal",
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted
            )
        }

        Spacer(Modifier.width(30.dp))
        IconButton(
            onClick = { onToggleLike(recipe) },
            modifier = Modifier.size(40.dp)
        ) {
            Icon(
                painter = painterResource(
                    if (isLiked) R.drawable.heart_filled else R.drawable.heart_outline
                ),
                contentDescription = if (isLiked) "Unlike" else "Like",
                tint = if (isLiked) LikeAccent else TextPrimary,
                modifier = Modifier.size(34.dp)
            )
        }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun Recipe_card_preview() {
    val recipe = MockRecipeRepository()
        .getAllRecipes()
        .first()
    CookSharpTheme {
        Recipe_card(
            recipe = recipe,
            onClick = {},
            isLiked = false,
            onToggleLike = {}
        )
    }
}
