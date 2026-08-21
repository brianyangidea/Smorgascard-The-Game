package ca.uwaterloo.cook_sharp.ui.screens.signup

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ca.uwaterloo.cook_sharp.ui.theme.CookSharpTheme
import ca.uwaterloo.cook_sharp.ui.theme.FilterSelected
import ca.uwaterloo.cook_sharp.ui.theme.TextMuted
import ca.uwaterloo.cook_sharp.ui.theme.TextPrimary
import ca.uwaterloo.cook_sharp.ui.theme.*
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun Food_allergy_screen(
    vm: FoodAllergyViewModel = viewModel(),
    onDone: (List<String>) -> Unit = {}
) {
    val state = vm.ui_state

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 40.dp, start = 24.dp, end = 24.dp, bottom = 70.dp)
            .navigationBarsPadding()
            .statusBarsPadding()
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Do you have\nany food allergies ?",
            style = MaterialTheme.typography.titleLarge,
            color = TextPrimary
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Select all that apply",
            style = MaterialTheme.typography.bodySmall,
            color = TextMuted
        )

        Spacer(modifier = Modifier.height(40.dp))
        
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(26.dp),
            horizontalArrangement = Arrangement.spacedBy(18.dp),
            contentPadding = PaddingValues(bottom = 8.dp)
        ) {
            items(
                items = vm.allergyOptions,
                key = { it.id }
            ) { opt ->
                val isSelected = state.allergies.contains(opt.id)
                AllergyTile(
                    option = opt,
                    selected = isSelected,
                    onClick = { vm.change_allergy(opt.id) }
                )
            }
        }
        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = { vm.saveAllergies { onDone(state.allergies.toList()) } },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = EnabledButton,
                contentColor = FilterSelected
            )
        ) {
            Text(
                text = "DONE",
                style = MaterialTheme.typography.labelLarge,
                textAlign = TextAlign.Center,
                color = TextPrimary
            )
        }
    }
}

@Composable
private fun AllergyTile(
    option: Ingredient,
    selected: Boolean,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(14.dp)
    val bg = if (selected) ListSelected else Color.Transparent
    val borderWidth = if (selected) 1.dp else 0.dp
    val borderColor = TextPrimary
    val modifier = Modifier
        .fillMaxWidth()
        .aspectRatio(1f)
        .clickable{onClick()}
        .then(
            if (selected){
                Modifier
                    .clip(shape)
                    .background(bg)
                    .border(borderWidth, borderColor, shape)

            }else{
                Modifier
            }
        ).padding(vertical = 1.dp)



    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (option.iconRes != null) {
                Image(
                    painter = painterResource(option.iconRes),
                    contentDescription = option.label,
                    modifier = Modifier.size(70.dp)
                )
            }
            Text(
                text = option.label,
                style = MaterialTheme.typography.bodyMedium,
                color = TextPrimary
            )
        }
    }
}

@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun Food_allergy_screen_preview() {
    CookSharpTheme {
        Food_allergy_screen(vm = FoodAllergyViewModel())
    }
}
