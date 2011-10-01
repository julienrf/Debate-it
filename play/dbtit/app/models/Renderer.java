package models;

import etoffe.*;
import etoffe.java.JEtoffe;

import static play.libs.F.Matcher.ClassOf;
import static scala.collection.JavaConversions.asJavaCollection;

/**
 * Render Etoffe text into paragraphs and footnotes
 */
public class Renderer {

    public static void buildPost(final Post post, final String content) {

        final Document result = JEtoffe.parse(content);

        int i = 0;
        for (Block block : asJavaCollection(result.blocks())) {
            final StringBuilder buffer = new StringBuilder();
            final Paragraph paragraph = new Paragraph(post, i).save();

            appendBlock(block, paragraph, buffer);

            paragraph.content = buffer.toString();
            paragraph.save();
            post.paragraphs.add(paragraph);
            i++;
        }
    }

    private static void appendBlock(Block block, Paragraph paragraph, StringBuilder buffer) {
        for (Section section : ClassOf(Section.class).match(block)) {
            buffer.append("<h3>");
            escapeAndAppend(section.title(), buffer);
            buffer.append("</h3>");
            return;
        }

        for (Bullet bullet : ClassOf(Bullet.class).match(block)) {
            buffer.append("<ul><li>");
            appendParagraph(bullet.content(), paragraph, buffer);
            buffer.append("</ul></li>");
            return;
        }

        for (etoffe.Paragraph p: ClassOf(etoffe.Paragraph.class).match(block)) {
            appendParagraph(p, paragraph, buffer);
            return;
        }
    }

    private static void appendParagraph(final etoffe.Paragraph p, final Paragraph paragraph, final StringBuilder buffer) {
        for (Inline inline : asJavaCollection(p.content())) {
            appendInline(inline, paragraph, buffer);
        }
    }

    private static void appendInline(Inline inline, Paragraph paragraph, StringBuilder buffer) {
        for (Text text : ClassOf(Text.class).match(inline)) {
            escapeAndAppend(text.text(), buffer);
            return;
        }

        for (Emphasized em : ClassOf(Emphasized.class).match(inline)) {
            wrapAndAppend(em.text(), "em", buffer);
            return;
        }

        for (Strong strong : ClassOf(Strong.class).match(inline)) {
            wrapAndAppend(strong.text(), "strong", buffer);
            return;
        }

        for (Code code : ClassOf(Code.class).match(inline)) {
            wrapAndAppend(code.text(), "code", buffer);
            return;
        }

        for (Link link : ClassOf(Link.class).match(inline)) {
            buffer.append("<a href=\"");
            escapeAndAppend(link.url(), buffer);
            buffer.append("\" title=\"");
            escapeAndAppend(link.title(), buffer);
            buffer.append("\">");
            escapeAndAppend(link.title(), buffer);
            buffer.append("</a>");
            return;
        }

        for (Footnote footnote : ClassOf(Footnote.class).match(inline)) {
            final FootNote model = new FootNote(paragraph).save();
            // Write footnote reference
            buffer.append("<sup id=\"ref-footnote_");
            buffer.append(model.id);
            buffer.append("\"><a href=\"#footnote_");
            buffer.append(model.id);
            buffer.append("\">");
            buffer.append(model.id);
            buffer.append("</a></sup>");
            // Write footnote
            final StringBuilder fnBuffer = new StringBuilder();
            fnBuffer.append("<div id=\"footnote_");
            fnBuffer.append(model.id);
            fnBuffer.append("\">");
            fnBuffer.append("<a href=\"#ref-footnote_");
            fnBuffer.append(model.id);
            fnBuffer.append("\">&uarr;</a>");
            escapeAndAppend(footnote.content(), fnBuffer);
            fnBuffer.append("</div>");
            model.content = fnBuffer.toString();
            model.save();
            paragraph.footNotes.add(model);
            return;
        }
    }

    private static void wrapAndAppend(String text, String tag, StringBuilder buffer) {
        buffer.append('<');
        buffer.append(tag);
        buffer.append('>');
        escapeAndAppend(text, buffer);
        buffer.append("</");
        buffer.append(tag);
        buffer.append('>');
    }

    private static void escapeAndAppend(final String title, final StringBuilder buffer) {
        for (Character c : title.toCharArray()) {
            switch (c) {
                case '<':
                    buffer.append("&lt;");
                    break;
                case '>':
                    buffer.append("&gt;");
                    break;
                case '&':
                    buffer.append("&amp;");
                    break;
                case '"':
                    buffer.append("&quot;");
                    break;
                default:
                    buffer.append(c);
            }
        }
    }
}
