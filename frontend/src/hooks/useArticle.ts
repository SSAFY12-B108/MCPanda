// hooks/useArticle.ts
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import apiClient from '@/api/client';

// 공통 인터페이스
export interface Author {
  memberId: string;
  email: string;
  name?: string;
}

export interface Comment {
  _id: string;
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
  _id: string;
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
      const { data } = await apiClient.post(`/articles/${articleId}/recommends`);
      return data;
    },
    onMutate: async () => {
      await queryClient.cancelQueries({ queryKey: ['article', articleId] });
      const previousArticle = queryClient.getQueryData<ArticleDetail>(['article', articleId]);
      
      // 로컬 스토리지에서 현재 추천 상태 확인
      const recommendedArticles = JSON.parse(localStorage.getItem('recommendedArticles') || '{}');
      const isCurrentlyRecommended = !!recommendedArticles[articleId];
      
      if (previousArticle) {
        queryClient.setQueryData<ArticleDetail>(['article', articleId], {
          ...previousArticle,
          // 토글 방식으로 추천 수 변경
          recommendCount: isCurrentlyRecommended 
            ? previousArticle.recommendCount - 1 
            : previousArticle.recommendCount + 1,
        });
      }
      
      // 로컬 스토리지 업데이트
      if (isCurrentlyRecommended) {
        delete recommendedArticles[articleId];
      } else {
        recommendedArticles[articleId] = true;
      }
      localStorage.setItem('recommendedArticles', JSON.stringify(recommendedArticles));
      
      return { previousArticle, isCurrentlyRecommended };
    },
    onError: (err, variables, context) => {
      if (context?.previousArticle) {
        queryClient.setQueryData<ArticleDetail>(
          ['article', articleId],
          context.previousArticle
        );
      }
    },
    onSettled: () => {
      queryClient.invalidateQueries({ queryKey: ['article', articleId] });
      queryClient.invalidateQueries({ 
        queryKey: ['articles'],
        refetchType: 'none'
      });
    },
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