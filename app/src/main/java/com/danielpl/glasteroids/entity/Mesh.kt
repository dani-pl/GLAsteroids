package com.danielpl.glasteroids.entity

import android.graphics.PointF
import android.opengl.GLES20
import android.util.Log
import com.danielpl.glasteroids.util.Config.COORDINATES_PER_VERTEX
import com.danielpl.glasteroids.util.Config.SIZE_OF_FLOAT
import com.danielpl.glasteroids.util.Config.TO_RADIANS
import com.danielpl.glasteroids.util.Point3D
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import kotlin.math.*


@Suppress("SameParameterValue")
class Mesh(geometry: FloatArray, var drawMode: Int = GLES20.GL_TRIANGLES) {
    private val tag = "Mesh"
    lateinit var vertexBuffer: FloatBuffer
    var vertexCount = 0
    private var _width = 0f
    private var _height = 0f
    private var _depth = 0f
    private var _radius = 0f
    private var _min = Point3D()
    private var _max = Point3D()
    private val x = 0
    private val y = 1
    private val z = 2

    init {
        setVertices(geometry)
        setDrawModeMesh(drawMode)
        updateBounds()
    }

    private fun setDrawModeMesh(drawMode: Int) {
        assert(drawMode == GLES20.GL_TRIANGLES || drawMode == GLES20.GL_LINES || drawMode == GLES20.GL_POINTS)
        this.drawMode = drawMode
    }

    private fun setVertices(geometry: FloatArray) {
        // create a floating point buffer from a ByteBuffer
        vertexBuffer = ByteBuffer.allocateDirect(geometry.size * SIZE_OF_FLOAT)
            .order(ByteOrder.nativeOrder()) // use the device hardware's native byte order
            .asFloatBuffer()
        vertexBuffer.put(geometry) //add the coordinates to the FloatBuffer
        vertexBuffer.position(0) // set the buffer to read the first coordinate
        vertexCount = geometry.size / COORDINATES_PER_VERTEX
    }

    private fun flip(axis: Int) {
        assert(axis == x || axis == y || axis == z)
        vertexBuffer.position(0)
        for (i in 0 until vertexCount) {
            val index = i * COORDINATES_PER_VERTEX + axis
            val invertedCoordinate = vertexBuffer[index] * -1
            vertexBuffer.put(index, invertedCoordinate)
        }
        updateBounds()
    }


    fun flipY() = flip(y)

    /*
    // Other flips which are unused in this version
    fun flipX() = flip(X)
    fun flipZ() = flip(Z)

     */

    private fun updateBounds() {
        var minX = Float.MAX_VALUE
        var minY = Float.MAX_VALUE
        var minZ = Float.MAX_VALUE
        var maxX = -Float.MAX_VALUE
        var maxY = -Float.MAX_VALUE
        var maxZ = -Float.MAX_VALUE
        var i = 0
        while (i < vertexCount * COORDINATES_PER_VERTEX) {
            val x = vertexBuffer[i + x]
            val y = vertexBuffer[i + y]
            val z = vertexBuffer[i + z]
            minX = min(minX, x)
            minY = min(minY, y)
            minZ = min(minZ, z)
            maxX = max(maxX, x)
            maxY = max(maxY, y)
            maxZ = max(maxZ, z)
            i += COORDINATES_PER_VERTEX
        }
        _min.x = minX
        _max.x = maxX
        _min.y = minY
        _max.y = maxY

        _width = maxX - minX
        _height = maxY - minY
        _depth = maxZ - minZ
        _radius = max(max(_width, _height), _depth) * 0.5f
    }

    fun left() = _min.x
    fun right() = _max.x
    fun top() = _min.y
    fun bottom() = _max.y

    /*
    // Currently unused methods
    fun centerX() = _min._x + _width * 0.5f
    fun centerY() = _min._y + _height * 0.5f

     */

    //scale mesh to normalized device coordinates [-1.0, 1.0]
    private fun normalize() {
        val inverseW = if (_width == 0.0f) 0.0f else (1f / _width)
        val inverseH = if (_height == 0.0f) 0.0f else (1f / _height)
        val inverseD = if (_depth == 0.0f) 0.0f else (1f / _depth)
        var i = 0
        while (i < vertexCount * COORDINATES_PER_VERTEX) {
            val dx = (vertexBuffer[i + x] - _min.x) //"d" for "delta" or "difference"
            val dy = (vertexBuffer[i + y] - _min.y)
            val dz = (vertexBuffer[i + z] - _min.z)
            val xNorm =
                2.0f * (dx * inverseW) - 1.0f //(dx * inverseW) is equivalent to (dx / _width)
            val yNorm = 2.0f * (dy * inverseH) - 1.0f //but avoids the risk of division-by-zero.
            val zNorm = 2.0f * (dz * inverseD) - 1.0f
            vertexBuffer.put(i + x, xNorm)
            vertexBuffer.put(i + y, yNorm)
            vertexBuffer.put(i + z, zNorm)
            i += COORDINATES_PER_VERTEX
        }
        updateBounds()

        if (!(_min.y >= -1.0f && _max.y <= 1.0f)) {
            Log.e(tag, "normalized y[${_min.y} , ${_max.y}] expected y[-1.0, 1.0]")
        }
        assert(
            _min.z >= -1.0f && _max.z <= 1.0f
        ) { "normalized z[${_min.z} , ${_max.z}] expected z[-1.0, 1.0]" }
        assert(
            _min.y >= -1.0f && _max.y <= 1.0f
        ) { "normalized y[${_min.y} , ${_max.y}] expected y[-1.0, 1.0]" }
        assert(
            _min.x >= -1.0f && _max.x <= 1.0f
        ) { "normalized x[${_min.x} , ${_max.x}] expected x[-1.0, 1.0]" }
    }


    private fun scale(xFactor: Float, yFactor: Float, zFactor: Float) {
        var i = 0
        while (i < vertexCount * COORDINATES_PER_VERTEX) {
            vertexBuffer.put(i + x, (vertexBuffer[i + x] * xFactor))
            vertexBuffer.put(i + y, (vertexBuffer[i + y] * yFactor))
            vertexBuffer.put(i + z, (vertexBuffer[i + z] * zFactor))
            i += COORDINATES_PER_VERTEX
        }
        updateBounds()
    }

    private fun rotate(axis: Int, theta: Float) {
        assert(axis == x || axis == y || axis == z)
        val sinTheta = sin(theta)
        val cosTheta = cos(theta)
        var i = 0
        while (i < vertexCount * COORDINATES_PER_VERTEX) {
            val x = vertexBuffer[i + x]
            val y = vertexBuffer[i + y]
            val z = vertexBuffer[i + z]
            when (axis) {
                this.z -> {
                    vertexBuffer.put(i + this.x, (x * cosTheta - y * sinTheta))
                    vertexBuffer.put(i + this.y, (y * cosTheta + x * sinTheta))
                }
                this.y -> {
                    vertexBuffer.put(i + this.x, (x * cosTheta - z * sinTheta))
                    vertexBuffer.put(i + this.z, (z * cosTheta + x * sinTheta))
                }
                this.x -> {
                    vertexBuffer.put(i + this.y, (y * cosTheta - z * sinTheta))
                    vertexBuffer.put(i + this.z, (z * cosTheta + y * sinTheta))
                }
            }
            i += COORDINATES_PER_VERTEX
        }
        updateBounds()
    }

    /*
    // Currently unused methods
    fun rotateX(theta: Float) =  rotate(X, theta)
    fun rotateY(theta: Float) = rotate(Y, theta)

     */
    fun rotateZ(theta: Float) = rotate(z, theta)


    fun setWidthHeight(w: Float, h: Float) {
        updateBounds()
        normalize() //a normalized mesh is centered at [0,0] and ranges from [-1,1]
        scale(
            (w * 0.5f),
            (h * 0.5f),
            1.0f
        ) //meaning we now scale from the center, so *0.5 (radius)


    }

    fun getPointList(offsetX: Float, offsetY: Float, facingAngleDegrees: Float): ArrayList<PointF> {
        val sinTheta = sin(facingAngleDegrees * TO_RADIANS)
        val cosTheta = cos(facingAngleDegrees * TO_RADIANS)
        val vertexes = FloatArray(vertexCount * COORDINATES_PER_VERTEX)
        vertexBuffer.position(0)
        vertexBuffer.get(vertexes)
        vertexBuffer.position(0)
        val out = ArrayList<PointF>(vertexCount)
        var i = 0
        while (i < vertexCount * COORDINATES_PER_VERTEX) {
            val x = vertexes[i + x]
            val y = vertexes[i + y]
            val rotatedX = (x * cosTheta - y * sinTheta) + offsetX
            val rotatedY = (y * cosTheta + x * sinTheta) + offsetY
            out.add(PointF(rotatedX, rotatedY)) //warning! creating new PointFs... use a pool!
            i += COORDINATES_PER_VERTEX
        }
        return out
    }


}

fun generateLinePolygon(numPoints: Int, radius: Float): FloatArray {
    assert(numPoints > 2) { "a polygon requires at least 3 points." }
    val numVert = numPoints * 2 //we render lines, and each line requires 2 points
    val vert = FloatArray(numVert * COORDINATES_PER_VERTEX)
    val step = 2.0 * PI / numPoints
    var i = 0
    var point = 0
    while (point < numPoints) { //generate vert on circle, 2 per point
        var theta = point * step
        vert[i++] = (cos(theta) * radius).toFloat() //X
        vert[i++] = (sin(theta) * radius).toFloat() //Y
        vert[i++] = 0f //Z
        point++
        theta = point * step
        vert[i++] = (cos(theta) * radius).toFloat() //X
        vert[i++] = (sin(theta) * radius).toFloat() //Y
        vert[i++] = 0f //Z
    }
    return vert
}