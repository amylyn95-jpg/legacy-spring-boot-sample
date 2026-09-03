import React, { useState, useEffect } from 'react';
import {
  Container,
  Grid,
  Paper,
  Typography,
  Box,
  Tab,
  Tabs,
  Card,
  CardContent,
  Chip,
  CircularProgress,
  Button,
  Select,
  MenuItem,
  FormControl,
  InputLabel,
} from '@material-ui/core';
import { Alert } from '@material-ui/lab';
import { makeStyles } from '@material-ui/core/styles';
import {
  TrendingUp,
  TrendingDown,
  Timeline,
  Assessment,
  ShowChart,
  TableChart,
} from '@material-ui/icons';
import TechCategoryTrendChart from './TechCategoryTrendChart';
import { ResponsiveContainer, PieChart, Pie, Cell, BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, Legend } from 'recharts';

const useStyles = makeStyles((theme) => ({
  root: {
    padding: theme.spacing(3),
  },
  header: {
    marginBottom: theme.spacing(3),
  },
  tabContent: {
    marginTop: theme.spacing(3),
  },
  summaryCard: {
    height: '100%',
    display: 'flex',
    flexDirection: 'column',
  },
  metricValue: {
    fontSize: '2rem',
    fontWeight: 'bold',
    color: theme.palette.primary.main,
  },
  trendIcon: {
    fontSize: '3rem',
    marginRight: theme.spacing(1),
  },
  emergingChip: {
    margin: theme.spacing(0.5),
  },
  chartContainer: {
    height: 400,
    marginTop: theme.spacing(2),
  },
  loadingContainer: {
    display: 'flex',
    justifyContent: 'center',
    alignItems: 'center',
    height: 200,
  },
  filterContainer: {
    marginBottom: theme.spacing(2),
    display: 'flex',
    gap: theme.spacing(2),
  },
  categoryGrid: {
    marginTop: theme.spacing(2),
  },
}));

// Color palette for charts
const COLORS = ['#0088FE', '#00C49F', '#FFBB28', '#FF8042', '#8884D8', '#82CA9D'];

const TechTrendReportPage = () => {
  const classes = useStyles();
  const [activeTab, setActiveTab] = useState(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [selectedCategory, setSelectedCategory] = useState('');
  
  // State for different data sets
  const [trendReport, setTrendReport] = useState(null);
  const [categoryAnalysis, setCategoryAnalysis] = useState(null);
  const [emergingTech, setEmergingTech] = useState([]);
  const [correlations, setCorrelations] = useState(null);
  const [lifecycleDistribution, setLifecycleDistribution] = useState(null);
  const [investmentRecommendations, setInvestmentRecommendations] = useState([]);

  useEffect(() => {
    loadTrendReport();
    loadEmergingTechnologies();
    loadCorrelations();
    loadLifecycleDistribution();
    loadInvestmentRecommendations();
  }, []);

  useEffect(() => {
    if (selectedCategory) {
      loadCategoryAnalysis(selectedCategory);
    }
  }, [selectedCategory]);

  const loadTrendReport = async () => {
    try {
      const response = await fetch('/api/v1/tech-trends/report');
      if (response.ok) {
        const data = await response.json();
        setTrendReport(data);
      } else {
        throw new Error('Failed to load trend report');
      }
    } catch (err) {
      setError('技術トレンドレポートの読み込みに失敗しました: ' + err.message);
    }
  };

  const loadCategoryAnalysis = async (categoryCode) => {
    try {
      const response = await fetch(`/api/v1/tech-trends/categories/${categoryCode}/analysis`);
      if (response.ok) {
        const data = await response.json();
        setCategoryAnalysis(data);
      } else {
        throw new Error('Failed to load category analysis');
      }
    } catch (err) {
      setError('カテゴリ分析の読み込みに失敗しました: ' + err.message);
    }
  };

  const loadEmergingTechnologies = async () => {
    try {
      const response = await fetch('/api/v1/tech-trends/emerging');
      if (response.ok) {
        const data = await response.json();
        setEmergingTech(data);
      }
    } catch (err) {
      console.error('Error loading emerging technologies:', err);
    }
  };

  const loadCorrelations = async () => {
    try {
      const response = await fetch('/api/v1/tech-trends/correlations');
      if (response.ok) {
        const data = await response.json();
        setCorrelations(data);
      }
    } catch (err) {
      console.error('Error loading correlations:', err);
    }
  };

  const loadLifecycleDistribution = async () => {
    try {
      const response = await fetch('/api/v1/tech-trends/lifecycle-distribution');
      if (response.ok) {
        const data = await response.json();
        setLifecycleDistribution(data);
      }
    } catch (err) {
      console.error('Error loading lifecycle distribution:', err);
    } finally {
      setLoading(false);
    }
  };

  const loadInvestmentRecommendations = async () => {
    try {
      const response = await fetch('/api/v1/tech-trends/investment-recommendations');
      if (response.ok) {
        const data = await response.json();
        setInvestmentRecommendations(data);
      }
    } catch (err) {
      console.error('Error loading investment recommendations:', err);
    }
  };

  const handleTabChange = (event, newValue) => {
    setActiveTab(newValue);
  };

  const formatCurrency = (value) => {
    return new Intl.NumberFormat('ja-JP', {
      style: 'currency',
      currency: 'JPY',
      notation: 'compact'
    }).format(value || 0);
  };

  // Prepare pie chart data for lifecycle distribution
  const lifecyclePieData = lifecycleDistribution?.distribution ? 
    Object.entries(lifecycleDistribution.distribution).map(([stage, count]) => ({
      name: stage,
      value: count,
    })) : [];

  // Tab panel component
  const TabPanel = ({ children, value, index }) => (
    <div hidden={value !== index} className={classes.tabContent}>
      {value === index && children}
    </div>
  );

  if (loading) {
    return (
      <Container maxWidth="lg" className={classes.root}>
        <div className={classes.loadingContainer}>
          <CircularProgress />
          <Typography variant="h6" style={{ marginLeft: 16 }}>
            技術トレンドデータを読み込み中...
          </Typography>
        </div>
      </Container>
    );
  }

  return (
    <Container maxWidth="lg" className={classes.root}>
      {/* Header */}
      <Box className={classes.header}>
        <Typography variant="h4" gutterBottom>
          <Assessment className={classes.trendIcon} />
          技術トレンド分析レポート
        </Typography>
        <Typography variant="subtitle1" color="textSecondary">
          技術専門書店のための包括的トレンド分析と投資推奨
        </Typography>
      </Box>

      {error && (
        <Alert severity="error" style={{ marginBottom: 16 }}>
          {error}
        </Alert>
      )}

      {/* Summary Cards */}
      {trendReport?.marketOverview && (
        <Grid container spacing={3} style={{ marginBottom: 24 }}>
          <Grid item xs={12} sm={6} md={3}>
            <Card className={classes.summaryCard}>
              <CardContent>
                <Typography color="textSecondary" gutterBottom>
                  総市場売上
                </Typography>
                <Typography className={classes.metricValue}>
                  {formatCurrency(trendReport.marketOverview.totalMarketRevenue)}
                </Typography>
              </CardContent>
            </Card>
          </Grid>
          <Grid item xs={12} sm={6} md={3}>
            <Card className={classes.summaryCard}>
              <CardContent>
                <Typography color="textSecondary" gutterBottom>
                  平均成長率
                </Typography>
                <Typography className={classes.metricValue}>
                  {trendReport.marketOverview.averageGrowthRate?.toFixed(1)}%
                </Typography>
                <TrendingUp style={{ color: '#4caf50' }} />
              </CardContent>
            </Card>
          </Grid>
          <Grid item xs={12} sm={6} md={3}>
            <Card className={classes.summaryCard}>
              <CardContent>
                <Typography color="textSecondary" gutterBottom>
                  技術カテゴリ数
                </Typography>
                <Typography className={classes.metricValue}>
                  {trendReport.marketOverview.totalCategories}
                </Typography>
              </CardContent>
            </Card>
          </Grid>
          <Grid item xs={12} sm={6} md={3}>
            <Card className={classes.summaryCard}>
              <CardContent>
                <Typography color="textSecondary" gutterBottom>
                  新興技術
                </Typography>
                <Typography className={classes.metricValue}>
                  {emergingTech.length}
                </Typography>
                <ShowChart style={{ color: '#ff9800' }} />
              </CardContent>
            </Card>
          </Grid>
        </Grid>
      )}

      {/* Tabs */}
      <Paper square>
        <Tabs value={activeTab} onChange={handleTabChange} indicatorColor="primary" textColor="primary">
          <Tab label="市場概要" icon={<Timeline />} />
          <Tab label="カテゴリ分析" icon={<Assessment />} />
          <Tab label="新興技術" icon={<TrendingUp />} />
          <Tab label="技術相関" icon={<TableChart />} />
          <Tab label="ライフサイクル" icon={<ShowChart />} />
          <Tab label="投資推奨" icon={<TrendingDown />} />
        </Tabs>
      </Paper>

      {/* Tab Content */}
      <TabPanel value={activeTab} index={0}>
        {/* Market Overview */}
        <Grid container spacing={3}>
          <Grid item xs={12} md={8}>
            <Paper style={{ padding: 16, height: 450 }}>
              <Typography variant="h6" gutterBottom>
                技術カテゴリ別市場シェア
              </Typography>
              {trendReport?.categoryAnalysis && (
                <ResponsiveContainer width="100%" height={400}>
                  <BarChart data={trendReport.categoryAnalysis.slice(0, 6)}>
                    <CartesianGrid strokeDasharray="3 3" />
                    <XAxis dataKey="categoryName" />
                    <YAxis />
                    <Tooltip formatter={(value, name) => [
                      name === 'metrics.totalRevenue' ? formatCurrency(value) : value,
                      name === 'metrics.totalRevenue' ? '売上' : 'シェア'
                    ]} />
                    <Legend />
                    <Bar dataKey="metrics.totalRevenue" fill="#8884d8" name="売上" />
                  </BarChart>
                </ResponsiveContainer>
              )}
            </Paper>
          </Grid>
          <Grid item xs={12} md={4}>
            <Paper style={{ padding: 16, height: 450 }}>
              <Typography variant="h6" gutterBottom>
                ライフサイクル分布
              </Typography>
              {lifecyclePieData.length > 0 && (
                <ResponsiveContainer width="100%" height={350}>
                  <PieChart>
                    <Pie
                      data={lifecyclePieData}
                      cx="50%"
                      cy="50%"
                      labelLine={false}
                      label={({ name, percent }) => `${name} ${(percent * 100).toFixed(0)}%`}
                      outerRadius={80}
                      fill="#8884d8"
                      dataKey="value"
                    >
                      {lifecyclePieData.map((entry, index) => (
                        <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
                      ))}
                    </Pie>
                    <Tooltip />
                  </PieChart>
                </ResponsiveContainer>
              )}
            </Paper>
          </Grid>
        </Grid>
      </TabPanel>

      <TabPanel value={activeTab} index={1}>
        {/* Category Analysis */}
        <div className={classes.filterContainer}>
          <FormControl variant="outlined" style={{ minWidth: 200 }}>
            <InputLabel>技術カテゴリ</InputLabel>
            <Select
              value={selectedCategory}
              onChange={(e) => setSelectedCategory(e.target.value)}
              label="技術カテゴリ"
            >
              <MenuItem value="">選択してください</MenuItem>
              <MenuItem value="JAVA">Java</MenuItem>
              <MenuItem value="PYTHON">Python</MenuItem>
              <MenuItem value="JAVASCRIPT">JavaScript</MenuItem>
              <MenuItem value="REACT">React</MenuItem>
              <MenuItem value="SPRING">Spring</MenuItem>
            </Select>
          </FormControl>
          <Button
            variant="contained"
            color="primary"
            onClick={() => selectedCategory && loadCategoryAnalysis(selectedCategory)}
            disabled={!selectedCategory}
          >
            分析実行
          </Button>
        </div>

        {categoryAnalysis && (
          <Grid container spacing={3} className={classes.categoryGrid}>
            <Grid item xs={12}>
              <TechCategoryTrendChart 
                categoryAnalysis={categoryAnalysis}
                showMetrics={true}
                height={400}
              />
            </Grid>
          </Grid>
        )}
      </TabPanel>

      <TabPanel value={activeTab} index={2}>
        {/* Emerging Technologies */}
        <Typography variant="h6" gutterBottom>
          新興技術検出結果
        </Typography>
        <Typography variant="body2" color="textSecondary" paragraph>
          過去3ヶ月間の売上パフォーマンスと成長率に基づく新興技術の検出
        </Typography>
        
        <Grid container spacing={2}>
          {emergingTech.map((tech, index) => (
            <Grid item xs={12} sm={6} md={4} key={index}>
              <Card>
                <CardContent>
                  <Typography variant="h6">{tech.categoryName}</Typography>
                  <Box mt={1} mb={2}>
                    <Chip
                      label={`スコア: ${tech.emergingScore?.toFixed(1)}`}
                      color="primary"
                      size="small"
                      className={classes.emergingChip}
                    />
                    <Chip
                      label={`成長率: ${tech.growthRate?.toFixed(1)}%`}
                      color="secondary"
                      size="small"
                      className={classes.emergingChip}
                    />
                  </Box>
                  <Typography variant="body2" color="textSecondary">
                    ライフサイクル: {tech.lifecycleStage}
                  </Typography>
                </CardContent>
              </Card>
            </Grid>
          ))}
        </Grid>
      </TabPanel>

      <TabPanel value={activeTab} index={3}>
        {/* Technology Correlations */}
        <Typography variant="h6" gutterBottom>
          技術相関分析
        </Typography>
        <Typography variant="body2" color="textSecondary" paragraph>
          同時購入パターンに基づく技術間の関連性分析
        </Typography>
        
        {correlations?.relationships && (
          <Grid container spacing={2}>
            {correlations.relationships.slice(0, 9).map((rel, index) => (
              <Grid item xs={12} sm={6} md={4} key={index}>
                <Card>
                  <CardContent>
                    <Typography variant="subtitle1">
                      {rel.primaryTech} ↔ {rel.relatedTech}
                    </Typography>
                    <Box mt={1}>
                      <Chip
                        label={rel.relationshipType}
                        color={rel.relationshipType === 'COMPLEMENTARY' ? 'primary' : 'secondary'}
                        size="small"
                      />
                      <Typography variant="body2" style={{ marginTop: 8 }}>
                        相関強度: {rel.correlationStrength?.toFixed(2)}
                      </Typography>
                      <Typography variant="body2" color="textSecondary">
                        信頼度: {rel.confidenceLevel}
                      </Typography>
                    </Box>
                  </CardContent>
                </Card>
              </Grid>
            ))}
          </Grid>
        )}
      </TabPanel>

      <TabPanel value={activeTab} index={4}>
        {/* Lifecycle Analysis */}
        <Typography variant="h6" gutterBottom>
          技術ライフサイクル分析
        </Typography>
        <Grid container spacing={3}>
          <Grid item xs={12} md={6}>
            <Paper style={{ padding: 16, height: 400 }}>
              <Typography variant="subtitle1" gutterBottom>
                ライフサイクル分布
              </Typography>
              {lifecyclePieData.length > 0 && (
                <ResponsiveContainer width="100%" height={350}>
                  <PieChart>
                    <Pie
                      data={lifecyclePieData}
                      cx="50%"
                      cy="50%"
                      labelLine={false}
                      label={({ name, value }) => `${name}: ${value}`}
                      outerRadius={100}
                      fill="#8884d8"
                      dataKey="value"
                    >
                      {lifecyclePieData.map((entry, index) => (
                        <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
                      ))}
                    </Pie>
                    <Tooltip />
                  </PieChart>
                </ResponsiveContainer>
              )}
            </Paper>
          </Grid>
          <Grid item xs={12} md={6}>
            <Paper style={{ padding: 16, height: 400 }}>
              <Typography variant="subtitle1" gutterBottom>
                ライフサイクル詳細
              </Typography>
              {Object.entries(lifecycleDistribution?.distribution || {}).map(([stage, count]) => (
                <Box key={stage} mb={2}>
                  <Typography variant="body1">
                    {stage}: {count}カテゴリ
                  </Typography>
                  <Typography variant="body2" color="textSecondary">
                    {stage === 'EMERGING' && '新興期 - 投資機会を検討'}
                    {stage === 'GROWTH' && '成長期 - 積極的投資推奨'}
                    {stage === 'MATURITY' && '成熟期 - 安定した需要'}
                    {stage === 'DECLINE' && '衰退期 - 在庫調整が必要'}
                  </Typography>
                </Box>
              ))}
            </Paper>
          </Grid>
        </Grid>
      </TabPanel>

      <TabPanel value={activeTab} index={5}>
        {/* Investment Recommendations */}
        <Typography variant="h6" gutterBottom>
          技術投資推奨
        </Typography>
        <Typography variant="body2" color="textSecondary" paragraph>
          トレンド分析に基づく在庫・投資戦略の推奨事項
        </Typography>
        
        <Grid container spacing={2}>
          {investmentRecommendations.map((rec, index) => (
            <Grid item xs={12} sm={6} md={4} key={index}>
              <Card>
                <CardContent>
                  <Typography variant="h6">{rec.categoryName}</Typography>
                  <Box mt={1} mb={2}>
                    <Chip
                      label={rec.lifecycleStage}
                      color="primary"
                      size="small"
                      style={{ marginRight: 8 }}
                    />
                    <Chip
                      label={`スコア: ${rec.investmentScore?.toFixed(1)}`}
                      color="secondary"
                      size="small"
                    />
                  </Box>
                  <Typography variant="body2" style={{ marginTop: 8 }}>
                    成長率: {rec.growthRate?.toFixed(1)}%
                  </Typography>
                  <Typography variant="body2" color="textSecondary" style={{ marginTop: 8 }}>
                    {rec.recommendation || '標準的な投資戦略を推奨'}
                  </Typography>
                </CardContent>
              </Card>
            </Grid>
          ))}
        </Grid>
      </TabPanel>
    </Container>
  );
};

export default TechTrendReportPage;