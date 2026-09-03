#!/bin/bash

# TechBookStore - アプリケーション起動スクリプト
# Backend と Frontend を同時に起動します

set -e

# 色付きログ出力用の関数
log_info() {
    echo -e "\033[32m[INFO]\033[0m $1"
}

log_warn() {
    echo -e "\033[33m[WARN]\033[0m $1"
}

log_error() {
    echo -e "\033[31m[ERROR]\033[0m $1"
}

# プロセスID格納用ファイル
BACKEND_PID_FILE="/tmp/techbookstore_backend.pid"
FRONTEND_PID_FILE="/tmp/techbookstore_frontend.pid"

# クリーンアップ関数
cleanup() {
    log_info "アプリケーションを停止中..."
    
    # Backend プロセスを停止
    if [ -f "$BACKEND_PID_FILE" ]; then
        BACKEND_PID=$(cat "$BACKEND_PID_FILE")
        if kill -0 "$BACKEND_PID" 2>/dev/null; then
            log_info "Backend を停止中 (PID: $BACKEND_PID)..."
            kill -TERM "$BACKEND_PID" 2>/dev/null
            # Graceful shutdown を待つ
            sleep 5
            if kill -0 "$BACKEND_PID" 2>/dev/null; then
                log_warn "Backend を強制終了中..."
                kill -KILL "$BACKEND_PID" 2>/dev/null
            fi
        fi
        rm -f "$BACKEND_PID_FILE"
    fi
    
    # Frontend プロセスを停止
    if [ -f "$FRONTEND_PID_FILE" ]; then
        FRONTEND_PID=$(cat "$FRONTEND_PID_FILE")
        if kill -0 "$FRONTEND_PID" 2>/dev/null; then
            log_info "Frontend を停止中 (PID: $FRONTEND_PID)..."
            kill -TERM "$FRONTEND_PID" 2>/dev/null
            # プロセスツリー全体を停止
            pkill -P "$FRONTEND_PID" 2>/dev/null || true
        fi
        rm -f "$FRONTEND_PID_FILE"
    fi
    
    # Node.js プロセスをクリーンアップ
    pkill -f "react-scripts start" 2>/dev/null || true
    pkill -f "node.*react-scripts" 2>/dev/null || true
    
    log_info "アプリケーションが停止されました"
}

# シグナルハンドラー設定
trap cleanup EXIT INT TERM

# ワークスペースディレクトリを確認
WORKSPACE_DIR="/workspace"
if [ ! -d "$WORKSPACE_DIR" ]; then
    log_error "ワークスペースディレクトリが見つかりません: $WORKSPACE_DIR"
    exit 1
fi

# Backend ディレクトリを確認
BACKEND_DIR="$WORKSPACE_DIR/backend"
if [ ! -d "$BACKEND_DIR" ]; then
    log_error "Backend ディレクトリが見つかりません: $BACKEND_DIR"
    exit 1
fi

# Frontend ディレクトリを確認
FRONTEND_DIR="$WORKSPACE_DIR/frontend"
if [ ! -d "$FRONTEND_DIR" ]; then
    log_error "Frontend ディレクトリが見つかりません: $FRONTEND_DIR"
    exit 1
fi

# 既存のプロセスをチェック
if [ -f "$BACKEND_PID_FILE" ]; then
    OLD_BACKEND_PID=$(cat "$BACKEND_PID_FILE")
    if kill -0 "$OLD_BACKEND_PID" 2>/dev/null; then
        log_warn "Backend が既に実行中です (PID: $OLD_BACKEND_PID)"
        log_info "既存のプロセスを停止します..."
        kill -TERM "$OLD_BACKEND_PID" 2>/dev/null || true
        sleep 3
    fi
    rm -f "$BACKEND_PID_FILE"
fi

if [ -f "$FRONTEND_PID_FILE" ]; then
    OLD_FRONTEND_PID=$(cat "$FRONTEND_PID_FILE")
    if kill -0 "$OLD_FRONTEND_PID" 2>/dev/null; then
        log_warn "Frontend が既に実行中です (PID: $OLD_FRONTEND_PID)"
        log_info "既存のプロセスを停止します..."
        kill -TERM "$OLD_FRONTEND_PID" 2>/dev/null || true
        pkill -P "$OLD_FRONTEND_PID" 2>/dev/null || true
        sleep 3
    fi
    rm -f "$FRONTEND_PID_FILE"
fi

log_info "TechBookStore アプリケーションを起動中..."
log_info "作業ディレクトリ: $WORKSPACE_DIR"

# Redis接続チェック（Dev Container環境の場合）
if [ -n "$REDIS_HOST" ]; then
    log_info "Redis接続をチェック中..."
    REDIS_HOST_TO_CHECK="${REDIS_HOST:-localhost}"
    REDIS_PORT_TO_CHECK="${REDIS_PORT:-6379}"
    
    for i in {1..10}; do
        if timeout 3 bash -c "</dev/tcp/$REDIS_HOST_TO_CHECK/$REDIS_PORT_TO_CHECK" >/dev/null 2>&1; then
            log_info "Redis ($REDIS_HOST_TO_CHECK:$REDIS_PORT_TO_CHECK) への接続が確認できました"
            break
        fi
        if [ $i -eq 10 ]; then
            log_warn "Redis ($REDIS_HOST_TO_CHECK:$REDIS_PORT_TO_CHECK) への接続に失敗しました"
            log_warn "アプリケーションは起動しますが、Redis機能が利用できない可能性があります"
        fi
        sleep 1
    done
fi

# Backend を起動
log_info "Backend を起動中..."
cd "$BACKEND_DIR"

# Maven wrapper の実行権限を確認
if [ ! -x "./mvnw" ]; then
    log_info "Maven wrapper に実行権限を付与中..."
    chmod +x ./mvnw
fi

# Backend をバックグラウンドで起動
log_info "Spring Boot アプリケーションを起動中..."
nohup ./mvnw spring-boot:run > "/tmp/techbookstore_backend.log" 2>&1 &
BACKEND_PID=$!
echo "$BACKEND_PID" > "$BACKEND_PID_FILE"
log_info "Backend が起動中です (PID: $BACKEND_PID)"

# Backend の起動を待機
log_info "Backend の起動を待機中..."
for i in {1..60}; do
    if curl -s "http://localhost:8080/actuator/health" > /dev/null 2>&1; then
        log_info "Backend が正常に起動しました (http://localhost:8080)"
        break
    fi
    if [ $i -eq 60 ]; then
        log_error "Backend の起動がタイムアウトしました"
        exit 1
    fi
    echo -n "."
    sleep 2
done
echo ""

# Frontend を起動
log_info "Frontend を起動中..."
cd "$FRONTEND_DIR"

# node_modules が存在しない場合は npm install を実行
if [ ! -d "node_modules" ]; then
    log_info "依存関係をインストール中..."
    npm install
fi

# Frontend をバックグラウンドで起動
log_info "React アプリケーションを起動中..."
nohup npm start > "/tmp/techbookstore_frontend.log" 2>&1 &
FRONTEND_PID=$!
echo "$FRONTEND_PID" > "$FRONTEND_PID_FILE"
log_info "Frontend が起動中です (PID: $FRONTEND_PID)"

# Frontend の起動を待機
log_info "Frontend の起動を待機中..."
for i in {1..60}; do
    if curl -s "http://localhost:3000" > /dev/null 2>&1; then
        log_info "Frontend が正常に起動しました (http://localhost:3000)"
        break
    fi
    if [ $i -eq 60 ]; then
        log_error "Frontend の起動がタイムアウトしました"
        exit 1
    fi
    echo -n "."
    sleep 3
done
echo ""

# 起動完了メッセージ
log_info "=================================================================================="
log_info "🎉 TechBookStore アプリケーションが正常に起動しました！"
log_info "=================================================================================="
log_info "📖 Frontend (React):     http://localhost:3000"
log_info "⚙️  Backend (Spring Boot): http://localhost:8080"
log_info "📊 Swagger UI:          http://localhost:8080/swagger-ui/index.html"
log_info "🗄️  H2 Console:          http://localhost:8080/h2-console"
log_info "   ├─ JDBC URL:         jdbc:h2:mem:testdb"
log_info "   ├─ Username:         sa"
log_info "   └─ Password:         (空)"
log_info "=================================================================================="
log_info "📋 プロセス情報:"
log_info "   ├─ Backend PID:      $BACKEND_PID"
log_info "   └─ Frontend PID:     $FRONTEND_PID"
log_info "=================================================================================="
log_info "📜 ログファイル:"
log_info "   ├─ Backend:          /tmp/techbookstore_backend.log"
log_info "   └─ Frontend:         /tmp/techbookstore_frontend.log"
log_info "=================================================================================="
log_info "停止するには Ctrl+C を押してください"

# メインループ - プロセスの監視
while true; do
    # Backend プロセスの監視
    if ! kill -0 "$BACKEND_PID" 2>/dev/null; then
        log_error "Backend プロセスが停止しました"
        exit 1
    fi
    
    # Frontend プロセスの監視
    if ! kill -0 "$FRONTEND_PID" 2>/dev/null; then
        log_error "Frontend プロセスが停止しました"
        exit 1
    fi
    
    sleep 5
done
