"use client";
import React, { useState } from "react";
import { Comment } from "@/hooks/useArticle";
import { useDateFormat } from "@/hooks/useDateFormat";
import { useAddComment, useDeleteComment } from "@/hooks/useComment";
import { useParams, useRouter } from "next/navigation"; // useRouter 추가

interface CommentSectionProps {
  comments: Comment[];
  currentUserId?: string; // 현재 로그인한 사용자 ID (옵션)
}

export default function CommentSection({ comments = [], currentUserId }: CommentSectionProps) {
  const { id: articleId } = useParams<{ id: string }>();
  const router = useRouter(); // 라우터 추가
  const [newComment, setNewComment] = useState("");
  const { formatDate } = useDateFormat();
  
  // TanStack Query 훅 사용
  const addCommentMutation = useAddComment(articleId);
  const deleteCommentMutation = useDeleteComment(articleId);
  
  // 로그인 체크 및 리다이렉트 함수
  const checkLoginAndProceed = (callback: () => void) => {
    if (!currentUserId) {
      // 로그인하지 않은 경우 알림 표시
      const confirmLogin = window.confirm("로그인이 필요한 서비스입니다. 로그인 페이지로 이동하시겠습니까?");
      
      if (confirmLogin) {
        // 확인을 누른 경우 로그인 페이지로 이동
        router.push("/auth/login");
      }
      // 취소한 경우 아무 동작 없음
      return false;
    }
    
    // 로그인한 경우 콜백 실행
    callback();
    return true;
  };
  
  const handleCommentSubmit = () => {
    if (!newComment.trim()) return;
    
    // 로그인 체크 후 댓글 추가
    checkLoginAndProceed(() => {
      // 댓글 추가 API 호출
      addCommentMutation.mutate({ 
        content: newComment.trim() 
      }, {
        onSuccess: () => {
          // 성공 시 입력 필드 초기화
          setNewComment("");
        }
      });
    });
  };
  
  const handleDeleteComment = (commentId: string) => {
    checkLoginAndProceed(() => {
      if (window.confirm("댓글을 삭제하시겠습니까?")) {
        deleteCommentMutation.mutate(commentId);
      }
    });
  };
  
  const handleKeyPress = (e: React.KeyboardEvent<HTMLTextAreaElement>) => {
    if (e.key === 'Enter' && e.ctrlKey) {
      handleCommentSubmit();
    }
  };

  // 텍스트 영역 포커스 시 로그인 체크
  const handleTextareaFocus = () => {
    if (!currentUserId) {
      // 사용자가 댓글창에 포커스를 두면 바로 로그인 체크
      const confirmLogin = window.confirm("로그인이 필요한 서비스입니다. 로그인 페이지로 이동하시겠습니까?");
      
      if (confirmLogin) {
        router.push("/auth/login");
      }
    }
  };

  return (
    <div className="mt-8">
      <h2 className="text-xl font-bold">댓글 ({comments.length})</h2>
      
      <div className="mt-4">
        <textarea
          className="w-full p-4 border border-gray-200 rounded-md resize-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition"
          rows={4}
          placeholder={currentUserId ? "댓글을 작성하세요. (Ctrl + Enter로 등록)" : "로그인 후 댓글을 작성할 수 있습니다."}
          value={newComment}
          onChange={(e) => setNewComment(e.target.value)}
          onKeyDown={handleKeyPress}
          onFocus={handleTextareaFocus} // 포커스 이벤트 추가
          disabled={!currentUserId} // 로그인하지 않은 경우 비활성화
        ></textarea>
        <div className="flex justify-end mt-2">
          <button 
            className="bg-blue-500 text-white px-4 py-2 rounded-md cursor-pointer disabled:opacity-50 hover:bg-blue-600 transition"
            onClick={handleCommentSubmit}
            disabled={!newComment.trim() || addCommentMutation.isPending || !currentUserId}
          >
            {addCommentMutation.isPending ? "등록 중..." : "등록"}
          </button>
        </div>
      </div>
      
      {/* 댓글 목록 */}
      <div className="mt-8 space-y-4">
        {comments.length === 0 ? (
          <div className="text-center py-6 text-gray-500">
            첫 번째 댓글을 작성해보세요!
          </div>
        ) : (
          comments.map((comment) => (
            <div key={comment._id} className="border-b border-gray-100 pb-4">
              <div className="flex justify-between items-center">
                <p className="font-bold">{comment.author.name || comment.author.email}</p>
                <div className="flex items-center gap-3">
                  {/* 댓글 작성자인 경우에만 삭제 옵션 표시 */}
                  {comment.author.memberId === currentUserId && (
                    <button 
                      className="text-gray-500 text-sm hover:text-red-500 transition"
                      onClick={() => handleDeleteComment(comment._id)}
                      disabled={deleteCommentMutation.isPending}
                    >
                      {deleteCommentMutation.isPending && deleteCommentMutation.variables === comment._id 
                        ? "삭제 중..." 
                        : "삭제"}
                    </button>
                  )}
                  <p className="text-sm text-gray-500">{formatDate(comment.createdAt)}</p>
                </div>
              </div>
              <p className="mt-2 whitespace-pre-wrap">{comment.content}</p>
            </div>
          ))
        )}
      </div>
    </div>
  );
}