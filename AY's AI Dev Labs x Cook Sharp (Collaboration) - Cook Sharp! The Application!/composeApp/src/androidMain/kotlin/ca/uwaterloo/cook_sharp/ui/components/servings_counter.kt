package ca.uwaterloo.cook_sharp.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ca.uwaterloo.cook_sharp.ui.theme.*

@Composable
fun ServingsCounter(
    count: Int,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .border(1.dp, PrimaryButtonBackground, RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            "-",
            modifier = Modifier.clickable { onDecrement() },
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimary
        )
        Text(
            count.toString(),
            style = MaterialTheme.typography.bodyLarge,
            color = TextPrimary
        )
        Text(
            "+",
            modifier = Modifier.clickable { onIncrement() },
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimary
        )
    }
}
