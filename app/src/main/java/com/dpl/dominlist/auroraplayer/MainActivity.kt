package com.dpl.dominlist.auroraplayer

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FileOpen
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.media3.ui.PlayerView
import com.dpl.dominlist.auroraplayer.ui.theme.AuroraPlayerTheme
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val viewModel: MainViewModel = hiltViewModel()
            val mediaItems by viewModel.videoItems.collectAsState()
            val selectMediaLauncher: ManagedActivityResultLauncher<String, Uri?> =
                rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.GetContent(),
                    onResult = { uri ->
                        uri?.let(viewModel::addUri)
                    })
            var lifecycleEvent: Lifecycle.Event by remember {
                mutableStateOf(Lifecycle.Event.ON_CREATE)
            }
            val lifecycleOwner = LocalLifecycleOwner.current
            DisposableEffect(lifecycleOwner) {
                val observer = LifecycleEventObserver { _, event ->
                    lifecycleEvent = event
                }
                lifecycleOwner.lifecycle.addObserver(observer)
                onDispose {
                    lifecycleOwner.lifecycle.removeObserver(observer)
                }
            }
            PlayerMainView(
                viewModel = viewModel,
                lifecycleEvent = lifecycleEvent,
                mediaItems = mediaItems,
                selectMediaLauncher = selectMediaLauncher
            )

        }
    }
}

@Composable
fun PlayerMainView(
    viewModel: MainViewModel,
    lifecycleEvent: Lifecycle.Event,
    mediaItems: List<AudioItem>,
    selectMediaLauncher: ManagedActivityResultLauncher<String, Uri?>
) {
    AuroraPlayerTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            Column {
                VideoPlayer(
                    viewModel = viewModel,
                    lifecycleEvent = lifecycleEvent,
                    mediaItems = mediaItems,
                    selectMediaLauncher = selectMediaLauncher
                )
                PlayerButtonsView(
                    viewModel = viewModel,
                    lifecycleEvent = lifecycleEvent,
                    mediaItems = mediaItems,
                    selectMediaLauncher = selectMediaLauncher
                )
            }

        }
    }
}


@Composable
fun VideoPlayer(
    viewModel: MainViewModel,
    lifecycleEvent: Lifecycle.Event,
    mediaItems: List<AudioItem>,
    selectMediaLauncher: ManagedActivityResultLauncher<String, Uri?>
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
    ) {
        AndroidView(
            factory = { context ->
                PlayerView(context).also {
                    it.player = viewModel.player
                }
            },
            update = {
                when (lifecycleEvent) {
                    Lifecycle.Event.ON_PAUSE -> {
                        it.onPause()
                        it.player?.pause()
                    }

                    Lifecycle.Event.ON_RESUME -> {
                        it.onResume()
                    }

                    else -> Unit
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16 / 9f)
        )

        Spacer(modifier = Modifier.height(10.dp))
        IconButton(onClick = {
            selectMediaLauncher.launch("audio/mp3")
        }) {
            Icon(
                imageVector = Icons.Default.FileOpen,
                contentDescription = "Select an audio file"
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .background(Color.Magenta)
        ) {
            items(mediaItems) { item ->
                Text(
                    text = item.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            viewModel.setMediaItem(item.contentUri)
                        }
                        .padding(16.dp)
                )
            }
        }
    }
}

@Composable
fun PlayerButtonsView(
    viewModel: MainViewModel,
    lifecycleEvent: Lifecycle.Event,
    mediaItems: List<AudioItem>,
    selectMediaLauncher: ManagedActivityResultLauncher<String, Uri?>
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        PlayButton(
            mediaItems = mediaItems,
            playPause = { uri, isPlaying ->

                viewModel.setMediaItem(uri)
                viewModel.apply { if (isPlaying) play() else pause()  }
            }
        )
    }
}

@Composable
fun PlayButton(
    mediaItems: List<AudioItem>,
    playPause: (Uri, Boolean) -> Unit
) {

    var playerState: Boolean by remember {
        mutableStateOf(false)
    }
    Surface(
        modifier = Modifier
            .wrapContentHeight()
            .fillMaxWidth()
            .padding(30.dp)
    ) {
        Column(
            modifier = Modifier
                .wrapContentHeight()
                .wrapContentWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Button(
                modifier = Modifier.size(150.dp),
                onClick = {
                    playerState = playerState.not()
                    playPause.invoke(mediaItems.first().contentUri, playerState)
                },
                shape = CircleShape,
            ) {
                Image(
                    imageVector = if (playerState) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = "Play Arrow",
                    Modifier.size(80.dp)
                )
            }
        }
    }
}