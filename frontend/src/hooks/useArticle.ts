// hooks/useArticle.ts
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import apiClient from '@/api/client';

// 공통 인터페이스
export interface Author {
  memberId: string;
  email: string;
  name?: string;
  nickname?: string;
}

export interface Comment {
  id: string;
  author: Author;
  content: string;
  createdAt: string;
}

// MCP 타입 정의
// 목록 페이지의 MCP (문자열 배열)
export type McpList = string[];

// 상세 페이지의 MCP (객체)
export interface McpContent {
  [key: string]: Record<string, unknown>;
}

// 게시글 공통 속성 (mcps 제외)
interface ArticleBase {
  id: string;
  title: string;
  content: string;
  createdAt: string;
  author: Author;
  recommendCount: number;
  isNotice: boolean;
}

// 게시글 목록용 인터페이스
export interface ArticleListItem extends ArticleBase {
  mcps: McpList;
  commentsCount: number;
}

// 게시글 상세 보기용 인터페이스
export interface ArticleDetail extends ArticleBase {
  mcps: McpContent;
  comments: Comment[];
}

// API 응답 타입
export interface ArticlesResponse {
  page: number;
  totalPages: number;
  totalArticles: number;
  articles: ArticleListItem[];
}

export interface ArticlesParams {
  search?: string;
  type: 'latest' | 'recommend';
  page: number;
}

// 추천 API 응답 타입 정의
interface RecommendResponse {
  message: string;
  recommendCount: number;
  isLiked: boolean;
}

// 게시글 목록 조회 hook
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
    placeholderData: (previousData) => previousData,
    staleTime: 1000 * 60 * 5,
  });
};

// 게시글 상세 조회 hook
export const useArticleDetail = (articleId: string) => {
  return useQuery({
    queryKey: ['article', articleId],
    queryFn: async () => {
      const { data } = await apiClient.get<ArticleDetail>(`/articles/${articleId}`);
      return data;
    },
    enabled: !!articleId,
    staleTime: 1000 * 60 * 5,
  });
};

// 게시글 추천 API 호출 hook
export const useRecommendArticle = (articleId: string) => {
  const queryClient = useQueryClient();
  
  return useMutation({
    mutationFn: async () => {
      const { data } = await apiClient.post<RecommendResponse>(`/articles/${articleId}/recommends`);
      return data;
    },
    onSuccess: (data) => {
      // API 응답에서 받은 추천 정보로 게시글 데이터 직접 업데이트
      // 낙관적 업데이트 없이 실제 응답 데이터 사용
      
      // 현재 캐시된 게시글 데이터 가져오기
      const currentArticle = queryClient.getQueryData<ArticleDetail>(['article', articleId]);
      
      if (currentArticle) {
        // 응답 데이터의 recommendCount로 업데이트
        queryClient.setQueryData<ArticleDetail>(['article', articleId], {
          ...currentArticle,
          recommendCount: data.recommendCount
        });
      }
      
      // 추천 상태 업데이트 (UI에서 버튼 상태 표시용)
      queryClient.setQueryData(['articleRecommendStatus', articleId], {
        isLiked: data.isLiked
      });
      
      // 목록 페이지의 캐시된 데이터도 업데이트 (필요한 경우)
      const articlesQueries = queryClient.getQueriesData<ArticlesResponse>({ 
        queryKey: ['articles'] 
      });
      
      articlesQueries.forEach(([queryKey, queryData]) => {
        if (queryData && queryData.articles) {
          const updatedArticles = queryData.articles.map(article => {
            if (article.id === articleId) {
              return {
                ...article,
                recommendCount: data.recommendCount
              };
            }
            return article;
          });
          
          queryClient.setQueryData(queryKey, {
            ...queryData,
            articles: updatedArticles
          });
        }
      });
    },
    // onError 핸들러는 유지 (백엔드 호출 실패 시 대응)
    onError: (error) => {
      console.error('추천 처리 중 오류 발생:', error);
      // 오류 발생 시 사용자에게 알림 표시 등의 처리 가능
    }
  });
};

// 게시글 추천 상태를 조회하는 훅
export const useArticleRecommendStatus = (articleId: string) => {
  return useQuery({
    queryKey: ['articleRecommendStatus', articleId],
    queryFn: async () => {
      const { data } = await apiClient.get<{ isLiked: boolean }>(`/articles/${articleId}/recommends/status`);
      return data;
    },
    staleTime: 1000 * 60 * 5, // 5분 동안 캐시
  });
};

// 게시글 삭제 hook
export const useDeleteArticle = () => {
  const queryClient = useQueryClient();
  
  return useMutation({
    mutationFn: async (articleId: string) => {
      const { data } = await apiClient.delete(`/articles/${articleId}`);
      return data;
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['articles'] });
    }
  });
};