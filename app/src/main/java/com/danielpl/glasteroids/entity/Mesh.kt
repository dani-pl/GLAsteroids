package com.danielpl.glasteroids.entity

import android.graphics.PointF
import android.opengl.GLES20
import android.util.Log
import com.danielpl.glasteroids.util.Point3D
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import kotlin.math.*

// find the size of the float type, in bytes
const val SIZE_OF_FLOAT = java.lang.Float.SIZE / java.lang.Byte.SIZE //32bit/8bit = 4 bytes

// number of coordinates per vertex in our meshes
const val COORDS_PER_VERTEX = 3 //X, Y, Z

// number of bytes per vertex
const val VERTEX_STRIDE = COORDS_PER_VERTEX * SIZE_OF_FLOAT


class Mesh(geometry: FloatArray, drawMode: Int = GLES20.GL_TRIANGLES) {
    private val TAG = "Mesh"
    lateinit var _vertexBuffer: FloatBuffer
    var _vertexCount = 0
    var _drawMode = drawMode
    var _width = 0f
    var _height = 0f
    var _depth = 0f
    var _radius = 0f
    var _min = Point3D()
    var _max = Point3D()

    init {
        setVertices(geometry)
        setDrawmode(drawMode)
        updateBounds()
    }

    fun setDrawmode(drawMode: Int) {
        assert(drawMode == GLES20.GL_TRIANGLES || drawMode == GLES20.GL_LINES || drawMode == GLES20.GL_POINTS)
        _drawMode = drawMode
    }

    fun setVertices(geometry: FloatArray) {
        // create a floating point buffer from a ByteBuffer
        _vertexBuffer = ByteBuffer.allocateDirect(geometry.size * SIZE_OF_FLOAT)
            .order(ByteOrder.nativeOrder()) // use the device hardware's native byte order
            .asFloatBuffer()
        _vertexBuffer.put(geometry) //add the coordinates to the FloatBuffer
        _vertexBuffer.position(0) // set the buffer to read the first coordinate
        _vertexCount = geometry.size / COORDS_PER_VERTEX

        //updateBounds()
    }

    fun vertexStride() = VERTEX_STRIDE
    fun coordinatesPerVertex() = COORDS_PER_VERTEX

    private val X = 0
    private val Y = 1
    private val Z = 2

    fun flip(axis: Int) {
        assert(axis == X || axis == Y || axis == Z)
        _vertexBuffer.position(0)
        for (i in 0 until _vertexCount) {
            val index = i * COORDS_PER_VERTEX + axis
            val invertedCoordinate = _vertexBuffer[index] * -1
            _vertexBuffer.put(index, invertedCoordinate)
        }
        updateBounds()
    }

    fun flipX() = flip(X)
    fun flipY() = flip(Y)
    fun flipZ() = flip(Z)

    open fun updateBounds() {
        var minX = Float.MAX_VALUE
        var minY = Float.MAX_VALUE
        var minZ = Float.MAX_VALUE
        var maxX = -Float.MAX_VALUE
        var maxY = -Float.MAX_VALUE
        var maxZ = -Float.MAX_VALUE
        var i = 0
        while (i < _vertexCount * COORDS_PER_VERTEX) {
            val x = _vertexBuffer[i + X]
            val y = _vertexBuffer[i + Y]
            val z = _vertexBuffer[i + Z]
            minX = min(minX, x)
            minY = min(minY, y)
            minZ = min(minZ, z)
            maxX = max(maxX, x)
            maxY = max(maxY, y)
            maxZ = max(maxZ, z)
            i += COORDS_PER_VERTEX
        }
        _min._x = minX
        _max._x = maxX
        _min._y = minY
        _max._y = maxY

        _width = maxX - minX
        _height = maxY - minY
        _depth = maxZ - minZ
        _radius = max(max(_width, _height), _depth) * 0.5f
    }

    open fun left() = _min._x
    open fun right() = _max._x
    open fun top() = _min._y
    open fun bottom() = _max._y
    open fun centerX() = _min._x + _width * 0.5f
    open fun centerY() = _min._y + _height * 0.5f

    //scale mesh to normalized device coordinates [-1.0, 1.0]
    open fun normalize() {
        val inverseW = if (_width == 0.0f) 0.0f else (1f / _width)
        val inverseH = if (_height == 0.0f) 0.0f else (1f / _height)
        val inverseD = if (_depth == 0.0f) 0.0f else (1f / _depth)
        var i = 0
        while (i < _vertexCount * COORDS_PER_VERTEX) {
            val dx = (_vertexBuffer[i + X] - _min._x) //"d" for "delta" or "difference"
            val dy = (_vertexBuffer[i + Y] - _min._y)
            val dz = (_vertexBuffer[i + Z] - _min._z)
            val xNorm =
                2.0f * (dx * inverseW) - 1.0f //(dx * inverseW) is equivalent to (dx / _width)
            val yNorm = 2.0f * (dy * inverseH) - 1.0f //but avoids the risk of division-by-zero.
            val zNorm = 2.0f * (dz * inverseD) - 1.0f
            _vertexBuffer.put(i + X, xNorm)
            _vertexBuffer.put(i + Y, yNorm)
            _vertexBuffer.put(i + Z, zNorm)
            i += COORDS_PER_VERTEX
        }
        updateBounds()

       /* Temporarily disabled due to bug with normalizing a generate(d)LinePolygon of odd vertices
            AssertionError: normalized y[-1.0465177 , 0.98433554] expected y[-1.0, 1.0]
           notes:
           - it's always on the y axis
           - even number of verts seem to work fine
           - we have tried replacing the invert (*) with vanilla division. no difference.

           We are disabling the developer error messages until the lack of proper normalization
           causes real run-time problems with the game.

        assert(_min._y >= -1.0f && _max._y <= 1.0f,
            { "normalized y[${_min._y} , ${_max._y}] expected y[-1.0, 1.0]" }
        )*/
        if(!(_min._y >= -1.0f && _max._y <= 1.0f)){
            Log.e(TAG, "normalized y[${_min._y} , ${_max._y}] expected y[-1.0, 1.0]" )
        }
        assert(_min._z >= -1.0f && _max._z <= 1.0f,
            { "normalized z[${_min._z} , ${_max._z}] expected z[-1.0, 1.0]" }
        )
        assert(_min._y >= -1.0f && _max._y <= 1.0f,
            { "normalized y[${_min._y} , ${_max._y}] expected y[-1.0, 1.0]" }
        )
        assert(_min._x >= -1.0f && _max._x <= 1.0f,
            { "normalized x[${_min._x} , ${_max._x}] expected x[-1.0, 1.0]" }
        )
    }


    fun scale(xFactor: Float, yFactor: Float, zFactor: Float) {
        var i = 0
        while (i < _vertexCount * COORDS_PER_VERTEX) {
            _vertexBuffer.put(i + X, (_vertexBuffer[i + X] * xFactor).toFloat())
            _vertexBuffer.put(i + Y, (_vertexBuffer[i + Y] * yFactor).toFloat())
            _vertexBuffer.put(i + Z, (_vertexBuffer[i + Z] * zFactor).toFloat())
            i += COORDS_PER_VERTEX
        }
        updateBounds()
    }

    fun scale(factor: Float) = scale(factor, factor, factor)
    fun scaleX(factor: Float) = scale(factor, 1.0f, 1.0f)
    fun scaleY(factor: Float) = scale(1.0f, factor, 1.0f)
    fun scaleZ(factor: Float) = scale(1.0f, 1.0f, factor)
    /*
    fun flipX() = scaleX(-1.0f)
    fun flipY() = scaleY(-1.0f)
    fun flipZ() = scaleZ(-1.0f)

     */


    private fun rotate(axis: Int, theta: Float) {
        assert(axis == X || axis == Y || axis == Z)
        val sinTheta = sin(theta)
        val cosTheta = cos(theta)
        var i = 0
        while (i < _vertexCount * COORDS_PER_VERTEX) {
            val x = _vertexBuffer[i + X]
            val y = _vertexBuffer[i + Y]
            val z = _vertexBuffer[i + Z]
            if (axis == Z) {
                _vertexBuffer.put(i + X, (x * cosTheta - y * sinTheta))
                _vertexBuffer.put(i + Y, (y * cosTheta + x * sinTheta))
            } else if (axis == Y) {
                _vertexBuffer.put(i + X, (x * cosTheta - z * sinTheta))
                _vertexBuffer.put(i + Z, (z * cosTheta + x * sinTheta))
            } else if (axis == X) {
                _vertexBuffer.put(i + Y, (y * cosTheta - z * sinTheta))
                _vertexBuffer.put(i + Z, (z * cosTheta + y * sinTheta))
            }
            i += COORDS_PER_VERTEX
        }
        updateBounds()
    }
    fun rotateX(theta: Float) =  rotate(X, theta)
    fun rotateY(theta: Float) = rotate(Y, theta)
    fun rotateZ(theta: Float) = rotate(Z, theta)


    fun setWidthHeight(w: Float, h: Float) {
        updateBounds()
        normalize() //a normalized mesh is centered at [0,0] and ranges from [-1,1]
        scale(
            (w * 0.5f),
            (h * 0.5f),
            1.0f
        ) //meaning we now scale from the center, so *0.5 (radius)


    }

    open fun getPointList(offsetX: Float, offsetY: Float, facingAngleDegrees: Float): ArrayList<PointF> {
        val sinTheta = sin(facingAngleDegrees * TO_RADIANS)
        val cosTheta = cos(facingAngleDegrees * TO_RADIANS)
        val verts = FloatArray(_vertexCount * COORDS_PER_VERTEX)
        _vertexBuffer.position(0)
        _vertexBuffer.get(verts)
        _vertexBuffer.position(0)
        val out = ArrayList<PointF>(_vertexCount)
        var i = 0
        while (i < _vertexCount * COORDS_PER_VERTEX) {
            val x = verts[i + X]
            val y = verts[i + Y]
            val rotatedX = (x * cosTheta - y * sinTheta) + offsetX
            val rotatedY = (y * cosTheta + x * sinTheta) + offsetY
            //final float z = verts[i + Z];
            out.add(PointF(rotatedX, rotatedY)) //warning! creating new PointFs... use a pool!
            i += COORDS_PER_VERTEX
        }
        return out
    }


}

fun generateLinePolygon(numPoints: Int, radius: Float): FloatArray {
    assert(numPoints > 2, { "a polygon requires at least 3 points." })
    val numVerts = numPoints * 2 //we render lines, and each line requires 2 points
    val verts = FloatArray(numVerts * COORDS_PER_VERTEX)
    val step = 2.0 * PI / numPoints
    var i = 0
    var point = 0
    while (point < numPoints) { //generate verts on circle, 2 per point
        var theta = point * step
        verts[i++] = (cos(theta) * radius).toFloat() //X
        verts[i++] = (sin(theta) * radius).toFloat() //Y
        verts[i++] = 0f //Z
        point++
        theta = point * step
        verts[i++] = (cos(theta) * radius).toFloat() //X
        verts[i++] = (sin(theta) * radius).toFloat() //Y
        verts[i++] = 0f //Z
    }
    return verts
}