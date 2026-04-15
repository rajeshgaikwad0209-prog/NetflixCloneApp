package com.example.netflixclone

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import kotlinx.coroutines.delay

// --- 1. DATA MODEL & CONTENT ---
data class Movie(
    val id: Int,
    val title: String,
    val posterUrl: String,
    val trailerUrl: String,
    val type: String // "TV Show" or "Movie"
)

object MovieData {
    val list = listOf(
        Movie(1, "Stranger Things", "https://images.unsplash.com/photo-1626814026160-2237a95fc5a0?q=80&w=500", "https://www.youtube.com/watch?v=b9EkMc79ZSU", "TV Show"),
        Movie(2, "The Witcher", "https://images.unsplash.com/photo-1536440136628-849c177e76a1?q=80&w=500", "https://www.youtube.com/watch?v=ndl1W4ltcmg", "TV Show"),
        Movie(3, "Inception", "https://images.unsplash.com/photo-1440404653325-ab127d49abc1?q=80&w=500", "https://www.youtube.com/watch?v=YoHD9XEInc0", "Movie"),
        Movie(4, "The Batman", "https://images.unsplash.com/photo-1478720568477-152d9b164e26?q=80&w=500", "https://www.youtube.com/watch?v=mqqft2E_V4A", "Movie"),
        Movie(5, "Money Heist", "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?q=80&w=500", "https://www.youtube.com/watch?v=_InqQJRqGW4", "TV Show"),
        Movie(6, "Interstellar", "https://images.unsplash.com/photo-1446776811953-b23d57bd21aa?q=80&w=500", "https://www.youtube.com/watch?v=zSWdZVtXT7E", "Movie")
    )
}

// --- 2. MAIN ACTIVITY & NAVIGATION ---
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            NavHost(navController = navController, startDestination = "splash") {
                composable("splash") {
                    NetflixSplashScreen {
                        navController.navigate("home") { popUpTo("splash") { inclusive = true } }
                    }
                }
                composable("home") { HomeScreen() }
            }
        }
    }
}

// --- 3. SPLASH SCREEN ---
@Composable
fun NetflixSplashScreen(onFinished: () -> Unit) {
    val scale = remember { Animatable(0.5f) }
    LaunchedEffect(Unit) {
        scale.animateTo(1.2f, tween(1100, easing = FastOutSlowInEasing))
        delay(500)
        onFinished()
    }
    Box(Modifier.fillMaxSize().background(Color.Black), Alignment.Center) {
        Text("NETFLIX", color = Color.Red, fontSize = (55 * scale.value).sp, fontWeight = FontWeight.Bold, letterSpacing = 4.sp)
    }
}

// --- 4. HOME SCREEN WITH TAB FILTERING ---
@Composable
fun HomeScreen() {
    val context = LocalContext.current
    var selectedTab by remember { mutableStateOf("Categories") }

    // Logic to filter movies based on the tab clicked
    val mainDisplayList = when (selectedTab) {
        "TV Shows" -> MovieData.list.filter { it.type == "TV Show" }
        "Movies" -> MovieData.list.filter { it.type == "Movie" }
        else -> MovieData.list // Categories shows everything
    }

    Box(Modifier.fillMaxSize().background(Color.Black)) {
        LazyColumn(Modifier.fillMaxSize()) {
            // Big Hero Poster
            item {
                val hero = if (selectedTab == "Movies") MovieData.list[2] else MovieData.list[0]
                HeroBanner(hero) { url -> openVideo(context, url) }
            }

            // Filtered Row
            item {
                MovieRow(title = selectedTab, movies = mainDisplayList) { url -> openVideo(context, url) }
            }

            // Static Row for variety
            item {
                MovieRow(title = "Trending Now", movies = MovieData.list.shuffled()) { url -> openVideo(context, url) }
            }

            item { Spacer(Modifier.height(100.dp)) }
        }

        // --- TOP NAVIGATION BAR ---
        Row(
            Modifier.fillMaxWidth().statusBarsPadding().background(Color.Black.copy(0.75f)).padding(vertical = 12.dp),
            Arrangement.SpaceEvenly,
            Alignment.CenterVertically
        ) {
            TabLabel("TV Shows", active = selectedTab == "TV Shows") { selectedTab = "TV Shows" }
            TabLabel("Movies", active = selectedTab == "Movies") { selectedTab = "Movies" }
            TabLabel("Categories", active = selectedTab == "Categories") { selectedTab = "Categories" }
        }
    }
}

// --- 5. UI BUILDING BLOCKS ---

@Composable
fun TabLabel(text: String, active: Boolean, onClick: () -> Unit) {
    Text(
        text = text,
        color = if (active) Color.White else Color.Gray,
        fontWeight = if (active) FontWeight.Bold else FontWeight.Normal,
        fontSize = 16.sp,
        modifier = Modifier.clickable { onClick() }.padding(8.dp)
    )
}

@Composable
fun HeroBanner(movie: Movie, onClick: (String) -> Unit) {
    Box(Modifier.fillMaxWidth().height(480.dp)) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current).data(movie.posterUrl).crossfade(true).build(),
            contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop
        )
        Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black), startY = 850f)))
        Button(
            onClick = { onClick(movie.trailerUrl) },
            Modifier.align(Alignment.BottomCenter).padding(bottom = 30.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
            shape = RoundedCornerShape(4.dp)
        ) {
            Icon(Icons.Default.PlayArrow, null, tint = Color.Black)
            Text("Play Trailer", color = Color.Black, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun MovieRow(title: String, movies: List<Movie>, onClick: (String) -> Unit) {
    Column(Modifier.padding(vertical = 10.dp)) {
        Text(title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp, modifier = Modifier.padding(start = 15.dp))
        LazyRow(contentPadding = PaddingValues(horizontal = 10.dp)) {
            items(movies) { movie ->
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current).data(movie.posterUrl).crossfade(true).build(),
                    contentDescription = null,
                    modifier = Modifier.width(130.dp).height(190.dp).padding(6.dp).clip(RoundedCornerShape(8.dp))
                        .background(Color.DarkGray).clickable { onClick(movie.trailerUrl) },
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}

fun openVideo(context: Context, url: String) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    context.startActivity(intent)
}