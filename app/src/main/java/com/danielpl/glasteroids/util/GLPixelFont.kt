package com.danielpl.glasteroids.util

// GLPixelFont provides meshes to render basic ASCII characters in OpenGL.
// The font is fixed-width 5x7, and contains [0,9], uppercase [A,Z] and - . : = ?
// Ulf Benjaminsson, 2021-10-15

import android.opengl.GLES20
import com.danielpl.glasteroids.entity.Mesh
import com.danielpl.glasteroids.util.Config.BLANK_SPACE
import com.danielpl.glasteroids.util.Config.CHAR_COUNT
import com.danielpl.glasteroids.util.Config.CHAR_OFFSET
import com.danielpl.glasteroids.util.Config.COORDINATES_PER_VERTEX
import com.danielpl.glasteroids.util.Config.FONT_DEFINITION
import com.danielpl.glasteroids.util.Config.GLYPH_HEIGHT
import com.danielpl.glasteroids.util.Config.GLYPH_WIDTH
import java.util.*

object GLPixelFont {
    private val glyphs =
        ArrayList<Mesh>(CHAR_COUNT) //a vertex buffer for each glyph, for rendering with OpenGL.

    init {
        for (c in 45..90) {
            glyphs.add(createMeshForGlyph(c.toChar()))
        }
    }

    fun getString(s: String): ArrayList<Mesh> {
        val count = s.length
        val result = ArrayList<Mesh>(count)
        for (i in 0 until count) {
            val c = s[i]
            val m = getChar(c)
            result.add(m)
        }
        return result
    }

    private fun getChar(c: Char): Mesh {
        val upperCaseChar = Character.toUpperCase(c)
        if (upperCaseChar.code < CHAR_OFFSET || c.code >= CHAR_OFFSET + CHAR_COUNT) {
            return BLANK_SPACE
        }
        val i = upperCaseChar.code - CHAR_OFFSET
        return glyphs[i]
    }

    private fun createMeshForGlyph(c: Char): Mesh {
        assert(c.code >= CHAR_OFFSET && c.code < CHAR_OFFSET + CHAR_COUNT)
        val vertices = FloatArray(GLYPH_HEIGHT * GLYPH_WIDTH * COORDINATES_PER_VERTEX)
        val z = 0f
        val charIndex = c.code - CHAR_OFFSET
        var i = 0
        for (y in 0 until GLYPH_HEIGHT) {
            for (x in 0 until GLYPH_WIDTH) {
                val index = GLYPH_HEIGHT * GLYPH_WIDTH * charIndex + GLYPH_WIDTH * y + x
                if (FONT_DEFINITION[index] == '0') {
                    continue
                }
                vertices[i++] = x.toFloat()
                vertices[i++] = y.toFloat()
                vertices[i++] = z
            }
        }
        val clean: FloatArray = vertices.copyOfRange(0, i)
        return Mesh(clean, GLES20.GL_POINTS)
    }
}