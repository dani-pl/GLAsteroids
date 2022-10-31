package com.danielpl.glasteroids

import android.content.res.AssetManager
import android.opengl.GLES20
import android.util.Log
import com.danielpl.glasteroids.entity.COORDS_PER_VERTEX
import com.danielpl.glasteroids.entity.Mesh
import com.danielpl.glasteroids.entity.VERTEX_STRIDE
import com.danielpl.glasteroids.util.Config
import com.danielpl.glasteroids.util.Config.INITIAL_LINE_WIDTH
import com.danielpl.glasteroids.util.Config.INITIAL_POINT_SIZE
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.FloatBuffer


const val OFFSET = 0 //just to have a name for the parameter

class GLManager()  {
    val TAG = "GLManager"

    init{

    }

    //handles to various GL objects:
    private var glProgramHandle //handle to the compiled shader program
            = 0
    var colorUniformHandle //handle to the color setting
            = 0
    var positionAttributeHandle //handle to the vertex data (eg. coordinates of each vert, thus "positionAttribute")
            = 0

    var MVPMatrixHandle //handle to the model-view-projection matrix
            = 0

    var pointSizeHandle = 0 // handle to the point size



    fun setPointSize(pointSize: Float) {
        GLES20.glUniform1f(pointSizeHandle, pointSize)
    }

    fun setLineWidth(lineWidth: Float){
        GLES20.glLineWidth(lineWidth) //draw lines 5px wide
    }

    private fun setShaderColor(color: FloatArray) {
        val COUNT = 1
        // set color for drawing the pixels of our geometry
        GLES20.glUniform4fv(colorUniformHandle, COUNT, color, OFFSET)
        checkGLError("setShaderColor")
    }


    fun enableShader(){
        GLES20.glUseProgram(glProgramHandle)
    }

    fun disableShader(){
        GLES20.glDeleteProgram(glProgramHandle)
    }
    /*

    Now it is read from text file

    //shader source code (could be loaded from textfile!)
    private const val vertexShaderCode =
            """
        uniform mat4 modelViewProjection;  // A constant representing the combined model/view/projection matrix.
        attribute vec4 position;  // Per-vertex position information that we will pass in.
        attribute float pointSize
        void main() {             // The entry point for our vertex shader.
            gl_Position = modelViewProjection  // gl_Position is a special variable used to store the final position.
                * position;       // Multiply the vertex by the matrix to get the final point in normalized screen coordinates.
            gl_PointSize = pointSize; //pixel width of points
        }
    """

    private const val fragmentShaderCode =
        """
        precision mediump float;    //we don't need high precision floats for fragments
        uniform vec4 color;         // a constant color to apply to all pixels
        void main() {               // The entry point for our fragment shader.
            gl_FragColor = color;   // Pass the color directly through the pipeline.
        }
    """

     */

    fun checkGLError(func: String?) {
        var error: Int
        while (GLES20.glGetError().also { error = it } != GLES20.GL_NO_ERROR) {
            Log.e(func, "glError $error")
        }
    }


    private fun compileShader(type: Int, shaderCode: String): Int {
        assert(type == GLES20.GL_VERTEX_SHADER || type == GLES20.GL_FRAGMENT_SHADER)
        val handle = GLES20.glCreateShader(type) // Create a shader object and store its handle
        GLES20.glShaderSource(handle, shaderCode) // Pass in the code
        GLES20.glCompileShader(handle) // then compile the shader
        Log.d(TAG, "Shader Compile Log: ${GLES20.glGetShaderInfoLog(handle)}")
        checkGLError("compileShader")
        return handle
    }

    private fun linkShaders(vertexShader: Int, fragmentShader: Int): Int {
        val handle = GLES20.glCreateProgram()
        GLES20.glAttachShader(handle, vertexShader)
        GLES20.glAttachShader(handle, fragmentShader)
        GLES20.glLinkProgram(handle)
        Log.d(TAG, "Shader Link Log: ${GLES20.glGetProgramInfoLog(handle)}")
        checkGLError("linkShaders")
        return handle
    }

    fun buildProgram(assetManager: AssetManager) {

        // read shader code from text files
        val inputStreamVertex =assetManager.open("shaderCode/vertexShaderCode.txt")
        val bufferedReaderVertex = BufferedReader(InputStreamReader(inputStreamVertex))

        val inputStreamFragment =assetManager.open("shaderCode/fragmentShaderCode.txt")
        val bufferedReaderFragment = BufferedReader(InputStreamReader(inputStreamFragment))

        val vertexShaderCode = bufferedReaderVertex.readText()
        val fragmentShaderCode = bufferedReaderFragment.readText()

        val vertex = compileShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)
        val fragment = compileShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)
        glProgramHandle = linkShaders(vertex, fragment)
        // delete the shaders as they're linked into our program now and no longer necessary
        GLES20.glDeleteShader(vertex)
        GLES20.glDeleteShader(fragment)
        //get the handles to our shader settings
        //so that we can manipulate these later
        positionAttributeHandle = GLES20.glGetAttribLocation(glProgramHandle, "position")
        colorUniformHandle = GLES20.glGetUniformLocation(glProgramHandle, "color")
        MVPMatrixHandle = GLES20.glGetUniformLocation(glProgramHandle, "modelViewProjection")
        pointSizeHandle = GLES20.glGetUniformLocation(glProgramHandle, "pointSize")

        //activate the program
        enableShader()
        disableShader()


        setLineWidth(INITIAL_LINE_WIDTH)
        setPointSize(INITIAL_POINT_SIZE)
        checkGLError("buildProgram")
    }

    private fun setModelViewProjection(modelViewMatrix: FloatArray) {
        val COUNT = 1
        val TRANSPOSED = false
        GLES20.glUniformMatrix4fv(MVPMatrixHandle, COUNT, TRANSPOSED, modelViewMatrix, OFFSET)
        checkGLError("setModelViewProjection")
    }

    fun draw(model: Mesh, modelViewMatrix: FloatArray, color: FloatArray) {
        setShaderColor(color)
        uploadMesh(model._vertexBuffer)
        setModelViewProjection(modelViewMatrix)
        drawMesh(model._drawMode, model._vertexCount)
    }



    val verts = floatArrayOf( // in counterclockwise order:
        0.0f, 0.5f, 0.0f,    // top
        -0.5f, -0.5f, 0.0f,  // bottom left
        0.5f, -0.5f, 0.0f    // bottom right
    )



    private fun uploadMesh(vertexBuffer: FloatBuffer) {
        val NORMALIZED = false
        // get a handle to a region of the GPU memory
        GLES20.glEnableVertexAttribArray(positionAttributeHandle)
        // upload the vertex data to that region, and tell the OpenGL exactly how it's laid out
        GLES20.glVertexAttribPointer(
            positionAttributeHandle, COORDS_PER_VERTEX,
            GLES20.GL_FLOAT, NORMALIZED, VERTEX_STRIDE,
            vertexBuffer
        )
        checkGLError("uploadMesh")
    }

    private fun drawMesh(drawMode: Int, vertexCount: Int) {
        assert(drawMode == GLES20.GL_TRIANGLES || drawMode == GLES20.GL_LINES || drawMode == GLES20.GL_POINTS)
        // draw the vertices that we've uploaded to the GPU
        GLES20.glDrawArrays(drawMode, OFFSET, vertexCount)
        // disable vertex array
        GLES20.glDisableVertexAttribArray(positionAttributeHandle)
        checkGLError("drawMesh")
    }

}