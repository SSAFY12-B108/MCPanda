// hooks/useArticle.ts
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import apiClient from '@/api/client';
import toast from 'react-hot-toast';

// 공통 인터페이스
export interface Author {
  memberId?: string;  // 게시글 상세에는 있지만 목록에는 없을 수 있음
  nickname?: string;  // 게시글 상세에는 있지만 목록에는 없을 수 있음
  name?: string;      // 목록에는 있지만 상세에는 없을 수 있음
}

export interface Comment {
  id: string | null;  // API 응답에서 id가 null일 수 있음
  author: Author;
  content: string;
  createdAt: string;
}

// 새로운 MCP 타입 정의
export interface McpServer {
  command: string;
  args: string[];
  env?: Record<string, string>;
  disabled?: boolean;
  autoApprove?: string[];
}

export interface McpServers {
  [serverName: string]: McpServer;
}

export interface McpCategory {
  mcpServers: McpServers;
}

export interface Mcps {
  [category: string]: McpCategory;
}

// 게시글 공통 속성
interface ArticleBase {
  id: string;
  title: string;
  content: string;
  createdAt: string;
  author: Author;
  recommendCount: number;
  notice: boolean; // API 응답에서는 isNotice가 아닌 notice로 변경됨
}

// 게시글 목록용 인터페이스
export interface ArticleListItem extends ArticleBase {
  mcps: Mcps;
  commentsCount: number;
}

// 게시글 상세 보기용 인터페이스
export interface ArticleDetail extends ArticleBase {
  mcps: Mcps;
  comments: Comment[];
}

// 전체 API 응답 구조를 위한 새로운 인터페이스
export interface ArticleDetailResponse {
  article: ArticleDetail;
  isLiked: boolean;
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
      // 디버깅을 위한 로그 추가
      console.log('검색 파라미터:', params);
      
      const searchParams = new URLSearchParams();
      
      if (params.search) {
        searchParams.append('search', params.search);
      }
      
      searchParams.append('type', params.type);
      searchParams.append('page', params.page.toString());
      
      const queryString = searchParams.toString();
      
      try {
        const { data } = await apiClient.get<ArticlesResponse>(`/articles?${queryString}`);
        return data;
      } catch (error) {
        console.error('API 요청 오류:', error);
        throw error;
      }
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
      // ArticleDetailResponse 타입으로 응답을 받음
      const { data } = await apiClient.get<ArticleDetailResponse>(`/articles/${articleId}`);
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
      // 현재 캐시된 게시글 데이터 가져오기 (ArticleDetailResponse 타입으로)
      const currentResponse = queryClient.getQueryData<ArticleDetailResponse>(['article', articleId]);
      
      if (currentResponse) {
        // 응답 데이터의 recommendCount와 isLiked로 업데이트
        queryClient.setQueryData<ArticleDetailResponse>(['article', articleId], {
          ...currentResponse,
          // article 객체 내부의 recommendCount 업데이트
          article: {
            ...currentResponse.article,
            recommendCount: data.recommendCount
          },
          // 루트 레벨의 isLiked 업데이트
          isLiked: data.isLiked
        });
      }
      
      // 목록 페이지의 캐시된 데이터도 업데이트
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
      
      // 추천 성공 토스트 메시지 추가
      if (data.isLiked) {
        toast.success('게시글을 추천했어요. 👍');
      } else {
        toast.success('게시글 추천을 취소했어요. 👌');
      }
    },
    onError: (error) => {
      console.error('추천 처리 중 오류 발생:', error);
      // 오류 발생 시 사용자에게 토스트 메시지로 알림
      toast.error('게시글 추천 처리 중 오류가 발생했어요. 😢');
    }
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
    },
    onError: (error) => {
      console.error('게시글 삭제 중 오류 발생:', error);
    }
  });
};
