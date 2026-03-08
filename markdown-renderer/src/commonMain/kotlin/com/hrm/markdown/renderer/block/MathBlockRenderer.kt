package com.hrm.markdown.renderer.block

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.sp
import com.hrm.latex.renderer.Latex
import com.hrm.latex.renderer.measure.rememberLatexMeasurer
import com.hrm.latex.renderer.model.LatexConfig
import com.hrm.markdown.parser.ast.MathBlock
import com.hrm.markdown.renderer.LocalMarkdownTheme

@Composable
internal fun MathBlockRenderer(
    node: MathBlock,
    modifier: Modifier = Modifier,
) {
    val theme = LocalMarkdownTheme.current
    val latex = node.literal.trim()
    val config = LatexConfig(
        fontSize = (theme.mathFontSize * 1.2f).sp,
        color = theme.mathColor,
        darkColor = theme.mathColor,
    )

    val latexMeasurer = rememberLatexMeasurer(config)
    val density = LocalDensity.current
    val dims = try {
        latexMeasurer.measure(latex, config)
    } catch (_: Exception) {
        null
    }

    // pre-check if latex can be measured; if not, show raw text fallback
    val canRender = remember(latex) {
        try {
            latexMeasurer.measure(latex, config) != null
        } catch (_: Exception) {
            false
        }
    }

    val heightModifier = if (dims != null) {
        val heightDp = with(density) { dims.heightPx.toDp() }
        Modifier.height(heightDp)
    } else {
        Modifier
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(theme.codeBlockCornerRadius))
            .background(theme.mathBlockBackground)
            .padding(theme.codeBlockPadding)
            .horizontalScroll(rememberScrollState()),
        contentAlignment = Alignment.Center,
    ) {
        if (canRender) {
            Latex(
                latex = latex,
                modifier = heightModifier,
                config = config,
            )
        } else {
            Text(
                text = "\$\$${latex}\$\$",
                color = theme.mathColor,
            )
        }
    }
}
