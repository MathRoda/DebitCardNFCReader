package com.mathroda.debitcardnfcreader

import android.app.Activity
import android.nfc.NfcAdapter
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.mathroda.debitcardnfcreader.model.DebitCard
import com.mathroda.debitcardnfcreader.ui.theme.DebitCardNFCReaderTheme
import kotlinx.coroutines.DelicateCoroutinesApi

@ExperimentalMaterial3Api
@DelicateCoroutinesApi
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val cardState = remember { mutableStateOf(DebitCard()) }
            val context = LocalContext.current
            val activity = (context as? Activity)
            val nfcAdapter = remember { NfcAdapter.getDefaultAdapter(context) }

            LaunchedEffect(nfcAdapter) {
                if (nfcAdapter == null) {
                    Log.e(TAG, "No NFC on this device")
                }
                if (nfcAdapter?.isEnabled == false) {
                    Log.e(TAG, "Enable NFC!")
                }
            }

            val nfcReaderHandler = NFCReaderHandler { result ->
                when(result) {
                    is CardReaderResult.UnsupportedCard -> Log.e(TAG, "Error unsupported")
                    is CardReaderResult.CardMovedToFastOrNFCLocked -> Log.e(TAG, "Tap Again")
                    is CardReaderResult.CardSuccessfullyCaptured -> {
                        cardState.value = result.card
                        val info = result.card.toString()
                        Log.d(TAG, info)
                    }
                }
            }

            DebitCardNFCReaderTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                   CardDetails(
                       card = cardState.value
                   )
                }
            }

            OnLifecycleEvent { _, event ->
                when(event) {
                    Lifecycle.Event.ON_RESUME -> {
                        if (activity != null) {
                            nfcAdapter?.enableReaderMode(
                                activity,
                                nfcReaderHandler,
                                NfcAdapter.FLAG_READER_NFC_A or
                                        NfcAdapter.FLAG_READER_NFC_B or
                                        NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK,
                                null
                            )
                        }
                    }
                    Lifecycle.Event.ON_PAUSE -> {
                        if (activity != null) {
                            nfcAdapter?.disableReaderMode(activity)
                        }
                    }
                    else -> Unit
                }
            }
        }
    }

    companion object {
        const val TAG = "Mouawia"
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Composable
fun OnLifecycleEvent(onEvent: (owner: LifecycleOwner, event: Lifecycle.Event) -> Unit) {
    val eventHandler = rememberUpdatedState(onEvent)
    val lifecycleOwner = rememberUpdatedState(LocalLifecycleOwner.current)

    DisposableEffect(lifecycleOwner.value) {
        val lifecycle = lifecycleOwner.value.lifecycle
        val observer = LifecycleEventObserver { owner, event ->
            eventHandler.value(owner, event)
        }

        lifecycle.addObserver(observer)
        onDispose {
            lifecycle.removeObserver(observer)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    DebitCardNFCReaderTheme {
        Greeting("Android")
    }
}