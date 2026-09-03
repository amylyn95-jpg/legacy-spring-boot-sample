package com.techbookstore.app.controller;

import com.techbookstore.app.dto.TechCategoryAnalysisDto;
import com.techbookstore.app.exception.TechCategoryNotFoundException;
import com.techbookstore.app.service.TechTrendAnalysisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * REST Controller for Tech Trend Analysis
 * 技術トレンド分析のRESTコントローラ
 */
@RestController
@RequestMapping("/api/v1/tech-trends")
@CrossOrigin(origins = "${app.cors.allowed-origins:http://localhost:3000}")
public class TechTrendController {

    private static final Logger logger = LoggerFactory.getLogger(TechTrendController.class);

    private final TechTrendAnalysisService techTrendAnalysisService;

    public TechTrendController(TechTrendAnalysisService techTrendAnalysisService) {
        this.techTrendAnalysisService = techTrendAnalysisService;
    }

    /**
     * Get comprehensive tech trend report
     * 技術トレンド総合レポート
     */
    @GetMapping("/report")
    public ResponseEntity<Map<String, Object>> getTechTrendReport() {
        logger.info("Getting comprehensive tech trend report");
        Map<String, Object> report = techTrendAnalysisService.generateTechTrendReport();
        return ResponseEntity.ok(report);
    }

    /**
     * Get detailed analysis for specific tech category
     * 特定技術カテゴリの詳細分析
     */
    @GetMapping("/categories/{categoryCode}/analysis")
    public ResponseEntity<TechCategoryAnalysisDto> getCategoryAnalysis(@PathVariable String categoryCode) {
        logger.info("Getting analysis for tech category: {}", categoryCode);
        try {
            TechCategoryAnalysisDto analysis = techTrendAnalysisService.analyzeTechCategoryTrends(categoryCode);
            return ResponseEntity.ok(analysis);
        } catch (IllegalArgumentException e) {
            throw new TechCategoryNotFoundException(categoryCode);
        }
    }

    /**
     * Get emerging technologies
     * 新興技術検出結果
     */
    @GetMapping("/emerging")
    public ResponseEntity<List<Map<String, Object>>> getEmergingTechnologies() {
        logger.info("Getting emerging technologies");
        List<Map<String, Object>> emergingTech = techTrendAnalysisService.findEmergingTechnologies();
        return ResponseEntity.ok(emergingTech);
    }

    /**
     * Get technology correlation matrix
     * 技術関連性マトリックス
     */
    @GetMapping("/correlations")
    public ResponseEntity<Map<String, Object>> getTechnologyCorrelations(
            @RequestParam(value = "minCorrelation", defaultValue = "0.3") Double minCorrelation) {
        logger.info("Getting technology correlations with minimum correlation: {}", minCorrelation);
        Map<String, Object> correlations = techTrendAnalysisService.generateTechnologyCorrelations();
        return ResponseEntity.ok(correlations);
    }

    /**
     * Get technology lifecycle distribution
     * 技術ライフサイクル分布
     */
    @GetMapping("/lifecycle-distribution")
    public ResponseEntity<Map<String, Object>> getLifecycleDistribution() {
        logger.info("Getting technology lifecycle distribution");
        Map<String, Object> distribution = techTrendAnalysisService.generateLifecycleDistribution();
        return ResponseEntity.ok(distribution);
    }

    /**
     * Get investment recommendations
     * 技術投資推奨
     */
    @GetMapping("/investment-recommendations")
    public ResponseEntity<List<Map<String, Object>>> getInvestmentRecommendations(
            @RequestParam(value = "limit", defaultValue = "10") Integer limit) {
        logger.info("Getting investment recommendations with limit: {}", limit);
        List<Map<String, Object>> recommendations = techTrendAnalysisService.generateInvestmentRecommendations();
        
        // Apply limit if specified
        if (limit != null && limit > 0 && recommendations.size() > limit) {
            recommendations = recommendations.subList(0, limit);
        }
        
        return ResponseEntity.ok(recommendations);
    }

    /**
     * Health check endpoint for tech trend service
     * 技術トレンドサービスのヘルスチェック
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "OK");
        health.put("service", "TechTrendAnalysisService");
        health.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(health);
    }
}