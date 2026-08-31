-- TechBookStore Sample Data
-- This file provides initial data for the TechBookStore application
-- Works alongside DataInitializer.java

-- System user will be created programmatically by AdvancedInventoryService

-- Publishers
INSERT INTO publishers (id, name, address, contact, email, phone) VALUES 
(1, '技術評論社', '東京都新宿区新宿1-1-1', '技術評論社編集部', 'contact@gihyo.co.jp', '03-1234-5678'),
(2, 'オライリー・ジャパン', '東京都渋谷区渋谷2-2-2', 'オライリー編集部', 'info@oreilly.co.jp', '03-2345-6789'),
(3, '翔泳社', '東京都新宿区四谷1-6-2', '翔泳社編集部', 'contact@shoeisha.co.jp', '03-3456-7890'),
(4, 'インプレス', '東京都千代田区神田錦町3-1', 'インプレス編集部', 'info@impress.co.jp', '03-4567-8901');

-- Authors  
INSERT INTO authors (id, name, name_kana, profile) VALUES
(1, '山田太郎', 'ヤマダタロウ', 'Java専門のソフトウェアエンジニア。10年以上の開発経験を持つ。'),
(2, '佐藤花子', 'サトウハナコ', 'フロントエンド開発のエキスパート。React/Vue.jsの著書多数。'),
(3, '田中一郎', 'タナカイチロウ', 'DevOps エンジニア。クラウドインフラの設計・運用を専門とする。'),
(4, '鈴木美咲', 'スズキミサキ', 'データサイエンティスト。機械学習・AI分野での実務経験豊富。');

-- Tech Categories
INSERT INTO tech_categories (id, category_name, category_code, category_level, display_order, parent_id) VALUES
(1, 'プログラミング言語', 'PROGRAMMING', 1, 1, NULL),
(2, 'Web開発', 'WEB_DEV', 1, 2, NULL),
(3, 'データベース', 'DATABASE', 1, 3, NULL),
(4, 'クラウド・インフラ', 'CLOUD_INFRA', 1, 4, NULL),
(5, 'Java', 'JAVA', 2, 1, 1),
(6, 'Python', 'PYTHON', 2, 2, 1),
(7, 'JavaScript', 'JAVASCRIPT', 2, 3, 1),
(8, 'React', 'REACT', 2, 1, 2),
(9, 'Spring', 'SPRING', 2, 2, 2),
(10, 'MySQL', 'MYSQL', 2, 1, 3);

-- Books (Only if DataInitializer hasn't run)
INSERT INTO books (id, isbn13, title, title_en, publisher_id, publication_date, edition, list_price, selling_price, pages, level, version_info, sample_code_url) 
SELECT * FROM (VALUES
(1, '9784774187123', 'Javaプログラミング入門', 'Java Programming Introduction', 1, '2023-01-15', 1, 3200.00, 2880.00, 420, 'BEGINNER', NULL, NULL),
(2, '9784873119465', 'Spring Boot実践ガイド', 'Spring Boot Practical Guide', 2, '2023-03-10', 2, 4500.00, 4050.00, 580, 'INTERMEDIATE', NULL, NULL),
(3, '9784798167312', 'React開発現場のテクニック', 'React Development Techniques', 1, '2023-02-20', 1, 3800.00, 3420.00, 350, 'ADVANCED', NULL, NULL),
(4, '9784295013495', 'Python機械学習プログラミング', 'Python Machine Learning Programming', 3, '2023-04-01', 3, 4200.00, 3780.00, 520, 'ADVANCED', '第3版', 'https://github.com/example/python-ml'),
(5, '9784822295301', 'AWSクラウド設計・構築ガイド', 'AWS Cloud Design & Build Guide', 4, '2023-05-15', 1, 3600.00, 3240.00, 480, 'INTERMEDIATE', NULL, NULL)
) AS new_books(id, isbn13, title, title_en, publisher_id, publication_date, edition, list_price, selling_price, pages, level, version_info, sample_code_url)
WHERE NOT EXISTS (SELECT 1 FROM books);

-- Book Authors (Only if books exist)
INSERT INTO book_authors (book_id, author_id, author_type, display_order)
SELECT * FROM (VALUES
(1, 1, 'AUTHOR', 1),
(2, 1, 'AUTHOR', 1),
(3, 2, 'AUTHOR', 1),
(4, 4, 'AUTHOR', 1),
(5, 3, 'AUTHOR', 1)
) AS new_book_authors(book_id, author_id, author_type, display_order)
WHERE EXISTS (SELECT 1 FROM books WHERE id = new_book_authors.book_id);

-- Book Categories (Only if books exist)
INSERT INTO book_categories (book_id, category_id, is_primary)
SELECT * FROM (VALUES
(1, 5, true),   -- Java book -> Java category
(1, 1, false),  -- Java book -> Programming category
(2, 9, true),   -- Spring book -> Spring category
(2, 5, false),  -- Spring book -> Java category
(3, 8, true),   -- React book -> React category
(3, 7, false),  -- React book -> JavaScript category
(4, 6, true),   -- Python book -> Python category
(4, 1, false),  -- Python book -> Programming category
(5, 4, true)    -- AWS book -> Cloud category
) AS new_book_categories(book_id, category_id, is_primary)
WHERE EXISTS (SELECT 1 FROM books WHERE id = new_book_categories.book_id);

-- Inventory (Only if books exist and no inventory exists)
INSERT INTO inventory (id, book_id, store_stock, warehouse_stock, reserved_count, location_code, reorder_point, reorder_quantity, last_received_date, last_sold_date)
SELECT * FROM (VALUES
(1, 1, 25, 100, 0, 'A-001', 5, 20, '2025-07-15', NULL),
(2, 2, 15, 80, 0, 'A-002', 3, 15, '2025-07-20', NULL),
(3, 3, 30, 60, 0, 'B-001', 8, 25, '2025-07-22', NULL),
(4, 4, 12, 45, 0, 'C-001', 5, 20, '2025-07-18', NULL),
(5, 5, 8, 32, 0, 'C-002', 3, 15, '2025-07-23', NULL)
) AS new_inventory(id, book_id, store_stock, warehouse_stock, reserved_count, location_code, reorder_point, reorder_quantity, last_received_date, last_sold_date)
WHERE EXISTS (SELECT 1 FROM books WHERE id = new_inventory.book_id)
AND NOT EXISTS (SELECT 1 FROM inventory WHERE book_id = new_inventory.book_id);

-- Customers (Initial test data)
INSERT INTO customers (id, customer_type, name, name_kana, email, phone, birth_date, gender, occupation, company_name, department, postal_code, address, status, notes, created_at, updated_at)
SELECT * FROM (VALUES
(1, 'INDIVIDUAL', '山田太郎', 'ヤマダタロウ', 'yamada.taro@example.com', '090-1234-5678', '1985-03-15', 'MALE', 'ソフトウェアエンジニア', '株式会社テックラボ', '開発部', '150-0002', '東京都渋谷区渋谷1-1-1', 'ACTIVE', 'Java開発者として5年の経験', '2025-07-01 10:00:00', '2025-07-01 10:00:00'),
(2, 'CORPORATE', '株式会社ソフトウェア開発', 'カブシキガイシャソフトウェアカイハツ', 'procurement@softdev.co.jp', '03-1234-5678', NULL, NULL, NULL, '株式会社ソフトウェア開発', '調達部', '100-0001', '東京都千代田区千代田1-1-1', 'ACTIVE', '技術書籍の一括購入担当', '2025-07-02 14:30:00', '2025-07-02 14:30:00'),
(3, 'INDIVIDUAL', '佐藤花子', 'サトウハナコ', 'sato.hanako@example.com', '080-9876-5432', '1990-08-22', 'FEMALE', 'データサイエンティスト', 'データアナリティクス株式会社', 'AI研究部', '107-0052', '東京都港区赤坂2-2-2', 'ACTIVE', 'Python・機械学習専門', '2025-07-03 09:15:00', '2025-07-03 09:15:00')
) AS new_customers(id, customer_type, name, name_kana, email, phone, birth_date, gender, occupation, company_name, department, postal_code, address, status, notes, created_at, updated_at)
WHERE NOT EXISTS (SELECT 1 FROM customers WHERE email = new_customers.email);

-- Orders (Only if books exist and no orders exist)
INSERT INTO orders (id, order_number, customer_id, status, type, payment_method, total_amount, order_date, confirmed_date, shipped_date, delivered_date, notes, created_at, updated_at)
SELECT * FROM (VALUES
(1, 'ORD-20250125-0001', 1, 'DELIVERED', 'ONLINE', 'CREDIT_CARD', 9810.00, '2025-07-22 07:16:34', '2025-07-23 07:16:34', '2025-07-24 07:16:34', '2025-07-25 07:16:34', '初回購入特典適用', '2025-07-25 07:16:34', '2025-07-25 07:16:34'),
(2, 'ORD-20250125-0002', NULL, 'PENDING', 'WALK_IN', 'CASH', 3420.00, '2025-07-25 01:16:34', NULL, NULL, NULL, '店頭での現金購入', '2025-07-25 07:16:34', '2025-07-25 07:16:34'),
(3, 'ORD-20250125-0003', 2, 'CONFIRMED', 'PHONE', 'BANK_TRANSFER', 33390.00, '2025-07-25 05:16:35', '2025-07-25 06:16:35', NULL, NULL, '法人での一括購入', '2025-07-25 07:16:35', '2025-07-25 07:16:35'),
(4, 'ORD-20250125-0004', 3, 'PICKING', 'ONLINE', 'CREDIT_CARD', 7020.00, '2025-07-25 08:30:00', '2025-07-25 09:00:00', NULL, NULL, 'データサイエンス関連書籍', '2025-07-25 08:30:00', '2025-07-25 09:00:00')
) AS new_orders(id, order_number, customer_id, status, type, payment_method, total_amount, order_date, confirmed_date, shipped_date, delivered_date, notes, created_at, updated_at)
WHERE EXISTS (SELECT 1 FROM books LIMIT 1)
AND NOT EXISTS (SELECT 1 FROM orders WHERE order_number = new_orders.order_number);

-- Order Items (Only if orders exist)
INSERT INTO order_items (id, order_id, book_id, quantity, unit_price, total_price)
SELECT * FROM (VALUES
(1, 1, 1, 2, 2880.00, 5760.00),   -- Order 1: Java book x2
(2, 1, 2, 1, 4050.00, 4050.00),   -- Order 1: Spring book x1
(3, 2, 3, 1, 3420.00, 3420.00),   -- Order 2: React book x1
(4, 3, 1, 5, 2880.00, 14400.00),  -- Order 3: Java book x5
(5, 3, 2, 3, 4050.00, 12150.00),  -- Order 3: Spring book x3
(6, 3, 3, 2, 3420.00, 6840.00),   -- Order 3: React book x2
(7, 4, 4, 1, 3780.00, 3780.00),   -- Order 4: Python ML book x1
(8, 4, 5, 1, 3240.00, 3240.00)    -- Order 4: AWS book x1
) AS new_order_items(id, order_id, book_id, quantity, unit_price, total_price)
WHERE EXISTS (SELECT 1 FROM orders WHERE id = new_order_items.order_id)
AND EXISTS (SELECT 1 FROM books WHERE id = new_order_items.book_id);

-- Report Configurations
INSERT INTO report_configs (id, report_type, report_name, description, config_json, created_at, updated_at) VALUES
(1, 'SALES', '売上レポート', '日次・月次の売上分析レポート', '{"defaultPeriod": "month", "includeCharts": true}', '2025-07-26 00:00:00', '2025-07-26 00:00:00'),
(2, 'INVENTORY', '在庫レポート', '在庫状況と発注提案レポート', '{"includeReorderSuggestions": true, "lowStockThreshold": 10}', '2025-07-26 00:00:00', '2025-07-26 00:00:00'),
(3, 'CUSTOMER', '顧客分析レポート', 'RFM分析と顧客セグメント', '{"includeRFM": true, "segmentationRules": "standard"}', '2025-07-26 00:00:00', '2025-07-26 00:00:00'),
(4, 'DASHBOARD', 'ダッシュボードKPI', '経営ダッシュボード指標', '{"refreshInterval": 300, "includeAlerts": true}', '2025-07-26 00:00:00', '2025-07-26 00:00:00'),
(5, 'TREND', '技術トレンド分析', '技術カテゴリ別のトレンド分析', '{"categories": ["Java", "Python", "React"], "period": "quarter"}', '2025-07-26 00:00:00', '2025-07-26 00:00:00');

-- Report Executions (Recent execution history)
INSERT INTO report_executions (id, report_config_id, executed_by, executed_at, parameters, status, result_path, execution_time_ms, created_at) VALUES
(1, 1, 'admin', '2025-07-26 00:00:00', '{"startDate": "2025-06-01", "endDate": "2025-06-30"}', 'COMPLETED', '/reports/sales_202506.json', 1250, '2025-07-26 00:00:00'),
(2, 2, 'admin', '2025-07-26 00:05:00', '{}', 'COMPLETED', '/reports/inventory_20250726.json', 800, '2025-07-26 00:05:00'),
(3, 3, 'admin', '2025-07-26 00:10:00', '{"includeRFM": true}', 'COMPLETED', '/reports/customer_20250726.json', 2100, '2025-07-26 00:10:00'),
(4, 4, 'system', '2025-07-26 00:15:00', '{}', 'COMPLETED', '/reports/dashboard_20250726.json', 450, '2025-07-26 00:15:00'),
(5, 5, 'admin', '2025-07-26 00:20:00', '{"category": "Java"}', 'COMPLETED', '/reports/trends_java_20250726.json', 1650, '2025-07-26 00:20:00');

-- Aggregation Cache (Sample cached data)
INSERT INTO aggregation_cache (id, cache_key, aggregation_type, aggregation_date, aggregation_data, created_at, expires_at) VALUES
(1, 'sales_daily_2025-07-25', 'DAILY_SALES', '2025-07-25', '{"totalRevenue": 12450.00, "orderCount": 15, "avgOrderValue": 830.00}', '2025-07-26 00:00:00', '2025-07-27 00:00:00'),
(2, 'inventory_summary_2025-07-26', 'INVENTORY_SUMMARY', '2025-07-26', '{"totalProducts": 50, "lowStock": 5, "outOfStock": 2, "totalValue": 150000.00}', '2025-07-26 00:00:00', '2025-07-26 12:00:00'),
(3, 'customer_metrics_2025-07-26', 'CUSTOMER_METRICS', '2025-07-26', '{"totalCustomers": 25, "activeCustomers": 20, "newThisMonth": 3}', '2025-07-26 00:00:00', '2025-07-26 06:00:00'),
(4, 'kpi_snapshot_2025-07-26', 'DASHBOARD_KPI', '2025-07-26', '{"revenue": {"today": 2500, "week": 18000}, "orders": {"today": 8, "week": 45}}', '2025-07-26 00:00:00', '2025-07-26 01:00:00');

-- =================================================================
-- TECH TREND ANALYSIS SAMPLE DATA
-- =================================================================

-- Insert Tech Trend Analysis data for major technology categories
INSERT INTO tech_trend_analysis (
    analysis_date, tech_category_id, lifecycle_stage, trend_direction, growth_rate, 
    market_share, total_revenue, total_units_sold, emerging_score, obsolescence_risk,
    trend_analysis, investment_recommendation, created_at, updated_at
)
SELECT 
    CURRENT_DATE - INTERVAL '30' DAY as analysis_date,
    tc.id as tech_category_id,
    CASE tc.category_code
        WHEN 'PYTHON' THEN 'GROWTH'
        WHEN 'REACT' THEN 'GROWTH' 
        WHEN 'JAVA' THEN 'MATURITY'
        WHEN 'JAVASCRIPT' THEN 'GROWTH'
        WHEN 'SPRING' THEN 'MATURITY'
        WHEN 'MYSQL' THEN 'MATURITY'
        ELSE 'EMERGING'
    END as lifecycle_stage,
    CASE tc.category_code
        WHEN 'PYTHON' THEN 'RISING'
        WHEN 'REACT' THEN 'RISING'
        WHEN 'JAVA' THEN 'STABLE'
        WHEN 'JAVASCRIPT' THEN 'RISING'
        WHEN 'SPRING' THEN 'STABLE'
        WHEN 'MYSQL' THEN 'STABLE'
        ELSE 'RISING'
    END as trend_direction,
    CASE tc.category_code
        WHEN 'PYTHON' THEN 15.2
        WHEN 'REACT' THEN 22.8
        WHEN 'JAVA' THEN 5.1
        WHEN 'JAVASCRIPT' THEN 18.7
        WHEN 'SPRING' THEN 3.2
        WHEN 'MYSQL' THEN 2.8
        ELSE 12.0
    END as growth_rate,
    CASE tc.category_code
        WHEN 'PYTHON' THEN 22.5
        WHEN 'REACT' THEN 18.3
        WHEN 'JAVA' THEN 25.8
        WHEN 'JAVASCRIPT' THEN 35.2
        WHEN 'SPRING' THEN 15.7
        WHEN 'MYSQL' THEN 12.1
        ELSE 5.0
    END as market_share,
    CASE tc.category_code
        WHEN 'PYTHON' THEN 45600.00
        WHEN 'REACT' THEN 32400.00
        WHEN 'JAVA' THEN 68200.00
        WHEN 'JAVASCRIPT' THEN 58700.00
        WHEN 'SPRING' THEN 28900.00
        WHEN 'MYSQL' THEN 21300.00
        ELSE 8500.00
    END as total_revenue,
    CASE tc.category_code
        WHEN 'PYTHON' THEN 1250
        WHEN 'REACT' THEN 890
        WHEN 'JAVA' THEN 1820
        WHEN 'JAVASCRIPT' THEN 1650
        WHEN 'SPRING' THEN 780
        WHEN 'MYSQL' THEN 620
        ELSE 200
    END as total_units_sold,
    CASE tc.category_code
        WHEN 'PYTHON' THEN 88.5
        WHEN 'REACT' THEN 92.1
        WHEN 'JAVA' THEN 65.8
        WHEN 'JAVASCRIPT' THEN 95.2
        WHEN 'SPRING' THEN 72.3
        WHEN 'MYSQL' THEN 68.9
        ELSE 75.0
    END as emerging_score,
    CASE tc.category_code
        WHEN 'PYTHON' THEN 12.5
        WHEN 'REACT' THEN 8.3
        WHEN 'JAVA' THEN 25.1
        WHEN 'JAVASCRIPT' THEN 5.2
        WHEN 'SPRING' THEN 18.7
        WHEN 'MYSQL' THEN 35.8
        ELSE 15.0
    END as obsolescence_risk,
    CASE tc.category_code
        WHEN 'PYTHON' THEN 'Python continues strong growth in data science and AI. Market demand increasing rapidly.'
        WHEN 'REACT' THEN 'React maintains dominant position in frontend development. Strong ecosystem and community support.'
        WHEN 'JAVA' THEN 'Java remains stable in enterprise development. Gradual adoption of modern features.'
        WHEN 'JAVASCRIPT' THEN 'JavaScript growth driven by full-stack development and new frameworks.'
        WHEN 'SPRING' THEN 'Spring framework stable with consistent enterprise adoption. Cloud-native features growing.'
        WHEN 'MYSQL' THEN 'MySQL maintains steady position despite NoSQL competition. Performance improvements continue.'
        ELSE 'Technology showing emerging potential with growing market interest.'
    END as trend_analysis,
    CASE tc.category_code
        WHEN 'PYTHON' THEN 'STRONG BUY - Increase inventory 25-30%. Focus on data science and ML titles.'
        WHEN 'REACT' THEN 'BUY - Increase inventory 20-25%. Frontend development demand remains high.'
        WHEN 'JAVA' THEN 'HOLD - Maintain current levels. Focus on modern Java and enterprise patterns.'
        WHEN 'JAVASCRIPT' THEN 'STRONG BUY - Increase inventory 30-35%. Full-stack development trend continues.'
        WHEN 'SPRING' THEN 'HOLD - Maintain current levels. Cloud-native and microservices focus.'
        WHEN 'MYSQL' THEN 'HOLD - Maintain current levels. Performance and optimization titles preferred.'
        ELSE 'EVALUATE - Monitor market trends before major inventory changes.'
    END as investment_recommendation,
    CURRENT_TIMESTAMP as created_at,
    CURRENT_TIMESTAMP as updated_at
FROM tech_categories tc
WHERE tc.category_code IN ('PYTHON', 'REACT', 'JAVA', 'JAVASCRIPT', 'SPRING', 'MYSQL');

-- Insert Technology Relationships
INSERT INTO tech_relationships (
    primary_tech_id, related_tech_id, relationship_type, correlation_strength, 
    analysis_date, confidence_level, statistical_significance, created_at
)
SELECT 
    t1.id as primary_tech_id,
    t2.id as related_tech_id,
    rel.relationship_type,
    rel.correlation_strength,
    CURRENT_DATE as analysis_date,
    rel.confidence_level,
    rel.statistical_significance,
    CURRENT_TIMESTAMP as created_at
FROM tech_categories t1
CROSS JOIN tech_categories t2
JOIN (
    SELECT 'JAVA' as code1, 'SPRING' as code2, 'COMPLEMENTARY' as relationship_type, 
           9.2 as correlation_strength, 'HIGH' as confidence_level, 0.9851 as statistical_significance
    UNION ALL
    SELECT 'REACT', 'JAVASCRIPT', 'PREREQUISITE', 8.8, 'HIGH', 0.9723
    UNION ALL  
    SELECT 'PYTHON', 'JAVA', 'COMPETITIVE', 6.5, 'MEDIUM', 0.8456
    UNION ALL
    SELECT 'MYSQL', 'JAVA', 'COMPLEMENTARY', 7.8, 'HIGH', 0.9123
    UNION ALL
    SELECT 'MYSQL', 'PYTHON', 'COMPLEMENTARY', 8.1, 'HIGH', 0.9267
    UNION ALL
    SELECT 'SPRING', 'MYSQL', 'COMPLEMENTARY', 8.5, 'HIGH', 0.9431
) rel ON t1.category_code = rel.code1 AND t2.category_code = rel.code2;

-- Insert Technology Predictions
INSERT INTO tech_predictions (
    tech_category_id, prediction_date, prediction_for_date, 
    predicted_revenue, predicted_growth_rate, confidence_interval, 
    prediction_model, model_accuracy, created_at
)
SELECT 
    tc.id as tech_category_id,
    CURRENT_DATE as prediction_date,
    CURRENT_DATE + INTERVAL '180' DAY as prediction_for_date,
    CASE tc.category_code
        WHEN 'PYTHON' THEN 54200.00
        WHEN 'REACT' THEN 41800.00
        WHEN 'JAVA' THEN 71500.00
        WHEN 'JAVASCRIPT' THEN 69600.00
        WHEN 'SPRING' THEN 31200.00
        WHEN 'MYSQL' THEN 22800.00
        ELSE 12000.00
    END as predicted_revenue,
    CASE tc.category_code
        WHEN 'PYTHON' THEN 18.8
        WHEN 'REACT' THEN 29.0
        WHEN 'JAVA' THEN 4.8
        WHEN 'JAVASCRIPT' THEN 18.5
        WHEN 'SPRING' THEN 7.8
        WHEN 'MYSQL' THEN 7.0
        ELSE 15.0
    END as predicted_growth_rate,
    CASE tc.category_code
        WHEN 'PYTHON' THEN 8.2
        WHEN 'REACT' THEN 12.5
        WHEN 'JAVA' THEN 3.1
        WHEN 'JAVASCRIPT' THEN 9.8
        WHEN 'SPRING' THEN 5.2
        WHEN 'MYSQL' THEN 4.8
        ELSE 15.0
    END as confidence_interval,
    'ENHANCED_LINEAR_REGRESSION' as prediction_model,
    CASE tc.category_code
        WHEN 'PYTHON' THEN 87.2
        WHEN 'REACT' THEN 82.8
        WHEN 'JAVA' THEN 92.1
        WHEN 'JAVASCRIPT' THEN 89.5
        WHEN 'SPRING' THEN 85.8
        WHEN 'MYSQL' THEN 88.2
        ELSE 75.0
    END as model_accuracy,
    CURRENT_TIMESTAMP as created_at
FROM tech_categories tc
WHERE tc.category_code IN ('PYTHON', 'REACT', 'JAVA', 'JAVASCRIPT', 'SPRING', 'MYSQL');

-- =================================================================
-- IDENTITY SEQUENCE ALIGNMENT (H2 2.x)
-- Explicit ID inserts above do not advance identity sequences on H2 2.x
-- =================================================================
ALTER TABLE publishers ALTER COLUMN id RESTART WITH 5;
ALTER TABLE authors ALTER COLUMN id RESTART WITH 5;
ALTER TABLE tech_categories ALTER COLUMN id RESTART WITH 11;
ALTER TABLE books ALTER COLUMN id RESTART WITH 6;
ALTER TABLE inventory ALTER COLUMN id RESTART WITH 6;
ALTER TABLE customers ALTER COLUMN id RESTART WITH 4;
ALTER TABLE orders ALTER COLUMN id RESTART WITH 5;
ALTER TABLE order_items ALTER COLUMN id RESTART WITH 9;
ALTER TABLE report_configs ALTER COLUMN id RESTART WITH 6;
ALTER TABLE report_executions ALTER COLUMN id RESTART WITH 6;
ALTER TABLE aggregation_cache ALTER COLUMN id RESTART WITH 5;
