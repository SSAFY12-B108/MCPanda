"use client";
import React, { useState } from "react";
import { Comment } from "@/hooks/useArticle";
import { useDateFormat } from "@/hooks/useDateFormat";
import { useAddComment, useDeleteComment } from "@/hooks/useComment";
import { useParams, useRouter } from "next/navigation";
import useAuthStore from "@/stores/authStore"; // Import useAuthStore

interface CommentSectionProps {
  comments?: Comment[] | null; // null이나 undefined도 허용
}

// Remove currentUserId from props
export default function CommentSection({ comments = [] }: CommentSectionProps) {
  const { id: articleId } = useParams<{ id: string }>();
  const router = useRouter();
  const [newComment, setNewComment] = useState("");
  const { formatDate } = useDateFormat();
  
  // Get isLoggedIn from authStore
  const { isLoggedIn, user } = useAuthStore();
  
  // TanStack Query 훅 사용
  const addCommentMutation = useAddComment(articleId);
  const deleteCommentMutation = useDeleteComment(articleId);
  
  // 로그인 체크 및 리다이렉트 함수
  const checkLoginAndProceed = (callback: () => void) => {
    if (!isLoggedIn) { // Use isLoggedIn
      const confirmLogin = window.confirm("로그인이 필요한 서비스입니다. 로그인 페이지로 이동하시겠습니까?");
      
      if (confirmLogin) {
        router.push("/auth/login");
      }
      return false;
    }
    
    callback();
    return true;
  };
  
  const handleCommentSubmit = () => {
    if (!newComment.trim()) return;
    
    checkLoginAndProceed(() => {
      addCommentMutation.mutate({ 
        content: newComment.trim() 
      }, {
        onSuccess: () => {
          setNewComment("");
        }
      });
    });
  };
  
  const handleDeleteComment = (commentId: string) => {
    checkLoginAndProceed(() => {
      if (window.confirm("댓글을 삭제하시겠습니까?")) {
        // null 체크 추가
        if (commentId) {
          deleteCommentMutation.mutate(commentId);
        } else {
          console.error("댓글 ID가 null입니다");
        }
      }
    });
  };
  
  const handleKeyPress = (e: React.KeyboardEvent<HTMLTextAreaElement>) => {
    if (e.key === 'Enter' && e.ctrlKey) {
      handleCommentSubmit();
    }
  };

  // 안전하게 comments 배열의 길이 확인
  const commentsCount = comments?.length || 0;

  return (
    <div className="mt-8">
      <h2 className="text-xl font-bold">댓글 ({commentsCount})</h2>
      
      <div className="mt-4">
        <textarea
          className="w-full p-4 border border-gray-200 rounded-md resize-none focus:outline-none focus:border-blue-500 transition"
          rows={4}
          placeholder={isLoggedIn ? "댓글을 작성하세요. (Ctrl + Enter로 등록)" : "로그인 후 댓글을 작성할 수 있습니다."} // Use isLoggedIn
          value={newComment}
          onChange={(e) => setNewComment(e.target.value)}
          onKeyDown={handleKeyPress}
        ></textarea>
        <div className="flex justify-end mt-2">
          <button
            className="bg-blue-500 text-white px-4 py-2 rounded-md cursor-pointer disabled:opacity-50 hover:bg-blue-600 transition"
            onClick={handleCommentSubmit}
            disabled={!newComment.trim() || addCommentMutation.isPending || !isLoggedIn} // Disable if not logged in
          >
            {addCommentMutation.isPending ? "등록 중..." : "등록"}
          </button>
        </div>
      </div>
      
      {/* 댓글 목록 */}
      <div className="mt-8 space-y-4">
        {!comments || comments.length === 0 ? (
          <div className="text-center py-6 text-gray-500">
            첫 번째 댓글을 작성해보세요!
          </div>
        ) : (
          comments.map((comment) => (
            <div key={comment.id} className="border-b border-gray-100 pb-4">
              <div className="flex justify-between items-center">
                {/* API 응답 변경: 이제 author는 nickname을 포함함 */}
                <p className="font-bold">{comment.author?.nickname || '익명'}</p>
                <div className="flex items-center gap-3">
                  {/* memberId 필드 유지: 현재 사용자의 ID와 비교 */}
                  {/* Use user?._id for comparison */}
                  {isLoggedIn && comment.author?.memberId === user?._id && (
                    <button 
                      className="text-gray-500 text-sm hover:text-red-500 transition"
                      onClick={() => handleDeleteComment(comment.id || '')}
                      disabled={deleteCommentMutation.isPending}
                    >
                      {deleteCommentMutation.isPending && deleteCommentMutation.variables === comment.id 
                        ? "삭제 중..." 
                        : "댓글 삭제"}
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
