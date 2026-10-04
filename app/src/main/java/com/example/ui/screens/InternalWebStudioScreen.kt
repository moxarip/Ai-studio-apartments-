package com.example.ui.screens

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.viewinterop.AndroidView
import com.example.ui.ShortsForgeViewModel
import com.example.ui.components.ShortsForgeTopBar
import com.example.ui.theme.ForgeBackground
import com.example.ui.theme.ForgePrimary
import com.example.ui.theme.ForgeSecondary

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun InternalWebStudioScreen(
    viewModel: ShortsForgeViewModel
) {
    var webViewInstance by remember { mutableStateOf<WebView?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            ShortsForgeTopBar(
                title = "النسخة الداخلية (Web Studio)",
                canNavigateBack = true,
                onNavigateBack = { viewModel.navigateBack() },
                actions = {
                    IconButton(
                        onClick = { webViewInstance?.reload() },
                        modifier = Modifier.testTag("reload_web_studio")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Reload",
                            tint = ForgeSecondary
                        )
                    }
                }
            )
        },
        containerColor = ForgeBackground
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(ForgeBackground)
        ) {
            AndroidView(
                factory = { context ->
                    WebView(context).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                        settings.javaScriptEnabled = true
                        settings.domStorageEnabled = true
                        settings.allowFileAccess = true
                        settings.allowContentAccess = true
                        settings.cacheMode = WebSettings.LOAD_NO_CACHE
                        setBackgroundColor(0xFF090D16.toInt())

                        webViewClient = object : WebViewClient() {
                            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                                isLoading = true
                            }
                            override fun onPageFinished(view: WebView?, url: String?) {
                                isLoading = false
                            }
                        }
                        webChromeClient = WebChromeClient()

                        loadUrl("file:///android_asset/web/index.html")
                        webViewInstance = this
                    }
                },
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("internal_webview")
            )

            if (isLoading) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth(),
                    color = ForgePrimary
                )
            }
        }
    }
}
