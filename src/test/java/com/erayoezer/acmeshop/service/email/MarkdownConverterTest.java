package com.erayoezer.acmeshop.service.email;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.stream.Stream;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class MarkdownConverterTest {

    @Mock
    private Parser parser;

    @Mock
    private HtmlRenderer renderer;

    @Mock
    private Node document;

    private MarkdownConverter markdownConverter;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        markdownConverter = new MarkdownConverter();
    }

    private static Stream<Arguments> provideMarkdownAndHtml() {
        return Stream.of(
                Arguments.of("**bold**", "<p><strong>bold</strong></p>"),
                Arguments.of("# heading", "<h1>heading</h1>"),
                Arguments.of("*italic*", "<p><em>italic</em></p>"),
                Arguments.of("[Google](https://www.google.com)", "<p><a href=\"https://www.google.com\">Google</a></p>"),
                Arguments.of("![alt text](image.jpg)", "<p><img src=\"image.jpg\" alt=\"alt text\" /></p>"),
                Arguments.of("```\nint x = 10;\n```", "<pre><code>int x = 10;\n</code></pre>"),
                Arguments.of("- item 1\n- item 2", "<ul>\n<li>item 1</li>\n<li>item 2</li>\n</ul>")
        );
    }

    @ParameterizedTest
    @MethodSource("provideMarkdownAndHtml")
    public void testConvertToHtml(String markdownContent, String expectedHtml) {
        when(parser.parse(markdownContent)).thenReturn(document);
        when(renderer.render(document)).thenReturn(expectedHtml);

        String actualHtml = markdownConverter.convertToHtml(markdownContent);

        assertEquals(expectedHtml, actualHtml);
    }
}
