package com.example.dlinkexam

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.dlinkexam.navigation.DLinkExamNavHost
import com.example.dlinkexam.ui.theme.DLINKExamTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DLINKExamTheme {
                DLinkExamNavHost()
            }
        }
    }
}
