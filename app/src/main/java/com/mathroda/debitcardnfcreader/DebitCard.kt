package com.mathroda.debitcardnfcreader

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.mathroda.debitcardnfcreader.model.DebitCard


@ExperimentalMaterial3Api
@Composable
fun CardDetails(
    card: DebitCard = DebitCard()
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        DebitCardComponent(
            cardNumber = card.cardNumber,
            holderName = card.holderName,
            expiryDate = card.expireDateYear + card.expireDateMonth,
            cardCVV = ""
        )
    }
}


@Composable
fun DebitCardComponent(
    cardNumber: String,
    holderName: String,
    expiryDate: String,
    cardCVV: String,
) {
    // Mutable state to track the flip state of the card
    var backSwitch by remember { mutableStateOf(false) }

    // Mutable state to track the detected card type (Visa, Mastercard, etc.)
    var cardType by remember { mutableStateOf(Card.None) }

    // Calculate the length of the card number and mask it for display
    val length = if (cardNumber.length > 16) 16 else cardNumber.length
    val maskedNumber =
        remember { "*****************" }.replaceRange(0..length, cardNumber.take(16))


    val cvv = if (cardCVV.length > 3) 3 else cardCVV.length
    val maskedCVV = remember { "*".repeat(3) }.replaceRange(0 until cvv, cardCVV.take(3))

    // Determine whether to switch to the back side of the card based on CVV length
    when {
        cardCVV.length == 1 && !backSwitch -> backSwitch = true
        cardCVV.length == 2 ->  backSwitch = true
        cardCVV.length == 3 -> backSwitch = false
    }

    cardType = when {
        cardNumber.isNotEmpty() -> {
            when (cardNumber.take(2)) {
                "40", "47" -> Card.Visa
                "50", "51", "52", "53", "54", "55" -> Card.Mastercard
                "37" -> Card.AmericanExpress
                else -> Card.None
            }
        }
        else -> Card.None
    }

    // Set the card's background color based on its type
    val animatedColor = animateColorAsState(
        targetValue =
        when (cardType) {
            Card.Visa -> {
                Color(0xFF1C478B)
            }

            Card.Mastercard -> {
                Color(0xFF3BB9A1)
            }

            Card.AmericanExpress -> {
                Color(0xFFA671FC)
            }

            else -> {
                MaterialTheme.colorScheme.onBackground
            }
        },
        label = ""
    )

    Box {
        Surface(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .height(200.dp)
                .graphicsLayer(
                    rotationY = animateFloatAsState(
                        if (backSwitch) 180f else 0f,
                        label = "",
                        animationSpec = tween(500),
                    ).value,
                    translationY = 0f
                )
                .clickable {
                    backSwitch = !backSwitch
                },
            shape = RoundedCornerShape(20.dp),
            color = animatedColor.value,
            tonalElevation = 18.dp
        ) {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                AnimatedVisibility(visible = !backSwitch) {
                    ConstraintLayout(
                        modifier = Modifier.fillMaxSize()
                    ) {

                        val (cardImage, cardName, cardHolderName, number, cardExpiry, expiry) = createRefs()

                        AnimatedVisibility(visible = cardType != Card.None,
                            modifier = Modifier
                                .padding(start = 12.dp, top = 10.dp)
                                .constrainAs(cardImage) {
                                    start.linkTo(parent.start)
                                    top.linkTo(parent.top)
                                }) {
                            Image(
                                painter = painterResource(id = cardType.logo),
                                contentDescription = "Card Image"
                            )
                        }

                        Text(
                            text = maskedNumber.chunked(4).joinToString(" "),
                            style = MaterialTheme.typography.headlineSmall,
                            maxLines = 1,
                            color = Color.White,
                            modifier = Modifier
                                .animateContentSize(spring())
                                .padding(bottom = 20.dp)
                                .constrainAs(number) {
                                    linkTo(
                                        start = parent.start,
                                        end = parent.end
                                    )
                                    linkTo(
                                        top = parent.top,
                                        bottom = parent.bottom
                                    )
                                }
                        )

                        Text(
                            text = "Card Holder Name",
                            color = Color.White,
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .constrainAs(cardHolderName) {
                                    start.linkTo(parent.start)
                                    bottom.linkTo(cardName.top)
                                }
                        )

                        Text(
                            text = holderName,
                            color = Color.White,
                            modifier = Modifier
                                .animateContentSize(TweenSpec(300))
                                .padding(top = 10.dp, start = 16.dp, bottom = 16.dp)
                                .constrainAs(cardName) {
                                    start.linkTo(parent.start)
                                    bottom.linkTo(parent.bottom)
                                }
                        )

                        Text(
                            text = "Expiry Date",
                            color = Color.White,
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .constrainAs(expiry) {
                                    end.linkTo(parent.end)
                                    bottom.linkTo(cardExpiry.top)
                                }
                        )

                        Text(
                            text = expiryDate.take(4).chunked(2).joinToString(" / "),
                            color = Color.White,
                            modifier = Modifier
                                .padding(top = 10.dp, end = 16.dp, bottom = 16.dp)
                                .constrainAs(cardExpiry) {
                                    end.linkTo(parent.end)
                                    bottom.linkTo(parent.bottom)
                                }
                        )
                    }
                }

                AnimatedVisibility(visible = backSwitch) {
                    ConstraintLayout(modifier = Modifier.fillMaxSize()) {
                        val (back) = createRefs()
                        Spacer(modifier = Modifier
                            .height(50.dp)
                            .background(
                                Color.Black
                            )
                            .fillMaxWidth()
                            .constrainAs(back) {
                                linkTo(
                                    top = parent.top,
                                    bottom = parent.bottom
                                )
                            }
                        )
                    }
                }
            }
        }

        AnimatedVisibility(
            visible = backSwitch,
            modifier = Modifier
                .padding(end = 50.dp, bottom = 50.dp)
                .align(Alignment.BottomEnd)
        ) {
            Box(
                modifier = Modifier
                    .defaultMinSize(minWidth = 60.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = maskedCVV,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Black,
                    modifier = Modifier
                        .animateContentSize(TweenSpec(300))
                        .padding(vertical = 4.dp, horizontal = 16.dp)

                )
            }

        }
    }
}

private val cardNumberOffsetTranslation = object : OffsetMapping {
    override fun originalToTransformed(offset: Int): Int {
        if (offset <= 3) return offset
        if (offset <= 7) return offset + 1
        if (offset <= 11) return offset + 2
        if (offset <= 16) return offset + 3
        return 19
    }

    override fun transformedToOriginal(offset: Int): Int {
        if (offset <= 4) return offset
        if (offset <= 9) return offset - 1
        if (offset <= 14) return offset - 2
        if (offset <= 19) return offset - 3
        return 16
    }
}

val CardNumberFilter = VisualTransformation { text ->
    val trimmed = if (text.text.length >= 16) text.text.substring(0..15) else text.text
    var space = ""
    for (i in trimmed.indices) {
        space += trimmed[i]
        if (i % 4 == 3 && i != 15) space += " "
    }
    TransformedText(AnnotatedString(space), cardNumberOffsetTranslation)
}

