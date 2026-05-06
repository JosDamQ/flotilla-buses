package flotabuses.servicios;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Minimal PDF 1.4 builder — no external dependencies.
 * Supports Helvetica + Helvetica-Bold with WinAnsiEncoding (Spanish chars OK).
 *
 * Usage:
 *   PdfBuilder pdf = new PdfBuilder();
 *   pdf.setup("Titulo", "Subtitulo", new String[]{"Col1","Col2"}, new float[]{200,300});
 *   pdf.addRow("valor1", "valor2");
 *   pdf.addFooter("Total: N registros");
 *   byte[] bytes = pdf.build();
 */
public class PdfBuilder {

    /* ── Page layout ─────────────────────────────────────── */
    private static final float PW    = 612f; // Letter width
    private static final float PH    = 792f; // Letter height
    private static final float ML    = 42f;  // left margin
    private static final float MR    = 42f;  // right margin
    private static final float MT    = 45f;  // top margin
    private static final float MB    = 40f;  // bottom margin
    private static final float ROW_H = 13f;  // data row height

    /* ── State ───────────────────────────────────────────── */
    private String   title;
    private String   subtitle;
    private String[] colHeaders;
    private float[]  colX;
    private float[]  colW;

    private final List<byte[]> pageStreams = new ArrayList<>();
    private StringBuilder      cur        = null;
    private float              yPos;
    private boolean            evenRow    = false;

    /* ══ PUBLIC API ═══════════════════════════════════════════ */

    public PdfBuilder setup(String title, String subtitle,
                            String[] headers, float[] widths) {
        this.title      = title;
        this.subtitle   = subtitle;
        this.colHeaders = headers;
        this.colW       = widths;
        this.colX       = new float[widths.length];
        float x = ML;
        for (int i = 0; i < widths.length; i++) {
            colX[i] = x;
            x += widths[i];
        }
        startPage(true);
        return this;
    }

    public void addRow(String... values) {
        if (yPos < MB + ROW_H * 3) {
            commit();
            startPage(false);
        }
        float ry = yPos - ROW_H;

        // Alternating background
        if (evenRow) {
            rg(0.93f, 0.96f, 1.00f);
            rect(ML, ry, PW - ML - MR, ROW_H, true);
        }
        evenRow = !evenRow;

        // Row bottom border
        cur.append(String.format("%.3f G %.2f w\n", 0.80f, 0.3f));
        line(ML, ry, PW - MR, ry);

        // Text
        rg(0f, 0f, 0f);
        cur.append("BT\n/F1 8 Tf\n");
        for (int i = 0; i < Math.min(values.length, colHeaders.length); i++) {
            String v = values[i] == null ? "" : values[i];
            v = cut(v, colW[i] - 4f, 8f);
            cur.append(String.format("1 0 0 1 %.2f %.2f Tm\n%s Tj\n",
                    colX[i] + 2f, ry + 3f, ps(v)));
        }
        cur.append("ET\n");
        yPos = ry;
    }

    public void addFooter(String text) {
        if (cur == null) return;
        cur.append(String.format("%.3f G %.2f w\n", 0.5f, 0.3f));
        line(ML, MB - 4f, PW - MR, MB - 4f);
        rg(0.4f, 0.4f, 0.4f);
        cur.append(String.format("BT\n/F1 8 Tf\n1 0 0 1 %.2f %.2f Tm\n%s Tj\nET\n",
                ML, MB - 14f, ps(text)));
    }

    public byte[] build() {
        if (cur != null && cur.length() > 0) commit();

        int n          = pageStreams.size();
        int totalObjs  = 4 + 2 * n;
        long[] offsets = new long[totalObjs + 1];

        ByteArrayOutputStream out = new ByteArrayOutputStream(8192);

        // Header
        w(out, "%PDF-1.4\n");

        // Obj 3: Helvetica (F1)
        offsets[3] = out.size();
        w(out, "3 0 obj\n<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica"
              + " /Encoding /WinAnsiEncoding >>\nendobj\n");

        // Obj 4: Helvetica-Bold (F2)
        offsets[4] = out.size();
        w(out, "4 0 obj\n<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica-Bold"
              + " /Encoding /WinAnsiEncoding >>\nendobj\n");

        // Page / Content pairs  — IDs: content=5+2i, page=6+2i
        for (int i = 0; i < n; i++) {
            int cId   = 5 + 2 * i;
            int pId   = 6 + 2 * i;
            byte[] cs = pageStreams.get(i);

            offsets[cId] = out.size();
            w(out, cId + " 0 obj\n<< /Length " + cs.length + " >>\nstream\n");
            try { out.write(cs); } catch (IOException ignored) {}
            w(out, "\nendstream\nendobj\n");

            offsets[pId] = out.size();
            w(out, pId + " 0 obj\n<< /Type /Page /Parent 2 0 R"
                  + " /MediaBox [0 0 612 792] /Contents " + cId + " 0 R"
                  + " /Resources << /Font << /F1 3 0 R /F2 4 0 R >> >> >>\nendobj\n");
        }

        // Obj 2: Pages
        offsets[2] = out.size();
        StringBuilder kids = new StringBuilder();
        for (int i = 0; i < n; i++) kids.append(6 + 2 * i).append(" 0 R ");
        w(out, "2 0 obj\n<< /Type /Pages /Count " + n
              + " /Kids [" + kids + "] >>\nendobj\n");

        // Obj 1: Catalog
        offsets[1] = out.size();
        w(out, "1 0 obj\n<< /Type /Catalog /Pages 2 0 R >>\nendobj\n");

        // Xref
        long xrefPos = out.size();
        w(out, "xref\n0 " + (totalObjs + 1) + "\n");
        w(out, String.format("%010d 65535 f \n", 0));
        for (int i = 1; i <= totalObjs; i++)
            w(out, String.format("%010d 00000 n \n", offsets[i]));

        // Trailer
        w(out, "trailer\n<< /Size " + (totalObjs + 1) + " /Root 1 0 R >>\n");
        w(out, "startxref\n" + xrefPos + "\n%%EOF\n");

        return out.toByteArray();
    }

    /* ══ PRIVATE ══════════════════════════════════════════════ */

    private void startPage(boolean isFirst) {
        cur     = new StringBuilder();
        yPos    = PH - MT;
        evenRow = false;

        if (isFirst) {
            // Title
            rg(0.12f, 0.24f, 0.49f);
            cur.append(String.format("BT\n/F2 14 Tf\n1 0 0 1 %.2f %.2f Tm\n%s Tj\nET\n",
                    ML, yPos - 14f, ps(title)));
            yPos -= 22f;
            // Subtitle
            rg(0.30f, 0.30f, 0.30f);
            cur.append(String.format("BT\n/F1 9 Tf\n1 0 0 1 %.2f %.2f Tm\n%s Tj\nET\n",
                    ML, yPos - 9f, ps(subtitle)));
            yPos -= 16f;
            // Separator line
            cur.append(String.format("%.3f G %.2f w\n", 0.6f, 0.5f));
            line(ML, yPos, PW - MR, yPos);
            yPos -= 8f;
        } else {
            rg(0.30f, 0.30f, 0.30f);
            cur.append(String.format("BT\n/F1 8 Tf\n1 0 0 1 %.2f %.2f Tm\n%s Tj\nET\n",
                    ML, yPos - 8f, ps(title + " (continuacion)")));
            yPos -= 16f;
        }

        // Column header row
        float hY = yPos - ROW_H;
        rg(0.20f, 0.39f, 0.65f);
        rect(ML, hY, PW - ML - MR, ROW_H, true);
        rg(1f, 1f, 1f);
        cur.append("BT\n/F2 8 Tf\n");
        for (int i = 0; i < colHeaders.length; i++) {
            String h = cut(colHeaders[i], colW[i] - 4f, 8f);
            cur.append(String.format("1 0 0 1 %.2f %.2f Tm\n%s Tj\n",
                    colX[i] + 2f, hY + 4f, ps(h)));
        }
        cur.append("ET\n");
        yPos = hY;
    }

    private void commit() {
        if (cur != null) {
            String s = cur.toString();
            if (!s.endsWith("\n")) s += "\n";
            pageStreams.add(s.getBytes(StandardCharsets.ISO_8859_1));
            cur = null;
        }
    }

    private void rg(float r, float g, float b) {
        cur.append(String.format("%.3f %.3f %.3f rg\n", r, g, b));
    }

    private void rect(float x, float y, float w, float h, boolean fill) {
        cur.append(String.format("%.2f %.2f %.2f %.2f re %s\n",
                x, y, w, h, fill ? "f" : "S"));
    }

    private void line(float x1, float y1, float x2, float y2) {
        cur.append(String.format("%.2f %.2f m %.2f %.2f l S\n", x1, y1, x2, y2));
    }

    /** Truncate text to fit column width at given font size (Helvetica ≈ 0.52 avg). */
    private static String cut(String text, float w, float fs) {
        int max = Math.max(1, (int) (w / (fs * 0.52f)));
        if (text.length() <= max) return text;
        return text.substring(0, Math.max(0, max - 3)) + "...";
    }

    /** PDF string literal with ISO-8859-1 / WinAnsiEncoding. */
    private static String ps(String text) {
        StringBuilder sb = new StringBuilder("(");
        for (char c : text.toCharArray()) {
            if (c == '(' || c == ')' || c == '\\') {
                sb.append('\\').append(c);
            } else if (c < 32 || c == 127) {
                // skip control chars
            } else if (c < 128) {
                sb.append(c);
            } else {
                // Extended: encode as octal using ISO-8859-1 byte value
                try {
                    byte[] b = String.valueOf(c).getBytes(StandardCharsets.ISO_8859_1);
                    if (b.length == 1 && (b[0] & 0xFF) >= 128)
                        sb.append(String.format("\\%03o", b[0] & 0xFF));
                    else
                        sb.append('?');
                } catch (Exception e) { sb.append('?'); }
            }
        }
        sb.append(')');
        return sb.toString();
    }

    private static void w(ByteArrayOutputStream out, String s) {
        try { out.write(s.getBytes(StandardCharsets.ISO_8859_1)); }
        catch (IOException ignored) {}
    }
}
