package at.ac.ubik.archadvisor.service.documentcreator;

import at.ac.ubik.archadvisor.DTO.FinalStackRequestDto;
import at.ac.ubik.archadvisor.DTO.QuestionnaireRequestDto;
import at.ac.ubik.archadvisor.domain.enums.ArchitectureScope;
import at.ac.ubik.archadvisor.domain.enums.DeploymentPreference;
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

        FinalStackRequestDto finalStack = new FinalStackRequestDto();
        finalStack.setArchitectureScope(ArchitectureScope.FULL_STACK);
        finalStack.setAuthorName("Jona");
        finalStack.setOrganization("");
        finalStack.setNotes("");
        finalStack.setDraftLink("http://localhost:3000/");
        finalStack.setDraftId("draft-123");

        QuestionnaireRequestDto questionnaireDto = new QuestionnaireRequestDto();
        questionnaireDto.setProjectName("Demo Project");

        byte[] pdf = creator.createStackPdf(
                finalStack,
                "Spring Boot",
                "React",
                "PostgreSQL",
                null,
                questionnaireDto,
                1L
        );

        assertNotNull(pdf);
        assertTrue(pdf.length > 500, "PDF should not be tiny/empty");

        try (PDDocument doc = PDDocument.load(new ByteArrayInputStream(pdf))) {
            assertEquals(2, doc.getNumberOfPages());

            String text = new PDFTextStripper().getText(doc);

            assertTrue(text.contains("Archadvisor"));

            assertTrue(text.contains("Architecture scope: Full Stack"));
            assertTrue(text.contains("Backend: Spring Boot"));
            assertTrue(text.contains("Frontend: React"));
            assertTrue(text.contains("Database: PostgreSQL"));

            assertFalse(text.contains("Mobile:"), "Mobile section should not be printed when empty");

            assertTrue(DATE_PATTERN.matcher(text).find(), "Header timestamp should match expected format");
        }
    }


    @Test
    void createStackPdf_includesLogoIfResourcePresent() throws Exception {
        DocumentCreator creator = new DocumentCreator();
        FinalStackRequestDto finalStack = new FinalStackRequestDto();
        finalStack.setArchitectureScope(ArchitectureScope.FULL_STACK);
        finalStack.setAuthorName("Jona");
        finalStack.setOrganization("");
        finalStack.setNotes("");
        finalStack.setDraftLink("http://localhost:3000/");
        finalStack.setDraftId("draft-123");

        QuestionnaireRequestDto questionnaireDto = new QuestionnaireRequestDto();
        questionnaireDto.setProjectName("Demo Project");

        byte[] pdf = creator.createStackPdf(
                finalStack,
                "BACKEND_ONLY",
                "Spring Boot",
                "",
                "",
                questionnaireDto,
                1
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

    @Test
    void createStackPdf_withoutBudgetTierOption() throws Exception {
        DocumentCreator creator = new DocumentCreator();
        FinalStackRequestDto finalStack = new FinalStackRequestDto();
        finalStack.setArchitectureScope(ArchitectureScope.FULL_STACK);
        finalStack.setAuthorName("Max");
        QuestionnaireRequestDto questionnaireDto = new QuestionnaireRequestDto();
        questionnaireDto.setProjectName("Triple3");
        questionnaireDto.setDeploymentPreference(DeploymentPreference.SELF_HOSTED);

        byte[] pdf = creator.createStackPdf(
                finalStack,
                "Spring Boot",
                "React",
                "PostgreSQL",
                null,
                questionnaireDto,
                1L
        );
        try (PDDocument doc = PDDocument.load(new ByteArrayInputStream(pdf))) {
            String text = new PDFTextStripper().getText(doc);
            assertFalse(text.contains("Budget Tier"), "This deploymentMode should not contain a Budget Tier");
        }
    }

    @Test
    void createStackPdf_withBudgetTierOption() throws Exception {
        DocumentCreator creator = new DocumentCreator();
        FinalStackRequestDto finalStack = new FinalStackRequestDto();
        finalStack.setArchitectureScope(ArchitectureScope.FULL_STACK);
        finalStack.setAuthorName("Max");
        QuestionnaireRequestDto questionnaireDto = new QuestionnaireRequestDto();
        questionnaireDto.setProjectName("Triple3");
        questionnaireDto.setDeploymentPreference(DeploymentPreference.PAAS);

        byte[] pdf = creator.createStackPdf(
                finalStack,
                "Spring Boot",
                "React",
                "PostgreSQL",
                null,
                questionnaireDto,
                1L
        );
        try (PDDocument doc = PDDocument.load(new ByteArrayInputStream(pdf))) {
            String text = new PDFTextStripper().getText(doc);
            System.out.println(text);
            assertTrue(text.contains("Budget tier"), "This deploymentMode should contain a Budget Tier");
        }
    }
}