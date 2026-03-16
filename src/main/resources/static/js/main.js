// Vue 3 앱 생성
const { createApp, ref, onMounted } = Vue;

const app = createApp({
    setup() {
        const token = ref('');
        const userInfo = ref({});
        const errorMessage = ref('');

        // 화면이 처음 렌더링될 때 실행 (mounted)
        onMounted(() => {
            // localStorage에 저장된 토큰이 있는지 확인
            const savedToken = localStorage.getItem('token');
            if (!savedToken) {
                alert('로그인이 필요합니다.');
                window.location.href = '/auth/login'; // 토큰이 없으면 로그인 화면으로 이동
                return;
            }

            // 토큰이 존재하면 화면에 표시하기 위해 변수에 담기
            token.value = savedToken;

            // 필요에 따라 페이지 로딩과 동시에 API 호출 가능
            // fetchUserData();
        });

        // 인증(JWT 토큰)이 필요한 API 호출 테스트
        const fetchUserData = async () => {
            errorMessage.value = '';

            try {
                // TODO: 실제 Spring Boot의 보호된 API 경로로 변경하세요. (예: /api/user/me)
                const response = await fetch('/api/board/list', {
                    method: 'GET',
                    headers: {
                        'Authorization': `Bearer ${token.value}`, // 중요! 발급받은 JWT 토큰을 Header에 담아 보냅니다.
                        'Content-Type': 'application/json'
                    }
                });

                if (response.ok) {
                    const data = await response.json();
                    userInfo.value = data; // 응답 데이터(유저 정보)를 화면에 반영
                    alert('정보를 성공적으로 불러왔습니다.');
                } else if (response.status === 401 || response.status === 403) {
                    // 401: 인증 실패 (토큰 만료 등) / 403: 권한 없음
                    alert('인증이 만료되었거나 권한이 없습니다. 다시 로그인해주세요.');
                    handleLogout();
                } else {
                    errorMessage.value = '데이터를 불러오는데 실패했습니다. 상태 코드: ' + response.status;
                }
            } catch (error) {
                console.error('API Error:', error);
                errorMessage.value = '서버 통신 중 오류가 발생했습니다. API 주소를 확인하세요.';
            }
        };

        // 로그아웃 버튼 클릭 시
        const handleLogout = () => {
            // localStorage에서 JWT 토큰 삭제
            localStorage.removeItem('token');
            alert('로그아웃 되었습니다.');
            // 로그인 화면으로 이동
            window.location.href = '/auth/login';
        };

        // 템플릿과 바인딩될 변수/함수 반환
        return {
            token,
            userInfo,
            errorMessage,
            fetchUserData,
            handleLogout
        };
    }
});

// 앱 마운트
app.mount('#app');
