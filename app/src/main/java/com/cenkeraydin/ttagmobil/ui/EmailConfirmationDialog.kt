package com.cenkeraydin.ttagmobil.ui

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cenkeraydin.ttagmobil.ui.logins.RegisterViewModel

@Composable
fun EmailConfirmDialog(
    email: String,
    onDismiss: () -> Unit,
    onSuccess: () -> Unit,
) {
    val viewModel: RegisterViewModel = viewModel()
    var code by remember { mutableStateOf("") }
    var context = LocalContext.current
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("E-posta Doğrulama") },
        text = {
            Column {
                Text("E-postana gelen kodu gir:")
                Spacer(Modifier.height(8.dp))
                TextField(
                    value = code,
                    onValueChange = { code = it },
                    placeholder = { Text("Doğrulama Kodu") }
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                viewModel.confirmEmail(
                    email = email,
                    code = code,
                    onSuccess = {
                        Toast.makeText(context, "E-posta doğrulandı", Toast.LENGTH_SHORT).show()
                        onSuccess()
                    },
                    onError = {
                        Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                    }
                )
            }) {
                Text("Gönder")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("İptal")
            }
        }
    )
}
