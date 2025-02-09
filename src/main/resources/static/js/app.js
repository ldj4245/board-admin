let stompClient = null;

// 시간 포맷팅 함수
function formatTime() {
    const now = new Date();
    return now.toLocaleTimeString('ko-KR', { hour: '2-digit', minute: '2-digit' });
}

// WebSocket 연결 함수
function connect() {
    const socket = new SockJS('/chat');
    stompClient = Stomp.over(socket);
    stompClient.debug = null;  // debug 로그 비활성화

    stompClient.connect({}, function (frame) {
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/chat', function (message) {
            respondMessage(JSON.parse(message.body).content);
        });
        // 연결 성공 메시지 표시
        showSystemMessage("채팅방에 연결되었습니다.");
    });
}

// 메시지 전송 함수
function sendMessage() {
    const messageInput = $('#input-message');
    const message = messageInput.val().trim();

    // 빈 메시지 체크
    if (!message) return;

    $(".direct-chat-messages").append(`
        <div class="direct-chat-msg">
            <div class="direct-chat-infos clearfix">
                <span class="direct-chat-name float-left">Uno</span>
                <span class="direct-chat-timestamp float-right">${formatTime()}</span>
            </div>
            <div class="direct-chat-text">
                ${escapeHtml(message)}
            </div>
        </div>
    `);

    stompClient.send("/app/hello", {},
        JSON.stringify({'content': message})  // 입력값 직접 사용
    );

    // 입력창 초기화 및 포커스
    messageInput.val('').focus();

    // 스크롤 자동 이동
    scrollToBottom();
}

// 응답 메시지 표시 함수
function respondMessage(message) {
    $(".direct-chat-messages").append(`
        <div class="direct-chat-msg right">
            <div class="direct-chat-infos clearfix">
                <span class="direct-chat-name float-right">Bot</span>
                <span class="direct-chat-timestamp float-left">${formatTime()}</span>
            </div>
            <div class="direct-chat-text">
                ${escapeHtml(message)}
            </div>
        </div>
    `);
    scrollToBottom();
}

// 시스템 메시지 표시 함수
function showSystemMessage(message) {
    $(".direct-chat-messages").append(`
        <div class="direct-chat-msg system-message">
            <div class="direct-chat-text text-center">
                <small class="text-muted">${escapeHtml(message)}</small>
            </div>
        </div>
    `);
    scrollToBottom();
}

// HTML 이스케이프 함수 (XSS 방지)
function escapeHtml(unsafe) {
    return unsafe
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;")
        .replace(/'/g, "&#039;");
}

// 스크롤을 항상 최신 메시지로 이동
function scrollToBottom() {
    const chatMessages = $(".direct-chat-messages");
    chatMessages.scrollTop(chatMessages[0].scrollHeight);
}

// 초기화 및 이벤트 리스너
$(function () {
    // 페이지 로드 시 연결
    connect();

    // 폼 제출 이벤트
    $("#chat-form").on('submit', function (e) {
        e.preventDefault();
        sendMessage();
    });

    // 전송 버튼 클릭 이벤트
    $("#chat-form button").click(function(e) {
        e.preventDefault();
        sendMessage();
    });

    // 입력창 엔터 키 이벤트
    $("#input-message").on('keypress', function(e) {
        // Shift + Enter는 줄바꿈, Enter만 누르면 전송
        if (e.which === 13 && !e.shiftKey) {
            e.preventDefault();
            sendMessage();
        }
    });
});