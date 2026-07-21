package com.example.catjump.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.catjump.domain.model.ControlMode
import com.example.catjump.domain.model.GameSettings
import com.example.catjump.presentation.components.GameBackground
import com.example.catjump.presentation.components.SecondaryGameButton

@Composable
fun SettingsScreen(
    settings: GameSettings,
    onSoundToggle: (Boolean) -> Unit,
    onControlModeSelected: (ControlMode) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        GameBackground(cameraYProvider = { 0f }, scoreProvider = { 0 })

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "AJUSTES",
                color = Color.White,
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            // --- Sonido ---
            SettingCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Sonido",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Switch(
                        checked = settings.soundEnabled,
                        onCheckedChange = onSoundToggle,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Color(0xFF4CAF50),
                            uncheckedThumbColor = Color.White,
                            uncheckedTrackColor = Color(0xFF78909C)
                        ),
                        modifier = Modifier.semantics {
                            contentDescription =
                                if (settings.soundEnabled) "Sonido activado" else "Sonido desactivado"
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // --- Modo de control ---
            SettingCard {
                Text(
                    text = "Control",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ControlOption(
                        label = "Toque",
                        description = "Tocar los lados de la pantalla",
                        selected = settings.controlMode == ControlMode.TAP,
                        onClick = { onControlModeSelected(ControlMode.TAP) },
                        modifier = Modifier.weight(1f)
                    )
                    ControlOption(
                        label = "Inclinar",
                        description = "Inclinar el dispositivo",
                        selected = settings.controlMode == ControlMode.TILT,
                        onClick = { onControlModeSelected(ControlMode.TILT) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            SecondaryGameButton(
                text = "Volver",
                onClick = onBackClick
            )
        }
    }
}

@Composable
private fun SettingCard(content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.Black.copy(alpha = 0.5f))
            .padding(20.dp),
        content = content
    )
}

@Composable
private fun ControlOption(
    label: String,
    description: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = if (selected) Color(0xFF4CAF50) else Color.White.copy(alpha = 0.3f)
    val bgColor = if (selected) Color(0xFF4CAF50).copy(alpha = 0.25f) else Color.Transparent

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .border(2.dp, borderColor, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 16.dp, horizontal = 8.dp)
            .semantics {
                contentDescription = if (selected) "$label, seleccionado" else label
            },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = description,
            color = Color.White.copy(alpha = 0.7f),
            fontSize = 11.sp
        )
    }
}
