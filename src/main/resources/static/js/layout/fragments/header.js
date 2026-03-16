(function() {
    // 전역 변수 충돌 방지를 위해 즉시 실행 함수(IIFE)로 감쌈
    const { createApp, ref, onMounted } = Vue;

    const headerApp = createApp({
        setup() {
            const isLoggedIn = ref(false);
            const username = ref('사용자');

            onMounted(() => {
                const token = localStorage.getItem('token');
                if (token) {
                    isLoggedIn.value = true;
                    try {
                        // JWT 페이로드 파싱 (base64url 디코딩)
                        const payloadBase64 = token.split('.')[1].replace(/-/g, '+').replace(/_/g, '/');
                        const jsonPayload = decodeURIComponent(atob(payloadBase64).split('').map(function (c) {
                            return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
                        }).join(''));
                        const decodedPayload = JSON.parse(jsonPayload);

                        if (decodedPayload.sub) {
                            username.value = decodedPayload.sub;
                        }
                    } catch (error) {
                        console.error("JWT 파싱 오류:", error);
                    }
                }
            });

            const handleLogout = () => {
                localStorage.removeItem('token');
                alert('로그아웃 되었습니다.');
                window.location.href = '/auth/login';
            };

            return {
                isLoggedIn,
                username,
                handleLogout
            };
        }
    });

    // 헤더 영역에만 컴포넌트 마운트 (본문 #app과는 별도 동작)
    headerApp.mount('#header-app');
})();
