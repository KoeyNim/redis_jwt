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
                    const decodedPayload = api.parseJwt(token);
                    if (decodedPayload && decodedPayload.sub) {
                        username.value = decodedPayload.sub;
                    }
                }
            });

            const handleLogout = () => {
                api.handleLogout();
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
