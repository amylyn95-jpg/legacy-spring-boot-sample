import React, { createContext, useContext, useState, useEffect, useCallback } from 'react';
import axios from 'axios';

const I18nContext = createContext();

// Used when a component renders outside an I18nProvider: every t() call falls
// back to the literal passed at the call site.
const standaloneI18n = {
  messages: {},
  locale: 'ja',
  loading: false,
  t: (key, fallback = key) => fallback,
  switchLanguage: () => Promise.resolve(),
};

export const useI18n = () => {
  const context = useContext(I18nContext);
  return context || standaloneI18n;
};

export const I18nProvider = ({ children }) => {
  const [messages, setMessages] = useState({});
  const [locale, setLocale] = useState('ja');
  const [loading, setLoading] = useState(true);

  const loadMessages = useCallback(async (lang = locale) => {
    try {
      setLoading(true);
      const response = await axios.get(`/api/v1/i18n/messages`, {
        headers: {
          'Accept-Language': lang === 'en' ? 'en' : 'ja'
        }
      });
      setMessages(response.data);
      setLocale(lang);
      // Store preference
      localStorage.setItem('preferredLanguage', lang);
    } catch (error) {
      console.error('Failed to load messages:', error);
      // Fallback to hardcoded messages
      setMessages(getDefaultMessages(lang));
    } finally {
      setLoading(false);
    }
  }, [locale]);

  const switchLanguage = async (lang) => {
    await loadMessages(lang);
    // Also call backend to set cookie
    try {
      await axios.get(`/language/switch?lang=${lang}`);
    } catch (error) {
      console.error('Failed to switch language on backend:', error);
    }
  };

  const t = (key, fallback = key) => {
    return messages[key] || fallback;
  };

  useEffect(() => {
    // Load initial language preference
    const savedLang = localStorage.getItem('preferredLanguage') || 'ja';
    loadMessages(savedLang);
  }, [loadMessages]);

  const value = {
    messages,
    locale,
    loading,
    t,
    switchLanguage
  };

  return (
    <I18nContext.Provider value={value}>
      {children}
    </I18nContext.Provider>
  );
};

// Fallback messages if API is not available
const getDefaultMessages = (lang) => {
  if (lang === 'en') {
    return {
      'app.title': 'Legacy Application',
      'app.language': 'Language',
      'menu.dashboard': 'Dashboard',
      'menu.books': 'Book Management',
      'menu.orders': 'Order Management',
      'menu.customers': 'Customer Management',
      'menu.reports': 'Reports',
      'inventory.title': 'Inventory Management',
      'inventory.book.title': 'Book Title',
      'inventory.available.stock': 'Available Stock',
      'inventory.status': 'Status',
      'inventory.location': 'Location',
      'inventory.actions': 'Actions',
      'inventory.receive': 'Receive',
      'inventory.sell': 'Sell',
      'inventory.status.outofstock': 'Out of Stock',
      'inventory.status.lowstock': 'Low Stock',
      'inventory.status.instock': 'In Stock',
      'inventory.location.store': 'Store',
      'inventory.location.warehouse': 'Warehouse',
      'customer.basic.info': 'Basic Information',
      'customer.birthdate': 'Birth Date',
      'customer.gender.male': 'Male',
      'customer.gender.female': 'Female',
      'customer.gender.other': 'Other',
      'customer.occupation.info': 'Occupation & Company Information',
      'customer.occupation': 'Occupation',
      'customer.company': 'Company Name',
      'customer.department': 'Department',
      'book.title': 'Title',
      'book.title.english': 'English Title',
      'book.publication.date': 'Publication Date',
      'book.detail.dialog.title': 'Book Detail Information',
      'book.level.beginner': 'Beginner',
      'book.level.intermediate': 'Intermediate',
      'book.level.advanced': 'Advanced',
      'book.edition': 'Edition',
      'book.list.price': 'List Price',
      'book.selling.price': 'Selling Price',
      'receive.dialog.title': 'Receive Stock',
      'receive.quantity': 'Receive Quantity',
      'receive.location': 'Receive Location',
      'receive.reason': 'Reason & Notes',
      'receive.delivery.note': 'Delivery Note Number',
      'receive.operation.success': 'Operation completed successfully',
      'form.save': 'Save',
      'form.cancel': 'Cancel',
      'form.submit': 'Submit',
      'form.edit': 'Edit',
      'form.delete': 'Delete',
      'error.data.load.failed': 'Failed to load data',
      'error.server.connection': 'Cannot connect to server. Please try again later.',
      'error.details': 'Error details: {0}',
      'action.retry': 'Retry',
      'actions': 'Actions',
      // UI Labels
      'ui.close': 'Close',
      'ui.edit': 'Edit', 
      'ui.detail': 'Detail',
      'ui.search': 'Search',
      'ui.search.keyword': 'Search Keyword',
      'ui.search.placeholder': 'Search by title, ISBN, author',
      'ui.page.title.books': 'Book List',
      'ui.tab.basic.info': 'Basic Information',
      'ui.tab.related.info': 'Related Information',
      'ui.tab.inventory.info': 'Inventory Information',
      'ui.field.title': 'Title',
      'ui.field.isbn13': 'ISBN-13',
      'ui.field.publisher': 'Publisher',
      'ui.field.publication.date': 'Publication Date',
      'ui.field.edition': 'Edition',
      'ui.field.pages': 'Pages',
      'ui.field.level': 'Tech Level',
      'ui.field.list.price': 'List Price',
      'ui.field.selling.price': 'Selling Price',
      'ui.table.header.id': 'ID',
      'ui.table.header.title': 'Title',
      'ui.table.header.publisher': 'Publisher',
      'ui.table.header.publication.date': 'Publication Date',
      'ui.table.header.list.price': 'List Price',
      'ui.table.header.level': 'Level',
      'ui.reload': 'Reload',
      // Reports
      'report.customers.under.development': 'Customer Analysis Report (Under Development)',
      // Version Info and Description
      'book.version.info.label': 'Version Information',
      'book.sample.code.url.label': 'Sample Code URL',
      'book.description.label': 'Description',
      // Future Development Messages
      'future.author.info': 'Author Information',
      'future.author.info.description': 'Author information display feature will be implemented in Phase 2.',
      'future.author.info.future': 'The following information will be displayed in the future:',
      'future.author.profile': 'Author name and profile',
      'future.author.other.books': 'Author\'s other books',
      'future.author.experience': 'Experience and expertise',
      'future.tech.category': 'Technology Category',
      'future.tech.category.description': 'Category information display feature will be implemented in Phase 2.',
      'future.tech.category.future': 'The following information will be displayed in the future:',
      'future.tech.category.fields': 'Technical fields (programming languages, frameworks, etc.)',
      'future.tech.category.keywords': 'Related technology keywords',
      'future.tech.category.related': 'Related books in the same category',
      'future.inventory.status': 'Inventory Status',
      'future.inventory.status.description': 'Inventory information display feature will be implemented in Phase 2.',
      'future.inventory.status.future': 'The following information will be displayed in the future:',
      'future.inventory.store.stock': 'Store stock quantity',
      'future.inventory.warehouse.stock': 'Warehouse stock quantity',
      'future.inventory.total.stock': 'Total stock quantity',
      'future.inventory.reserved.orders': 'Reserved order quantity',
      'future.inventory.planned.orders': 'Planned order quantity',
      'future.inventory.alerts': 'Inventory alert settings',
      'future.sales.performance': 'Sales Performance',
      'future.sales.performance.description': 'Sales performance display feature will be implemented in Phase 2.',
      'future.sales.performance.future': 'The following information will be displayed in the future:',
      'future.sales.monthly.quantity': 'Monthly sales quantity',
      'future.sales.trend.graph': 'Sales trend graph',
      'future.sales.popularity.ranking': 'Popularity ranking',
      'ui.field.suffix.pages': 'pages'
    };
  } else {
    return {
      'app.title': 'レガシーアプリケーション',
      'app.language': '言語',
      'menu.dashboard': 'ダッシュボード',
      'menu.books': '書籍管理',
      'menu.orders': '注文管理',
      'menu.customers': '顧客管理',
      'menu.reports': 'レポート',
      'inventory.title': '在庫管理',
      'inventory.book.title': '書籍タイトル',
      'inventory.available.stock': '在庫数',
      'inventory.status': 'ステータス',
      'inventory.location': '場所',
      'inventory.actions': '操作',
      'inventory.receive': '入荷',
      'inventory.sell': '販売',
      'inventory.status.outofstock': '在庫切れ',
      'inventory.status.lowstock': '在庫少',
      'inventory.status.instock': '在庫有',
      'inventory.location.store': '店頭',
      'inventory.location.warehouse': '倉庫',
      'customer.basic.info': '基本情報',
      'customer.birthdate': '生年月日',
      'customer.gender.male': '男性',
      'customer.gender.female': '女性',
      'customer.gender.other': 'その他',
      'customer.occupation.info': '職業・会社情報',
      'customer.occupation': '職業',
      'customer.company': '会社名',
      'customer.department': '部署',
      'book.title': 'タイトル',
      'book.title.english': '英語タイトル',
      'book.publication.date': '発行日',
      'book.edition': '版次',
      'book.list.price': '定価',
      'book.selling.price': '販売価格',
      'book.detail.dialog.title': '書籍詳細情報',
      'book.level.beginner': '初級',
      'book.level.intermediate': '中級',
      'book.level.advanced': '上級',
      'receive.dialog.title': '入荷登録',
      'receive.quantity': '入荷数量',
      'receive.location': '入荷場所',
      'receive.reason': '入荷理由・備考',
      'receive.delivery.note': '納品書番号',
      'receive.operation.success': '操作が完了しました',
      'form.save': '保存',
      'form.cancel': 'キャンセル',
      'form.submit': '送信',
      'form.edit': '編集',
      'form.delete': '削除',
      'error.data.load.failed': 'データの読み込みに失敗しました',
      'error.server.connection': 'サーバーに接続できません。しばらく時間をおいてから再度お試しください。',
      'error.details': 'エラー詳細: {0}',
      'action.retry': '再試行',
      'actions': 'アクション',
      // UI Labels
      'ui.close': '閉じる',
      'ui.edit': '編集',
      'ui.detail': '詳細',
      'ui.search': '検索',
      'ui.search.keyword': '検索キーワード',
      'ui.search.placeholder': 'タイトル、ISBN、著者名で検索',
      'ui.page.title.books': '書籍一覧',
      'ui.tab.basic.info': '基本情報',
      'ui.tab.related.info': '関連情報',
      'ui.tab.inventory.info': '在庫情報',
      'ui.field.title': 'タイトル',
      'ui.field.isbn13': 'ISBN-13',
      'ui.field.publisher': '出版社',
      'ui.field.publication.date': '発行日',
      'ui.field.edition': '版次',
      'ui.field.pages': 'ページ数',
      'ui.field.level': '技術レベル',
      'ui.field.list.price': '定価',
      'ui.field.selling.price': '販売価格',
      'ui.table.header.id': 'ID',
      'ui.table.header.title': 'タイトル',
      'ui.table.header.publisher': '出版社',
      'ui.table.header.publication.date': '発行日',
      'ui.table.header.list.price': '定価',
      'ui.table.header.level': 'レベル',
      'ui.reload': '再読み込み',
      // Reports
      'report.customers.under.development': '顧客分析レポート（開発中）',
      // Version Info and Description
      'book.version.info.label': 'バージョン情報',
      'book.sample.code.url.label': 'サンプルコードURL',
      'book.description.label': '説明',
      // Future Development Messages
      'future.author.info': '著者情報',
      'future.author.info.description': '著者情報の表示機能は Phase 2 で実装予定です。',
      'future.author.info.future': '将来的に以下の情報が表示されます：',
      'future.author.profile': '著者名・プロフィール',
      'future.author.other.books': '著者の他の書籍',
      'future.author.experience': '経歴・専門分野',
      'future.tech.category': '技術カテゴリ',
      'future.tech.category.description': 'カテゴリ情報の表示機能は Phase 2 で実装予定です。',
      'future.tech.category.future': '将来的に以下の情報が表示されます：',
      'future.tech.category.fields': '技術分野（プログラミング言語、フレームワーク等）',
      'future.tech.category.keywords': '関連技術キーワード',
      'future.tech.category.related': '同カテゴリの関連書籍',
      'future.inventory.status': '在庫状況',
      'future.inventory.status.description': '在庫情報の表示機能は Phase 2 で実装予定です。',
      'future.inventory.status.future': '将来的に以下の情報が表示されます：',
      'future.inventory.store.stock': '店頭在庫数',
      'future.inventory.warehouse.stock': '倉庫在庫数',
      'future.inventory.total.stock': '総在庫数',
      'future.inventory.reserved.orders': '予約注文数',
      'future.inventory.planned.orders': '発注予定数',
      'future.inventory.alerts': '在庫アラート設定',
      'future.sales.performance': '販売実績',
      'future.sales.performance.description': '販売実績の表示機能は Phase 2 で実装予定です。',
      'future.sales.performance.future': '将来的に以下の情報が表示されます：',
      'future.sales.monthly.quantity': '月別売上数量',
      'future.sales.trend.graph': '売上推移グラフ',
      'future.sales.popularity.ranking': '人気ランキング',
      'ui.field.suffix.pages': 'ページ'
    };
  }
};