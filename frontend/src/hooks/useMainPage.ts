import { useQuery } from '@tanstack/react-query';
import axios from 'axios';

export interface Article {
  articleId: string;
  title: string;
  mcps: string[];
}
//홈페이지 게시글 조회 
// fetch 함수 export
export const fetchMainArticles = async (): Promise<Article[]> => {
  const response = await axios.get('/api/main');
  return response.data;
};

// 쿼리 훅
export const useMainPage = () => {
  return useQuery({
    queryKey: ['mainArticles'],
    queryFn: fetchMainArticles,
  });
};


// 홈페이지 게시글 카테고리별 fetch 함수
export const fetchArticlesByCategory = async (category: string): Promise<Article[]> => {
  const response = await axios.get(`/api/main/category/${category}`);
  return response.data;
};

export const useMainPageByCategory = (category: string) => {
  return useQuery({
    queryKey: ['mainArticlesByCategory', category],
    queryFn: () => fetchArticlesByCategory(category),
    enabled: !!category, // category 선택됐을 때만 실행
  });
};