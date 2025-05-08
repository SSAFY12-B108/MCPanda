// hooks/useComment.ts
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { AxiosError } from 'axios';
import apiClient from '@/api/client';
import { Comment } from '@/hooks/useArticle';
import { toast } from 'react-hot-toast'; // 토스트 메시지 라이브러리 추가 (선택적)

// 댓글 작성 요청 인터페이스
interface AddCommentRequest {
  content: string;
}

// 댓글 작성 응답 타입
type AddCommentResponse = Comment;

// 댓글 작성 훅
export const useAddComment = (articleId: string) => {
  const queryClient = useQueryClient();
  
  return useMutation<AddCommentResponse, AxiosError, AddCommentRequest>({
    mutationFn: async (request: AddCommentRequest) => {
      const { data } = await apiClient.post(`/api/comments/${articleId}/comment`, request);
      return data as Comment;
    },
    onSuccess: () => {
      // 댓글 작성 성공 시 캐시 갱신
      queryClient.invalidateQueries({ queryKey: ['article', articleId] });
      toast?.success('댓글이 등록되었습니다.');
    },
    onError: (error) => {
      console.error('댓글 작성 실패:', error);
      toast?.error('댓글 등록에 실패했습니다. 다시 시도해주세요.');
    }
  });
};

// 댓글 삭제 훅
export const useDeleteComment = (articleId: string) => {
  const queryClient = useQueryClient();
  
  return useMutation<unknown, AxiosError, string>({
    mutationFn: async (commentId: string) => {
      const { data } = await apiClient.delete(`/api/comments/${articleId}/${commentId}`);
      return data;
    },
    onSuccess: () => {
      // 댓글 삭제 성공 시 캐시 갱신
      queryClient.invalidateQueries({ queryKey: ['article', articleId] });
      toast?.success('댓글이 삭제되었습니다.');
    },
    onError: (error) => {
      console.error('댓글 삭제 실패:', error);
      toast?.error('댓글 삭제에 실패했습니다. 다시 시도해주세요.');
    }
  });
};