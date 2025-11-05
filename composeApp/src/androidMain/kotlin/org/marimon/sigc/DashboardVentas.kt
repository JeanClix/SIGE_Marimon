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
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import android.content.Intent
import android.net.Uri

private const val STREAMLIT_URL = "https://marimon-app-s4vp2v6qlwyislga7resbp.streamlit.app/?embed_options=light_theme,dark_theme"

@Composable
fun DashboardVentasScreen(
    currentRoute: String = "ventas",
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
                TopAdminBarWithBack(
                    onBackClick = { onNavigate(Routes.HOME) }
                )

                // WebView con iframe - CRÍTICO: debe tener height definida
                StreamlitWebView(
                    url = STREAMLIT_URL,
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
fun StreamlitWebView(
    url: String,
    modifier: Modifier = Modifier,
    onLoadingStateChange: (Boolean) -> Unit = {}
) {
    // HTML con IFRAME para cargar Streamlit
    val iframeHtml = """
        <!DOCTYPE html>
        <html style="height: 100%; margin: 0; padding: 0;">
        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
            <style>
                * {
                    margin: 0;
                    padding: 0;
                    box-sizing: border-box;
                }

                html, body {
                    width: 100%;
                    height: 100%;
                    overflow: hidden;
                    background-color: white;
                }

                iframe {
                    display: block;
                    width: 100%;
                    height: 100%;
                    border: none;
                    overflow: auto;
                    -webkit-overflow-scrolling: touch;
                }
            </style>
        </head>
        <body>
            <iframe
                id="streamlit-frame"
                src="$url"
                allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope"
                allowfullscreen
                loading="eager">
            </iframe>
            <script>
                console.log('HTML wrapper loaded - viewport: ' + window.innerHeight);

                var iframe = document.getElementById('streamlit-frame');

                // Función para forzar altura del iframe
                function forceIframeHeight() {
                    var height = Math.max(
                        document.documentElement.clientHeight,
                        window.innerHeight || 0
                    );

                    if (height > 0) {
                        iframe.style.height = height + 'px';
                        document.body.style.height = height + 'px';
                        document.documentElement.style.height = height + 'px';
                        console.log('Forced iframe height to: ' + height + 'px');
                    } else {
                        // Fallback a altura fija si no hay viewport
                        iframe.style.height = '2000px';
                        console.log('Fallback: Set iframe height to 2000px');
                    }
                }

                // Intentar forzar altura inmediatamente
                forceIframeHeight();

                iframe.onload = function() {
                    console.log('Streamlit iframe content loaded');
                    forceIframeHeight();
                };

                iframe.onerror = function() {
                    console.error('Error loading Streamlit iframe');
                };

                // Forzar altura después de cargar
                window.addEventListener('load', function() {
                    forceIframeHeight();
                    console.log('Window loaded - iframe actual height: ' + iframe.clientHeight);
                });

                // Re-calcular en resize
                window.addEventListener('resize', forceIframeHeight);

                // Re-calcular después de 500ms por si acaso
                setTimeout(forceIframeHeight, 500);
            </script>
        </body>
        </html>
    """.trimIndent()

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
                        println("WebView Error: $errorCode - $description")
                        onLoadingStateChange(false)
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

                    // Sin zoom
                    setSupportZoom(false)
                    builtInZoomControls = false
                    displayZoomControls = false

                    allowContentAccess = true
                    allowFileAccess = true
                    allowFileAccessFromFileURLs = true
                    allowUniversalAccessFromFileURLs = true

                    mixedContentMode = android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW

                    // User agent desktop para mejor compatibilidad con iframe
                    userAgentString = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"

                    cacheMode = android.webkit.WebSettings.LOAD_DEFAULT
                    javaScriptCanOpenWindowsAutomatically = true
                }

                // Cargar HTML con iframe
                loadDataWithBaseURL("https://streamlit.app", iframeHtml, "text/html", "UTF-8", null)
            }
        }
    )
}

@Composable
fun DashboardVentasApp(
    currentRoute: String = "ventas",
    onNavigate: (String) -> Unit = {}
) {
    DashboardVentasScreen(
        currentRoute = currentRoute,
        onNavigate = onNavigate
    )
}

// Componente personalizado del header para el dashboard con botón de retroceso
@Composable
fun TopAdminBarWithBack(onBackClick: () -> Unit) {
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
                        text = "Administrador",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White
                    )
                }
            }
        }
    }
}
