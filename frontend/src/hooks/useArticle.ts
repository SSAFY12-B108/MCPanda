// hooks/useArticle.ts
import { useQuery } from '@tanstack/react-query';
import apiClient from '@/api/client';

export interface Author {
  name: string;
}

export interface Article {
  _id: string;
  title: string;
  content: string;
  mcps: string[];
  createdAt: string;
  author: Author;
  recommendCount: number;
  commentsCount: number;
  isNotice: boolean;
}

export interface ArticlesResponse {
  page: number;
  totalPages: number;
  totalArticles: number;
  articles: Article[];
}

export interface ArticlesParams {
  search?: string;
  type: 'latest' | 'recommend';
  page: number;
}

export const useArticleQuery = (params: ArticlesParams) => {
  return useQuery({
    queryKey: ['articles', params],
    queryFn: async () => {
      const searchParams = new URLSearchParams();
      
      if (params.search) {
        searchParams.append('search', params.search);
      }
      
      searchParams.append('type', params.type);
      searchParams.append('page', params.page.toString());
      
      const { data } = await apiClient.get<ArticlesResponse>(`/articles?${searchParams.toString()}`);
      return data;
    },
    placeholderData: (previousData) => previousData, // 이전 데이터를 플레이스홀더로 사용
    staleTime: 1000 * 60 * 5, // 5분 동안 데이터를 신선하게 유지
  });
};