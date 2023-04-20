package com.dpl.dominlist.auroraplayer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dpl.dominlist.auroraplayer.ui.theme.AuroraPlayerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AuroraPlayerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PlayerView()
                }
            }
        }
    }
}

@Preview
@Composable
fun PlayButton() {

    var playerState: Boolean by remember{
        mutableStateOf(false)
    }

    Surface(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth()

    ) {
        Column(modifier = Modifier.wrapContentHeight().wrapContentWidth(),

            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
            ) {

            Button(
                modifier = Modifier.size(150.dp),
                onClick = { playerState = playerState.not() },
                shape = CircleShape,
            ) {
                Image(
                    imageVector = if (playerState) Icons.Filled.PlayArrow else Icons.Default.PlayArrow,
                    contentDescription = "Play Arrow",
                    Modifier.size(80.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PlayerView() {
    AuroraPlayerTheme {
        PlayButton()
    }
}