package com.techbookstore.app.controller;

import com.techbookstore.app.service.TechTrendAnalysisService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import com.techbookstore.app.config.SecurityConfig;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.*;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for TechTrendController
 * 技術トレンドコントローラのテストクラス
 */
@WebMvcTest(TechTrendController.class)
@Import(SecurityConfig.class)
public class TechTrendControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TechTrendAnalysisService techTrendAnalysisService;

    @BeforeEach
    void setUp() {
        // Mock data setup
        Map<String, Object> mockReport = new HashMap<>();
        Map<String, Object> marketOverview = new HashMap<>();
        marketOverview.put("totalMarketRevenue", 100000);
        marketOverview.put("averageGrowthRate", 12.5);
        marketOverview.put("totalCategories", 6);
        mockReport.put("marketOverview", marketOverview);
        
        when(techTrendAnalysisService.generateTechTrendReport()).thenReturn(mockReport);
        
        // Mock emerging technologies
        List<Map<String, Object>> emergingTech = new ArrayList<>();
        Map<String, Object> tech = new HashMap<>();
        tech.put("categoryName", "React");
        tech.put("emergingScore", 85.0);
        tech.put("growthRate", 15.5);
        emergingTech.add(tech);
        
        when(techTrendAnalysisService.findEmergingTechnologies()).thenReturn(emergingTech);
        
        // Mock correlations
        Map<String, Object> correlations = new HashMap<>();
        List<Map<String, Object>> relationships = new ArrayList<>();
        Map<String, Object> relationship = new HashMap<>();
        relationship.put("primaryTech", "React");
        relationship.put("relatedTech", "JavaScript");
        relationship.put("correlationStrength", 0.75);
        relationships.add(relationship);
        correlations.put("relationships", relationships);
        
        when(techTrendAnalysisService.generateTechnologyCorrelations()).thenReturn(correlations);
    }

    @Test
    void shouldGetTechTrendReport() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/tech-trends/report")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.marketOverview.totalMarketRevenue").value(100000))
                .andExpect(jsonPath("$.marketOverview.averageGrowthRate").value(12.5))
                .andExpect(jsonPath("$.marketOverview.totalCategories").value(6));
    }

    @Test
    void shouldGetEmergingTechnologies() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/tech-trends/emerging")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].categoryName").value("React"))
                .andExpect(jsonPath("$[0].emergingScore").value(85.0))
                .andExpect(jsonPath("$[0].growthRate").value(15.5));
    }

    @Test
    void shouldGetTechnologyCorrelations() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/tech-trends/correlations")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.relationships[0].primaryTech").value("React"))
                .andExpect(jsonPath("$.relationships[0].relatedTech").value("JavaScript"))
                .andExpect(jsonPath("$.relationships[0].correlationStrength").value(0.75));
    }

    @Test
    void shouldGetHealthCheck() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/tech-trends/health")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.service").value("TechTrendAnalysisService"));
    }

    @Test
    void shouldHandleNotFoundCategory() throws Exception {
        when(techTrendAnalysisService.analyzeTechCategoryTrends("NONEXISTENT"))
            .thenThrow(new IllegalArgumentException("Tech category not found: NONEXISTENT"));
        
        mockMvc.perform(MockMvcRequestBuilders.get("/api/tech-trends/categories/NONEXISTENT/analysis")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}