"use client";
import { useState } from "react";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import apiClient from '@/api/client';
import Header from "@/components/Layout/Header";
import { useRouter } from "next/navigation";
import useAuthStore from "@/stores/authStore";
import toast from 'react-hot-toast';
import useMcps, { McpCategory, Mcp } from "@/hooks/useMcps";


export default function Write() {
  const [title, setTitle] = useState("");
  const [content, setContent] = useState("");
  const [selectedTools, setSelectedTools] = useState<string[]>([]);
  const [openCategories, setOpenCategories] = useState<Set<string>>(new Set());

  const [errors, setErrors] = useState({
    title: "",
    tools: "",
    content: "",
  });

  const { data: mcpCategories, isLoading, isError } = useMcps();

  const router = useRouter();
  const queryClient = useQueryClient();

  const isLoggedIn = useAuthStore((state) => state.isLoggedIn);


  const toggleTool = (tool: string) => {
    setSelectedTools((prev) => {
      if (prev.includes(tool)) return prev.filter((t) => t !== tool);
      if (prev.length < 3) return [...prev, tool];
      return prev; // 3개 초과 시 무시
    });
  };

  // 작성하기 버튼 클릭 시 호출되는 함수
  const createArticle = useMutation({
    mutationFn: async () => {
      const mcpsObject = selectedTools.reduce((acc, tool) => {
        acc[tool] = "true"; // ✅ 문자열 "true"
        return acc;
      }, {} as Record<string, string>);

      const response = await apiClient.post("/articles", {
        title,
        content,
        mcps: mcpsObject,
      });

      return response.data;
    },
    onSuccess: () => {
      toast.success("작성 완료! 🎉");
      queryClient.invalidateQueries({ queryKey: ['articles'] }); // Invalidate articles query cache
      router.push('/community');
    },
    onError: (error) => {
      toast.error("게시글 등록에 실패했어요. 😢");
      console.log(title, content, selectedTools);
      console.error('게시글 업로드 실패', error);
    },
  });

  // 폼 제출 핸들러
  const handleSubmit = (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    const newErrors = { title: "", tools: "", content: "" };
    let hasError = false;

    if (!title.trim()) {
      newErrors.title = "제목을 입력해주세요.";
      hasError = true;
    }

    if (selectedTools.length === 0) {
      newErrors.tools = "하나 이상의 툴을 선택해주세요.";
      hasError = true;
    }

    if (!content.trim()) {
      newErrors.content = "내용을 입력해주세요.";
      hasError = true;
    }

    setErrors(newErrors);

    if (!hasError && isLoggedIn) {
      createArticle.mutate(); // 글 생성
    }

    else if (!isLoggedIn) {
      alert("로그인 후 작성할 수 있어요!");
      router.push("/auth/login");
    }
  };

  return (
    <div>
      <Header />
      <form onSubmit={handleSubmit} className="max-w-3xl mx-auto p-6">
        {/* 제목 */}
        <div className="mb-6">
          <label className="block mb-2 font-semibold text-gray-800">제목</label>
          <input
            type="text"
            placeholder="제목을 입력하세요."
            className="w-full border border-gray-300 rounded-md px-4 py-2 text-sm bg-white"
            value={title}
            onChange={(e) => setTitle(e.target.value)}
          />
          {errors.title && (
            <p className="text-red-500 text-sm mt-1">{errors.title}</p>
          )}
        </div>

        {/* MCP 선택 */}
        <div className="mb-6">
          <label className="block mb-2 font-semibold text-gray-800">
            MCP 선택
            <span className="text-xs text-gray-500">(최대 3개 선택)</span>
          </label>
          {isLoading && <p>Loading MCPs...</p>}
          {isError && <p>Error loading MCPs.</p>}
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
          {errors.tools && (
            <p className="text-red-500 text-sm mt-1">{errors.tools}</p>
          )}
        </div>

        {/* 내용 */}
        <div className="mb-6">
          <label className="block mb-2 font-semibold text-gray-800">내용</label>
          <textarea
            placeholder="내용을 입력하세요."
            className="w-full min-h-[300px] border border-gray-300 rounded-md px-4 py-2 text-sm resize-none bg-white"
            value={content}
            onChange={(e) => setContent(e.target.value)}
          />
          {errors.content && (
            <p className="text-red-500 text-sm mt-1">{errors.content}</p>
          )}
        </div>

        {/* 작성하기 버튼 */}
        <div className="flex justify-end">
          <button
            type="submit"
            className="bg-[#1E88E5] text-white px-6 py-2 rounded-md hover:bg-blue-600 transition"
          >
            작성하기
          </button>
        </div>
      </form>
    </div>
  );
}
