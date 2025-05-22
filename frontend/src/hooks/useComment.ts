import { useMutation, useQueryClient } from '@tanstack/react-query';
import { AxiosError } from 'axios';
import apiClient from '@/api/client';
import { Comment } from '@/hooks/useArticle';
import toast from 'react-hot-toast'; // 옵셔널 체이닝 제거

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
      const { data } = await apiClient.post(`/comments/${articleId}/comment`, request);
      return data as Comment;
    },
    onSuccess: () => {
      // 댓글 작성 성공 시 캐시 갱신
      queryClient.invalidateQueries({ queryKey: ['article', articleId] });
      toast.success('댓글 등록 완료! 🐼');
    },
    onError: (error) => {
      console.error('댓글 작성 실패:', error);
      toast.error('댓글 등록에 실패했어요. 😢');
    }
  });
};

// 댓글 삭제 훅
export const useDeleteComment = (articleId: string) => {
  const queryClient = useQueryClient();
  
  return useMutation<unknown, AxiosError, string>({
    mutationFn: async (commentId: string) => {
      const { data } = await apiClient.delete(`/comments/${articleId}/${commentId}`);
      return data;
    },
    onSuccess: () => {
      // 댓글 삭제 성공 시 캐시 갱신
      queryClient.invalidateQueries({ queryKey: ['article', articleId] });
      toast.success('댓글 삭제 완료! 🐼');
    },
    onError: (error) => {
      console.error('댓글 삭제 실패:', error);
      toast.error('댓글 삭제에 실패했어요.');
    }
  });
};