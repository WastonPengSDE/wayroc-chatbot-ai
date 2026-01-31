package com.wayroc.wayrocchatbot;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wayroc.wayrocchatbot.manager.AiManager;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;


public class AiManagerPromptTest {
    @Test
    void buildPrompt_shouldContainGoalAndCsvAndMarkers() {
        AiManager manager = new AiManager(null); // 不用 client

        String goal = "Analyze monthly sales trend";
        String csv = "month,sales\nJan,100\nFeb,200";

        String prompt = manager.buildPrompt(goal, csv);

        assertThat(prompt).contains("USER GOAL:");
        assertThat(prompt).contains(goal);

        assertThat(prompt).contains("CSV DATA:");
        assertThat(prompt).contains(csv);

        assertThat(prompt).contains("===ECHARTS_CODE===");
        assertThat(prompt).contains("===SUMMARY===");
    }

    @Tag("integration")
    @Test
    void realModel_shouldReturnEchartsAndSummary() throws Exception{
        AiManager manager = new AiManager();

        String out = manager.generateEchartsOptionJson(
                "Show sales trend",
                "month,sales\n"
                        + "Jan,120\n"
                        + "Feb,150\n"
                        + "Mar,90\n"
                        + "Apr,180\n"
                        + "May,170\n"
                        + "Jun,210"
        );
        System.out.println("===== AI OUTPUT START =====");
        System.out.println(out);
        System.out.println("===== AI OUTPUT END =====");
        // 1️⃣ 能被解析成 JSON
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(out);

        // 2️⃣ 必须包含字段
        assertThat(root.hasNonNull("echartsCode")).isTrue();
        assertThat(root.hasNonNull("summary")).isTrue();

        // 3️⃣ 字段内容不为空（基础质量保障）
        assertThat(root.get("echartsCode").asText()).isNotBlank();
        assertThat(root.get("summary").asText()).isNotBlank();
    }

}
