package com.hrm.markdown.renderer.block

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.hrm.markdown.parser.ast.*

/**
 * 块级节点分发器。
 * 根据节点类型分发到对应的块级渲染器。
 */
@Composable
internal fun BlockRenderer(
    node: Node,
    modifier: Modifier = Modifier,
) {
    when (node) {
        is Heading -> HeadingRenderer(node, modifier)
        is SetextHeading -> SetextHeadingRenderer(node, modifier)
        is Paragraph -> ParagraphRenderer(node, modifier)
        is ThematicBreak -> ThematicBreakRenderer(modifier)
        is FencedCodeBlock -> FencedCodeBlockRenderer(node, modifier)
        is IndentedCodeBlock -> IndentedCodeBlockRenderer(node, modifier)
        is BlockQuote -> BlockQuoteRenderer(node, modifier)
        is ListBlock -> ListBlockRenderer(node, modifier)
        is HtmlBlock -> HtmlBlockRenderer(node, modifier)
        is Table -> TableRenderer(node, modifier)
        is MathBlock -> MathBlockRenderer(node, modifier)
        is Admonition -> AdmonitionRenderer(node, modifier)
        is DefinitionList -> DefinitionListRenderer(node, modifier)
        is FootnoteDefinition -> FootnoteDefinitionRenderer(node, modifier)
        is FrontMatter -> { /* FrontMatter 通常不渲染 */ }
        is LinkReferenceDefinition -> { /* 引用定义不直接渲染 */ }
        is BlankLine -> { /* 空行不渲染 */ }
        else -> {
            // 未知块级节点，尝试渲染子节点
            if (node is ContainerNode) {
                for (child in node.children) {
                    BlockRenderer(child)
                }
            }
        }
    }
}
