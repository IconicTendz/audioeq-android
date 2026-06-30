package com.audioeq.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.audioeq.ui.theme.GlassmorphismTokens
import com.audioeq.ui.theme.LocalThemeColors

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    shape: RoundedCornerShape = GlassmorphismTokens.cardCorner,
    elevation: Dp = GlassmorphismTokens.elevationGlass,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    content: @Composable ColumnScope.() -> Unit
) {
    val colors = LocalThemeColors.current
    val interactionSource = remember { MutableInteractionSource() }

    val elevationState by animateFloatAsState(
        targetValue = if (onClick != null) elevation.value else GlassmorphismTokens.elevationGlass.value,
        label = "cardElevation"
    )

    Box(
        modifier = modifier
            .shadow(elevationState.dp, shape, ambientColor = Color.Black.copy(alpha = 0.2f), spotColor = Color.Black.copy(alpha = 0.1f))
            .clip(shape)
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        colors.surface.copy(alpha = 0.55f),
                        colors.surface.copy(alpha = 0.30f)
                    )
                ),
                shape = shape
            )
            .border(
                width = GlassmorphismTokens.glassBorderWidth,
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color.White.copy(alpha = GlassmorphismTokens.borderOpacity + 0.1f),
                        colors.primary.copy(alpha = 0.1f),
                        Color.White.copy(alpha = 0.05f)
                    )
                ),
                shape = shape
            )
            .then(
                if (onClick != null) Modifier.clickable(interactionSource = interactionSource, indication = null, onClick = onClick)
                else Modifier
            )
            .padding(contentPadding)
    ) {
        // Top highlight
        Box(
            modifier = Modifier
                .matchParentSize()
                .clip(shape)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.15f),
                            Color.Transparent,
                            Color.Transparent
                        ),
                        startY = 0f,
                        endY = 120f
                    )
                )
        )
        Column(modifier = Modifier.fillMaxWidth(), content = content)
    }
}

@Composable
fun GlassCardHeader(
    title: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    subtitle: String? = null,
    trailing: @Composable RowScope.() -> Unit = {}
) {
    val colors = LocalThemeColors.current
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = colors.primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(Modifier.width(12.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = colors.textPrimary
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = colors.textSecondary,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
        trailing()
    }
}

@Composable
fun GlassDivider(modifier: Modifier = Modifier) {
    val colors = LocalThemeColors.current
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(0.5.dp)
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color.Transparent,
                        colors.primary.copy(alpha = 0.2f),
                        Color.Transparent
                    )
                )
            )
    )
}

@Composable
fun GlassToggleCard(
    title: String,
    subtitle: String? = null,
    icon: ImageVector? = null,
    isEnabled: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    GlassCard(
        modifier = modifier,
        onClick = onToggle,
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isEnabled) LocalThemeColors.current.primary else LocalThemeColors.current.textSecondary.copy(alpha = 0.5f),
                    modifier = Modifier.size(22.dp)
                )
                Spacer(Modifier.width(12.dp))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = LocalThemeColors.current.textPrimary
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        fontSize = 11.sp,
                        color = LocalThemeColors.current.textSecondary,
                        modifier = Modifier.padding(top = 1.dp)
                    )
                }
            }
            Switch(
                checked = isEnabled,
                onCheckedChange = { onToggle() }
            )
        }
    }
}

@Composable
private fun Switch(
    checked: Boolean,
    onCheckedChange: () -> Unit
) {
    val colors = LocalThemeColors.current
    val trackColor by animateColorAsState(
        targetValue = if (checked) colors.primary else colors.textSecondary.copy(alpha = 0.3f),
        label = "switchTrack"
    )
    Box(
        modifier = Modifier
            .width(48.dp)
            .height(28.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(trackColor)
            .clickable(onClick = onCheckedChange)
            .padding(4.dp),
        contentAlignment = if (checked) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        Box(
            modifier = Modifier
                .size(20.dp)
                .shadow(2.dp, RoundedCornerShape(10.dp))
                .background(Color.White)
                .clip(RoundedCornerShape(10.dp))
        )
    }
}

@Composable
private fun animateColorAsState(
    targetValue: Color,
    label: String
): State<Color> {
    return androidx.compose.animation.animateColorAsState(
        targetValue = targetValue,
        label = label
    )
}
