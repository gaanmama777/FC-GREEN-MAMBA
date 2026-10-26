package com.example

import com.example.data.model.FormationLibrary
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun testFormationFormatsExist() {
        val formats = FormationLibrary.ALL_FORMATS
        assertTrue(formats.contains("5-a-side"))
        assertTrue(formats.contains("6-a-side"))
        assertTrue(formats.contains("7-a-side"))
        assertTrue(formats.contains("8-a-side"))
        assertTrue(formats.contains("9-a-side"))
    }

    @Test
    fun testFormationSlotCounts() {
        val f5 = FormationLibrary.getPresetsForFormat("5-a-side")
        assertTrue(f5.isNotEmpty())
        assertEquals(5, f5.first().slots.size)

        val f6 = FormationLibrary.getPresetsForFormat("6-a-side")
        assertEquals(6, f6.first().slots.size)

        val f7 = FormationLibrary.getPresetsForFormat("7-a-side")
        assertEquals(7, f7.first().slots.size)

        val f8 = FormationLibrary.getPresetsForFormat("8-a-side")
        assertEquals(8, f8.first().slots.size)

        val f9 = FormationLibrary.getPresetsForFormat("9-a-side")
        assertEquals(9, f9.first().slots.size)
    }

    @Test
    fun testFormationHasGoalkeeperAndValidCoords() {
        FormationLibrary.ALL_FORMATS.forEach { format ->
            val presets = FormationLibrary.getPresetsForFormat(format)
            presets.forEach { preset ->
                val gks = preset.slots.count { it.category == "GK" }
                assertEquals("Preset ${preset.name} should have 1 GK", 1, gks)
                preset.slots.forEach { slot ->
                    assertTrue("X coordinate in range", slot.normX in 0.0f..1.0f)
                    assertTrue("Y coordinate in range", slot.normY in 0.0f..1.0f)
                }
            }
        }
    }
}
