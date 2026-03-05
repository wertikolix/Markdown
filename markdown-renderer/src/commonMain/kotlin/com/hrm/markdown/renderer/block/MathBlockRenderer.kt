package com.hrm.markdown.renderer.block

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hrm.latex.renderer.Latex
import com.hrm.latex.renderer.model.LatexConfig
import com.hrm.markdown.parser.ast.MathBlock
import com.hrm.markdown.renderer.LocalMarkdownTheme

/**
 * 数学公式块渲染器 ($$...$$)
 * 使用 LaTeX 库渲染数学公式。
 */
@Composable
internal fun MathBlockRenderer(
    node: MathBlock,
    modifier: Modifier = Modifier,
) {
    val theme = LocalMarkdownTheme.current
    val latex = node.literal.trim()

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(theme.codeBlockCornerRadius))
            .background(theme.mathBlockBackground)
            .padding(theme.codeBlockPadding),
        contentAlignment = Alignment.Center,
    ) {
        Latex(
            latex = latex,
            config = LatexConfig(
                fontSize = (theme.mathFontSize * 1.2f).sp,
            ),
        )
    }
}
