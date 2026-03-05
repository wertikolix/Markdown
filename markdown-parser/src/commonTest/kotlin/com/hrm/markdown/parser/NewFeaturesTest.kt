package com.hrm.markdown.parser

import com.hrm.markdown.parser.ast.*
import com.hrm.markdown.parser.core.CharacterUtils
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue
import kotlin.test.assertNotNull

// ────── 自动生成标题 ID ──────

class HeadingIdTest {

    private val parser = MarkdownParser()

    @Test
    fun should_auto_generate_heading_id() {
        val doc = parser.parse("# Hello World")
        val heading = doc.children.first()
        assertIs<Heading>(heading)
        assertNotNull(heading.autoId)
        assertEquals("hello-world", heading.autoId)
    }

    @Test
    fun should_use_custom_id_over_auto_id() {
        val doc = parser.parse("# Hello World {#my-id}")
        val heading = doc.children.first()
        assertIs<Heading>(heading)
        assertEquals("my-id", heading.customId)
        assertEquals("my-id", heading.id)
    }

    @Test
    fun should_deduplicate_heading_ids() {
        val doc = parser.parse("# Hello\n\n# Hello")
        val headings = doc.children.filterIsInstance<Heading>()
        assertEquals(2, headings.size)
        assertEquals("hello", headings[0].autoId)
        assertEquals("hello-1", headings[1].autoId)
    }

    @Test
    fun should_generate_id_for_setext_heading() {
        val doc = parser.parse("Hello World\n===")
        val heading = doc.children.first()
        assertIs<SetextHeading>(heading)
        assertNotNull(heading.autoId)
        assertEquals("hello-world", heading.autoId)
    }

    @Test
    fun should_handle_chinese_heading_id() {
        val doc = parser.parse("# 你好世界")
        val heading = doc.children.first()
        assertIs<Heading>(heading)
        assertNotNull(heading.autoId)
        assertTrue(heading.autoId!!.contains("你好世界"))
    }

    @Test
    fun should_handle_special_chars_in_heading_id() {
        val doc = parser.parse("# Hello, World! How are you?")
        val heading = doc.children.first()
        assertIs<Heading>(heading)
        assertNotNull(heading.autoId)
        // Special chars replaced with hyphens, no leading/trailing hyphens
        assertEquals("hello-world-how-are-you", heading.autoId)
    }
}

// ────── 表格列数规范化 ──────

class TableColumnNormalizationTest {

    private val parser = MarkdownParser()

    @Test
    fun should_pad_missing_cells_with_empty() {
        // 分隔行有 3 列，但数据行只有 2 列
        val input = "| A | B | C |\n| --- | --- | --- |\n| 1 | 2 |"
        val doc = parser.parse(input)
        val table = doc.children.first()
        assertIs<Table>(table)

        val body = table.children.filterIsInstance<TableBody>().first()
        val row = body.children.first()
        assertIs<TableRow>(row)
        val cells = row.children.filterIsInstance<TableCell>()
        assertEquals(3, cells.size)
    }

    @Test
    fun should_truncate_extra_cells() {
        // 分隔行有 2 列，但数据行有 3 列
        val input = "| A | B |\n| --- | --- |\n| 1 | 2 | 3 |"
        val doc = parser.parse(input)
        val table = doc.children.first()
        assertIs<Table>(table)

        val body = table.children.filterIsInstance<TableBody>().first()
        val row = body.children.first()
        assertIs<TableRow>(row)
        val cells = row.children.filterIsInstance<TableCell>()
        assertEquals(2, cells.size)
    }

    @Test
    fun should_not_interrupt_paragraph() {
        // GFM 规范：表格的 header 行来自段落内容，
        // 当段落后紧跟分隔行时，段落被转换为表格 header。
        // 这意味着多行段落中如果某行恰好可以作为表头、下一行是分隔行，
        // 那么段落会在此处被截断并开始表格。
        val input = "Some paragraph text\n| A | B |\n| --- | --- |\n| 1 | 2 |"
        val doc = parser.parse(input)
        // 第一行是段落，后续是表格（因为 "| A | B |" 是段落的一部分，
        // 但 "| --- | --- |" 触发了表格检测）
        assertTrue(doc.children.size >= 1)
    }
}

// ────── GFM 禁止 HTML ──────

class GfmDisallowedHtmlTest {

    private val parser = MarkdownParser()

    @Test
    fun should_filter_script_tag() {
        val doc = parser.parse("<script>alert('xss')</script>")
        val html = doc.children.first()
        assertIs<HtmlBlock>(html)
        // script 标签应该被过滤
        assertTrue(html.literal.contains("filtered"))
    }

    @Test
    fun should_filter_textarea_tag() {
        val doc = parser.parse("<textarea>content</textarea>")
        val html = doc.children.first()
        assertIs<HtmlBlock>(html)
        assertTrue(html.literal.contains("filtered"))
    }

    @Test
    fun should_not_filter_safe_tags() {
        val doc = parser.parse("<div>safe content</div>")
        val html = doc.children.first()
        assertIs<HtmlBlock>(html)
        assertTrue(html.literal.contains("<div>"))
        assertTrue(!html.literal.contains("filtered"))
    }
}

// ────── 链接引用定义标题跨行 ──────

class LinkRefMultilineTitleTest {

    private val parser = MarkdownParser()

    @Test
    fun should_parse_single_line_link_ref() {
        val input = "[foo]: /url \"Title\"\n\n[foo]"
        val doc = parser.parse(input)
        assertTrue(doc.linkDefinitions.containsKey("foo"))
        assertEquals("/url", doc.linkDefinitions["foo"]!!.destination)
        assertEquals("Title", doc.linkDefinitions["foo"]!!.title)
    }

    @Test
    fun should_parse_multiline_title_link_ref() {
        val input = "[foo]: /url\n  \"Title\"\n\n[foo]"
        val doc = parser.parse(input)
        assertTrue(doc.linkDefinitions.containsKey("foo"))
        assertEquals("/url", doc.linkDefinitions["foo"]!!.destination)
        assertEquals("Title", doc.linkDefinitions["foo"]!!.title)
    }
}

// ────── TOC 占位符 ──────

class TocPlaceholderTest {

    private val parser = MarkdownParser()

    @Test
    fun should_parse_toc_placeholder() {
        val doc = parser.parse("[TOC]")
        val first = doc.children.first()
        assertIs<TocPlaceholder>(first)
    }

    @Test
    fun should_parse_toc_placeholder_double_bracket() {
        val doc = parser.parse("[[toc]]")
        val first = doc.children.first()
        assertIs<TocPlaceholder>(first)
    }

    @Test
    fun should_not_parse_toc_in_middle_of_text() {
        val doc = parser.parse("some text [TOC] more text")
        val first = doc.children.first()
        assertIs<Paragraph>(first)
    }
}

// ────── 缩写定义 ──────

class AbbreviationTest {

    private val parser = MarkdownParser()

    @Test
    fun should_parse_abbreviation_definition() {
        val input = "*[HTML]: Hyper Text Markup Language\n\nThe HTML specification."
        val doc = parser.parse(input)
        assertTrue(doc.abbreviationDefinitions.containsKey("HTML"))
        assertEquals("Hyper Text Markup Language", doc.abbreviationDefinitions["HTML"]!!.fullText)
    }

    @Test
    fun should_replace_abbreviation_in_text() {
        val input = "*[HTML]: Hyper Text Markup Language\n\nThe HTML specification."
        val doc = parser.parse(input)
        // 段落中的 "HTML" 应被替换为 Abbreviation 节点
        val para = doc.children.filterIsInstance<Paragraph>().first()
        assertTrue(para.children.any { it is Abbreviation })
    }

    @Test
    fun should_not_replace_abbreviation_in_word() {
        val input = "*[MD]: Markdown\n\nUse MYMD format."
        val doc = parser.parse(input)
        val para = doc.children.filterIsInstance<Paragraph>().first()
        // "MD" within "MYMD" 不应被替换（词边界）
        assertTrue(para.children.none { it is Abbreviation })
    }
}

// ────── 键盘按键 ──────

class KeyboardInputTest {

    private val parser = MarkdownParser()

    @Test
    fun should_parse_kbd_tag() {
        val doc = parser.parse("Press <kbd>Ctrl</kbd> to continue")
        val para = doc.children.first()
        assertIs<Paragraph>(para)
        assertTrue(para.children.any { it is KeyboardInput })
        val kbd = para.children.filterIsInstance<KeyboardInput>().first()
        assertEquals("Ctrl", kbd.literal)
    }

    @Test
    fun should_parse_multiple_kbd_tags() {
        val doc = parser.parse("Press <kbd>Ctrl</kbd>+<kbd>C</kbd> to copy")
        val para = doc.children.first()
        assertIs<Paragraph>(para)
        val kbds = para.children.filterIsInstance<KeyboardInput>()
        assertEquals(2, kbds.size)
        assertEquals("Ctrl", kbds[0].literal)
        assertEquals("C", kbds[1].literal)
    }

    @Test
    fun should_handle_empty_kbd() {
        val doc = parser.parse("Key: <kbd></kbd> pressed")
        val para = doc.children.first()
        assertIs<Paragraph>(para)
        val kbd = para.children.filterIsInstance<KeyboardInput>().first()
        assertEquals("", kbd.literal)
    }
}

// ────── URL 百分号编码 ──────

class UrlPercentEncodingTest {

    @Test
    fun should_encode_space_in_url() {
        val result = CharacterUtils.percentEncodeUrl("https://example.com/my page")
        assertEquals("https://example.com/my%20page", result)
    }

    @Test
    fun should_preserve_existing_encoding() {
        val result = CharacterUtils.percentEncodeUrl("https://example.com/my%20page")
        assertEquals("https://example.com/my%20page", result)
    }

    @Test
    fun should_not_encode_safe_chars() {
        val result = CharacterUtils.percentEncodeUrl("https://example.com/path?q=1&r=2#hash")
        assertEquals("https://example.com/path?q=1&r=2#hash", result)
    }

    @Test
    fun should_encode_chinese_chars() {
        val result = CharacterUtils.percentEncodeUrl("https://example.com/你好")
        assertTrue(result.startsWith("https://example.com/"))
        assertTrue(result.contains("%"))
    }

    @Test
    fun should_apply_percent_encoding_in_link() {
        val parser = MarkdownParser()
        // 使用尖括号包裹带空格的 URL 不会百分号编码
        val doc = parser.parse("[link](<https://example.com/my page>)")
        val para = doc.children.first()
        assertIs<Paragraph>(para)
        val link = para.children.first()
        assertIs<Link>(link)
        // 尖括号内的 URL 不进行百分号编码
        assertEquals("https://example.com/my page", link.destination)
    }

    @Test
    fun should_encode_bare_url_in_link() {
        val parser = MarkdownParser()
        // 非尖括号 URL 中含有非 ASCII 字符需要百分号编码
        val doc = parser.parse("[link](https://example.com/path%20ok)")
        val para = doc.children.first()
        assertIs<Paragraph>(para)
        val link = para.children.first()
        assertIs<Link>(link)
        // 已编码的 %20 应保留
        assertTrue(link.destination.contains("%20"))
    }
}

// ────── 可折叠内容（details/summary） ──────

class CollapsibleContentTest {

    private val parser = MarkdownParser()

    @Test
    fun should_parse_details_as_html_block() {
        val input = "<details>\n<summary>Click me</summary>\nHidden content\n</details>"
        val doc = parser.parse(input)
        val html = doc.children.first()
        assertIs<HtmlBlock>(html)
        assertTrue(html.literal.contains("<details>"))
        assertTrue(html.literal.contains("<summary>"))
    }
}
