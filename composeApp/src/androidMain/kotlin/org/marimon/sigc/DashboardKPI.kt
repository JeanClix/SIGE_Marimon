package org.marimon.sigc

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView

private const val POWERBI_URL = "https://app.powerbi.com/view?r=eyJrIjoiY2E5YjdkYjgtZjIxNS00NzNlLWFhZjgtYmZiY2QzZmZmMTNhIiwidCI6Ijk4MjAxZmVmLWQ5ZjYtNGU2OC04NGY1LWMyNzA1MDc0ZTM0MiIsImMiOjR9"

@Composable
fun DashboardKPIScreen(
    currentRoute: String = "grafico",
    onNavigate: (String) -> Unit = {}
) {
    val context = LocalContext.current
    var isLoading by remember { mutableStateOf(true) }

    Scaffold(
        bottomBar = {
            CustomBottomNavBar(
                currentRoute = currentRoute,
                onNavigate = onNavigate
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Header rojo con saludo y botón de retroceso
                TopKPIBarWithBack(
                    onBackClick = { onNavigate(Routes.HOME) }
                )

                // WebView con Power BI Dashboard
                PowerBIWebView(
                    url = POWERBI_URL,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f), // Ocupa el espacio restante
                    onLoadingStateChange = { isLoading = it }
                )

                // Overlay de carga
                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.3f)),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(60.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PowerBIWebView(
    url: String,
    modifier: Modifier = Modifier,
    onLoadingStateChange: (Boolean) -> Unit = {}
) {
    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            WebView(ctx).apply {
                // CRÍTICO: Establecer LayoutParams con altura MATCH_PARENT
                layoutParams = android.view.ViewGroup.LayoutParams(
                    android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                    android.view.ViewGroup.LayoutParams.MATCH_PARENT
                )
                
                webViewClient = object : WebViewClient() {
                    override fun onPageStarted(view: WebView?, url: String?, favicon: android.graphics.Bitmap?) {
                        super.onPageStarted(view, url, favicon)
                        onLoadingStateChange(true)
                    }

                    override fun onPageFinished(view: WebView?, url: String?) {
                        super.onPageFinished(view, url)
                        onLoadingStateChange(false)
                    }

                    override fun onReceivedError(
                        view: WebView?,
                        errorCode: Int,
                        description: String?,
                        failingUrl: String?
                    ) {
                        super.onReceivedError(view, errorCode, description, failingUrl)
                        println("WebView Error: $errorCode - $description - URL: $failingUrl")
                        onLoadingStateChange(false)
                    }
                    
                    override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                        // Permitir que el WebView maneje todas las URLs
                        return false
                    }
                }

                webChromeClient = android.webkit.WebChromeClient()

                settings.apply {
                    javaScriptEnabled = true
                    domStorageEnabled = true
                    databaseEnabled = true

                    // Viewport y layout
                    loadWithOverviewMode = true
                    useWideViewPort = true

                    // Configuración de zoom para mejor visualización de los KPIs
                    setSupportZoom(true)
                    builtInZoomControls = true
                    displayZoomControls = false

                    allowContentAccess = true
                    allowFileAccess = false  // Deshabilitar por seguridad
                    
                    mixedContentMode = android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW

                    // User agent desktop para mejor compatibilidad con Power BI
                    userAgentString = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"

                    cacheMode = android.webkit.WebSettings.LOAD_DEFAULT
                    javaScriptCanOpenWindowsAutomatically = true
                    
                    // Configuraciones adicionales para mejor compatibilidad
                    mediaPlaybackRequiresUserGesture = false
                }

                // Cargar URL directamente
                println("Loading Power BI Dashboard URL: $url")
                loadUrl(url)
            }
        },
        update = { webView ->
            // Actualizar si la URL cambia
            if (webView.url != url) {
                webView.loadUrl(url)
            }
        }
    )
}

@Composable
fun DashboardKPIApp(
    currentRoute: String = "grafico",
    onNavigate: (String) -> Unit = {}
) {
    DashboardKPIScreen(
        currentRoute = currentRoute,
        onNavigate = onNavigate
    )
}

// Función wrapper para compatibilidad con Navigation.kt
@Composable
fun AdminKPIApp(
    currentRoute: String = "grafico",
    onNavigate: (String) -> Unit = {}
) {
    DashboardKPIApp(
        currentRoute = currentRoute,
        onNavigate = onNavigate
    )
}

// Componente personalizado del header para el dashboard KPI con botón de retroceso
@Composable
fun TopKPIBarWithBack(onBackClick: () -> Unit) {
    Surface(
        color = Color(0xFFE53E3E),
        shape = RoundedCornerShape(
            topStart = 0.dp,
            topEnd = 0.dp,
            bottomStart = 32.dp,
            bottomEnd = 32.dp
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 32.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = CircleShape,
                    color = Color.White,
                    modifier = Modifier.size(48.dp)
                ) {
                    androidx.compose.material3.Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Volver",
                        modifier = Modifier
                            .size(44.dp)
                            .padding(4.dp)
                            .clickable { onBackClick() },
                        tint = Color.Black
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Hola!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White
                    )
                    Text(
                        text = "Dashboard KPI",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White
                    )
                }
            }
        }
    }
}

