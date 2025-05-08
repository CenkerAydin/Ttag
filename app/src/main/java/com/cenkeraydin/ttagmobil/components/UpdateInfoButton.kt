package com.cenkeraydin.ttagmobil.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.cenkeraydin.ttagmobil.R

@Composable
fun UpdateInfoButton(
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 0.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF7C4DFF), // biraz daha modern mor tonu
            contentColor = Color.White
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 6.dp,
            pressedElevation = 10.dp
        )
    ) {
        Icon(
            imageVector = Icons.Default.Edit,
            contentDescription = "Bilgileri Güncelle",
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = stringResource(R.string.updateInfo),
            style = MaterialTheme.typography.labelLarge
        )
    }
}
