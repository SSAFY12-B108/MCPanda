"use client";

import { useRouter } from "next/navigation";
import { useState } from "react";
import Header from "@/components/Layout/Header";
import Chatbot from "@/components/Layout/Chatbot";
import ArticleItem from "@/components/community/ArticleItem";
import { useArticleQuery, ArticlesParams } from "@/hooks/useArticle";
import Image from "next/image";
import pandaImage from "@/images/community-panda.png";
import useAuthStore from "@/stores/authStore";

export default function CommunityClient() {
  const router = useRouter();
  const { isLoggedIn } = useAuthStore();

  // 검색어 상태
  const [searchInput, setSearchInput] = useState("");

  // API 요청 파라미터 상태
  const [queryParams, setQueryParams] = useState<ArticlesParams>({
    type: "latest",
    page: 1,
  });

  // API 요청 훅 사용
  const { data, isLoading, isError } = useArticleQuery(queryParams);

  // 검색 실행 함수
  const handleSearch = () => {
    setQueryParams((prev) => ({
      ...prev,
      search: searchInput,
      page: 1, // 검색 시 첫 페이지로 리셋
    }));
  };

  // 엔터 키 처리
  const handleKeyPress = (e: React.KeyboardEvent) => {
    if (e.key === "Enter") {
      handleSearch();
    }
  };

  // 정렬 타입 변경 함수
  const handleTypeChange = (type: "recommend" | "latest") => {
    setQueryParams((prev) => ({
      ...prev,
      type,
      page: 1, // 정렬 변경 시 첫 페이지로 리셋
    }));
  };

  // 페이지 변경 함수
  const handlePageChange = (page: number) => {
    setQueryParams((prev) => ({
      ...prev,
      page,
    }));
  };

  return (
    <div className="mx-auto mb-[100px]">
      <Header />

      <div className="mx-auto w-[920px]">
        {/* 안내 문구 및 캐릭터 */}
        <div className="mt-[120px] flex flex-col items-center">
          <p className="text-2xl font-bold">나만의 MCP 조합을 공유해봐요!</p>
          <Image
            src={pandaImage}
            alt="Panda Community"
            width={200}
            height={200}
            className="mt-[35px] mb-[100px]"
          />
        </div>

        {/* 검색바 */}
        <div
          className="w-full flex items-center border rounded-md overflow-hidden bg-white"
          style={{ borderColor: "#E2E8F0", borderWidth: 1 }}
        >
          <div className="pl-5 pr-3">
            <svg
              xmlns="http://www.w3.org/2000/svg"
              fill="none"
              viewBox="0 0 24 24"
              strokeWidth={1.5}
              stroke="currentColor"
              className="w-6 h-6 text-gray-400"
            >
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                d="m21 21-5.197-5.197m0 0A7.5 7.5 0 1 0 5.196 5.196a7.5 7.5 0 0 0 10.607 10.607Z"
              />
            </svg>
          </div>
          <input
            type="text"
            placeholder="검색어를 입력하세요."
            className="w-full py-3 focus:outline-none text-base placeholder-custom"
            value={searchInput}
            onChange={(e) => setSearchInput(e.target.value)}
            onKeyPress={handleKeyPress}
          />
          <button
            type="button"
            className="bg-[#1E88E5] text-white text-base font-semibold whitespace-nowrap mr-5"
            style={{ padding: "4px 16px", borderRadius: 10 }}
            onClick={handleSearch}
          >
            검색
          </button>
        </div>

        {/* 최신순 | 추천순 필터 및 글 작성 버튼 */}
        <div
          className="flex items-center justify-between mt-[25px] text-[1rem]"
          style={{ color: "#6B7280" }}
        >
          <div className="flex items-center">
            <span
              className="cursor-pointer"
              style={{
                color: queryParams.type === "latest" ? "#1E88E5" : undefined,
              }}
              onClick={() => handleTypeChange("latest")}
            >
              최신순
            </span>
            <span className="mx-2">|</span>
            <span
              className="cursor-pointer"
              style={{
                color: queryParams.type === "recommend" ? "#1E88E5" : undefined,
              }}
              onClick={() => handleTypeChange("recommend")}
            >
              추천순
            </span>
          </div>
          {/* 글 작성 버튼 */}
          <button
            style={{
              padding: '8px 12px',
              backgroundColor: '#1E88E5',
              color: 'white',
              textDecoration: 'none',
              borderRadius: '5px',
              fontSize: '0.9rem',
              cursor: 'pointer'
            }}
            onClick={() => {
              if (isLoggedIn) {
                router.push("/community/write");
              } else {
                const confirmLogin = window.confirm("로그인한 사용자만 이용할 수 있습니다. 로그인 페이지로 이동하시겠습니까?");
                if (confirmLogin) {
                  router.push("/auth/login");
                }
              }
            }}
          >
            글 작성하기
          </button>
        </div>

        {/* 게시글 목록 */}
        <div className="mt-[10px] space-y-4">
          {isLoading ? (
            <div className="text-center py-8">로딩 중...</div>
          ) : isError ? (
            <div className="text-center py-8">
              데이터를 불러오는데 실패했습니다
            </div>
          ) : data?.articles && data.articles.length > 0 ? (
            data.articles.map((article) => (
              <ArticleItem
                key={article.id}
                article={article}
                onClick={() => router.push(`/community/${article.id}`)}
              />
            ))
          ) : (
            <div className="text-center py-8">게시글이 없습니다</div>
          )}
        </div>

        {/* 페이지네이션 */}
        {data && data.totalPages > 1 && (
          <div className="flex justify-center mt-[40px]">
            <div className="flex items-center space-x-2 bg-white rounded-md shadow-sm p-2">
              {/* Left arrow */}
              <button
                className="p-2 rounded-md hover:bg-gray-200 disabled:opacity-50"
                onClick={() => handlePageChange(queryParams.page - 1)}
                disabled={queryParams.page === 1}
              >
                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  fill="none"
                  viewBox="0 0 24 24"
                  strokeWidth={1.5}
                  stroke="currentColor"
                  className="w-5 h-5"
                >
                  <path
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    d="M15.75 19.5 8.25 12l7.5-7.5"
                  />
                </svg>
              </button>

              {/* Page numbers */}
              {(() => {
                const totalPages = data.totalPages;
                const currentPage = queryParams.page;
                const maxPageButtons = 10;

                let startPage = Math.max(1, currentPage - Math.floor(maxPageButtons / 2));
                const endPage = Math.min(totalPages, startPage + maxPageButtons - 1);

                // Adjust startPage if endPage hits totalPages but we don't have maxPageButtons
                if (endPage - startPage + 1 < maxPageButtons) {
                  startPage = Math.max(1, endPage - maxPageButtons + 1);
                }

                const pageNumbers = Array.from({ length: endPage - startPage + 1 }, (_, i) => startPage + i);

                return pageNumbers.map((pageNum) => (
                  <button
                    key={pageNum}
                    className={`px-4 py-2 rounded-md ${
                      pageNum === currentPage
                        ? "bg-[#1E88E5] text-white"
                        : "hover:bg-gray-200 text-gray-700"
                    }`}
                    onClick={() => handlePageChange(pageNum)}
                  >
                    {pageNum}
                  </button>
                ));
              })()}

              {/* Right arrow */}
              <button
                className="p-2 rounded-md hover:bg-gray-200 disabled:opacity-50"
                onClick={() => handlePageChange(queryParams.page + 1)}
                disabled={queryParams.page === data.totalPages}
              >
                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  fill="none"
                  viewBox="0 0 24 24"
                  strokeWidth={1.5}
                  stroke="currentColor"
                  className="w-5 h-5"
                >
                  <path
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    d="m8.25 4.5 7.5 7.5-7.5 7.5"
                  />
                </svg>
              </button>
            </div>
          </div>
        )}
      </div>

      <Chatbot />
    </div>
  );
}
