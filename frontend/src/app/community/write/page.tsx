"use client"
import { useState } from "react";

const toolsList = [
  "Figma", "React", "Docker", "MongoDB", "Node.js", "Vue.js", "Kubernetes", "AWS", "Spring Boot"
];

export default function Write() {
  const [title, setTitle] = useState("");
  const [content, setContent] = useState("");
  const [selectedTools, setSelectedTools] = useState<string[]>([]);

  const [errors, setErrors] = useState({
    title: "",
    tools: "",
    content: ""
  });

  const toggleTool = (tool: string) => {
    setSelectedTools((prev) => {
      if (prev.includes(tool)) return prev.filter(t => t !== tool);
      if (prev.length < 3) return [...prev, tool];
      return prev; // 3개 초과 시 무시
    });
  };

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

    if (!hasError) {
      alert("작성 완료");
    }
  };

  return (
    <form onSubmit={handleSubmit} className="max-w-3xl mx-auto p-6">
      {/* 제목 */}
      <div className="mb-6">
        <label className="block mb-2 font-semibold text-gray-800">제목</label>
        <input
          type="text"
          placeholder="제목을 입력하세요."
          className="w-full border border-gray-300 rounded-md px-4 py-2 text-sm"
          value={title}
          onChange={(e) => setTitle(e.target.value)}
        />
        {errors.title && <p className="text-red-500 text-sm mt-1">{errors.title}</p>}
      </div>

      {/* 툴 선택 */}
      <div className="mb-6">
        <label className="block mb-2 font-semibold text-gray-800">
          활용 툴 <span className="text-xs text-gray-500">(최대 3개 선택)</span>
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
        {errors.tools && <p className="text-red-500 text-sm mt-1">{errors.tools}</p>}
      </div>

      {/* 내용 */}
      <div className="mb-6">
        <label className="block mb-2 font-semibold text-gray-800">내용</label>
        <textarea
          placeholder="내용을 입력하세요."
          className="w-full min-h-[300px] border border-gray-300 rounded-md px-4 py-2 text-sm resize-none"
          value={content}
          onChange={(e) => setContent(e.target.value)}
        />
        {errors.content && <p className="text-red-500 text-sm mt-1">{errors.content}</p>}
      </div>

      {/* 작성하기 버튼 */}
      <div className="flex justify-end">
        <button
          type="submit"
          className="bg-[#0095FF] text-white px-6 py-2 rounded-md hover:bg-blue-600 transition"
        >
          작성하기
        </button>
      </div>
    </form>
  );
}
