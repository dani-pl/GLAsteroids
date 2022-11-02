package com.danielpl.glasteroids.entity

import android.opengl.Matrix
import com.danielpl.glasteroids.*
import com.danielpl.glasteroids.util.*
import com.danielpl.glasteroids.util.Config.BLANK_SPACE
import com.danielpl.glasteroids.util.Config.GLYPH_HEIGHT
import com.danielpl.glasteroids.util.Config.GLYPH_SPACING
import com.danielpl.glasteroids.util.Config.GLYPH_WIDTH
import com.danielpl.glasteroids.util.Config.OFFSET
import com.danielpl.glasteroids.util.Config.TEXT_SCALING


class Text(s: String, x: Float, y: Float) : GLEntity() {
    private var meshes = ArrayList<Mesh>()
    private var _spacing = GLYPH_SPACING //spacing between characters
    private var _glyphWidth = GLYPH_WIDTH.toFloat()
    private var _glyphHeight = GLYPH_HEIGHT.toFloat()


    init {
        setString(s)
        this.x = x
        this.y = y
        //we can't use setWidthHeight, because normalization will break
        //the layout of the pixel-font. So we resort to simply scaling the text-entity
        setTextScale()
    }

    private fun setString(s: String) {
        meshes = GLPixelFont.getString(s)
    }

    override fun render(viewportMatrix: FloatArray, glManager: GLManager) {
        for (i in meshes.indices) {
            if (meshes[i] == BLANK_SPACE) {
                continue
            }
            Matrix.setIdentityM(modelMatrix, OFFSET) //reset model matrix
            Matrix.translateM(modelMatrix, OFFSET, x + (_glyphWidth + _spacing) * i, y, depth)
            Matrix.scaleM(modelMatrix, OFFSET, scale, scale, 1f)
            Matrix.multiplyMM(
                viewportModelMatrix,
                OFFSET,
                viewportMatrix,
                OFFSET,
                modelMatrix,
                OFFSET
            )
            glManager.draw(meshes[i], viewportModelMatrix, color)
        }
    }

    private fun setTextScale() {
        scale = TEXT_SCALING
        _spacing = GLYPH_SPACING * scale
        _glyphWidth = GLYPH_WIDTH * scale
        _glyphHeight = GLYPH_HEIGHT * scale
        height = _glyphHeight
        width = (_glyphWidth + _spacing) * meshes.size
    }
}