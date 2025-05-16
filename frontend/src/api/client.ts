// src/api/client.ts
import axios, { AxiosError, AxiosRequestConfig } from 'axios';
import useAuthStore from '@/stores/authStore';

// axios 인스턴스 생성
const apiClient = axios.create({
  baseURL: '/api',
  headers: {
    'Content-Type': 'application/json',
  },
  withCredentials: true, // HTTP-only 쿠키를 위한 설정
});

// 요청 중인 토큰 갱신 작업을 저장할 변수
let isRefreshing = false;
// 토큰 갱신 중 대기 중인 요청들을 저장할 배열
let refreshSubscribers: ((success: boolean) => void)[] = [];

// 토큰 갱신 후 대기 중인 요청들을 처리하는 함수
const onRefreshed = (success: boolean) => {
  refreshSubscribers.forEach(callback => callback(success));
  refreshSubscribers = [];
};

// 토큰 갱신 중 새로운 요청을 대기열에 추가하는 함수
const addRefreshSubscriber = (callback: (success: boolean) => void) => {
  refreshSubscribers.push(callback);
};

// 401 에러 발생 시 로그아웃 처리 함수
const handleLogout = () => {
  const logout = useAuthStore.getState().logout;
  logout();
  
  // 로그인 페이지로 리다이렉트 (클라이언트 컴포넌트에서 사용될 때)
  // next/navigation의 useRouter는 훅이므로 여기서 직접 사용할 수 없음
  // 대신 window.location을 사용하거나, 실제 컴포넌트에서 라우팅 처리해야 함
  window.location.href = '/login';
};

// 응답 인터셉터 - 401 에러 처리 및 토큰 재발급
apiClient.interceptors.response.use(
  (response) => response,
  async (error: AxiosError) => {
    const originalRequest = error.config as AxiosRequestConfig & { _retry?: boolean };
    console.log('🐼에러,,', error.response?.status)
    
    // 401 에러이고 재시도하지 않은 요청인 경우
    if (error.response?.status === 401 && !originalRequest._retry) {
      // 이미 토큰 갱신 중이면 대기열에 추가
      if (isRefreshing) {
        return new Promise((resolve, reject) => {
          addRefreshSubscriber((success: boolean) => {
            if (success) {
              resolve(apiClient(originalRequest));
            } else {
              reject(error);
            }
          });
        });
      }

      // 토큰 갱신 시작
      originalRequest._retry = true;
      isRefreshing = true;

      try {
        // 토큰 재발급 요청 - 쿠키는 자동으로 함께 전송됨
        const response = await apiClient.post<{ success: boolean }>('/auth/reissue');

        console.log('🐼response.data', response.data)

        // 성공적으로 토큰을 갱신한 경우
        if (response.data.success) {
          isRefreshing = false;
          onRefreshed(true);
          return apiClient(originalRequest); // 원래 요청 재시도
        } else {
          // 토큰 갱신 실패 (리프레시 토큰 만료)
          isRefreshing = false;
          onRefreshed(false);
          
          // 로그아웃 처리
          handleLogout();
          return Promise.reject(error);
        }
      } catch (refreshError) {
        // 토큰 갱신 요청 자체가 실패한 경우
        isRefreshing = false;
        onRefreshed(false);
        
        // 로그아웃 처리
        handleLogout();
        return Promise.reject(refreshError);
      }
    }

    // 다른 에러는 그대로 전파
    return Promise.reject(error);
  }
);

export default apiClient;