package ca.uwaterloo.cook_sharp.ui.screens.signup

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
fun Signup_screen(
    vm: SignupViewModel = viewModel(),
    onLoginClick: () -> Unit = {},
    onCreateAccountClick: () -> Unit = {}
) {
    val state = vm.ui_state

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colorStops = arrayOf(
                        0.0f to AuthGradientTop,
                        0.6f to AuthGradientTop,
                        1.0f to AuthGradientBottom
                    )
                )
            )
            .padding(start = 24.dp, end = 24.dp, top = 24.dp, bottom = 60.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(76.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(R.drawable.ramen_bowl),
                    contentDescription = "Ramen",
                    modifier = Modifier.size(100.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "Create An Account",
                    style = MaterialTheme.typography.labelLarge,
                    color = TextOnDark
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Start building healthier meal habits today!",
                style = MaterialTheme.typography.bodyMedium,
                color = TextOnDark,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(46.dp))

            placetext_field(
                value = state.name,
                onValueChange = vm::name_change,
                placeholder = "Name",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

            placetext_field(
                value = state.email,
                onValueChange = vm::email_change,
                placeholder = "Email",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

            placetext_field(
                value = state.password,
                onValueChange = vm::password_change,
                placeholder = "Password",
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

            placetext_field(
                value = state.confirmPassword,
                onValueChange = vm::confirm_password_change,
                placeholder = "Confirm Password",
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(40.dp))

            primary_button(
                text = "Create An Account",
                onClick = {
                    // val ok = vm.create_account_clicked()
                    // if (ok) onCreateAccountClick()
                    vm.create_account_clicked(onSuccess = onCreateAccountClick)
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
                    text = "Already have an account? ",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextPrimary
                )

                Text(
                    text = "Sign In",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextPrimary,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier.clickable { onLoginClick() }
                )
            }
        }
    }
}

@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun Signup_screen_preview() {
    CookSharpTheme {
        Signup_screen(vm = SignupViewModel())
    }
}
