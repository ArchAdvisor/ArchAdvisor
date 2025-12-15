package at.ac.ubik.archadvisor.service.documentcreator;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

class DocumentCreatorTest {

    private static final Pattern DATE_PATTERN =
            Pattern.compile("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2} \\S+"); // yyyy-MM-dd HH:mm z

    @Test
    void createStackPdf_containsHeaderTitleScopeAndOptionalSections() throws Exception {
        DocumentCreator creator = new DocumentCreator();

        byte[] pdf = creator.createStackPdf(
                "Tech Stack Report",
                "FULL_STACK",
                "Spring Boot",
                "React",
                "PostgreSQL",
                ""
        );

        assertNotNull(pdf);
        assertTrue(pdf.length > 500, "PDF should not be tiny/empty");

        try (PDDocument doc = PDDocument.load(new ByteArrayInputStream(pdf))) {
            assertEquals(1, doc.getNumberOfPages());

            String text = new PDFTextStripper().getText(doc);

            assertTrue(text.contains("Archadvisor"));

            assertTrue(text.contains("Tech Stack Report"));
            assertTrue(text.contains("Architecture scope: Full Stack"));
            assertTrue(text.contains("Backend: Spring Boot"));
            assertTrue(text.contains("Frontend: React"));
            assertTrue(text.contains("Database: PostgreSQL"));

            assertFalse(text.contains("Mobile:"), "Mobile section should not be printed when empty");

            assertTrue(DATE_PATTERN.matcher(text).find(), "Header timestamp should match expected format");
        }
    }

    @Test
    void createStackPdf_invalidScope_throws() {
        DocumentCreator creator = new DocumentCreator();

        assertThrows(IllegalArgumentException.class, () ->
                creator.createStackPdf("x", "NOT_A_SCOPE", null, null, null, null)
        );
    }

    @Test
    void createStackPdf_includesLogoIfResourcePresent() throws Exception {
        DocumentCreator creator = new DocumentCreator();

        byte[] pdf = creator.createStackPdf(
                "Tech Stack Report",
                "BACKEND_ONLY",
                "Spring Boot",
                "",
                "",
                ""
        );

        try (PDDocument doc = PDDocument.load(new ByteArrayInputStream(pdf))) {
            PDPage page = doc.getPage(0);
            PDResources resources = page.getResources();
            assertNotNull(resources);

            boolean hasImage = false;
            for (var name : resources.getXObjectNames()) {
                PDXObject xo = resources.getXObject(name);
                if (xo instanceof PDImageXObject) {
                    hasImage = true;
                    break;
                }
            }

            assertTrue(hasImage, "Expected an embedded logo image XObject");
        }
    }
}