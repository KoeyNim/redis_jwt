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

        // 로그인 처리
        const handleLogin = async () => {
            // 로딩 상태 시작
            isLoading.value = true;
            errorMessage.value = '';

            try {
                const response = await api.post('/api/auth/login', loginForm);

                const res = await response.json();

                if (res.success) {
                    // 성공 (ApiResponse의 data 필드에서 토큰 추출)
                    localStorage.setItem('token', res.data);

                    alert('로그인 성공!');
                    window.location.href = '/main';
                } else {
                    errorMessage.value = res.message || '아이디 또는 비밀번호가 일치하지 않습니다.';
                }
            } catch (error) {
                console.error('Login error:', error);
                errorMessage.value = '서버 접속 중 오류가 발생했습니다.';
            } finally {
                // 로딩 상태 종료
                isLoading.value = false;
            }
        };

        // 템플릿에서 사용할 데이터와 함수를 반환
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
