package ca.uwaterloo.cook_sharp.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.VisualTransformation
import ca.uwaterloo.cook_sharp.ui.theme.*

@Composable
fun placetext_field(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    visualTransformation: VisualTransformation = VisualTransformation.None
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder, color = TextMuted) },
        singleLine = true,
        visualTransformation = visualTransformation,
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = TextFieldBackground,
            unfocusedContainerColor = TextFieldBackground,
            focusedBorderColor = TextFieldBackground,
            unfocusedBorderColor = TextFieldBackground
        ),
        modifier = modifier
    )
}

@Composable
fun primary_button(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = AuthPrimaryButton,
            contentColor = TextOnDark
        ),
        modifier = modifier
    ) {
        Text(text, style = MaterialTheme.typography.labelLarge)
    }
}