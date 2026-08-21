package ca.uwaterloo.cook_sharp.ui.screens.login

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ca.uwaterloo.cook_sharp.R
import ca.uwaterloo.cook_sharp.ui.components.placetext_field
import ca.uwaterloo.cook_sharp.ui.components.primary_button
import ca.uwaterloo.cook_sharp.ui.theme.*

@Composable
fun Login_screen(vm: LoginViewModel = viewModel(), onSignUpClick: () -> Unit = {}, onLoginClick: () -> Unit = {}) {
    val state = vm.ui_state

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colorStops =
                        arrayOf(0.0f to AuthGradientTop, 0.4f to AuthGradientTop, 1.0f to AuthGradientBottom)
                )
            )
            .padding(start = 24.dp, end = 24.dp, top = 24.dp, bottom = 60.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(80.dp))

            Text(
                text = "COOK SHARP!",
                style = MaterialTheme.typography.titleLarge,
                color = TextOnDark
            )

            Image(
                painter = painterResource(R.drawable.ramen_bowl),
                contentDescription = "Ramen bowl",
                modifier = Modifier.size(260.dp)
            )
            Text(
                text = "Finally Stop\nForgetting To Eat!",
                style = MaterialTheme.typography.titleMedium,
                color = TextOnDark,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(34.dp))

            placetext_field(
                value = state.username,
                onValueChange = vm::username_change,
                placeholder = "Username",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            placetext_field(
                value = state.password,
                onValueChange = vm::password_change,
                placeholder = "Password",
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

            primary_button(
                text = "Login",
                onClick = {
                    // val ok = vm.login_clicked()
                    // if (ok) onLoginClick()
                    vm.login_clicked(onSuccess = onLoginClick)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            )

            state.errorMessage?.let { msg ->
                Spacer(Modifier.height(10.dp))
                Text(
                    text = msg,
                    style = MaterialTheme.typography.bodySmall,
                    color = LikeAccent
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Don’t have an account? ",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextPrimary
                )

                Text(
                    text = "Sign Up",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextPrimary,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier.clickable { onSignUpClick() }
                )
            }
        }
    }
}

@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun Login_screen_preview() {
    CookSharpTheme {
        Login_screen(vm = LoginViewModel())
    }
}
