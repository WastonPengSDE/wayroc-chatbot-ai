package com.wayroc.wayrocchatbot.manager;
import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.responses.Response;
import com.openai.models.responses.ResponseCreateParams;
import org.springframework.stereotype.Service;


@Service
public class AiManager {

    private OpenAIClient openAIClient;

    public AiManager() {
        this.openAIClient = OpenAIOkHttpClient.fromEnv();
    }

    //For Test
    public AiManager(OpenAIClient client) {
        this.openAIClient = client;
    }

    public String generateEchartsOptionJson(String goal, String csv) {
        String prompt = buildPrompt(goal, csv);

        ResponseCreateParams params = ResponseCreateParams.builder()
                .model("gpt-5.2")        // 你也可以换成你项目配置的模型
                .input(prompt)
                .temperature(0.2)        // 更稳定
                .build();

        Response resp = openAIClient.responses().create(params);

        // outputText() 是官方示例用法
        // 這裏看下要不要修改
        String out = resp.output()
                .get(0)
                .message().get()
                .content()
                .get(0)
                .outputText().get()
                .text();
        return out;
    }

    public String buildPrompt(String goal, String csv) {
        return String.format("""
            You are a data visualization expert.
            
            Your task:
            - Generate ONE valid ECharts option
            - Generate ONE concise natural language summary interpreting the data
            
            STRICT OUTPUT JSON FORMAT (DO NOT VIOLATE):
            {
              "echartsCode": "<A JSON string representing the ECharts option object>",
              "summary": "<2-4 sentence natural language summary>"
            }
            
            ECHARTS REQUIREMENTS:
            - The ECharts option MUST be JSON-serializable
            - Do NOT include JavaScript functions, variable declarations, or executable code
            - Do NOT include var, let, const, or semicolons
            - Use double quotes only
            - The content of echartsCode MUST be valid JSON text that can be parsed by JSON.parse()
            - The parsed result MUST be directly usable in echarts.setOption(...)
            - Do NOT use Markdown
            - Do NOT include HTML, React, imports, or explanations
            - Choose the most appropriate chart type automatically
            - Include axis labels, tooltips, and legends if applicable
            
            SUMMARY REQUIREMENTS:
            - 2–4 sentences
            - Focus on key trends, comparisons, or anomalies
            - Do NOT describe the chart itself, only the data insights
            
            USER GOAL:
            %s
            
            CSV DATA:
            %s
            """, goal, csv);
        }
}

