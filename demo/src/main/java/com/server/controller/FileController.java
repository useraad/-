package com.server.controller;

import com.server.comon.Result;
import com.server.service.DeepSeekAIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/file")
public class FileController {

    @Autowired
    private DeepSeekAIService aiService;

    // 图片存储路径
    private static final String UPLOAD_DIR = "D:/waimai/images/";

    // ================= 文件上传 =================
    @PostMapping("/upload")
    public Result<String> upload(MultipartFile file) throws IOException {
        if (file.isEmpty()) return Result.error("文件为空");

        File dir = new File(UPLOAD_DIR);
        if (!dir.exists()) dir.mkdirs();

        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        String newFileName = UUID.randomUUID() + suffix;

        file.transferTo(new File(UPLOAD_DIR + newFileName));

        String url = "http://localhost:8080/images/" + newFileName;
        return Result.success(url);
    }

    // ================= 菜品 AI 分类 =================
    @GetMapping("/ai-category")
    public Result<String> aiCategory(@RequestParam String name) {
        String category = aiService.classifyDish(name);
        return Result.success(category);
    }
}
