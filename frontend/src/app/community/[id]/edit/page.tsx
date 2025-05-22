"use client";

import { useEffect, useState, useCallback } from "react";
import { useParams, useRouter } from "next/navigation";
import { useArticleDetail, useUpdateArticle, Mcps, McpServers } from "@/hooks/useArticle";
import useAuthStore from "@/stores/authStore";
import toast from 'react-hot-toast';
import Header from "@/components/Layout/Header";
import useMcps, { McpCategory, Mcp } from "@/hooks/useMcps";


export default function EditPage() {
  const { id } = useParams();
  const router = useRouter();

  const [title, setTitle] = useState("");
  const [content, setContent] = useState("");
  const [selectedTools, setSelectedTools] = useState<string[]>([]);
  const [openCategories, setOpenCategories] = useState<Set<string>>(new Set());
  const [errors, setErrors] = useState({ title: "", tools: "", content: "" });

  // 현재 로그인한 사용자 정보 가져오기
  const { user } = useAuthStore();

  // TanStack Query를 사용하여 게시글 데이터 가져오기
  const {
    data: articleResponse,
    isLoading: isArticleLoading,
    isError: isArticleError
  } = useArticleDetail(id as string);

  // Fetch MCP data
  const { data: mcpCategories, isLoading: isMcpsLoading, isError: isMcpsError } = useMcps();


  // 게시글 수정 mutation 가져오기
  const updateArticleMutation = useUpdateArticle();

  // 데이터 불러와서 상태에 세팅
  useEffect(() => {
    // articleResponse가 있는지 확인
    if (!articleResponse || !user || !mcpCategories) return;

    // articleResponse.article에서 author에 접근
    const isAuthor = articleResponse.article.author?.memberId === user.id;

    if (!isAuthor) {
      toast.error("수정 권한이 없어요.");
      router.replace("/community");
      return;
    }

    // article 객체에서 데이터 추출하여 상태 설정
    const { title, content, mcps } = articleResponse.article;
    setTitle(title);
    setContent(content);

    // mcps 객체에서 선택된 MCP 이름만 추출하여 설정
    const initiallySelectedTools = Object.keys(mcps || {});
    setSelectedTools(initiallySelectedTools);

    // Open categories that contain initially selected tools
    const categoriesToOpen = new Set<string>();
    mcpCategories.forEach(categoryData => {
      categoryData.mcps.forEach(mcp => {
        if (initiallySelectedTools.includes(mcp.name)) {
          categoriesToOpen.add(categoryData.category);
        }
      });
    });
    setOpenCategories(categoriesToOpen);

  }, [articleResponse, user, router, mcpCategories]);

  // useCallback으로 toggleTool 감싸기
  const toggleTool = useCallback((tool: string) => {
    setSelectedTools((prev) => {
      // 이미 선택된 도구인 경우 제거
      if (prev.includes(tool)) {
        return prev.filter((t) => t !== tool);
      }

      // 아직 3개 미만 선택된 경우 추가
      if (prev.length < 3) {
        return [...prev, tool];
      }

      // 3개 이상인 경우 상태 변화 없이 반환
      return prev;
    });

    // setState 외부에서 toast 처리
    if (!selectedTools.includes(tool) && selectedTools.length >= 3) {
      // setTimeout으로 다음 렌더링 사이클로 이동
      setTimeout(() => {
        toast.error("최대 3개까지 선택 가능해요!");
      }, 0);
    }
  }, [selectedTools]);

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();

    // 유효성 검사
    const newErrors = { title: "", tools: "", content: "" };
    let hasError = false;

    if (!title.trim()) {
      newErrors.title = "제목을 입력해주세요.";
      hasError = true;
    }

    if (selectedTools.length === 0) {
      newErrors.tools = "하나 이상의 MCP를 선택해주세요.";
      hasError = true;
    }

    if (!content.trim()) {
      newErrors.content = "내용을 입력해주세요.";
      hasError = true;
    }

    setErrors(newErrors);
    if (hasError) return;

    // McpServer와 관련된 타입 정의를 사용하여 mcps 데이터 구조 생성
    const mcpsObject: Mcps = {};

    selectedTools.forEach(tool => {
      // 각 도구에 대한 MCP 카테고리 생성
      mcpsObject[tool] = {
        mcpServers: {} as McpServers // 빈 서버 객체로 초기화
      };
    });

    // 수정 API 호출
    updateArticleMutation.mutate(
      {
        id: id as string,
        data: {
          title,
          content,
          mcps: mcpsObject
        }
      },
      {
        // 여기에 onSuccess 콜백 추가
        onSuccess: () => {
          // 성공 시 상세 페이지로 이동
          router.push(`/community/${id}`);
        }
      }
    );
  };

  if (isArticleLoading || isMcpsLoading) {
    return (
      <div className="flex justify-center p-20">
        <div className="animate-pulse">로딩 중...</div>
      </div>
    );
  }

  if (isArticleError || isMcpsError) {
    return (
      <div className="flex justify-center p-20 text-red-500">
        데이터를 불러오는데 실패했습니다.
      </div>
    );
  }

  return (
    <div>
      <Header />
      <form onSubmit={handleSubmit} className="max-w-3xl mx-auto p-6">
        <div className="mb-6">
          <label className="block mb-2 font-semibold text-gray-800">제목</label>
          <input
            type="text"
            className="w-full border px-4 py-2 rounded-md"
            value={title}
            onChange={(e) => setTitle(e.target.value)}
          />
          {errors.title && <p className="text-red-500 text-sm">{errors.title}</p>}
        </div>

        <div className="mb-6">
          <label className="block mb-2 font-semibold text-gray-800">
            MCP 선택 <span className="text-sm text-gray-500">(최대 3개)</span>
          </label>
          {mcpCategories && mcpCategories.map((categoryData: McpCategory) => (
            <div key={categoryData.category} className="mb-4">
              <h3
                className="text-md font-semibold text-gray-700 p-3 cursor-pointer flex justify-between items-center border-b border-gray-300"
                onClick={() => setOpenCategories((prev: Set<string>) => {
                  const newSet = new Set(prev);
                  if (newSet.has(categoryData.category)) {
                    newSet.delete(categoryData.category);
                  } else {
                    newSet.add(categoryData.category);
                  }
                  return newSet;
                })}
              >
                {categoryData.category}
                <span className={`transform transition-transform duration-200 text-lg px-1 ${openCategories.has(categoryData.category) ? 'rotate-180' : ''}`}>
                  ∨
                </span>
              </h3>
              {openCategories.has(categoryData.category) && (
                <div className="flex flex-wrap gap-2 p-3">
                  {categoryData.mcps.map((mcp: Mcp) => (
                    <button
                      key={mcp.id}
                      type="button"
                      onClick={() => toggleTool(mcp.name)}
                      className={`px-3 py-1 text-sm rounded-full
                      ${
                        selectedTools.includes(mcp.name)
                          ? "bg-[#E1F3FF] text-[#0095FF]"
                          : "bg-[#EDEDED] text-[#555555]"
                      }`}
                    >
                      {mcp.name}
                    </button>
                  ))}
                </div>
              )}
            </div>
          ))}
          {errors.tools && <p className="text-red-500 text-sm">{errors.tools}</p>}
        </div>

        <div className="mb-6">
          <label className="block mb-2 font-semibold text-gray-800">내용</label>
          <textarea
            className="w-full min-h-[300px] border px-4 py-2 rounded-md resize-none"
            value={content}
            onChange={(e) => setContent(e.target.value)}
          />
          {errors.content && <p className="text-red-500 text-sm">{errors.content}</p>}
        </div>

        <div className="flex justify-end">
          <button
            type="submit"
            className="bg-blue-500 text-white px-6 py-2 rounded-md hover:bg-blue-600"
            disabled={updateArticleMutation.isPending}
          >
            {updateArticleMutation.isPending ? '수정 중...' : '수정하기'}
          </button>
        </div>
      </form>
    </div>
  );
}
