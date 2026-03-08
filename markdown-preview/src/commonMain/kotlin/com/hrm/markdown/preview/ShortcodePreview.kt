package com.hrm.markdown.preview

import com.hrm.markdown.renderer.Markdown

internal val shortcodePreviewGroups = listOf(
    PreviewGroup(
        id = "block_shortcode",
        title = "块级短代码",
        description = "{% tag args %}...{% endtag %} 块级短代码语法",
        items = listOf(
            PreviewItem(
                id = "self_closing_shortcode",
                title = "自闭合短代码",
                content = {
                    Markdown(
                        markdown = """
{% youtube abc123 %}

{% include file="header.html" cache=true %}
                        """.trimIndent()
                    )
                }
            ),
            PreviewItem(
                id = "shortcode_with_content",
                title = "包含内容的短代码",
                content = {
                    Markdown(
                        markdown = """
{% alert %}
This is a warning message with **bold** text.
{% endalert %}

{% note %}
Important stuff here.

- Item 1
- Item 2
{% endnote %}
                        """.trimIndent()
                    )
                }
            ),
            PreviewItem(
                id = "shortcode_args",
                title = "参数解析",
                content = {
                    Markdown(
                        markdown = """
位置参数：

{% tag "hello world" %}

键值对参数：

{% include file="header.html" cache=true %}

混合参数：

{% video abc123 width=640 height=480 %}
                        """.trimIndent()
                    )
                }
            ),
        )
    ),
    PreviewGroup(
        id = "inline_shortcode",
        title = "行内短代码",
        description = "段落内嵌入的 {% tag args %} 行内短代码",
        items = listOf(
            PreviewItem(
                id = "inline_shortcode_basic",
                title = "行内短代码",
                content = {
                    Markdown(
                        markdown = """
Text with {% icon name=star %} inside a paragraph.

Click {% button text="Submit" %} to continue.
                        """.trimIndent()
                    )
                }
            ),
        )
    ),
)
