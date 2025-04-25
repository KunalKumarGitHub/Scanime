package com.example.scanime.ui.feature.home

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.domain.model.MangaDomain
import com.example.scanime.model.UiMangaModel
import com.example.scanime.navigation.MangaDetails
import com.example.scanime.ui.componenet.shimmerEffect
import kotlinx.coroutines.flow.distinctUntilChanged
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.HomeScreen(
    navController: NavController,
    animatedVisibilityScope: AnimatedVisibilityScope,
    viewModel: HomeViewModel = koinViewModel()
) {
    val mangaList = viewModel.mangaList
    var isLoading = viewModel.isLoading
    val gridState = rememberLazyGridState()

    val shouldLoadMore = remember {
        derivedStateOf {
            val layoutInfo = gridState.layoutInfo
            val lastVisible = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            lastVisible >= layoutInfo.totalItemsCount - 6
        }
    }

    LaunchedEffect(shouldLoadMore) {
        snapshotFlow { shouldLoadMore.value }
            .distinctUntilChanged()
            .collect { loadMore ->
                if (loadMore && !isLoading.value) {
                    viewModel.fetchMangaData(
                        genres = "Harem,Fantasy",
                        nsfw = "true",
                        type = "all"
                    )
                }
            }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        if (mangaList.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyVerticalGrid(
                state = gridState,
                columns = GridCells.Fixed(3),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                contentPadding = PaddingValues(horizontal = 4.dp)
            ) {
                items(mangaList, key = { it.id }) { manga ->
                    MangaItem(manga = manga,animatedVisibilityScope, onClick = {
                        navController.navigate(MangaDetails(UiMangaModel.fromMangaDomain(manga)))
                    })
                }

                if (isLoading.value) {
                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.MangaItem(
    manga: MangaDomain,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onClick: () -> Unit
) {
    var isImageLoading by remember { mutableStateOf(true) }
    val context = LocalContext.current

    Card(
        shape = RoundedCornerShape(4.dp),
        modifier = Modifier
            .size(width = 156.dp, height = 196.dp)
            .clickable {
                onClick()
            },
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (isImageLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .shimmerEffect()
                )
            }
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(manga.thumb)
                    .diskCacheKey(manga.id)
                    .build(),
                contentDescription = manga.title,
                modifier = Modifier
                    .fillMaxSize()
                    .sharedElement(
                        state = rememberSharedContentState(key = "image/${manga.id}"),
                        animatedVisibilityScope = animatedVisibilityScope
                    ),
                onSuccess = {
                    isImageLoading = false
                },
                onError = {
                    isImageLoading = true
                },
                contentScale = ContentScale.Crop
            )
        }
    }
}

