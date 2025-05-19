"use client";

import { useEffect, useState } from "react";
import { useParams, useRouter } from "next/navigation";
import axios from "axios";
import { useMutation, useQuery } from "@tanstack/react-query";
import Header from "@/components/Layout/Header";
import { useArticleDetail } from "@/hooks/useArticle";
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


  // TanStack Query를 사용하여 게시글 데이터 가져오기
  const { data: article } = useArticleDetail(id as string);

  const user = useAuthStore((state) => state.user);

  // 1. 기존 게시글 가져오기
  const { data } = useQuery({
    queryKey: ["article", id],
    queryFn: async () => {
      const res = await axios.get(`/api/articles/${id}`);
      return res.data;
    },
    enabled: !!id, // id가 있어야 요청함
  });

  // 2. 데이터 불러와서 상태에 세팅
  // useQuery로 가져온 data를 → useState에 다시 세팅하는 역할
  useEffect(() => {
  if (!article || !user) return;

  const isAuthor = article.author.memberId === user.id;
  if (!isAuthor) {
    toast.error("수정 권한이 없어요.");
    router.replace("/community");
  }

  setTitle(data.title);
  setContent(data.content);
  setSelectedTools(Object.keys(data.mcps)); // ✅ 여기 수정
}, [article, user]);

const toggleTool = (tool: string) => {
  setSelectedTools((prev) =>
    prev.includes(tool)
      ? prev.filter((t) => t !== tool)
      : prev.length < 3
        ? [...prev, tool]
        : (toast.error("최대 3개까지 선택 가능해요!"), prev) // 3개 초과 선택 시 토스트 메시지 추가
  );
};

const updateArticle = useMutation({
  mutationFn: async () => {
    const res = await axios.put(`/api/articles/${id}`, {
      title,
      content,
      mcps: selectedTools,
    });
    return res.data;
  },
  onSuccess: () => {
    toast.success("수정 완료! ✏️");
    router.push(`/community/${id}`); // 상세 페이지로 이동
  },
  onError: () => {
    toast.error("게시글 수정에 실패했어요. 😢");
    console.log("게시글 수정 실패", errors);
  },
});
  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();

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

    updateArticle.mutate();


  };


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
