package com.hrm.markdown

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.hrm.markdown.renderer.Markdown
import com.hrm.markdown.renderer.MarkdownTheme

@Composable
@Preview
fun App() {
    MaterialTheme {
        Markdown(
            markdown = demoMarkdown,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .safeContentPadding()
                .fillMaxSize(),
            theme = MarkdownTheme(),
        )
    }
}

private val demoMarkdown = """
# Markdown Renderer Demo

## 基础文本样式

这是一个段落，包含 **粗体**、*斜体*、***粗斜体***、~~删除线~~、`行内代码` 和 ==高亮文本== 等样式。

还支持 H~2~O 下标和 x^2^ 上标，以及 ++插入文本++。

---

## 链接与图片

这是一个 [链接](https://example.com "示例")。自动链接：<https://example.com>

## 引用

> 这是一段引用。
> 
> 引用中可以包含 **粗体** 和 *斜体*。
> 
> > 嵌套引用也是支持的。

## 列表

### 无序列表
- 项目一
- 项目二
  - 嵌套项目 A
  - 嵌套项目 B
- 项目三

### 有序列表
1. 第一步
2. 第二步
3. 第三步

### 任务列表
- [x] 已完成的任务
- [ ] 待完成的任务
- [x] 另一个已完成的任务

## 代码块

```kotlin
fun main() {
    val message = "Hello, Markdown!"
    println(message)
    
    val numbers = listOf(1, 2, 3, 4, 5)
    numbers.filter { it % 2 == 0 }
           .map { it * it }
           .forEach { println(it) }
}
```

## 表格

| 功能 | 状态 | 说明 |
|:-----|:----:|-----:|
| 标题 | ✅ | ATX & Setext |
| 代码块 | ✅ | 围栏 & 缩进 |
| 列表 | ✅ | 有序 & 无序 & 任务 |
| 数学公式 | ✅ | 行内 & 块级 |

## 数学公式

行内公式：质能方程 ${'$'}E = mc^2${'$'} 是物理学中最著名的公式之一。

块级公式：

${'$'}${'$'}
\frac{-b \pm \sqrt{b^2 - 4ac}}{2a}
${'$'}${'$'}

## Admonition

> [!NOTE]
> 这是一个提示信息，用于展示 Admonition 渲染效果。

> [!WARNING]
> 这是一个警告信息。

> [!TIP]
> 这是一个技巧提示。

## 脚注

这是一段带有脚注的文本[^1]。

[^1]: 这是脚注的内容。

## Emoji

支持 Emoji 短代码：:smile: :heart: :rocket:

---

*Powered by Markdown Renderer for Compose Multiplatform*
""".trimIndent()
