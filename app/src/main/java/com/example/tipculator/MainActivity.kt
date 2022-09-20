package com.example.tipculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Slider
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tipculator.components.InputField
import com.example.tipculator.ui.theme.DarkMagneta
import com.example.tipculator.ui.theme.ShadowedWhite
import com.example.tipculator.ui.theme.TipCulatorTheme
import com.example.tipculator.util.calculateTotalPerPerson
import com.example.tipculator.util.calculateTotalTip
import com.example.tipculator.widgets.RoundIconButton

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CreateApp {
                MainContent()
            }
        }
    }
}

@Composable
fun CreateApp(content: @Composable () -> Unit) {
    TipCulatorTheme {
        Surface(color = MaterialTheme.colors.background) {
            content()
        }
    }
}


@Composable
fun TopBanner(amountPerPerson: Double = 0.0) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .padding(20.dp),
        color = ShadowedWhite,
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(4.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val amount = "%.2f".format(amountPerPerson)
            Text(
                text = "Amount per person:",
                style = MaterialTheme.typography.h5,
                color = DarkMagneta
            )
            Text(
                text = "$$amount",
                style = MaterialTheme.typography.h4,
                fontWeight = FontWeight.ExtraBold,
                color = DarkMagneta
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun MainContent() {
    val totalPerPersonState = remember {
        mutableStateOf(0.0)
    }
    val tipState = remember {
        mutableStateOf(0.0)
    }
    val amountPeopleState = remember {
        mutableStateOf(1)
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {

        BillForm(
            modifier = Modifier
                .padding(2.dp)
                .fillMaxWidth(),
            amountOfPeopleState = amountPeopleState,
            tipAmountState = tipState,
            totalPerPersonState = totalPerPersonState
        )
    }

}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun BillForm(
    modifier: Modifier = Modifier,
    amountOfPeopleState: MutableState<Int>,
    tipAmountState: MutableState<Double>,
    totalPerPersonState: MutableState<Double>,
    onValChange: (String) -> Unit = {}
) {
    val totalBillState = remember {
        mutableStateOf("")
    }
    val validState = remember(totalBillState.value) {
        totalBillState.value.trim().isNotEmpty()
    }
    val keyboardController = LocalSoftwareKeyboardController.current

    val sliderPositionState = remember {
        mutableStateOf(0f)
    }
    val tipPercentage = (sliderPositionState.value * 100).toInt()

    TopBanner(totalPerPersonState.value)
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(corner = CornerSize(12.dp)),
        elevation = 2.dp,
        color = ShadowedWhite
    ) {
        Column() {
            InputField(
                valueState = totalBillState,
                labelId = "Enter bill:",
                enabled = true,
                isSingleLine = true,
                onAction = KeyboardActions {
                    if (!validState) return@KeyboardActions
                    onValChange(totalBillState.value.trim())

                    keyboardController?.hide()
                })
            if (validState) {
                Row(modifier = Modifier.padding(3.dp), horizontalArrangement = Arrangement.Start) {
                    Text(
                        text = "Split",
                        modifier = Modifier.align(alignment = Alignment.CenterVertically)
                    )
                    Spacer(modifier = Modifier.width(120.dp))
                    Row(
                        modifier = Modifier.padding(horizontal = 3.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        RoundIconButton(
                            imageVector = Icons.Default.Remove,
                            onClick = {
                                if (amountOfPeopleState.value > 1) {
                                    amountOfPeopleState.value--
                                    totalPerPersonState.value = calculateTotalPerPerson(
                                        totalBill = totalBillState.value,
                                        tipPercentage = tipPercentage,
                                        peopleAmount = amountOfPeopleState.value
                                    )
                                }
                            })
                        Text(
                            text = "${amountOfPeopleState.value}", modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .padding(start = 8.dp, end = 8.dp)
                        )
                        RoundIconButton(
                            imageVector = Icons.Default.Add,
                            onClick = {
                                amountOfPeopleState.value++
                                totalPerPersonState.value = calculateTotalPerPerson(
                                    totalBill = totalBillState.value,
                                    tipPercentage = tipPercentage,
                                    peopleAmount = amountOfPeopleState.value
                                )
                            })
                    }

                }
                Row(modifier = Modifier.padding(3.dp), horizontalArrangement = Arrangement.Start) {
                    Text(
                        text = "Tip",
                        modifier = Modifier.align(alignment = Alignment.CenterVertically)
                    )
                    Spacer(modifier = Modifier.width(200.dp))
                    Text(
                        text = "$ ${tipAmountState.value}",
                        modifier = Modifier.align(alignment = Alignment.CenterVertically)
                    )

                }
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "$tipPercentage %")
                    Spacer(modifier = Modifier.height(16.dp))
                    Slider(value = sliderPositionState.value, onValueChange = { newVal ->
                        sliderPositionState.value = newVal
                        tipAmountState.value =
                            calculateTotalTip(
                                totalBill = totalBillState.value,
                                tipPercentage = tipPercentage
                            )
                        totalPerPersonState.value = calculateTotalPerPerson(
                            totalBill = totalBillState.value,
                            tipPercentage = tipPercentage,
                            peopleAmount = amountOfPeopleState.value
                        )
                    }, modifier = Modifier.padding(16.dp), steps = 100)
                }
            } else {
                Box() {

                }
            }
        }
    }
}
