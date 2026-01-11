package com.wayroc.wayrocchatbot.manager;
import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.responses.Response;
import com.openai.models.responses.ResponseCreateParams;

public class AiManager {

    private OpenAIClient openAIClient;

    public AiManager() {
        this.openAIClient = OpenAIOkHttpClient.fromEnv();
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
        String out = resp.output().toString();
        return out;
    }

    private String buildPrompt(String goal, String csv) {
        return "tbd";
    }
}
