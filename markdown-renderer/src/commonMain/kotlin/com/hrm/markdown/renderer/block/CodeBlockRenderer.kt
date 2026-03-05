package com.hrm.markdown.renderer.block

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hrm.markdown.parser.ast.FencedCodeBlock
import com.hrm.markdown.parser.ast.IndentedCodeBlock
import com.hrm.markdown.renderer.LocalMarkdownTheme
import com.hrm.markdown.renderer.highlight.highlightCode

/**
 * fenced code block renderer (``` or ~~~).
 * uses syntax highlighting when a language is specified.
 */
@Composable
internal fun FencedCodeBlockRenderer(
    node: FencedCodeBlock,
    modifier: Modifier = Modifier,
) {
    val theme = LocalMarkdownTheme.current
    val code = node.literal.trimEnd('\n')
    val language = node.language

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(theme.codeBlockCornerRadius))
            .background(theme.codeBlockBackground),
    ) {
        // language label
        if (language.isNotBlank()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = theme.codeBlockPadding, vertical = 4.dp),
                contentAlignment = Alignment.CenterEnd,
            ) {
                Text(
                    text = language,
                    style = TextStyle(
                        fontSize = 12.sp,
                        color = Color(0xFF656D76),
                    ),
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(theme.codeBlockPadding),
        ) {
            if (theme.syntaxHighlightEnabled && language.isNotBlank()) {
                val highlighted = remember(code, language, theme.syntaxColors) {
                    highlightCode(code, language, theme.syntaxColors)
                }
                Text(
                    text = highlighted,
                    style = theme.codeBlockStyle,
                )
            } else {
                Text(
                    text = code,
                    style = theme.codeBlockStyle,
                )
            }
        }
    }
}

/**
 * indented code block renderer.
 * no language info available, so no syntax highlighting.
 */
@Composable
internal fun IndentedCodeBlockRenderer(
    node: IndentedCodeBlock,
    modifier: Modifier = Modifier,
) {
    val theme = LocalMarkdownTheme.current
    val code = node.literal.trimEnd('\n')

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(theme.codeBlockCornerRadius))
            .background(theme.codeBlockBackground)
            .horizontalScroll(rememberScrollState())
            .padding(theme.codeBlockPadding),
    ) {
        Text(
            text = code,
            style = theme.codeBlockStyle,
        )
    }
}
