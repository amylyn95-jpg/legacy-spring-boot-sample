package com.techbookstore.app.controller;

import com.techbookstore.app.dto.*;
import com.techbookstore.app.service.AnalyticsService;
import com.techbookstore.app.service.ReportService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import com.techbookstore.app.config.SecurityConfig;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.math.BigDecimal;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest({ReportController.class, com.techbookstore.app.exception.GlobalExceptionHandler.class})
@Import(SecurityConfig.class)
public class AnalyticsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AnalyticsService analyticsService;

    @MockitoBean 
    private ReportService reportService;

    @MockitoBean
    private com.techbookstore.app.service.CustomReportService customReportService;

    @MockitoBean
    private com.techbookstore.app.service.BatchProcessingService batchProcessingService;

    @Test
    public void testGetSalesAnalysis() throws Exception {
        // Create mock sales analysis data
        SalesAnalysisDto mockSalesAnalysis = new SalesAnalysisDto(
            LocalDate.now().minusMonths(1),
            LocalDate.now(),
            new BigDecimal("45000.00"),
            125,
            new BigDecimal("360.00")
        );
        
        // Create mock tech category sales
        SalesAnalysisDto.TechCategorySales javaSales = new SalesAnalysisDto.TechCategorySales(
            "JAVA", "Java Programming", new BigDecimal("15000.00"), 45, new BigDecimal("33.3")
        );
        mockSalesAnalysis.setTechCategorySales(Arrays.asList(javaSales));
        
        when(analyticsService.generateSalesAnalysis(any(LocalDate.class), any(LocalDate.class), any(), any()))
            .thenReturn(mockSalesAnalysis);

        mockMvc.perform(get("/api/v1/reports/sales/analysis")
                .param("startDate", "2024-01-01")
                .param("endDate", "2024-01-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalRevenue").value(45000.00))
                .andExpect(jsonPath("$.totalOrders").value(125))
                .andExpect(jsonPath("$.techCategorySales[0].categoryCode").value("JAVA"));
    }

    @Test
    public void testGetInventoryAnalysis() throws Exception {
        // Create mock inventory analysis data
        InventoryAnalysisDto mockInventoryAnalysis = new InventoryAnalysisDto(
            LocalDate.now(),
            150,
            new BigDecimal("125000.00"),
            new BigDecimal("4.2")
        );
        mockInventoryAnalysis.setDeadStockItems(15);
        mockInventoryAnalysis.setDeadStockValue(new BigDecimal("8500.00"));
        
        when(analyticsService.generateInventoryAnalysis(any(), any()))
            .thenReturn(mockInventoryAnalysis);

        mockMvc.perform(get("/api/v1/reports/inventory/analysis"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalItems").value(150))
                .andExpect(jsonPath("$.totalInventoryValue").value(125000.00))
                .andExpect(jsonPath("$.deadStockItems").value(15));
    }

    @Test
    public void testGetDemandPredictions() throws Exception {
        // Create mock prediction data
        PredictionDto mockPrediction = new PredictionDto(
            LocalDate.now(),
            "DEMAND",
            "MEDIUM_TERM",
            new BigDecimal("78.5")
        );
        mockPrediction.setAlgorithm("SEASONAL");
        
        when(analyticsService.predictDemand(any(), any(), any()))
            .thenReturn(mockPrediction);

        mockMvc.perform(get("/api/v1/reports/predictions/demand")
                .param("timeHorizon", "MEDIUM_TERM")
                .param("algorithm", "SEASONAL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.predictionType").value("DEMAND"))
                .andExpect(jsonPath("$.algorithm").value("SEASONAL"))
                .andExpect(jsonPath("$.accuracy").value(78.5));
    }

    @Test
    public void testGetOrderSuggestions() throws Exception {
        // Create mock order suggestions
        OrderSuggestionDto mockSuggestions = new OrderSuggestionDto(
            LocalDate.now(),
            "REORDER",
            5,
            new BigDecimal("1350.00")
        );
        mockSuggestions.setPriority("MEDIUM");
        
        when(analyticsService.generateOrderSuggestions(any(), any(), any()))
            .thenReturn(mockSuggestions);

        mockMvc.perform(get("/api/v1/reports/suggestions/orders")
                .param("suggestionType", "REORDER")
                .param("priority", "MEDIUM"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.suggestionType").value("REORDER"))
                .andExpect(jsonPath("$.totalSuggestions").value(5))
                .andExpect(jsonPath("$.priority").value("MEDIUM"));
    }

    @Test
    public void testGetTechCategoryTrends() throws Exception {
        // Create mock tech category analysis
        TechCategoryAnalysisDto mockAnalysis = new TechCategoryAnalysisDto(
            LocalDate.now(),
            "JAVA",
            "Java Programming"
        );
        
        when(analyticsService.analyzeTechTrends(any()))
            .thenReturn(mockAnalysis);

        mockMvc.perform(get("/api/v1/reports/trends/tech-categories")
                .param("categoryCode", "JAVA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categoryCode").value("JAVA"))
                .andExpect(jsonPath("$.categoryName").value("Java Programming"));
    }

    @Test
    public void testGetProfitabilityAnalysis() throws Exception {
        // Create mock profitability data
        SalesAnalysisDto.ProfitabilityItem mockItem = new SalesAnalysisDto.ProfitabilityItem(
            "CATEGORY",
            "Java Programming",
            new BigDecimal("15000.00"),
            new BigDecimal("9000.00"),
            new BigDecimal("6000.00"),
            new BigDecimal("40.0")
        );
        
        when(analyticsService.calculateProfitability(any(LocalDate.class), any(LocalDate.class), any()))
            .thenReturn(Arrays.asList(mockItem));

        mockMvc.perform(get("/api/v1/reports/profitability")
                .param("startDate", "2024-01-01")
                .param("endDate", "2024-01-31")
                .param("analysisLevel", "CATEGORY"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].itemType").value("CATEGORY"))
                .andExpect(jsonPath("$[0].itemName").value("Java Programming"))
                .andExpect(jsonPath("$[0].profitMargin").value(40.0));
    }

    @Test
    public void testValidationErrors() throws Exception {
        // Test missing required parameters
        mockMvc.perform(get("/api/v1/reports/sales/analysis"))
                .andExpect(status().isBadRequest());
        
        // Test invalid date range
        mockMvc.perform(get("/api/v1/reports/sales/analysis")
                .param("startDate", "2024-12-31")
                .param("endDate", "2024-01-01"))
                .andExpect(status().isBadRequest());
    }
}