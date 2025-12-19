package com.server.controller;

import com.server.comon.Result;
import com.server.service.DeepSeekAIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ai")
public class AiController {

    @Autowired
    private DeepSeekAIService aiService;

    @GetMapping("/chat")
    public Result<String> chat(@RequestParam String query) {
        String reply = aiService.chatWithWaiter(query);
        return Result.success(reply);
    }
}