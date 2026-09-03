package com.techbookstore.app.controller;

import com.techbookstore.app.dto.InventoryReportDto;
import com.techbookstore.app.service.ReportService;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

/**
 * Enhanced Inventory Report Controller Integration Tests - Phase 1
 */
@WebMvcTest(ReportController.class)
@Import(SecurityConfig.class)
public class EnhancedInventoryReportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReportService reportService;

    @MockitoBean
    private com.techbookstore.app.service.AnalyticsService analyticsService;

    @MockitoBean
    private com.techbookstore.app.service.CustomReportService customReportService;

    @MockitoBean
    private com.techbookstore.app.service.BatchProcessingService batchProcessingService;

    private InventoryReportDto mockBasicReport;
    private InventoryReportDto mockEnhancedReport;

    @BeforeEach
    void setUp() {
        // Setup mock basic report data
        mockBasicReport = new InventoryReportDto();
        mockBasicReport.setReportDate(LocalDate.now());
        mockBasicReport.setTotalProducts(25);
        mockBasicReport.setLowStockCount(3);
        mockBasicReport.setOutOfStockCount(1);
        mockBasicReport.setTotalInventoryValue(new BigDecimal("87500.00"));
        mockBasicReport.setAverageTurnoverRate(4.2);
        mockBasicReport.setDeadStockItems(2);
        mockBasicReport.setDeadStockValue(new BigDecimal("7000.00"));
        mockBasicReport.setObsolescenceRiskIndex(25.5);

        // Setup mock items
        List<InventoryReportDto.InventoryItem> mockItems = Arrays.asList(
            new InventoryReportDto.InventoryItem(1L, "Java Programming Basics", "Java", 15, 5, "NORMAL", 
                                               new BigDecimal("3500.00"), new BigDecimal("52500.00")),
            new InventoryReportDto.InventoryItem(2L, "Advanced React Development", "React", 2, 5, "LOW", 
                                               new BigDecimal("4200.00"), new BigDecimal("8400.00"))
        );
        mockBasicReport.setItems(mockItems);

        // Setup mock reorder suggestions
        List<InventoryReportDto.ReorderSuggestion> mockSuggestions = Arrays.asList(
            new InventoryReportDto.ReorderSuggestion(2L, "Advanced React Development", 2, 10, "HIGH", 5),
            new InventoryReportDto.ReorderSuggestion(3L, "Spring Boot Guide", 3, 8, "MEDIUM", 10)
        );
        mockBasicReport.setReorderSuggestions(mockSuggestions);

        // Setup mock turnover summary
        InventoryReportDto.InventoryTurnoverSummary mockTurnoverSummary = 
            new InventoryReportDto.InventoryTurnoverSummary(4.2, "Java", "Database");
        mockBasicReport.setTurnoverSummary(mockTurnoverSummary);

        // Setup enhanced report (filtered)
        mockEnhancedReport = new InventoryReportDto();
        mockEnhancedReport.setReportDate(LocalDate.now());
        mockEnhancedReport.setTotalProducts(15);
        mockEnhancedReport.setLowStockCount(1);
        mockEnhancedReport.setOutOfStockCount(0);
        mockEnhancedReport.setTotalInventoryValue(new BigDecimal("52500.00"));
        mockEnhancedReport.setAverageTurnoverRate(5.1);
        mockEnhancedReport.setDeadStockItems(1);
        mockEnhancedReport.setDeadStockValue(new BigDecimal("3500.00"));
        mockEnhancedReport.setObsolescenceRiskIndex(15.2);
    }

    @Test
    public void testGetBasicInventoryReport() throws Exception {
        // Given
        when(reportService.generateInventoryReport()).thenReturn(mockBasicReport);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/reports/inventory")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.totalProducts", is(25)))
                .andExpect(jsonPath("$.lowStockCount", is(3)))
                .andExpect(jsonPath("$.outOfStockCount", is(1)))
                .andExpect(jsonPath("$.averageTurnoverRate", is(4.2)))
                .andExpect(jsonPath("$.deadStockItems", is(2)))
                .andExpect(jsonPath("$.items", hasSize(2)));
    }

    @Test
    public void testGetEnhancedInventoryReportWithCategoryFilter() throws Exception {
        // Given
        when(reportService.generateInventoryReport(eq("Java"), isNull(), isNull(), isNull(), isNull(), isNull()))
                .thenReturn(mockEnhancedReport);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/reports/inventory/enhanced")
                .param("category", "Java")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.totalProducts", is(15)))
                .andExpect(jsonPath("$.averageTurnoverRate", is(5.1)));
    }

    @Test
    public void testGetEnhancedInventoryReportWithMultipleFilters() throws Exception {
        // Given
        when(reportService.generateInventoryReport(eq("Java"), eq("INTERMEDIATE"), eq("O'Reilly"), 
                                                 eq("NORMAL"), isNull(), eq(2023)))
                .thenReturn(mockEnhancedReport);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/reports/inventory/enhanced")
                .param("category", "Java")
                .param("level", "INTERMEDIATE")
                .param("publisher", "O'Reilly")
                .param("stockStatus", "NORMAL")
                .param("publicationYear", "2023")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalProducts", is(15)))
                .andExpect(jsonPath("$.averageTurnoverRate", is(5.1)));
    }

    @Test
    public void testInventoryReportContainsAllRequiredFields() throws Exception {
        // Given
        when(reportService.generateInventoryReport()).thenReturn(mockBasicReport);

        // When & Then - Verify all Phase 1 enhancement fields are present
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/reports/inventory")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reportDate", notNullValue()))
                .andExpect(jsonPath("$.totalProducts", notNullValue()))
                .andExpect(jsonPath("$.lowStockCount", notNullValue()))
                .andExpect(jsonPath("$.outOfStockCount", notNullValue()))
                .andExpect(jsonPath("$.totalInventoryValue", notNullValue()))
                .andExpect(jsonPath("$.averageTurnoverRate", notNullValue()))
                .andExpect(jsonPath("$.deadStockItems", notNullValue()))
                .andExpect(jsonPath("$.deadStockValue", notNullValue()))
                .andExpect(jsonPath("$.obsolescenceRiskIndex", notNullValue()))
                .andExpect(jsonPath("$.items", notNullValue()))
                .andExpect(jsonPath("$.reorderSuggestions", notNullValue()))
                .andExpect(jsonPath("$.turnoverSummary", notNullValue()));
    }
}