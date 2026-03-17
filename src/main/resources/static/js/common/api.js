/**
 * 전역 API 공통 유틸리티
 * - 모든 fetch 요청에 Authorization 헤더 자동 첨부
 * - 401 에러 발생 시 리프레시 토큰을 이용해 자동 재발급 및 재시도 로직 포함
 */
const api = {
    // JWT 페이로드 파싱 함수
    parseJwt(token) {
        try {
            return JSON.parse(atob(token.split('.')[1].replace(/-/g, '+').replace(/_/g, '/')));
        } catch (e) {
            return null;
        }
    },

    // 로그아웃 및 세션 종료
    async handleLogout() {
        const token = localStorage.getItem('token');
        if (token) {
            try {
                const decoded = this.parseJwt(token);
                if (decoded && decoded.sub) {
                    // 백엔드에 리프레시 토큰 삭제 요청 (비동기로 던져두기)
                    await fetch('/api/auth/logout', {
                        method: 'POST',
                        headers: { 'Content-Type': 'application/json' },
                        body: JSON.stringify({ username: decoded.sub })
                    });
                }
            } catch (e) {
                console.error("Logout API call failed:", e);
            }
        }

        localStorage.removeItem('token');
        alert('로그아웃 되었습니다.');
        window.location.href = '/auth/login';
    },

    // 공통 요청 함수
    async request(url, options = {}) {
        const token = localStorage.getItem('token');

        // 헤더 설정
        options.headers = {
            ...options.headers,
            'Content-Type': 'application/json'
        };

        if (token) {
            options.headers['Authorization'] = `Bearer ${token}`;
        }

        try {
            let response = await fetch(url, options);

            // 401 Unauthorized: 액세스 토큰 만료 가능성
            if (response.status === 401) {
                console.log("Access Token 만료. Refresh 시도...");

                const decoded = this.parseJwt(token);
                if (!decoded || !decoded.sub) {
                    this.handleLogout();
                    return;
                }

                // 토큰 재발급 요청
                const refreshRes = await fetch('/api/auth/refresh', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({
                        username: decoded.sub
                        // refreshToken은 HttpOnly 쿠키로 전송됨
                    })
                });

                if (refreshRes.ok) {
                    const result = await refreshRes.json();
                    if (result.success) {
                        localStorage.setItem('token', result.data);
                        console.log("Token 재발급 성공. 원래 요청 재시도.");

                        // 새 토큰으로 헤더 교체 후 재요청
                        options.headers['Authorization'] = `Bearer ${result.data}`;
                        return await fetch(url, options);
                    } else {
                        console.error("Token refresh failed:", result.message);
                        this.handleLogout();
                    }
                } else {
                    // 리프레시 토큰도 만료된 경우
                    this.handleLogout();
                }
            }

            return response;
        } catch (error) {
            console.error('API Request Error:', error);
            throw error;
        }
    },

    // 편의용 메서드들
    async get(url, headers = {}) {
        return this.request(url, { method: 'GET', headers });
    },

    async post(url, body, headers = {}) {
        return this.request(url, {
            method: 'POST',
            headers,
            body: JSON.stringify(body)
        });
    }
};
