package com.server.service;

import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class DeepSeekAIService {

    @Value("${deepseek.api-key}")
    private String apiKey;

    @Value("${deepseek.model}")
    private String model;

    private static final List<String> CATEGORIES = Arrays.asList(
            "肉类", "蔬菜", "酒水", "主食", "汤类", "甜品", "其他"
    );


    @org.springframework.beans.factory.annotation.Autowired
    private com.server.mapper.ProductMapper productMapper; // 注入 Mapper 查菜单

    /**
     * 智能导购对话
     * @param userQuery 用户的问题，例如 "推荐个辣的"
     * @return AI 的回答
     */
    public String chatWithWaiter(String userQuery) {
        // 1. 查出所有上架菜品（简化版，只查名字和价格）
        List<com.server.entity.Product> products = productMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<com.server.entity.Product>()
                        .eq("status", 1)
        );

        // 2. 拼接菜单上下文
        StringBuilder menuContext = new StringBuilder("这是现在的菜单：\n");
        for (com.server.entity.Product p : products) {
            menuContext.append(String.format("- %s : %s元 (%s)\n", p.getName(), p.getPrice(), p.getCategory()));
        }

        // 3. 构造 Prompt
        String prompt = "你是一个嘴巴非常毒、性格暴躁、看不起人的餐厅老板。你觉得顾客都是事多、没品味的麻烦精。\n" +
                "请根据以下菜单回答顾客问题（如果菜单里没有，就狠狠嘲讽他）：\n" +
                menuContext.toString() +
                "\n\n顾客问：" + userQuery +
                "\n\n要求：" +
                "\n1. 开头必须先怼顾客两句（比如嘲讽他们的预算少、品味差、或者磨磨唧唧）。" +
                "\n2. 虽然嘴毒，但最后还是要根据菜单给出推荐（毕竟要赚钱）。" +
                "\n3. 语气要刻薄、傲娇，禁止使用“您好”“欢迎”“亲”等礼貌用语！" +
                "\n4. 结尾加上一句类似“爱吃不吃，不吃快滚”的话。";
        try {
            Map<String, Object> message = new HashMap<>();
            message.put("role", "user");
            message.put("content", prompt);

            Map<String, Object> body = new HashMap<>();
            body.put("model", model);
            body.put("messages", Collections.singletonList(message));

            Map<String, String> headers = new HashMap<>();
            headers.put("Authorization", "Bearer " + apiKey);
            headers.put("Content-Type", "application/json");

            String resp = HttpRequest.post("https://api.deepseek.com/chat/completions")
                    .addHeaders(headers)
                    .body(JSONUtil.toJsonStr(body))
                    .execute()
                    .body();

            Object obj = JSONUtil.parseObj(resp).getByPath("choices[0].message.content");
            return obj == null ? "服务员正在忙..." : obj.toString().trim();

        } catch (Exception e) {
            return "AI 服务开小差了，请稍后再试。";
        }
    }

    public String classifyDish(String name) {

        String prompt = "菜品名：" + name +
                "。\n请从【肉类,蔬菜,酒水,主食,汤类,甜品,其他】中选最符合的一个，只返回分类名，不要解释。";

        try {
            // 构造 messages
            Map<String, Object> message = new HashMap<>();
            message.put("role", "user");
            message.put("content", prompt);

            Map<String, Object> body = new HashMap<>();
            body.put("model", model);
            body.put("messages", Collections.singletonList(message));

            Map<String, String> headers = new HashMap<>();
            headers.put("Authorization", "Bearer " + apiKey);
            headers.put("Content-Type", "application/json");

            // 调用 DeepSeek
            String resp = HttpRequest.post("https://api.deepseek.com/chat/completions")
                    .addHeaders(headers)
                    .body(JSONUtil.toJsonStr(body))
                    .execute()
                    .body();

            // 解析 DeepSeek 内容
            Object obj = JSONUtil.parseObj(resp)
                    .getByPath("choices[0].message.content");

            String category = obj == null ? "" : obj.toString().trim();

            // 校验合法性
            return CATEGORIES.contains(category) ? category : "其他";

        } catch (Exception e) {
            return "其他";
        }
    }
}
