package com.techbookstore.app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techbookstore.app.dto.CustomReportRequest;
import com.techbookstore.app.dto.ReportTemplateDto;
import com.techbookstore.app.service.CustomReportService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import com.techbookstore.app.config.SecurityConfig;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for Phase 1-4 Report functionality.
 * レポート機能のテストクラス
 */
@WebMvcTest(ReportController.class)
@Import(SecurityConfig.class)
public class ReportControllerPhase14Test {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private com.techbookstore.app.service.ReportService reportService;

    @MockitoBean
    private com.techbookstore.app.service.AnalyticsService analyticsService;

    @MockitoBean
    private CustomReportService customReportService;

    @MockitoBean
    private com.techbookstore.app.service.BatchProcessingService batchProcessingService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testGetDashboardKpis() throws Exception {
        // Phase 1: Test basic dashboard KPI endpoint
        // Mock the service response
        com.techbookstore.app.dto.DashboardKpiDto mockKpis = 
            new com.techbookstore.app.dto.DashboardKpiDto(LocalDate.now());
        when(reportService.generateDashboardKpis()).thenReturn(mockKpis);
        
        mockMvc.perform(get("/api/v1/reports/dashboard/kpis"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void testGetDashboardTrends() throws Exception {
        // Phase 1: Test dashboard trends endpoint
        // Mock the service response
        com.techbookstore.app.dto.DashboardKpiDto mockTrends = 
            new com.techbookstore.app.dto.DashboardKpiDto(LocalDate.now());
        when(reportService.generateDashboardTrends()).thenReturn(mockTrends);
        
        mockMvc.perform(get("/api/v1/reports/dashboard/trends"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void testGetDashboardAlerts() throws Exception {
        // Phase 1: Test dashboard alerts endpoint
        mockMvc.perform(get("/api/v1/reports/dashboard/alerts"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void testGetEnhancedSalesAnalysis() throws Exception {
        // Phase 2: Test enhanced sales analysis
        mockMvc.perform(get("/api/v1/reports/sales/enhanced-analysis")
                .param("startDate", "2024-01-01")
                .param("endDate", "2024-01-31")
                .param("techLevel", "INTERMEDIATE"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void testGetIntelligentInventoryOptimization() throws Exception {
        // Phase 2: Test intelligent inventory optimization
        mockMvc.perform(get("/api/v1/reports/inventory/intelligent-optimization")
                .param("categoryCode", "AI_ML")
                .param("riskLevel", "MEDIUM"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void testGetCustomerTechJourney() throws Exception {
        // Phase 3: Test customer tech journey analysis
        mockMvc.perform(get("/api/v1/reports/customers/tech-journey")
                .param("customerId", "123")
                .param("analysisType", "JOURNEY"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void testGetComprehensiveTechTrendAnalysis() throws Exception {
        // Phase 3: Test comprehensive tech trend analysis
        mockMvc.perform(get("/api/v1/reports/tech-trends/comprehensive-analysis")
                .param("analysisDepth", "DETAILED")
                .param("predictionHorizon", "90"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void testCreateCustomReport() throws Exception {
        // Phase 4: Test custom report creation
        CustomReportRequest request = new CustomReportRequest();
        request.setReportType("SALES_BY_TECH_CATEGORY");
        request.setReportName("Test Sales Report");
        request.setCreatedBy("test_user");
        request.setStartDate(LocalDate.of(2024, 1, 1));
        request.setEndDate(LocalDate.of(2024, 1, 31));

        // Mock the service response
        com.techbookstore.app.dto.CustomReportDto mockReport = 
            new com.techbookstore.app.dto.CustomReportDto("Test Sales Report", 
                "SALES_BY_TECH_CATEGORY", "test_user");
        mockReport.setReportId("test-report-id");
        when(customReportService.createCustomReport(any(CustomReportRequest.class)))
            .thenReturn(mockReport);

        mockMvc.perform(post("/api/v1/reports/custom-reports")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void testGetReportTemplates() throws Exception {
        // Phase 4: Test getting available report templates
        ReportTemplateDto template = new ReportTemplateDto(
                "SALES_BY_TECH_CATEGORY",
                "技術カテゴリ別売上分析",
                "技術カテゴリごとの売上実績と成長率を分析",
                Arrays.asList("startDate", "endDate", "techCategories"),
                Arrays.asList("CHART", "TABLE", "SUMMARY")
        );

        when(customReportService.getAvailableTemplates())
                .thenReturn(Arrays.asList(template));

        mockMvc.perform(get("/api/v1/reports/templates"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].templateName").value("技術カテゴリ別売上分析"));
    }

    @Test
    public void testGenerateDrillDownReport() throws Exception {
        // Phase 4: Test drill-down report generation
        Map<String, Object> filters = new HashMap<>();
        filters.put("category", "AI_ML");
        filters.put("timeRange", "6months");

        // Mock the service response
        Map<String, Object> mockFilters = new HashMap<>();
        Map<String, Object> mockData = new HashMap<>();
        mockData.put("result", "drill down data");
        com.techbookstore.app.dto.DrillDownReportDto mockDrillDown = 
            new com.techbookstore.app.dto.DrillDownReportDto("sales_trend", 
                "tech_category", mockFilters, mockData);
        when(customReportService.generateDrillDownReport(anyString(), anyString(), any(Map.class)))
            .thenReturn(mockDrillDown);

        mockMvc.perform(post("/api/v1/reports/drill-down")
                .param("reportType", "sales_trend")
                .param("drillDownDimension", "tech_category")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(filters)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void testRunManualBatch() throws Exception {
        // Phase 4: Test manual batch execution
        mockMvc.perform(post("/api/v1/reports/admin/batch/daily"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("SUCCESS"));
    }

    @Test
    public void testExportReport() throws Exception {
        // Phase 4: Test report export functionality
        when(customReportService.exportReport(anyString(), anyString()))
                .thenReturn(createMockExportDto());

        mockMvc.perform(get("/api/v1/reports/export/test-report-id")
                .param("format", "PDF"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/pdf"));
    }

    @Test
    public void testInvalidDateRange() throws Exception {
        // Test validation for invalid date range
        mockMvc.perform(get("/api/v1/reports/sales/enhanced-analysis")
                .param("startDate", "2024-01-31")
                .param("endDate", "2024-01-01")) // End before start
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testMissingRequiredParameters() throws Exception {
        // Test validation for missing required parameters
        CustomReportRequest request = new CustomReportRequest();
        // Missing reportType which is required

        mockMvc.perform(post("/api/v1/reports/custom-reports")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    private com.techbookstore.app.dto.ReportExportDto createMockExportDto() {
        com.techbookstore.app.dto.ReportExportDto export = 
            new com.techbookstore.app.dto.ReportExportDto("test-report-id", "PDF");
        export.setFileName("test_report.pdf");
        export.setMimeType("application/pdf");
        export.setFileData("Mock PDF content".getBytes());
        export.setStatus("COMPLETED");
        return export;
    }
}