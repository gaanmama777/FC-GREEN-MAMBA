package com.example.data.model

import java.util.UUID

data class PitchPlayerSlot(
    val slotId: String = UUID.randomUUID().toString(),
    val roleLabel: String,            // "GK", "CB", "LM", "ST", etc.
    val category: String,             // "GK", "DEF", "MID", "FWD"
    val normX: Float,                 // 0.0f..1.0f (left to right)
    val normY: Float,                 // 0.0f..1.0f (top = attacking, bottom = keeper)
    val playerId: String? = null,
    val playerName: String = "Player",
    val jerseyNumber: Int = 10,
    val goalsInMatch: Int = 0,
    val assistsInMatch: Int = 0,
    val yellowCards: Int = 0,
    val redCards: Int = 0,
    val isSubbedOut: Boolean = false
)

data class FormationDefinition(
    val name: String,
    val format: String, // "5-a-side", "6-a-side", etc.
    val slots: List<PitchPlayerSlot>
)

object FormationLibrary {
    val ALL_FORMATS = listOf("5-a-side", "6-a-side", "7-a-side", "8-a-side", "9-a-side", "11-a-side")

    fun getPresetsForFormat(format: String): List<FormationDefinition> {
        return when (format) {
            "5-a-side" -> listOf(formation5Diamond(), formation5Pyramid(), formation5Attacking())
            "6-a-side" -> listOf(formation6221(), formation6131(), formation6212())
            "7-a-side" -> listOf(formation7231(), formation7321(), formation7222())
            "8-a-side" -> listOf(formation8331(), formation8241(), formation8322())
            "9-a-side" -> listOf(formation9332(), formation9341(), formation9431())
            "11-a-side" -> listOf(formation11433(), formation114231(), formation11442())
            else -> listOf(formation7231())
        }
    }

    // 5-A-SIDE
    private fun formation5Diamond(): FormationDefinition = FormationDefinition(
        name = "1-2-1 (Diamond)",
        format = "5-a-side",
        slots = listOf(
            PitchPlayerSlot(roleLabel = "GK", category = "GK", normX = 0.50f, normY = 0.88f),
            PitchPlayerSlot(roleLabel = "CB", category = "DEF", normX = 0.50f, normY = 0.68f),
            PitchPlayerSlot(roleLabel = "LM", category = "MID", normX = 0.22f, normY = 0.44f),
            PitchPlayerSlot(roleLabel = "RM", category = "MID", normX = 0.78f, normY = 0.44f),
            PitchPlayerSlot(roleLabel = "ST", category = "FWD", normX = 0.50f, normY = 0.18f)
        )
    )

    private fun formation5Pyramid(): FormationDefinition = FormationDefinition(
        name = "2-1-1 (Pyramid)",
        format = "5-a-side",
        slots = listOf(
            PitchPlayerSlot(roleLabel = "GK", category = "GK", normX = 0.50f, normY = 0.88f),
            PitchPlayerSlot(roleLabel = "LB", category = "DEF", normX = 0.28f, normY = 0.68f),
            PitchPlayerSlot(roleLabel = "RB", category = "DEF", normX = 0.72f, normY = 0.68f),
            PitchPlayerSlot(roleLabel = "CM", category = "MID", normX = 0.50f, normY = 0.44f),
            PitchPlayerSlot(roleLabel = "ST", category = "FWD", normX = 0.50f, normY = 0.18f)
        )
    )

    private fun formation5Attacking(): FormationDefinition = FormationDefinition(
        name = "1-1-2 (Attack)",
        format = "5-a-side",
        slots = listOf(
            PitchPlayerSlot(roleLabel = "GK", category = "GK", normX = 0.50f, normY = 0.88f),
            PitchPlayerSlot(roleLabel = "CB", category = "DEF", normX = 0.50f, normY = 0.68f),
            PitchPlayerSlot(roleLabel = "CM", category = "MID", normX = 0.50f, normY = 0.45f),
            PitchPlayerSlot(roleLabel = "LS", category = "FWD", normX = 0.30f, normY = 0.20f),
            PitchPlayerSlot(roleLabel = "RS", category = "FWD", normX = 0.70f, normY = 0.20f)
        )
    )

    // 6-A-SIDE
    private fun formation6221(): FormationDefinition = FormationDefinition(
        name = "2-2-1 (Balanced)",
        format = "6-a-side",
        slots = listOf(
            PitchPlayerSlot(roleLabel = "GK", category = "GK", normX = 0.50f, normY = 0.88f),
            PitchPlayerSlot(roleLabel = "LB", category = "DEF", normX = 0.28f, normY = 0.70f),
            PitchPlayerSlot(roleLabel = "RB", category = "DEF", normX = 0.72f, normY = 0.70f),
            PitchPlayerSlot(roleLabel = "LM", category = "MID", normX = 0.28f, normY = 0.44f),
            PitchPlayerSlot(roleLabel = "RM", category = "MID", normX = 0.72f, normY = 0.44f),
            PitchPlayerSlot(roleLabel = "ST", category = "FWD", normX = 0.50f, normY = 0.18f)
        )
    )

    private fun formation6131(): FormationDefinition = FormationDefinition(
        name = "1-3-1 (Mid Control)",
        format = "6-a-side",
        slots = listOf(
            PitchPlayerSlot(roleLabel = "GK", category = "GK", normX = 0.50f, normY = 0.88f),
            PitchPlayerSlot(roleLabel = "CB", category = "DEF", normX = 0.50f, normY = 0.70f),
            PitchPlayerSlot(roleLabel = "LM", category = "MID", normX = 0.20f, normY = 0.45f),
            PitchPlayerSlot(roleLabel = "CM", category = "MID", normX = 0.50f, normY = 0.45f),
            PitchPlayerSlot(roleLabel = "RM", category = "MID", normX = 0.80f, normY = 0.45f),
            PitchPlayerSlot(roleLabel = "ST", category = "FWD", normX = 0.50f, normY = 0.18f)
        )
    )

    private fun formation6212(): FormationDefinition = FormationDefinition(
        name = "2-1-2 (Twin Strike)",
        format = "6-a-side",
        slots = listOf(
            PitchPlayerSlot(roleLabel = "GK", category = "GK", normX = 0.50f, normY = 0.88f),
            PitchPlayerSlot(roleLabel = "LB", category = "DEF", normX = 0.28f, normY = 0.70f),
            PitchPlayerSlot(roleLabel = "RB", category = "DEF", normX = 0.72f, normY = 0.70f),
            PitchPlayerSlot(roleLabel = "CM", category = "MID", normX = 0.50f, normY = 0.45f),
            PitchPlayerSlot(roleLabel = "LF", category = "FWD", normX = 0.30f, normY = 0.18f),
            PitchPlayerSlot(roleLabel = "RF", category = "FWD", normX = 0.70f, normY = 0.18f)
        )
    )

    // 7-A-SIDE
    private fun formation7231(): FormationDefinition = FormationDefinition(
        name = "2-3-1 (Classic 7s)",
        format = "7-a-side",
        slots = listOf(
            PitchPlayerSlot(roleLabel = "GK", category = "GK", normX = 0.50f, normY = 0.88f),
            PitchPlayerSlot(roleLabel = "LB", category = "DEF", normX = 0.26f, normY = 0.70f),
            PitchPlayerSlot(roleLabel = "RB", category = "DEF", normX = 0.74f, normY = 0.70f),
            PitchPlayerSlot(roleLabel = "LM", category = "MID", normX = 0.18f, normY = 0.44f),
            PitchPlayerSlot(roleLabel = "CM", category = "MID", normX = 0.50f, normY = 0.46f),
            PitchPlayerSlot(roleLabel = "RM", category = "MID", normX = 0.82f, normY = 0.44f),
            PitchPlayerSlot(roleLabel = "ST", category = "FWD", normX = 0.50f, normY = 0.18f)
        )
    )

    private fun formation7321(): FormationDefinition = FormationDefinition(
        name = "3-2-1 (Solid Defense)",
        format = "7-a-side",
        slots = listOf(
            PitchPlayerSlot(roleLabel = "GK", category = "GK", normX = 0.50f, normY = 0.88f),
            PitchPlayerSlot(roleLabel = "LB", category = "DEF", normX = 0.20f, normY = 0.70f),
            PitchPlayerSlot(roleLabel = "CB", category = "DEF", normX = 0.50f, normY = 0.72f),
            PitchPlayerSlot(roleLabel = "RB", category = "DEF", normX = 0.80f, normY = 0.70f),
            PitchPlayerSlot(roleLabel = "LCM", category = "MID", normX = 0.35f, normY = 0.44f),
            PitchPlayerSlot(roleLabel = "RCM", category = "MID", normX = 0.65f, normY = 0.44f),
            PitchPlayerSlot(roleLabel = "ST", category = "FWD", normX = 0.50f, normY = 0.18f)
        )
    )

    private fun formation7222(): FormationDefinition = FormationDefinition(
        name = "2-2-2 (Box & Wings)",
        format = "7-a-side",
        slots = listOf(
            PitchPlayerSlot(roleLabel = "GK", category = "GK", normX = 0.50f, normY = 0.88f),
            PitchPlayerSlot(roleLabel = "LB", category = "DEF", normX = 0.28f, normY = 0.70f),
            PitchPlayerSlot(roleLabel = "RB", category = "DEF", normX = 0.72f, normY = 0.70f),
            PitchPlayerSlot(roleLabel = "LCM", category = "MID", normX = 0.32f, normY = 0.44f),
            PitchPlayerSlot(roleLabel = "RCM", category = "MID", normX = 0.68f, normY = 0.44f),
            PitchPlayerSlot(roleLabel = "LF", category = "FWD", normX = 0.32f, normY = 0.18f),
            PitchPlayerSlot(roleLabel = "RF", category = "FWD", normX = 0.68f, normY = 0.18f)
        )
    )

    // 8-A-SIDE
    private fun formation8331(): FormationDefinition = FormationDefinition(
        name = "3-3-1 (Control)",
        format = "8-a-side",
        slots = listOf(
            PitchPlayerSlot(roleLabel = "GK", category = "GK", normX = 0.50f, normY = 0.88f),
            PitchPlayerSlot(roleLabel = "LB", category = "DEF", normX = 0.20f, normY = 0.70f),
            PitchPlayerSlot(roleLabel = "CB", category = "DEF", normX = 0.50f, normY = 0.72f),
            PitchPlayerSlot(roleLabel = "RB", category = "DEF", normX = 0.80f, normY = 0.70f),
            PitchPlayerSlot(roleLabel = "LM", category = "MID", normX = 0.20f, normY = 0.44f),
            PitchPlayerSlot(roleLabel = "CM", category = "MID", normX = 0.50f, normY = 0.46f),
            PitchPlayerSlot(roleLabel = "RM", category = "MID", normX = 0.80f, normY = 0.44f),
            PitchPlayerSlot(roleLabel = "ST", category = "FWD", normX = 0.50f, normY = 0.18f)
        )
    )

    private fun formation8241(): FormationDefinition = FormationDefinition(
        name = "2-4-1 (Overload)",
        format = "8-a-side",
        slots = listOf(
            PitchPlayerSlot(roleLabel = "GK", category = "GK", normX = 0.50f, normY = 0.88f),
            PitchPlayerSlot(roleLabel = "LB", category = "DEF", normX = 0.30f, normY = 0.70f),
            PitchPlayerSlot(roleLabel = "RB", category = "DEF", normX = 0.70f, normY = 0.70f),
            PitchPlayerSlot(roleLabel = "LW", category = "MID", normX = 0.15f, normY = 0.42f),
            PitchPlayerSlot(roleLabel = "LCM", category = "MID", normX = 0.38f, normY = 0.46f),
            PitchPlayerSlot(roleLabel = "RCM", category = "MID", normX = 0.62f, normY = 0.46f),
            PitchPlayerSlot(roleLabel = "RW", category = "MID", normX = 0.85f, normY = 0.42f),
            PitchPlayerSlot(roleLabel = "ST", category = "FWD", normX = 0.50f, normY = 0.18f)
        )
    )

    private fun formation8322(): FormationDefinition = FormationDefinition(
        name = "3-2-2 (Dual Striker)",
        format = "8-a-side",
        slots = listOf(
            PitchPlayerSlot(roleLabel = "GK", category = "GK", normX = 0.50f, normY = 0.88f),
            PitchPlayerSlot(roleLabel = "LB", category = "DEF", normX = 0.20f, normY = 0.70f),
            PitchPlayerSlot(roleLabel = "CB", category = "DEF", normX = 0.50f, normY = 0.72f),
            PitchPlayerSlot(roleLabel = "RB", category = "DEF", normX = 0.80f, normY = 0.70f),
            PitchPlayerSlot(roleLabel = "LCM", category = "MID", normX = 0.35f, normY = 0.45f),
            PitchPlayerSlot(roleLabel = "RCM", category = "MID", normX = 0.65f, normY = 0.45f),
            PitchPlayerSlot(roleLabel = "LF", category = "FWD", normX = 0.32f, normY = 0.18f),
            PitchPlayerSlot(roleLabel = "RF", category = "FWD", normX = 0.68f, normY = 0.18f)
        )
    )

    // 9-A-SIDE
    private fun formation9332(): FormationDefinition = FormationDefinition(
        name = "3-3-2 (Standard 9s)",
        format = "9-a-side",
        slots = listOf(
            PitchPlayerSlot(roleLabel = "GK", category = "GK", normX = 0.50f, normY = 0.88f),
            PitchPlayerSlot(roleLabel = "LB", category = "DEF", normX = 0.20f, normY = 0.70f),
            PitchPlayerSlot(roleLabel = "CB", category = "DEF", normX = 0.50f, normY = 0.72f),
            PitchPlayerSlot(roleLabel = "RB", category = "DEF", normX = 0.80f, normY = 0.70f),
            PitchPlayerSlot(roleLabel = "LM", category = "MID", normX = 0.20f, normY = 0.45f),
            PitchPlayerSlot(roleLabel = "CM", category = "MID", normX = 0.50f, normY = 0.47f),
            PitchPlayerSlot(roleLabel = "RM", category = "MID", normX = 0.80f, normY = 0.45f),
            PitchPlayerSlot(roleLabel = "LF", category = "FWD", normX = 0.33f, normY = 0.18f),
            PitchPlayerSlot(roleLabel = "RF", category = "FWD", normX = 0.67f, normY = 0.18f)
        )
    )

    private fun formation9341(): FormationDefinition = FormationDefinition(
        name = "3-4-1 (Midfield Wall)",
        format = "9-a-side",
        slots = listOf(
            PitchPlayerSlot(roleLabel = "GK", category = "GK", normX = 0.50f, normY = 0.88f),
            PitchPlayerSlot(roleLabel = "LB", category = "DEF", normX = 0.20f, normY = 0.70f),
            PitchPlayerSlot(roleLabel = "CB", category = "DEF", normX = 0.50f, normY = 0.72f),
            PitchPlayerSlot(roleLabel = "RB", category = "DEF", normX = 0.80f, normY = 0.70f),
            PitchPlayerSlot(roleLabel = "LM", category = "MID", normX = 0.15f, normY = 0.44f),
            PitchPlayerSlot(roleLabel = "LCM", category = "MID", normX = 0.38f, normY = 0.47f),
            PitchPlayerSlot(roleLabel = "RCM", category = "MID", normX = 0.62f, normY = 0.47f),
            PitchPlayerSlot(roleLabel = "RM", category = "MID", normX = 0.85f, normY = 0.44f),
            PitchPlayerSlot(roleLabel = "ST", category = "FWD", normX = 0.50f, normY = 0.18f)
        )
    )

    private fun formation9431(): FormationDefinition = FormationDefinition(
        name = "4-3-1 (Defensive 9s)",
        format = "9-a-side",
        slots = listOf(
            PitchPlayerSlot(roleLabel = "GK", category = "GK", normX = 0.50f, normY = 0.88f),
            PitchPlayerSlot(roleLabel = "LB", category = "DEF", normX = 0.15f, normY = 0.70f),
            PitchPlayerSlot(roleLabel = "LCB", category = "DEF", normX = 0.38f, normY = 0.72f),
            PitchPlayerSlot(roleLabel = "RCB", category = "DEF", normX = 0.62f, normY = 0.72f),
            PitchPlayerSlot(roleLabel = "RB", category = "DEF", normX = 0.85f, normY = 0.70f),
            PitchPlayerSlot(roleLabel = "LM", category = "MID", normX = 0.22f, normY = 0.45f),
            PitchPlayerSlot(roleLabel = "CM", category = "MID", normX = 0.50f, normY = 0.47f),
            PitchPlayerSlot(roleLabel = "RM", category = "MID", normX = 0.78f, normY = 0.45f),
            PitchPlayerSlot(roleLabel = "ST", category = "FWD", normX = 0.50f, normY = 0.18f)
        )
    )

    // 11-A-SIDE
    private fun formation11433(): FormationDefinition = FormationDefinition(
        name = "4-3-3 (Attack)",
        format = "11-a-side",
        slots = listOf(
            PitchPlayerSlot(roleLabel = "GK", category = "GK", normX = 0.50f, normY = 0.90f),
            PitchPlayerSlot(roleLabel = "LB", category = "DEF", normX = 0.14f, normY = 0.72f),
            PitchPlayerSlot(roleLabel = "LCB", category = "DEF", normX = 0.38f, normY = 0.74f),
            PitchPlayerSlot(roleLabel = "RCB", category = "DEF", normX = 0.62f, normY = 0.74f),
            PitchPlayerSlot(roleLabel = "RB", category = "DEF", normX = 0.86f, normY = 0.72f),
            PitchPlayerSlot(roleLabel = "DM", category = "MID", normX = 0.50f, normY = 0.56f),
            PitchPlayerSlot(roleLabel = "LCM", category = "MID", normX = 0.30f, normY = 0.44f),
            PitchPlayerSlot(roleLabel = "RCM", category = "MID", normX = 0.70f, normY = 0.44f),
            PitchPlayerSlot(roleLabel = "LW", category = "FWD", normX = 0.18f, normY = 0.20f),
            PitchPlayerSlot(roleLabel = "ST", category = "FWD", normX = 0.50f, normY = 0.14f),
            PitchPlayerSlot(roleLabel = "RW", category = "FWD", normX = 0.82f, normY = 0.20f)
        )
    )

    private fun formation114231(): FormationDefinition = FormationDefinition(
        name = "4-2-3-1 (Modern)",
        format = "11-a-side",
        slots = listOf(
            PitchPlayerSlot(roleLabel = "GK", category = "GK", normX = 0.50f, normY = 0.90f),
            PitchPlayerSlot(roleLabel = "LB", category = "DEF", normX = 0.14f, normY = 0.72f),
            PitchPlayerSlot(roleLabel = "LCB", category = "DEF", normX = 0.38f, normY = 0.74f),
            PitchPlayerSlot(roleLabel = "RCB", category = "DEF", normX = 0.62f, normY = 0.74f),
            PitchPlayerSlot(roleLabel = "RB", category = "DEF", normX = 0.86f, normY = 0.72f),
            PitchPlayerSlot(roleLabel = "LDM", category = "MID", normX = 0.36f, normY = 0.56f),
            PitchPlayerSlot(roleLabel = "RDM", category = "MID", normX = 0.64f, normY = 0.56f),
            PitchPlayerSlot(roleLabel = "LAM", category = "MID", normX = 0.20f, normY = 0.34f),
            PitchPlayerSlot(roleLabel = "CAM", category = "MID", normX = 0.50f, normY = 0.32f),
            PitchPlayerSlot(roleLabel = "RAM", category = "MID", normX = 0.80f, normY = 0.34f),
            PitchPlayerSlot(roleLabel = "ST", category = "FWD", normX = 0.50f, normY = 0.14f)
        )
    )

    private fun formation11442(): FormationDefinition = FormationDefinition(
        name = "4-4-2 (Classic)",
        format = "11-a-side",
        slots = listOf(
            PitchPlayerSlot(roleLabel = "GK", category = "GK", normX = 0.50f, normY = 0.90f),
            PitchPlayerSlot(roleLabel = "LB", category = "DEF", normX = 0.14f, normY = 0.72f),
            PitchPlayerSlot(roleLabel = "LCB", category = "DEF", normX = 0.38f, normY = 0.74f),
            PitchPlayerSlot(roleLabel = "RCB", category = "DEF", normX = 0.62f, normY = 0.74f),
            PitchPlayerSlot(roleLabel = "RB", category = "DEF", normX = 0.86f, normY = 0.72f),
            PitchPlayerSlot(roleLabel = "LM", category = "MID", normX = 0.16f, normY = 0.46f),
            PitchPlayerSlot(roleLabel = "LCM", category = "MID", normX = 0.38f, normY = 0.48f),
            PitchPlayerSlot(roleLabel = "RCM", category = "MID", normX = 0.62f, normY = 0.48f),
            PitchPlayerSlot(roleLabel = "RM", category = "MID", normX = 0.84f, normY = 0.46f),
            PitchPlayerSlot(roleLabel = "LS", category = "FWD", normX = 0.35f, normY = 0.16f),
            PitchPlayerSlot(roleLabel = "RS", category = "FWD", normX = 0.65f, normY = 0.16f)
        )
    )
}
