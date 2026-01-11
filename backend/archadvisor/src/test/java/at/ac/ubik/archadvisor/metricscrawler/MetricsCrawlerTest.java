package at.ac.ubik.archadvisor.metricscrawler;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

public class MetricsCrawlerTest {

    @Test
    void loadAllMetrics_returnsNonEmptyMap() {
        // نستدعي الميثود من MetricsCrawler
        Map<String, MetricsCrawler.TechnologyMetrics> all =
                MetricsCrawler.loadAllMetrics();

        // الماب لا تكون null
        assertNotNull(all, "Map should not be null");

        // ويكون فيها بيانات
        assertFalse(all.isEmpty(), "There should be at least one technology");

        // اختيارياً: نتأكد أن واحد من التكنولوجيز المعروفة موجود
        assertTrue(
            all.containsKey("react")
                || all.containsKey("angular")
                || all.containsKey("vue"),
            "Known technologies should be present"
        );
    }

    @Test
    void getMetricsFor_matchesDataFromMap() {
        Map<String, MetricsCrawler.TechnologyMetrics> all =
                MetricsCrawler.loadAllMetrics();

        // نختار تكنولوجيا نعرف أنها في الـ CSV (مثلاً react)
        String tech = "react";

        MetricsCrawler.TechnologyMetrics fromMap   = all.get(tech);
        MetricsCrawler.TechnologyMetrics fromHelper = MetricsCrawler.getMetricsFor(tech);

        if (fromMap == null) {
            // لو مش موجودة في الماب، المفروض برضه الهيلبر يرجع null
            assertNull(fromHelper);
        } else {
            // لو موجودة، لازم القيم تكون نفسها
            assertNotNull(fromHelper);
            assertEquals(fromMap.githubStars,      fromHelper.githubStars);
            assertEquals(fromMap.performanceScore, fromHelper.performanceScore);
            assertEquals(fromMap.securityScore,    fromHelper.securityScore);
        }

    }
}
