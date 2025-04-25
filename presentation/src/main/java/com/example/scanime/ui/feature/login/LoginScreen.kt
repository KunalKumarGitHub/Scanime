package com.example.scanime.ui.feature.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.scanime.R
import com.example.scanime.navigation.HomeScreen
import com.example.scanime.ui.theme.ScanimeTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun LoginScreen(navController: NavController, viewModel: LoginViewModel = koinViewModel()) {

    val context = LocalContext.current
    val loginState = viewModel.loginState.collectAsState()
    val errorMessage = viewModel.errorMessage.collectAsState()
    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val isLoading = remember {
            mutableStateOf(false)
        }
        when (loginState.value) {
            is LoginState.Success -> {
                isLoading.value = false
                LaunchedEffect(loginState.value) {
                    navController.navigate(HomeScreen) {
                        popUpTo(HomeScreen) {
                            inclusive = false
                        }
                    }
                }
            }

            is LoginState.Loading -> {
                isLoading.value = true
            }

            is LoginState.Idle->{
                isLoading.value = false
            }
        }
        LoginContent(
            onSignInClicked = { email, password ->
                viewModel.login(email, password, context)
            },
            onSetErrorMessage = { message->
                viewModel.setErrorMessage(message = message)
            },
            error = errorMessage.value,
            isLoading = isLoading.value
        )
    }
}


@Composable
fun LoginContent(
    onSignInClicked: (String, String) -> Unit,
    onSetErrorMessage: (String) -> Unit,
    error: String,
    isLoading: Boolean
) {
    val email = remember {
        mutableStateOf("")
    }
    val password = remember {
        mutableStateOf("")
    }

    val isVisible = remember {
        mutableStateOf(false)
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            modifier = Modifier
                .align(Alignment.TopStart),
            verticalAlignment = Alignment.CenterVertically
        ){
            Icon(
                Icons.Default.Close,
                contentDescription = "CLose"
            )
            Text(
                text = stringResource(R.string.sign_in),
                textAlign = TextAlign.Center
            )
        }
        Card(
            modifier = Modifier
                .width(350.dp)
                .height(IntrinsicSize.Min),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = stringResource(R.string.zenithra),
                    textAlign = TextAlign.Center
                )

                Text(
                    text = stringResource(R.string.welcome_back),
                    fontSize = 36.sp,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = stringResource(R.string.please_enter_details),
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .border(1.dp, Color.Gray, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(R.drawable.ic_google),
                            contentDescription = "Google",
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .border(1.dp, Color.Gray, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(R.drawable.apple),
                            contentDescription = "Apple",
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    HorizontalDivider(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 8.dp),
                        thickness = 1.dp,
                        color = Color.Gray
                    )
                    Text(
                        text = "OR",
                        modifier = Modifier.padding(8.dp)
                    )
                    HorizontalDivider(
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp),
                        thickness = 1.dp,
                        color = Color.Gray
                    )
                }

                OutlinedTextField(
                    value = email.value,
                    onValueChange = {
                        email.value = it
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email
                    ),
                    label = { Text("Email") },
                    maxLines = 1,
                    leadingIcon = {
                        Icon(Icons.Rounded.Person, contentDescription = "Email")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedLeadingIconColor = MaterialTheme.colorScheme.primary
                    ),
                )

                OutlinedTextField(
                    value = password.value,
                    onValueChange = {
                        password.value = it
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password
                    ),
                    label = { Text("Password") },
                    maxLines = 1,
                    leadingIcon = {
                        Icon(
                            Icons.Rounded.Lock,
                            contentDescription = "Password"
                        )
                    },
                    trailingIcon = {
                        if(isVisible.value) {
                            Icon(
                                painter = painterResource(id = R.drawable.visible),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(24.dp)
                                    .clickable {
                                        isVisible.value = !isVisible.value
                                    }
                            )
                        }else{
                            Icon(
                                painter = painterResource(id = R.drawable.visibility_off),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(24.dp)
                                    .clickable {
                                        isVisible.value = !isVisible.value
                                    }
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedLeadingIconColor = MaterialTheme.colorScheme.primary
                    ),
                    visualTransformation = if(!isVisible.value) PasswordVisualTransformation() else VisualTransformation.None
                )

                Text(
                    text = "Forgot password?",
                    color = Color(0xFF1E88E5),
                    textAlign = TextAlign.End,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp)
                        .align(Alignment.CenterHorizontally)
                )

                if (error.isNotEmpty()) {
                    Text(
                        text = "*$error",
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp)
                            .align(Alignment.CenterHorizontally)
                    )
                }

                Button(
                    modifier = Modifier
                        .fillMaxWidth(),
                    onClick = {
                        if (email.value.isEmpty()) {
                            onSetErrorMessage("Please fill email")
                        } else if (password.value.isBlank()) {
                            onSetErrorMessage("Please fill password")
                        } else {
                            onSignInClicked(email.value, password.value)
                        }
                    },
                    enabled = email.value.isNotEmpty() && password.value.isNotEmpty()
                ) {
                    Text("Submit")
                }

                Text(
                    text = buildAnnotatedString {
                        append("Don't have an account? ")
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append("Sign Up")
                        }
                    },
                    modifier = Modifier.padding(8.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Preview
@Composable
fun LoginPreview(){
    ScanimeTheme {
        LoginContent(
            onSignInClicked = { email, password -> println("Email: $email, Password: $password") },
            onSetErrorMessage = {""},
            error = "",
            isLoading = false,
        )
    }
}