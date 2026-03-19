// Vue 3 앱 생성
const { createApp, ref, onMounted } = Vue;

const app = createApp({
    setup() {
        const token = ref('');
        const refreshToken = ref('');
        const userInfo = ref({});
        const boardList = ref([]);
        const errorMessage = ref('');

        // 화면이 처음 렌더링될 때 실행 (mounted)
        onMounted(() => {
            // localStorage에 저장된 토큰이 있는지 확인
            const savedToken = localStorage.getItem('token');

            // 토큰이 존재하면 화면에 표시하기 위해 변수에 담기
            token.value = savedToken;
            refreshToken.value = localStorage.getItem('refreshToken') || '';

            // 필요에 따라 페이지 로딩과 동시에 API 호출 가능
            // fetchUserData();
        });

        // 인증(JWT 토큰)이 필요한 API 호출 테스트
        const fetchUserData = async () => {
            errorMessage.value = '';

            try {
                // 공통 API 유틸리티(api.js)를 사용하여 요청
                const response = await api.get('/api/board/list');

                if (response.ok) {
                    const data = await response.json();
                    boardList.value = data;
                } else if (response.status === 403) {
                    alert('권한이 없습니다.');
                } else {
                    errorMessage.value = '데이터를 불러오는데 실패했습니다. 상태 코드: ' + response.status;
                }
            } catch (error) {
                console.error('API Error:', error);
                errorMessage.value = '서버 통신 중 오류가 발생했습니다.';
            }
        };

        // 로그아웃 버튼 클릭 시
        const handleLogout = () => {
            api.handleLogout();
            alert('로그아웃 되었습니다.');
        };

        // 템플릿과 바인딩될 변수/함수 반환
        return {
            token,
            refreshToken,
            userInfo,
            boardList,
            errorMessage,
            fetchUserData,
            handleLogout
        };
    }
});

// 앱 마운트
app.mount('#app');
