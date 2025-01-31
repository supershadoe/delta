package dev.shadoe.delta.hotspot.buttons.shapes

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.graphics.shapes.Morph
import androidx.graphics.shapes.toPath

class MorphingShape(
    private val morph: Morph, private val percentage: Float,
) : Shape {
    // 4x4 xyzw matrix for transformations on the polygon.
    private val matrix = Matrix()

    override fun createOutline(
        size: Size, layoutDirection: LayoutDirection, density: Density
    ): Outline {
        // Generate the cubic path for the polygon to draw at every point
        // during the animation.
        val path = morph.toPath(progress = percentage).asComposePath()

        // centerX and centerY of polygons are (0,0) of the whole square
        // radius = 1 for the RoundedPolygons so you see a quarter of the
        // shape in view.

        // Resize it to half the original size.
        matrix.scale(size.width / 2f, size.height / 2f)

        // Center the widget by moving it by (0.5 * 2f = 1f) as the widget is
        // now half the size.
        matrix.translate(1f, 1f)

        // Transform the generated path using this matrix.
        path.transform(matrix)

        // Generate outline based on the cubic path.
        return Outline.Generic(path)
    }
}