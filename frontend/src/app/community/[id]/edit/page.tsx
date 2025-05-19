"use client";

import { useEffect, useState } from "react";
import { useParams, useRouter } from "next/navigation";
import apiClient from '@/api/client';
import { useMutation } from "@tanstack/react-query";
import Header from "@/components/Layout/Header";
import { useArticleDetail, McpServers, Mcps } from "@/hooks/useArticle";
import useAuthStore from "@/stores/authStore";
import toast from 'react-hot-toast';

const toolsList = [
  "Figma", "React", "Docker", "MongoDB", "Node.js",
  "Vue.js", "Kubernetes", "AWS", "Spring Boot",
];

export default function EditPage() {
  const { id } = useParams();
  const router = useRouter();

  const [title, setTitle] = useState("");
  const [content, setContent] = useState("");
  const [selectedTools, setSelectedTools] = useState<string[]>([]);
  const [errors, setErrors] = useState({ title: "", tools: "", content: "" });

  // 현재 로그인한 사용자 정보 가져오기
  const { user } = useAuthStore();

  // TanStack Query를 사용하여 게시글 데이터 가져오기
  const { 
    data: articleResponse, 
    isLoading, 
    isError 
  } = useArticleDetail(id as string);

  // 데이터 불러와서 상태에 세팅
  useEffect(() => {
    // articleResponse가 있는지 확인
    if (!articleResponse || !user) return;

    // articleResponse.article에서 author에 접근 (중요: 올바른 타입 구조 반영)
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
    
    // mcps 객체에서 카테고리 이름만 추출하여 설정
    const mcpCategories = Object.keys(mcps || {});
    setSelectedTools(mcpCategories);
  }, [articleResponse, user, router]);

  const toggleTool = (tool: string) => {
    setSelectedTools((prev) =>
      prev.includes(tool)
        ? prev.filter((t) => t !== tool)
        : prev.length < 3
          ? [...prev, tool]
          : (toast.error("최대 3개까지 선택 가능해요!"), prev)
    );
  };

  const updateArticle = useMutation({
    mutationFn: async () => {
      // 유효성 검사 후 제출
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
      if (hasError) throw new Error("유효성 검사 실패");

      // McpServer와 관련된 타입 정의를 사용하여 mcps 데이터 구조 생성
      const mcpsObject: Mcps = {};
      
      selectedTools.forEach(tool => {
        // 각 도구에 대한 MCP 카테고리 생성
        mcpsObject[tool] = {
          mcpServers: {} as McpServers // 빈 서버 객체로 초기화
        };
      });

      const res = await apiClient.put(`/articles/${id}`, {
        title,
        content,
        mcps: mcpsObject
      });
      
      return res.data;
    },
    onSuccess: () => {
      toast.success("수정 완료! ✏️");
      router.push(`/community/${id}`);
    },
    onError: (error) => {
      toast.error("게시글 수정에 실패했어요. 😢");
      console.error("게시글 수정 실패:", error);
    },
  });

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    updateArticle.mutate();
  };

  if (isLoading) {
    return (
      <div className="flex justify-center p-20">
        <div className="animate-pulse">로딩 중...</div>
      </div>
    );
  }

  if (isError) {
    return (
      <div className="flex justify-center p-20 text-red-500">
        게시글을 불러오는데 실패했습니다.
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
          <div className="flex flex-wrap gap-2">
            {toolsList.map((tool) => (
              <button
                key={tool}
                type="button"
                onClick={() => toggleTool(tool)}
                className={`px-3 py-1 text-sm rounded-full
                ${selectedTools.includes(tool)
                    ? "bg-[#E1F3FF] text-[#0095FF]"
                    : "bg-[#EDEDED] text-[#555555]"
                  }`}
              >
                {tool}
              </button>
            ))}
          </div>
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
          >
            수정하기
          </button>
        </div>
      </form>
    </div>
  );
}