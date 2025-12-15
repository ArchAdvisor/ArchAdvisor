package at.ac.ubik.archadvisor.service.documentcreator;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;


@Service
public class DocumentCreator {
    private static final float MARGIN = 50f;
    private static final float HEADER_HEIGHT = 60f;

    private float drawHeader(PDDocument doc, PDPage page, PDPageContentStream cs, String productName) throws Exception {
        PDRectangle mediaBox = page.getMediaBox();
        float pageWidth = mediaBox.getWidth();
        float topY = mediaBox.getHeight() - MARGIN;

        float logoH = 28f;
        float logoW = 28f;
        float logoX = MARGIN;
        float logoY = topY - logoH;

        try (InputStream is = getClass().getResourceAsStream("/favicon-32x32.png")) {
            if (is != null) {
                PDImageXObject logo = PDImageXObject.createFromByteArray(doc, is.readAllBytes(), "logo");
                cs.drawImage(logo, logoX, logoY, logoW, logoH);
            }
            if (is == null) {
                System.out.println("null resource");
            }

        }

        float textX = logoX + logoW + 10f;
        float textY = topY - 20f;

        cs.beginText();
        cs.setFont(PDType1Font.HELVETICA_BOLD, 14);
        cs.newLineAtOffset(textX, textY);
        cs.showText(productName);
        cs.endText();

        ZonedDateTime now = ZonedDateTime.now();
        String formatted = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm z"));

        cs.setFont(PDType1Font.HELVETICA, 10);

        float dateWidth = (PDType1Font.HELVETICA.getStringWidth(formatted) / 1000f) * 10f;
        float dateX = pageWidth - MARGIN - dateWidth;
        float dateY = topY - 10f;

        cs.beginText();
        cs.newLineAtOffset(dateX, dateY);
        cs.showText(formatted);
        cs.endText();

        float lineY = topY - HEADER_HEIGHT + 10f;
        cs.setLineWidth(0.6f);
        cs.moveTo(MARGIN, lineY);
        cs.lineTo(pageWidth - MARGIN, lineY);
        cs.stroke();

        return lineY - 25f;
    }

    public byte[] createStackPdf(String title, String scope,
                                 String backend, String frontend,
                                 String database, String mobile) throws Exception {

        try (PDDocument doc = new PDDocument();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {


            PDPage page = new PDPage();
            doc.addPage(page);

            try (PDPageContentStream cs = new PDPageContentStream(doc, page)) {
                float y = drawHeader(doc, page, cs, "Archadvisor");
                //float y = 750;

                cs.beginText();
                cs.setFont(PDType1Font.HELVETICA_BOLD, 18);
                cs.newLineAtOffset(50, y);
                cs.showText(title);
                cs.endText();
                y -= 40;

                y = writeLine(cs, "Architecture scope: " + makeArchitectureScopeReadable(scope), y);

                y -= 10;
                if (backend != null && !backend.isEmpty()) {
                    y = writeLine(cs, "Backend: " + backend, y);
                }
                if (frontend != null && !frontend.isEmpty()) {
                    y = writeLine(cs, "Frontend: " + frontend, y);
                }
                if (database != null && !database.isEmpty()) {
                    y = writeLine(cs, "Database: " + database, y);
                }
                if (mobile != null && !mobile.isEmpty()) {
                    y = writeLine(cs, "Mobile: " + mobile, y);
                }
            }

            doc.save(out);
            return out.toByteArray();
        }
    }

    private float writeLine(PDPageContentStream cs, String text, float y) throws Exception {
        cs.beginText();
        cs.setFont(PDType1Font.HELVETICA, 12);
        cs.newLineAtOffset(50, y);
        cs.showText(text);
        cs.endText();
        return y - 18;
    }

    private String makeArchitectureScopeReadable(String architectureScope) {
        return switch (architectureScope) {
            case "FULL_STACK" -> "Full Stack";
            case "BACKEND_ONLY" -> "Backend Only";
            case "MOBILE" -> "Mobile";
            default -> throw new IllegalArgumentException("Invalid architecture scope");
        };
    }
}
