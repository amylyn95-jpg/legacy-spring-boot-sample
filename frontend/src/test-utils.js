import React from 'react';
import { render } from '@testing-library/react';
import { Provider } from 'react-redux';
import { createStore, applyMiddleware } from 'redux';
import thunk from 'redux-thunk';
import rootReducer from './store/reducers';

// Mock store for testing
const mockStore = createStore(rootReducer, applyMiddleware(thunk));

// Mock I18n Context directly
const MockI18nProvider = ({ children, locale = 'en' }) => {
  const mockMessages = {
    'dashboard.title': locale === 'en' ? 'Dashboard' : 'ダッシュボード',
    'dashboard.total.books': locale === 'en' ? 'Total Books' : '総書籍数',
    'order.management': locale === 'en' ? 'Order Management' : '注文管理',
    'report.title': locale === 'en' ? 'Reports & Analysis' : 'レポート & 分析',
    'ui.detail': locale === 'en' ? 'Detail' : '詳細',
    'book.level.beginner': locale === 'en' ? 'Beginner' : '初級',
    'inventory.rotation.matrix': locale === 'en' ? 'Inventory Rotation Matrix' : '在庫回転マトリックス',
  };

  const mockContext = {
    messages: mockMessages,
    locale,
    loading: false,
    t: (key, fallback) => mockMessages[key] || fallback || key,
    switchLanguage: () => Promise.resolve(),
  };

  return React.createElement(
    'div',
    { 'data-testid': 'mock-i18n-provider' },
    children
  );
};

// Custom render function with providers
export const renderWithProviders = (ui, { locale = 'en', ...renderOptions } = {}) => {
  const Wrapper = ({ children }) => (
    <Provider store={mockStore}>
      <MockI18nProvider locale={locale}>
        {children}
      </MockI18nProvider>
    </Provider>
  );

  return render(ui, { wrapper: Wrapper, ...renderOptions });
};

export * from '@testing-library/react';