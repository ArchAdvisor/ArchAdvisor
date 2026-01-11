package at.ac.ubik.archadvisor.service.documentcreator;

import at.ac.ubik.archadvisor.DTO.FinalStackRequestDto;
import at.ac.ubik.archadvisor.DTO.QuestionnaireRequestDto;
import at.ac.ubik.archadvisor.domain.enums.DeploymentPreference;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionURI;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationLink;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;


@Service
public class DocumentCreator {

    private static final float MARGIN = 50f;
    private static final float HEADER_HEIGHT = 60f;

    private static final float BODY_FONT_SIZE = 12f;
    private static final float LINE_HEIGHT = 16f;

    private static final String PRODUCT_NAME = "Archadvisor";
    private static final String LOGO_RESOURCE = "/favicon-32x32.png";
    private static final String NOT_SPECIFIED = "Not specified";
    private static String normalizeUrl(String url) {
        if (url == null) return null;
        String cleaned = url.trim().replaceAll("\\p{Cntrl}", "");
        if (!cleaned.matches("^[a-zA-Z][a-zA-Z0-9+.-]*://.*$")) {
            cleaned = "https://" + cleaned;
        }
        return cleaned;
    }

    private static boolean notBlank(String s) {
        return s != null && !s.isBlank();
    }

    private static String safe(String s) {
        return notBlank(s) ? s : NOT_SPECIFIED;
    }

    private static String humanizeEnum(String enumName) {
        if (enumName == null || enumName.isBlank()) return NOT_SPECIFIED;
        String lower = enumName.toLowerCase(Locale.ROOT).replace('_', ' ');
        return Character.toUpperCase(lower.charAt(0)) + lower.substring(1);
    }

    public byte[] createStackPdf(
            FinalStackRequestDto finalStack,
            String backendName,
            String frontendName,
            String databaseName,
            String mobileName,
            QuestionnaireRequestDto questionnaire,
            long questionnaireVersion
    ) throws Exception {

        try (PDDocument doc = new PDDocument();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            PDPage page1 = new PDPage();
            doc.addPage(page1);

            try (PDPageContentStream cs = new PDPageContentStream(doc, page1)) {
                String generatedAt = ZonedDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm z"));
                float y = drawHeader(doc, page1, cs, PRODUCT_NAME, "Generated " + generatedAt);

                y = writeHeading(cs, questionnaire.getProjectName() + " – Recommended Stack" + " (v" + questionnaireVersion + ")", y);

                if (notBlank(finalStack.getAuthorName())) {
                    y = writeKeyValue(cs, page1, "Author", finalStack.getAuthorName(), y);
                }
                if (notBlank(finalStack.getOrganization())) {
                    y = writeKeyValue(cs, page1, "Organization", finalStack.getOrganization(), y);
                }

                if (notBlank(finalStack.getDraftLink())) {
                    y = writeClickableLink(page1, cs, "Draft link", finalStack.getDraftLink(), y);
                }

                if (notBlank(finalStack.getDraftId())) {
                    y = writeKeyValue(cs, page1, "Draft ID", finalStack.getDraftId(), y);
                }

                if (notBlank(finalStack.getNotes())) {
                    y -= 6;
                    y = writeSubheading(cs, "Notes", y);
                    y = writeWrapped(cs, page1, finalStack.getNotes(), y);
                }

                y -= 10;
                y = writeSubheading(cs, "Scope and components", y);

                String scope = (finalStack.getArchitectureScope() != null)
                        ? finalStack.getArchitectureScope().name()
                        : NOT_SPECIFIED;

                y = writeKeyValue(cs, page1, "Architecture scope", makeArchitectureScopeReadable(scope), y);

                if (notBlank(backendName)) y = writeKeyValue(cs, page1, "Backend", backendName, y);
                if (notBlank(frontendName)) y = writeKeyValue(cs, page1, "Frontend", frontendName, y);
                if (notBlank(databaseName)) y = writeKeyValue(cs, page1, "Database", databaseName, y);
                if (notBlank(mobileName)) y = writeKeyValue(cs, page1, "Mobile", mobileName, y);
            }

            //Page 2 (Questionnaire)
            if (questionnaire != null) {
                PDPage page2 = new PDPage();
                doc.addPage(page2);

                try (PDPageContentStream cs2 = new PDPageContentStream(doc, page2)) {
                    float y2 = drawHeader(doc, page2, cs2, PRODUCT_NAME, "");
                    y2 = writeHeading(cs2, "Questionnaire (copy)", y2);

                    y2 = writeKeyValue(cs2, page2, "Architecture scope",
                            questionnaire.getArchitectureScope() != null
                                    ? humanizeEnum(questionnaire.getArchitectureScope().name())
                                    : NOT_SPECIFIED,
                            y2);

                    y2 = writeKeyValue(cs2, page2, "Use only Open source-technologies", String.valueOf(questionnaire.isOpenSource()), y2);

                    y2 = writeKeyValue(cs2, page2, "Deployment preference",
                            questionnaire.getDeploymentPreference() != null
                                    ? humanizeEnum(questionnaire.getDeploymentPreference().name())
                                    : NOT_SPECIFIED,
                            y2);
                    if (needsBudgetTier(questionnaire.getDeploymentPreference())) {
                        y2 = writeKeyValue(cs2, page2, "Budget tier",
                                questionnaire.getBudgetTier() != null
                                        ? humanizeEnum(questionnaire.getBudgetTier().name())
                                        : NOT_SPECIFIED,
                                y2);
                    }
                    y2 = writeKeyValue(cs2, page2, "Expected users",
                            questionnaire.getExpectedUsers() != null
                                    ? questionnaire.getExpectedUsers().toString()
                                    : NOT_SPECIFIED,
                            y2);

                    y2 = writeKeyValue(cs2, page2, "Serverless-friendly",
                            String.valueOf(questionnaire.isServerlessFriendly()), y2);

                    y2 = writeKeyValue(cs2, page2, "Team size",
                            questionnaire.getTeamSize() != null
                                    ? questionnaire.getTeamSize().toString()
                                    : NOT_SPECIFIED,
                            y2);

                    y2 = writeKeyValue(cs2, page2, "Experience level",
                            safe(questionnaire.getExperienceLevel()), y2);

                    if (questionnaire.getProgrammingLanguages() != null && !questionnaire.getProgrammingLanguages().isEmpty()) {
                        String languages = questionnaire.getProgrammingLanguages().stream()
                                .map(pl -> humanizeEnum(pl.name()))
                                .sorted()
                                .collect(Collectors.joining(", "));
                        y2 = writeKeyValue(cs2, page2, "Programming languages", languages, y2);
                    }

                    if (questionnaire.getPriorityAspects() != null && !questionnaire.getPriorityAspects().isEmpty()) {
                        y2 = writeRankedList(cs2, page2, "Priority aspects (ranked)", questionnaire.getPriorityAspects(), y2);
                    }

                }
            }
            addPageNumbers(doc);
            doc.save(out);
            return out.toByteArray();
        }
    }

    private float drawHeader(
            PDDocument doc,
            PDPage page,
            PDPageContentStream cs,
            String productName,
            String rightHeaderText
    ) throws Exception {

        PDRectangle mediaBox = page.getMediaBox();
        float pageWidth = mediaBox.getWidth();
        float topY = mediaBox.getHeight() - MARGIN;

        float logoH = 28f;
        float logoW = 28f;
        float logoX = MARGIN;
        float logoY = topY - logoH;

        try (InputStream is = getClass().getResourceAsStream(LOGO_RESOURCE)) {
            if (is != null) {
                PDImageXObject logo = PDImageXObject.createFromByteArray(doc, is.readAllBytes(), "logo");
                cs.drawImage(logo, logoX, logoY, logoW, logoH);
            }
        }

        float textX = logoX + logoW + 10f;
        float textY = topY - 20f;

        cs.beginText();
        cs.setFont(PDType1Font.HELVETICA_BOLD, 14);
        cs.newLineAtOffset(textX, textY);
        cs.showText(productName);
        cs.endText();

        float fontSize = 10f;
        cs.setFont(PDType1Font.HELVETICA, fontSize);

        String right = (rightHeaderText == null || rightHeaderText.isBlank()) ? "" : rightHeaderText;

        float textWidth = (PDType1Font.HELVETICA.getStringWidth(right) / 1000f) * fontSize;
        float rightX = pageWidth - MARGIN - textWidth;
        float rightY = topY - 10f;

        if (!right.isBlank()) {
            cs.beginText();
            cs.newLineAtOffset(rightX, rightY);
            cs.showText(right);
            cs.endText();
        }

        float lineY = topY - HEADER_HEIGHT + 10f;
        cs.setLineWidth(0.6f);
        cs.moveTo(MARGIN, lineY);
        cs.lineTo(pageWidth - MARGIN, lineY);
        cs.stroke();

        return lineY - 25f;
    }

    private float writeHeading(PDPageContentStream cs, String text, float y) throws Exception {
        cs.beginText();
        cs.setFont(PDType1Font.HELVETICA_BOLD, 18);
        cs.newLineAtOffset(MARGIN, y);
        cs.showText(text);
        cs.endText();
        return y - 28;
    }

    private float writeSubheading(PDPageContentStream cs, String text, float y) throws Exception {
        cs.beginText();
        cs.setFont(PDType1Font.HELVETICA_BOLD, 13);
        cs.newLineAtOffset(MARGIN, y);
        cs.showText(text);
        cs.endText();
        return y - 20;
    }

    private float writeKeyValue(PDPageContentStream cs, PDPage page, String key, String value, float y) throws Exception {
        String line = key + ": " + (value == null ? NOT_SPECIFIED : value);
        return writeWrapped(cs, page, line, y);
    }

    private float writeWrapped(PDPageContentStream cs, PDPage page, String text, float y) throws Exception {
        if (text == null || text.isBlank()) return y;

        float pageWidth = page.getMediaBox().getWidth();
        float maxWidth = pageWidth - (2 * MARGIN);

        List<String> lines = wrapText(PDType1Font.HELVETICA, BODY_FONT_SIZE, text, maxWidth);

        for (String line : lines) {
            cs.beginText();
            cs.setFont(PDType1Font.HELVETICA, BODY_FONT_SIZE);
            cs.newLineAtOffset(MARGIN, y);
            cs.showText(line);
            cs.endText();
            y -= LINE_HEIGHT;
        }
        return y;
    }

    private List<String> wrapText(PDType1Font font, float fontSize, String text, float maxWidth) throws Exception {
        List<String> out = new ArrayList<>();
        String[] words = text.replace("\n", " ").split("\\s+");

        StringBuilder line = new StringBuilder();
        for (String w : words) {
            String candidate = line.isEmpty() ? w : line + " " + w;
            float candidateWidth = (font.getStringWidth(candidate) / 1000f) * fontSize;

            if (candidateWidth <= maxWidth) {
                line.setLength(0);
                line.append(candidate);
            } else {
                if (!line.isEmpty()) out.add(line.toString());
                line.setLength(0);
                line.append(w);
            }
        }
        if (!line.isEmpty()) out.add(line.toString());
        return out;
    }

    private float writeClickableLink(
            PDPage page,
            PDPageContentStream cs,
            String label,
            String url,
            float y
    ) throws Exception {
        if (url == null || url.isBlank()) return y;

        String normalized = normalizeUrl(url);

        String prefix = label + ": ";
        String full = prefix + normalized;

        cs.beginText();
        cs.setFont(PDType1Font.HELVETICA, BODY_FONT_SIZE);
        cs.newLineAtOffset(MARGIN, y);
        cs.showText(full);
        cs.endText();

        float prefixWidth = (PDType1Font.HELVETICA.getStringWidth(prefix) / 1000f) * BODY_FONT_SIZE;
        float urlWidth = (PDType1Font.HELVETICA.getStringWidth(normalized) / 1000f) * BODY_FONT_SIZE;

        PDAnnotationLink link = new PDAnnotationLink();
        PDRectangle rect = new PDRectangle(
                MARGIN + prefixWidth,
                y - 2,
                urlWidth,
                14
        );
        link.setRectangle(rect);

        PDActionURI action = new PDActionURI();
        action.setURI(normalized);
        link.setAction(action);

        page.getAnnotations().add(link);

        return y - LINE_HEIGHT;
    }

    private String makeArchitectureScopeReadable(String architectureScope) {
        return switch (architectureScope) {
            case "FULL_STACK" -> "Full Stack";
            case "BACKEND_ONLY" -> "Backend Only";
            case "MOBILE" -> "Mobile";
            case "N/A" -> "N/A";
            default -> humanizeEnum(architectureScope);
        };
    }

    private void addPageNumbers(PDDocument doc) throws Exception {
        int total = doc.getNumberOfPages();
        float fontSize = 9f;

        for (int i = 0; i < total; i++) {
            PDPage page = doc.getPage(i);
            PDRectangle box = page.getMediaBox();

            String text = "Page " + (i + 1) + " of " + total;

            float textWidth = (PDType1Font.HELVETICA.getStringWidth(text) / 1000f) * fontSize;

            float x = (box.getWidth() - textWidth) / 2f;
            float y = 20f;

            try (PDPageContentStream cs = new PDPageContentStream(
                    doc,
                    page,
                    PDPageContentStream.AppendMode.APPEND,
                    true,
                    true
            )) {
                cs.beginText();
                cs.setFont(PDType1Font.HELVETICA, fontSize);
                cs.newLineAtOffset(x, y);
                cs.showText(text);
                cs.endText();
            }
        }
    }

    private boolean needsBudgetTier(DeploymentPreference dp) {
        return dp == DeploymentPreference.PAAS
                || dp == DeploymentPreference.CLOUD_NATIVE
                || dp == DeploymentPreference.SERVERLESS;
    }

    private float writeRankedList(
            PDPageContentStream cs,
            PDPage page,
            String title,
            List<? extends Enum<?>> items,
            float y
    ) throws Exception {
        y = writeWrapped(cs, page, title + ":", y);
        for (int i = 0; i < items.size(); i++) {
            String label = humanizeEnum(items.get(i).name());
            String line = (i + 1) + ". " + label;
            y = writeWrapped(cs, page, line, y);
        }

        return y;
    }
}