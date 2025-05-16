"use client";
import McpCodeSection from "@/components/community/McpCodeSection";
import CommentSection from "@/components/community/CommentSection";
import Header from "@/components/Layout/Header";
import Chatbot from '@/components/Layout/Chatbot';
import React from "react"; // Import useEffect
import { useRouter, useParams } from "next/navigation";
import { useArticleDetail, useRecommendArticle, useDeleteArticle } from "@/hooks/useArticle";
import { useDateFormat } from "@/hooks/useDateFormat";
import useAuthStore from "@/stores/authStore";
import toast from 'react-hot-toast';

export default function Page() {
  const router = useRouter();
  const params = useParams();
  const articleId = params.id as string;
  
  // 현재 로그인한 사용자 정보 가져오기
  const { user, isLoggedIn } = useAuthStore();
  
  // TanStack Query를 사용하여 게시글 데이터 가져오기
  const { data: article, isLoading, isError } = useArticleDetail(articleId);
  
  // 추천 관련 Mutation
  const recommendMutation = useRecommendArticle(articleId);

  // 날짜 포맷팅
  const { formatDate } = useDateFormat();
  
  // 삭제 관련 Mutation
  const deleteMutation = useDeleteArticle();
  
  // 작성자 체크 (현재 로그인한 사용자가 글 작성자인지 확인)
  const isAuthor = user && article && article.author?.memberId === user.id;
  
  const handleCopyUrl = () => {
  navigator.clipboard.writeText(window.location.href)
    .then(() => {
      toast.success('페이지 URL 복사 완료! 🐼');
    })
    .catch((error) => {
      console.error('URL 복사 실패:', error);
      toast.error('URL 복사를 실패했어요. 😢');
    });
};

  const handleRecommendClick = () => {
    // 로그인 체크
    if (!isLoggedIn) {
      const confirmLogin = window.confirm("로그인이 필요한 서비스입니다. 로그인 페이지로 이동하시겠습니까?");
      if (confirmLogin) {
        router.push("/auth/login");
      }
      return;
    }
    
    // API 호출
    recommendMutation.mutate();
  };
  
  const handleDelete = () => {
    if (window.confirm('정말 삭제하시겠습니까?')) {
      deleteMutation.mutate(articleId, {
        onSuccess: () => {
          toast.success('게시글 삭제 완료! 🐼');
          router.push('/community');
        },
        onError: (error) => {
          console.error('게시글 삭제 실패:', error);
          toast.error('게시글 삭제에 실패했어요.');
        }
      });
    }
  };
  
  const handleEdit = () => {
    router.push(`/community/${articleId}/edit`);
  };

  if (isLoading) return <div className="flex justify-center p-20">로딩 중...</div>;
  if (isError) return <div className="flex justify-center p-20">게시글을 불러오는데 실패했습니다.</div>;
  if (!article) return <div className="flex justify-center p-20">게시글이 존재하지 않습니다.</div>;

  return (
    <div className="mx-auto">
      <Header />
      
      <div className="mx-auto w-[920px] bg-white p-20 min-h-screen">
        <div className="flex justify-between items-center">
          <div className="flex items-center">
            <h1 className="text-2xl font-bold">{article.title}</h1>
            {article.notice && (
              <div className="ml-2 text-[#0095FF] bg-[#E1F3FF] rounded-md px-2 py-1 text-sm">
                공지
              </div>
            )}
          </div>
          {/* 작성자인 경우에만 수정/삭제 버튼 표시 */}
          {isAuthor && (
            <div>
              <button className="text-gray-500 cursor-pointer" onClick={handleEdit}>수정</button>
              <span className="mx-1 text-gray-500">|</span>
              <button className="text-gray-500 cursor-pointer" onClick={handleDelete}>삭제</button>
            </div>
          )}
        </div>
        
        <div className="mt-2 text-[#888A8C] flex">
          <p className="mr-3">{article.author?.nickname || '익명'}</p>
          <p>{formatDate(article.createdAt)}</p>
        </div>
        
        <div className="mt-1 flex space-x-2">
          {/* 태그가 있다면 렌더링 */}
          {article.mcps && Object.keys(article.mcps).map((mcpKey: string, index: number) => (
            <span key={index} className="text-[#555555] bg-[#EDEDED] px-2 py-1 rounded-full text-sm">
              {mcpKey}
            </span>
          ))}
        </div>
        
        <div className="mt-4">
          <div dangerouslySetInnerHTML={{ __html: article.content }} />
        </div>

        {/* MCP 코드 블록 */}
        <McpCodeSection mcpContent={article.mcps} />

        {/* 추천/공유 버튼 그룹 */}
        <div className="mt-5 flex justify-end space-x-4">
          <button
            className={`w-24 px-4 py-2 rounded-full border ${
              article?.isLiked
                ? 'bg-[#0095FF] text-white border-[#0095FF]'
                : 'text-[#0095FF] border-[#0095FF]'
            }`}
            onClick={handleRecommendClick}
            title={!isLoggedIn ? "로그인 후 추천할 수 있습니다" : ""}
          >
            👍 {article?.recommendCount}
          </button>
          <button
            className="w-24 px-4 py-2 rounded-full border border-[#888888] text-[#888888]"
            onClick={handleCopyUrl}
          >
            🔗 공유
          </button>
        </div>

        {/* 댓글 섹션 */}
        <CommentSection comments={article?.comments} />
      </div>

      <Chatbot/>
    </div>
  );
}
