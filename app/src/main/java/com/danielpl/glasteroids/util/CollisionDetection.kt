package com.danielpl.glasteroids.util

import android.graphics.PointF


fun polygonVsPolygon(polyA: ArrayList<PointF>, polyB: ArrayList<PointF>): Boolean {
    val count = polyA.size
    var next: Int
    for (current in 0 until count) {
        next = current + 1
        if (next == count) {
            next = 0
        }
        val segmentStart = polyA[current] //get a line segment from polyA
        val segmentEnd = polyA[next]
        if (polygonVsSegment(
                polyB,
                segmentStart,
                segmentEnd
            )
        ) { //compare the segment to all segments in polyB
            return true
        }
    }
    return false
}

fun polygonVsSegment(
    vertices: ArrayList<PointF>,
    segmentStart: PointF,
    segmentEnd: PointF
): Boolean {
    val count = vertices.size
    var next: Int
    for (current in 0 until count) {
        next = current + 1
        if (next == count) {
            next = 0
        }
        val lineBStart = vertices[current]
        val lineBEnd = vertices[next]
        if (segmentVsSegment(segmentStart, segmentEnd, lineBStart, lineBEnd)) {
            return true
        }
    }
    return false
}

// takes 4 vertices, the begin and end for two line segments.
fun segmentVsSegment(
    lineAStart: PointF,
    lineAEnd: PointF,
    lineBStart: PointF,
    lineBEnd: PointF
): Boolean {
    val x1 = lineAStart.x
    val y1 = lineAStart.y //create some local names to make the typing easier further down
    val x2 = lineAEnd.x
    val y2 = lineAEnd.y
    val x3 = lineBStart.x
    val y3 = lineBStart.y
    val x4 = lineBEnd.x
    val y4 = lineBEnd.y
    //pre-calculate any value that's needed twice or more
    val dx1 = x2 - x1
    val dy1 = y2 - y1
    val dx2 = x4 - x3
    val dy2 = y4 - y3
    val cInv = 1f / (dy2 * dx1 - dx2 * dy1)
    // calculate the direction of the lines
    val uA = (dx2 * (y1 - y3) - dy2 * (x1 - x3)) * cInv
    val uB = (dx1 * (y1 - y3) - dy1 * (x1 - x3)) * cInv
    // if uA and uB are between 0-1, lines are colliding
    return uA in 0.0..1.0 && uB >= 0 && uB <= 1
}

// used to check if a point is inside a polygon, using the Jordan curve theorem.
// See:
// https://web.archive.org/web/20161108113341/https://www.ecse.rpi.edu/Homepages/wrf/Research/Short_Notes/pnpoly.html
fun polygonVsPoint(vertices: ArrayList<PointF>, px: Float, py: Float): Boolean {
    val count = vertices.size
    var collision = false
    var next: Int
    for (current in 0 until count) {
        next = current + 1
        if (next == count) next = 0
        val segmentStart = vertices[current]
        val segmentEnd = vertices[next]
        // compare position, flip 'collision' variable back and forth
        // Look up "Crossing Number Algorithm" for details.
        // If our variable is odd after testing the vertex against every line we have a hit. If it is even, no collision has occurred.
        if ((segmentStart.y > py && segmentEnd.y < py || segmentStart.y < py && segmentEnd.y > py) &&  //Is the point's Y coordinate within the lines' Y-range?
            px < (segmentEnd.x - segmentStart.x) * (py - segmentStart.y) / (segmentEnd.y - segmentStart.y) + segmentStart.x
        ) { //look up the "jordan curve theorem"
            collision = !collision
        }
    }
    return collision
}