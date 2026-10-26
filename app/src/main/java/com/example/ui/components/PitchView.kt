package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PitchPlayerSlot
import com.example.ui.theme.AssistCyan
import com.example.ui.theme.CardRed
import com.example.ui.theme.CardYellow
import com.example.ui.theme.GoalGold
import com.example.ui.theme.MambaAccentMint
import com.example.ui.theme.MambaGreenDark
import com.example.ui.theme.MambaGreenPrimary
import com.example.ui.theme.PitchGrassDark
import com.example.ui.theme.PitchGrassLight
import com.example.ui.theme.PitchLineWhite
import com.example.ui.theme.PureWhite
import kotlin.math.roundToInt

@Composable
fun TacticalPitchView(
    modifier: Modifier = Modifier,
    slots: List<PitchPlayerSlot>,
    isInteractive: Boolean = true,
    isDraggable: Boolean = false,
    onPlayerClick: (PitchPlayerSlot) -> Unit = {},
    onPlayerDragEnd: (PitchPlayerSlot, Float, Float) -> Unit = { _, _, _ -> }
) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(2.dp, MambaAccentMint.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
    ) {
        val width = constraints.maxWidth.toFloat()
        val height = constraints.maxHeight.toFloat()

        // Canvas for realistic athletic grass & white stadium markings
        Canvas(modifier = Modifier.fillMaxSize()) {
            val stripeCount = 9
            val stripeHeight = size.height / stripeCount

            // Alternate emerald turf stripes
            for (i in 0 until stripeCount) {
                val stripeColor = if (i % 2 == 0) PitchGrassDark else PitchGrassLight
                drawRect(
                    color = stripeColor,
                    topLeft = Offset(0f, i * stripeHeight),
                    size = Size(size.width, stripeHeight)
                )
            }

            val strokeWidth = 3.dp.toPx()
            val margin = 16.dp.toPx()
            val fieldLeft = margin
            val fieldTop = margin
            val fieldRight = size.width - margin
            val fieldBottom = size.height - margin
            val fieldWidth = fieldRight - fieldLeft
            val fieldHeight = fieldBottom - fieldTop

            // Field boundary
            drawRect(
                color = PitchLineWhite,
                topLeft = Offset(fieldLeft, fieldTop),
                size = Size(fieldWidth, fieldHeight),
                style = Stroke(width = strokeWidth)
            )

            // Center halfway line
            val centerY = fieldTop + fieldHeight / 2
            drawLine(
                color = PitchLineWhite,
                start = Offset(fieldLeft, centerY),
                end = Offset(fieldRight, centerY),
                strokeWidth = strokeWidth
            )

            // Center circle
            val centerCircleRadius = fieldWidth * 0.18f
            drawCircle(
                color = PitchLineWhite,
                center = Offset(size.width / 2, centerY),
                radius = centerCircleRadius,
                style = Stroke(width = strokeWidth)
            )
            // Center spot
            drawCircle(
                color = PitchLineWhite,
                center = Offset(size.width / 2, centerY),
                radius = 4.dp.toPx()
            )

            // Penalty box - TOP (Opponent side)
            val boxWidth = fieldWidth * 0.54f
            val boxHeight = fieldHeight * 0.16f
            val boxLeft = (size.width - boxWidth) / 2
            drawRect(
                color = PitchLineWhite,
                topLeft = Offset(boxLeft, fieldTop),
                size = Size(boxWidth, boxHeight),
                style = Stroke(width = strokeWidth)
            )

            // Penalty box - BOTTOM (Home / FC Green Mamba side)
            drawRect(
                color = PitchLineWhite,
                topLeft = Offset(boxLeft, fieldBottom - boxHeight),
                size = Size(boxWidth, boxHeight),
                style = Stroke(width = strokeWidth)
            )

            // Goal boxes (6-yard)
            val smallBoxWidth = fieldWidth * 0.28f
            val smallBoxHeight = fieldHeight * 0.07f
            val smallBoxLeft = (size.width - smallBoxWidth) / 2
            drawRect(
                color = PitchLineWhite,
                topLeft = Offset(smallBoxLeft, fieldTop),
                size = Size(smallBoxWidth, smallBoxHeight),
                style = Stroke(width = strokeWidth)
            )
            drawRect(
                color = PitchLineWhite,
                topLeft = Offset(smallBoxLeft, fieldBottom - smallBoxHeight),
                size = Size(smallBoxWidth, smallBoxHeight),
                style = Stroke(width = strokeWidth)
            )

            // Penalty spots
            drawCircle(
                color = PitchLineWhite,
                center = Offset(size.width / 2, fieldTop + fieldHeight * 0.11f),
                radius = 3.dp.toPx()
            )
            drawCircle(
                color = PitchLineWhite,
                center = Offset(size.width / 2, fieldBottom - fieldHeight * 0.11f),
                radius = 3.dp.toPx()
            )
        }

        // Render interactive player nodes
        slots.forEach { slot ->
            PlayerPitchNode(
                slot = slot,
                pitchWidth = width,
                pitchHeight = height,
                isInteractive = isInteractive,
                isDraggable = isDraggable,
                onClick = { onPlayerClick(slot) },
                onDragEnd = { nx, ny -> onPlayerDragEnd(slot, nx, ny) }
            )
        }
    }
}

@Composable
fun PlayerPitchNode(
    slot: PitchPlayerSlot,
    pitchWidth: Float,
    pitchHeight: Float,
    isInteractive: Boolean,
    isDraggable: Boolean,
    onClick: () -> Unit,
    onDragEnd: (Float, Float) -> Unit
) {
    var offsetX by remember(slot.normX, pitchWidth) { mutableStateOf(slot.normX * pitchWidth) }
    var offsetY by remember(slot.normY, pitchHeight) { mutableStateOf(slot.normY * pitchHeight) }

    val categoryColor = when (slot.category) {
        "GK" -> GoalGold
        "DEF" -> Color(0xFF42A5F5)
        "MID" -> MambaAccentMint
        "FWD" -> Color(0xFFFF5252)
        else -> PureWhite
    }

    Box(
        modifier = Modifier
            .offset {
                // Center the 52dp token
                val nodeRadiusPx = 26.dp.roundToPx()
                IntOffset(
                    (offsetX - nodeRadiusPx).roundToInt(),
                    (offsetY - nodeRadiusPx).roundToInt()
                )
            }
            .then(
                if (isDraggable) {
                    Modifier.pointerInput(slot.slotId) {
                        detectDragGestures(
                            onDrag = { change, dragAmount ->
                                change.consume()
                                offsetX = (offsetX + dragAmount.x).coerceIn(40f, pitchWidth - 40f)
                                offsetY = (offsetY + dragAmount.y).coerceIn(40f, pitchHeight - 40f)
                            },
                            onDragEnd = {
                                val normX = (offsetX / pitchWidth).coerceIn(0.05f, 0.95f)
                                val normY = (offsetY / pitchHeight).coerceIn(0.05f, 0.95f)
                                onDragEnd(normX, normY)
                            }
                        )
                    }
                } else Modifier
            )
            .then(
                if (isInteractive) {
                    Modifier
                        .testTag("pitch_player_${slot.jerseyNumber}")
                        .clickable { onClick() }
                } else Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Main Jersey Token
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(52.dp)
                    .shadow(6.dp, CircleShape)
                    .clip(CircleShape)
                    .background(MambaGreenDark)
                    .border(2.5.dp, categoryColor, CircleShape)
            ) {
                // Inner gradient / number
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = slot.jerseyNumber.toString(),
                        color = PureWhite,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.ExtraBold,
                        lineHeight = 18.sp
                    )
                    Text(
                        text = slot.roleLabel,
                        color = categoryColor,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black,
                        lineHeight = 10.sp
                    )
                }

                // In-match stats badges (floating on top of token)
                if (slot.goalsInMatch > 0) {
                    Surface(
                        color = GoalGold,
                        shape = CircleShape,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = 4.dp, y = (-2).dp)
                            .size(18.dp),
                        shadowElevation = 4.dp
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "⚽${slot.goalsInMatch}",
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                        }
                    }
                }

                if (slot.assistsInMatch > 0) {
                    Surface(
                        color = AssistCyan,
                        shape = CircleShape,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .offset(x = 4.dp, y = 2.dp)
                            .size(18.dp),
                        shadowElevation = 4.dp
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "A${slot.assistsInMatch}",
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.Black
                            )
                        }
                    }
                }

                if (slot.redCards > 0) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .offset(x = (-2).dp, y = (-2).dp)
                            .size(10.dp, 14.dp)
                            .background(CardRed, RoundedCornerShape(1.dp))
                    )
                } else if (slot.yellowCards > 0) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .offset(x = (-2).dp, y = (-2).dp)
                            .size(10.dp, 14.dp)
                            .background(CardYellow, RoundedCornerShape(1.dp))
                    )
                }
            }

            // Player Name Chip (High contrast pill for vision accessibility)
            Surface(
                color = MambaGreenDark.copy(alpha = 0.95f),
                shape = RoundedCornerShape(6.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, PureWhite.copy(alpha = 0.4f)),
                modifier = Modifier
                    .padding(top = 2.dp)
                    .shadow(3.dp, RoundedCornerShape(6.dp))
            ) {
                Text(
                    text = slot.playerName,
                    color = PureWhite,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
