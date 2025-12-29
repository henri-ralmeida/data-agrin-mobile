package com.example.dataagrin.app.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dataagrin.app.ui.theme.AppTheme

@Composable
fun GenericHeader(
    title: String,
    subtitle: String,
    emoji: String,
    emojiSize: TextUnit = 64.sp,
    titleSize: TextUnit = 28.sp,
    subtitleSize: TextUnit = 14.sp,
    backgroundColor: androidx.compose.ui.graphics.Color? = null,
    titleColor: androidx.compose.ui.graphics.Color? = null,
    subtitleColor: androidx.compose.ui.graphics.Color? = null,
    emojiAlpha: Float = 0.3f,
) {
    val colors = AppTheme.colors
    val configuration = LocalConfiguration.current
    val isSmallScreen = configuration.screenWidthDp < 360

    val finalTitleSize = if (isSmallScreen) 24.sp else titleSize
    val finalSubtitleSize = if (isSmallScreen) 12.sp else subtitleSize
    val finalEmojiSize = if (isSmallScreen) 48.sp else emojiSize

    val finalBackgroundColor = backgroundColor ?: colors.headerBackground
    val finalTitleColor = titleColor ?: colors.headerText
    val finalSubtitleColor = subtitleColor ?: colors.headerSubtext

    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .background(finalBackgroundColor)
                .padding(16.dp),
    ) {
        Column {
            Text(
                title,
                fontSize = finalTitleSize,
                fontWeight = FontWeight.Bold,
                color = finalTitleColor,
                modifier = Modifier.padding(bottom = 8.dp),
            )
            Text(
                subtitle,
                fontSize = finalSubtitleSize,
                color = finalSubtitleColor,
                modifier = Modifier.padding(bottom = 8.dp),
            )
        }

        Text(
            emoji,
            fontSize = finalEmojiSize,
            modifier =
                Modifier
                    .align(Alignment.CenterEnd)
                    .alpha(emojiAlpha),
        )
    }
}
