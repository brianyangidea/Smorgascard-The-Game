package ca.uwaterloo.cook_sharp.ui.screens.signup

import android.annotation.SuppressLint
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import ca.uwaterloo.cook_sharp.ui.theme.TextPrimary
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.Preview
import ca.uwaterloo.cook_sharp.ui.theme.CookSharpTheme
import androidx.compose.foundation.background
import androidx.compose.ui.text.style.TextAlign
import ca.uwaterloo.cook_sharp.ui.theme.*
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun Diet_preference_screen(
    vm: DietPreferenceViewModel = viewModel(),
    onContinue: (String) -> Unit = {}
) {
    val state = vm.ui_state
    val hasSelection = state.selectedDiet != null

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 70.dp, start = 24.dp, end = 24.dp, bottom = 70.dp)
            .navigationBarsPadding()
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "What is your\npreferred diet style ?",
            style = MaterialTheme.typography.titleLarge,
            color = TextPrimary
        )

        Spacer(modifier = Modifier.height(40.dp))

        LazyColumn(
            modifier = Modifier.fillMaxWidth().weight(1f),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            items(vm.dietOptions) { option ->
                DietOptionRow(
                    text = option,
                    selected = (state.selectedDiet == option),
                    onClick = {  vm.diet_selected(option) }
                )
            }
        }
        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = { vm.continue_clicked { diet -> onContinue(diet) } },
            enabled = true,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (hasSelection) EnabledButton else DisabledButton.copy(alpha = 0.5f),
                contentColor = TextPrimary
            )
        ) {
            Text(
                text = "CONTINUE",
                style = MaterialTheme.typography.labelLarge,
                textAlign = TextAlign.Center
            )
        }
        state.errorMessage?.let { msg ->
            Spacer(Modifier.height(10.dp))
            Text(
                text = msg,
                style = MaterialTheme.typography.bodySmall,
                color = LikeAccent,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun DietOptionRow(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val bg = if (selected) ListSelected else CardSurface
    val border = TextPrimary

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(bg)
            .border(1.dp, border, RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .padding(horizontal = 16.dp),
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = TextPrimary,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}
@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun Diet_preference_screen_preview() {
    CookSharpTheme {
        Diet_preference_screen(vm = DietPreferenceViewModel())
    }
}
