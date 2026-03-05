package com.hrm.markdown.renderer.block

import androidx.compose.foundation.border
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.hrm.markdown.parser.ast.Table
import com.hrm.markdown.parser.ast.TableBody
import com.hrm.markdown.parser.ast.TableCell
import com.hrm.markdown.parser.ast.TableHead
import com.hrm.markdown.parser.ast.TableRow
import com.hrm.markdown.renderer.LocalMarkdownTheme
import com.hrm.markdown.renderer.inline.rememberInlineContent

/**
 * GFM 表格渲染器。
 * 使用 Row/Column 布局实现表格，支持列对齐和水平滚动。
 */
@Composable
internal fun TableRenderer(
    node: Table,
    modifier: Modifier = Modifier,
) {
    val theme = LocalMarkdownTheme.current

    Box(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
    ) {
        Column(
            modifier = Modifier.border(
                width = 1.dp,
                color = theme.tableBorderColor,
            ),
        ) {
            // 渲染表头
            val head = node.children.filterIsInstance<TableHead>().firstOrNull()
            head?.children?.filterIsInstance<TableRow>()?.forEach { row ->
                TableRowRenderer(
                    row = row,
                    alignments = node.columnAlignments,
                    isHeader = true,
                )
            }

            // 渲染表体
            val body = node.children.filterIsInstance<TableBody>().firstOrNull()
            body?.children?.filterIsInstance<TableRow>()?.forEach { row ->
                TableRowRenderer(
                    row = row,
                    alignments = node.columnAlignments,
                    isHeader = false,
                )
            }
        }
    }
}

@Composable
private fun TableRowRenderer(
    row: TableRow,
    alignments: List<Table.Alignment>,
    isHeader: Boolean,
) {
    val theme = LocalMarkdownTheme.current
    val cells = row.children.filterIsInstance<TableCell>()

    Row(
        modifier = Modifier.height(IntrinsicSize.Min),
    ) {
        cells.forEachIndexed { index, cell ->
            val alignment = alignments.getOrElse(index) { Table.Alignment.NONE }
            TableCellRenderer(
                cell = cell,
                alignment = alignment,
                isHeader = isHeader,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .border(0.5.dp, theme.tableBorderColor)
                    .let {
                        if (isHeader) it.background(theme.tableHeaderBackground) else it
                    }
                    .padding(theme.tableCellPadding),
            )
        }
    }
}

@Composable
private fun TableCellRenderer(
    cell: TableCell,
    alignment: Table.Alignment,
    isHeader: Boolean,
    modifier: Modifier = Modifier,
) {
    val theme = LocalMarkdownTheme.current
    val (annotated, inlineContents) = rememberInlineContent(cell)

    val textAlign = when (alignment) {
        Table.Alignment.LEFT -> TextAlign.Start
        Table.Alignment.CENTER -> TextAlign.Center
        Table.Alignment.RIGHT -> TextAlign.End
        Table.Alignment.NONE -> TextAlign.Start
    }

    val style = if (isHeader) {
        theme.bodyStyle.copy(fontWeight = FontWeight.Bold, textAlign = textAlign)
    } else {
        theme.bodyStyle.copy(textAlign = textAlign)
    }

    Box(modifier = modifier, contentAlignment = Alignment.CenterStart) {
        if (inlineContents.isEmpty()) {
            BasicText(text = annotated, style = style)
        } else {
            BasicText(text = annotated, style = style, inlineContent = inlineContents)
        }
    }
}
