package com.cenkeraydin.ttagmobil.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.cenkeraydin.ttagmobil.util.isValidDate
import com.cenkeraydin.ttagmobil.util.isValidHour
import com.cenkeraydin.ttagmobil.util.isValidPersonCount

@Composable
fun ReservationScreen(navHostController: NavHostController) {


        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF1E1E1E))
                .padding(16.dp)
        ) {
            Text(
                text = "Reservation",
                color = Color.White,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            )
            Divider(
                color = Color.White,
                modifier = Modifier
                    .padding(vertical = 4.dp)
                    .width(120.dp)
            )


            ReservationScreen()

            Button(
                onClick = { /* Search Reservation */ },
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFD4AF37)),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 24.dp)
            ) {
                Text("Search a Reservation", color = Color.White)
            }
        }
    }


@Composable
fun ReservationScreen() {
    val gold = Color(0xFFD4AF37)
    val backgroundColor = Color(0xFF1E1E1E)

    var date by remember { mutableStateOf("") }
    var hour by remember { mutableStateOf("") }
    var personCount by remember { mutableStateOf("") }
    var fromWhere by remember { mutableStateOf("") }
    var toWhere by remember { mutableStateOf("") }
    var dateError by remember { mutableStateOf(false) }
    var hourError by remember { mutableStateOf(false) }
    var personError by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(16.dp)
    ) {
        Text(
            text = "For Reservation",
            color = gold,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Date and Hour
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OutlinedTextField(
                value = date,
                onValueChange = {
                    date = it
                    dateError = !isValidDate(it)
                },
                label = { Text("Date", color = Color.White) },
                leadingIcon = {
                    Icon(Icons.Default.DateRange, contentDescription = null, tint = Color.Gray)
                },
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = gold,
                    unfocusedBorderColor = gold,
                    textColor = Color.White,
                    placeholderColor = Color.Gray,
                    cursorColor = Color.White
                ),
                shape = RoundedCornerShape(8.dp)
            )


            OutlinedTextField(
                value = hour,
                onValueChange = {
                    hour = it
                    hourError = !isValidHour(it)
                },
                label = { Text("Hour", color = Color.White) },
                leadingIcon = {
                    Icon(Icons.Default.Star, contentDescription = null, tint = Color.Gray)
                },
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = gold,
                    unfocusedBorderColor = gold,
                    textColor = Color.White,
                    placeholderColor = Color.Gray,
                    cursorColor = Color.White
                ),
                shape = RoundedCornerShape(8.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Person Number
        OutlinedTextField(
            value = personCount,
            onValueChange = {
                personCount = it
                personError = !isValidPersonCount(it)
            },
            label = { Text("Person Number", color = Color.White) },
            leadingIcon = {
                Icon(Icons.Default.Person, contentDescription = null, tint = Color.Gray)
            },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = gold,
                unfocusedBorderColor = gold,
                textColor = Color.White,
                placeholderColor = Color.Gray,
                cursorColor = Color.White
            ),
            shape = RoundedCornerShape(8.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // From Where & To Where
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OutlinedTextField(
                value = fromWhere,
                onValueChange = { fromWhere = it },
                label = { Text("From", color = Color.White) },
                leadingIcon = {
                    Icon(Icons.Default.Place, contentDescription = null, tint = Color.Gray)
                },
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = gold,
                    unfocusedBorderColor = gold,
                    textColor = Color.White,
                    placeholderColor = Color.Gray,
                    cursorColor = Color.White
                ),
                shape = RoundedCornerShape(8.dp)
            )

            OutlinedTextField(
                value = toWhere,
                onValueChange = {toWhere = it},
                label = { Text("To", color = Color.White) },
                leadingIcon = {
                    Icon(Icons.Default.Place, contentDescription = null, tint = Color.Gray)
                },
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = gold,
                    unfocusedBorderColor = gold,
                    textColor = Color.White,
                    placeholderColor = Color.Gray,
                    cursorColor = Color.White
                ),
                shape = RoundedCornerShape(8.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Search Button
        Button(
            onClick = { /* Search logic goes here */ },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(backgroundColor = gold),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(text = "Search a Reservation", color = Color.Black, fontWeight = FontWeight.Bold)
        }
        if (dateError) {
            Text(
                text = "Please enter a valid date (dd/MM/yyyy)",
                color = Color.Red,
                fontSize = 16.sp,
                modifier = Modifier
                    .padding(start = 4.dp, top = 4.dp)
            )
        }
        if (hourError) {
            Text(
                text = "Please enter a valid time (HH:mm)",
                color = Color.Red,
                fontSize = 16.sp,
                modifier = Modifier.padding(start = 4.dp, top = 4.dp)
            )
        }

        if (personError) {
            Text(
                text = "Please enter a valid person number (e.g. 1, 2, 3)",
                color = Color.Red,
                fontSize = 16.sp,
                modifier = Modifier.padding(start = 4.dp, top = 4.dp)
            )
        }
    }
}


@Composable
fun textFieldColors() = TextFieldDefaults.outlinedTextFieldColors(
    focusedBorderColor = Color(0xFFD4AF37),
    unfocusedBorderColor = Color(0xFFD4AF37),
    cursorColor = Color.White,
    textColor = Color.White,
    placeholderColor = Color.Gray
)