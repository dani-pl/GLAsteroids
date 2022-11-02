package com.danielpl.glasteroids.entity

import android.graphics.PointF
import android.opengl.GLES20
import android.opengl.Matrix
import com.danielpl.glasteroids.GLManager
import com.danielpl.glasteroids.util.Config.WORLD_HEIGHT
import com.danielpl.glasteroids.util.Config.WORLD_WIDTH
import com.danielpl.glasteroids.util.Config.OFFSET
import com.danielpl.glasteroids.util.Jukebox


//re-usable singleton TriangleMesh
object Triangle {
    val vert = floatArrayOf( // in counterclockwise order:
        0.0f, 0.5f, 0.0f, // top
        -0.5f, -0.5f, 0.0f, // bottom left
        0.5f, -0.5f, 0.0f // bottom right
    )
    val mesh = Mesh(vert, GLES20.GL_TRIANGLES)
}

//re-usable matrices
val modelMatrix = FloatArray(4 * 4)
val viewportModelMatrix = FloatArray(4 * 4)
val rotationViewportModelMatrix = FloatArray(4 * 4)

open class GLEntity {
    lateinit var mesh: Mesh
    var color = floatArrayOf(1.0f, 1.0f, 1.0f, 1.0f) //RGBA, default white
    var x = 0.0f
    var y = 0.0f
    var velX = 0f
    var velY = 0f
    var depth = 0.0f //we'll use _depth for z-axis
    var scale = 1f
    var rotation = 0f
    var width = 0.0f
    var height = 0.0f


    open fun update(dt: Float, jukebox: Jukebox) {
        x += velX * dt
        y += velY * dt


        if (this !is Bullet) {


            if (left() > WORLD_WIDTH) {
                setRight()
            } else if (right() < 0f) {
                setLeft()
            }

            if (top() > WORLD_HEIGHT) {
                setBottom()
            } else if (bottom() < 0f) {
                setTop()
            }
        }

        /*

        // Action: Change color of entities depending on region

        if (_y > WORLD_HEIGHT / 2f) {
            setColors(1f, 0f, 0f, 1f);
        } else {
            setColors(1f, 1f, 1f, 1f);
        }

         */

    }

    fun left() = x + mesh.left()
    fun right() = x + mesh.right()
    private fun setLeft() {
        x = WORLD_WIDTH - mesh.left()
    }

    fun top() = y + mesh.top()
    fun bottom() = y + mesh.bottom()
    private fun setTop() {
        y = WORLD_HEIGHT - mesh.top()
    }

    private fun setBottom() {
        y = -mesh.bottom()
    }

    private fun setRight() {
        x = -mesh.right()
    }

    open fun render(viewportMatrix: FloatArray, glManager: GLManager) {
        //reset the model matrix and then translate (move) it into world space
        Matrix.setIdentityM(modelMatrix, OFFSET) //reset model matrix
        Matrix.translateM(modelMatrix, OFFSET, x, y, depth)
        //viewportMatrix * modelMatrix combines into the viewportModelMatrix
        //NOTE: projection matrix on the left side and the model matrix on the right side.
        Matrix.multiplyMM(viewportModelMatrix, OFFSET, viewportMatrix, OFFSET, modelMatrix, OFFSET)
        //apply a rotation around the Z-axis to our modelMatrix. Rotation is in degrees.
        Matrix.setRotateM(modelMatrix, OFFSET, rotation, 0f, 0f, 1.0f)
        //apply scaling to our modelMatrix, on the x and y axis only.
        Matrix.scaleM(modelMatrix, OFFSET, scale, scale, 1f)
        //finally, multiply the rotated & scaled model matrix into the model-viewport matrix
        //creating the final rotationViewportModelMatrix that we pass on to OpenGL
        Matrix.multiplyMM(
            rotationViewportModelMatrix,
            OFFSET,
            viewportModelMatrix,
            OFFSET,
            modelMatrix,
            OFFSET
        )

        glManager.draw(mesh, rotationViewportModelMatrix, color)
    }

    fun setColors(r: Float, g: Float, b: Float, a: Float) {
        color[0] = r //red
        color[1] = g //green
        color[2] = b //blue
        color[3] = a //alpha (transparency)
    }


    private var _isAlive = true
    open fun isDead(): Boolean {
        return !_isAlive
    }

    open fun onCollision(that: GLEntity) {
        _isAlive = false
    }

    open fun isColliding(that: GLEntity): Boolean {
        if (this === that) {
            throw AssertionError("isColliding: You shouldn't test Entities against themselves!")
        }
        return isAABBOverlapping(this, that)
    }

    open fun centerX(): Float {
        return x //assumes our mesh has been centered on [0,0] (normalized)
    }

    open fun centerY(): Float {
        return y //assumes our mesh has been centered on [0,0] (normalized)
    }

    open fun radius(): Float {
        //use the longest side to calculate radius
        return if (width > height) width * 0.5f else height * 0.5f
    }

    fun areBoundingSpheresOverlapping(a: GLEntity, b: GLEntity): Boolean {
        val dx = a.centerX() - b.centerX() //delta x
        val dy = a.centerY() - b.centerY()
        val distanceSq = dx * dx + dy * dy
        val minDistance = a.radius() + b.radius()
        val minDistanceSq = minDistance * minDistance
        return distanceSq < minDistanceSq
    }

    open fun getPointList(): ArrayList<PointF> {
        return mesh.getPointList(x, y, rotation)
    }

}

//a basic axis-aligned bounding box intersection test.
//https://gamedev.stackexchange.com/questions/586/what-is-the-fastest-way-to-work-out-2d-bounding-box-intersection
fun isAABBOverlapping(a: GLEntity, b: GLEntity): Boolean {
    return !(a.right() <= b.left() || b.right() <= a.left() || a.bottom() <= b.top() || b.bottom() <= a.top())
}

