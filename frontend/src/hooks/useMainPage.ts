import apiClient from '@/api/client';
import { useQuery } from '@tanstack/react-query';
import { ArticlesResponse } from './useArticle'; // 공통 타입 재사용

// 비동기 fetch 함수 (SSR/CSR 모두에서 사용 가능)
export const fetchMainArticles = async (category: string, page = 1): Promise<ArticlesResponse> => {
  const params = new URLSearchParams();
  params.append('type', 'recommend'); // 추천 조합만 가져오기
  params.append('page', page.toString());
  if (category) params.append('category', category); // 카테고리 필터링
  params.append('size', '9'); // 페이지당 10개로 설정

  const { data } = await apiClient.get(`/articles?${params.toString()}`);
  return data;
};

  // 클라이언트 컴포넌트에서 사용하는 React Query hook
  export const useMainArticles = (category: string, page = 1) => {
    return useQuery({
      queryKey: ['mainArticles', category, page],
      queryFn: () => fetchMainArticles(category, page),
    });
  };