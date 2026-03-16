// Vue 3 앱 생성
const { createApp, ref, reactive } = Vue;

const app = createApp({
    setup() {
        // 반응형 상태 데이터
        const title = ref('로그인');
        const loginForm = reactive({
            username: '',
            password: ''
        });
        const errorMessage = ref('');
        const isLoading = ref(false);

        // 로그인 처리 함수 주입 (실제 API 주소로 변경 필요)
        const handleLogin = async () => {
            // 로딩 상태 시작
            isLoading.value = true;
            errorMessage.value = '';

            try {
                const response = await fetch('/api/auth/login', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                    },
                    body: JSON.stringify(loginForm),
                });

                if (response.ok) {
                    // 성공 (실제 코드에서는 응답으로 온 JWT 토큰을 저장해야 함)
                    const data = await response.text();
                    localStorage.setItem('token', data);

                    alert('로그인 성공!');
                    // 성공 후 메인 페이지로 이동
                    window.location.href = '/main';
                } else {
                    // 실패 (예외 처리)
                    errorMessage.value = '아이디 또는 비밀번호가 일치하지 않습니다.';
                }
            } catch (error) {
                console.error('Login error:', error);
                errorMessage.value = '서버 접속 중 오류가 발생했습니다.';
            } finally {
                // 로딩 상태 종료
                isLoading.value = false;
            }
        };

        // 템플릿에서 사용할 데이터와 함수를 반환 (setup 문법)
        return {
            title,
            loginForm,
            errorMessage,
            isLoading,
            handleLogin
        };
    }
});

// id가 "app"인 div 요소에 Vue 앱 마운트
app.mount('#app');
