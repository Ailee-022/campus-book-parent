package com.campus.ai_service.controller;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.campus.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/ai")
public class AiController {

    @Autowired
    private RestTemplate restTemplate;

    private static final String API_KEY = "你的智谱APIKey";

    // ⚠️ 智谱AI的API地址
    private static final String API_URL = "https://open.bigmodel.cn/api/paas/v4/chat/completions";

    @PostMapping("/chat")
    public Result<String> chat(@RequestParam String message) {
        // 1. 调用 8082 图书服务查库（简化版 RAG）
        String bookUrl = "http://localhost:8082/book/list?page=1&size=5";
        Result bookResult = restTemplate.getForObject(bookUrl, Result.class);

        String context = bookResult != null ? JSONUtil.toJsonStr(bookResult.getData()) : "暂无图书数据";

        // 2. 拼接提示词
        String systemPrompt = "你是一个校园二手书助手。请根据以下数据库图书信息回答用户问题。如果数据库没有相关信息，请如实告知。\n数据库信息：" + context;

        // 3. 组装请求体的 JSON
        JSONObject requestBody = new JSONObject();
        requestBody.set("model", "glm-4-flash"); // 智谱免费模型

        JSONArray messages = new JSONArray();
        messages.add(new JSONObject().set("role", "system").set("content", systemPrompt));
        messages.add(new JSONObject().set("role", "user").set("content", message));
        requestBody.set("messages", messages);

        // 4. 发送 POST 请求给智谱大模型
        String responseStr = HttpRequest.post(API_URL)
                .header("Authorization", "Bearer " + API_KEY)
                .header("Content-Type", "application/json")
                .body(requestBody.toString())
                .execute()
                .body();

        // 5. 解析返回的结果
        try {
            JSONObject responseJson = JSONUtil.parseObj(responseStr);
            String aiReply = responseJson.getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getStr("content");
            return Result.success(aiReply);
        } catch (Exception e) {
            return Result.error("AI 调用失败，请检查 API Key 或模型名称是否正确");
        }
    }
}