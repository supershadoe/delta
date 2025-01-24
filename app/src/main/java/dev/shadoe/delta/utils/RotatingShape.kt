package dev.shadoe.delta.utils

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.graphics.shapes.RoundedPolygon
import androidx.graphics.shapes.toPath

class RotatingShape(
    polygon: RoundedPolygon,
    private val degrees: Float,
): Shape {
    // 4x4 xyzw matrix for transformations on the polygon.
    private val matrix = Matrix()

    // Path generated for the polygon.
    private val path = polygon.toPath().asComposePath()

    override fun createOutline(
        size: Size, layoutDirection: LayoutDirection, density: Density
    ): Outline {
        // centerX and centerY of polygons are (0,0) of the whole square
        // radius = 1 for the RoundedPolygons so you see a quarter of the
        // shape in view.

        // Resize it to half the original size.
        matrix.scale(size.width / 2f, size.height / 2f)

        // Center the widget by moving it by (0.5 * 2f = 1f) as the widget is
        // now half the size.
        matrix.translate(1f, 1f)

        // Rotate the polygon by some degrees.
        matrix.rotateZ(degrees)

        // Transform the generated path using this matrix.
        path.transform(matrix)

        // Generate outline based on the cubic path.
        return Outline.Generic(path)
    }
}