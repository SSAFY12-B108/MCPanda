"use client";
import React, { useState, useEffect } from "react";
import { Mcps } from "@/hooks/useArticle";
import toast from 'react-hot-toast';

interface McpCodeSectionProps {
  mcpContent?: Mcps;
}

export default function McpCodeSection({ mcpContent = {} }: McpCodeSectionProps) {
  // mcpContent의 키 값들을 태그로 사용
  const mcpTags = Object.keys(mcpContent || {});
  
  // 태그가 없는 경우 기본 태그 설정
  const [activeTag, setActiveTag] = useState<string>(mcpTags.length > 0 ? mcpTags[0] : "");
  
  // mcpContent가 변경될 때 태그 업데이트
  useEffect(() => {
    if (mcpTags.length > 0 && !mcpTags.includes(activeTag)) {
      setActiveTag(mcpTags[0]);
    }
  }, [mcpContent, mcpTags, activeTag]);

  const handleCopyCode = () => {
    const codeElement = document.getElementById("json-code");
    if (codeElement) {
      navigator.clipboard.writeText(codeElement.innerText)
        .then(() => {
          toast.success("코드 복사 완료! 🐼");
        })
        .catch((error) => {
          console.error('코드 복사 실패:', error);
          toast.error("코드 복사에 실패했어요. 😢");
        });
    } else {
      toast.error("복사할 코드를 찾을 수 없어요. 😢");
    }
  };

  // mcpContent가 없거나 빈 객체인 경우 렌더링하지 않음
  if (!mcpContent || Object.keys(mcpContent).length === 0) {
    return null;
  }

  return (
    <div className="mt-16 border border-[#DDDDDD] rounded-md">
      <div className="flex space-x-2 p-2 overflow-x-auto hide-scrollbar">
        {mcpTags.map((tag) => (
          <button
            key={tag}
            className={`px-2 py-1 text-base ${
              activeTag === tag
                ? "text-[#0095FF] border-b-2 border-[#0095FF]"
                : "text-[#666666]"
            }`}
            onClick={() => setActiveTag(tag)}
          >
            {tag}
          </button>
        ))}
      </div>

      <div className="bg-[#1E1E1E] text-white rounded-b-md overflow-hidden">
        {/* 커스텀 스크롤바 스타일을 위한 클래스 추가 */}
        <div className="p-4 font-mono custom-scrollbar" id="json-code-container">
          <pre id="json-code" className="whitespace-pre w-max">
            {activeTag && mcpContent[activeTag] 
              ? JSON.stringify(mcpContent[activeTag], null, 2)
              : "{}"}
          </pre>
        </div>
        <div className="flex justify-end p-2 border-t border-[#3E3E3E] bg-[#303030]">
          <button
            className="bg-[#3E3E3E] text-white px-4 py-1.5 rounded text-sm hover:bg-[#4E4E4E] transition-colors"
            onClick={handleCopyCode}
          >
            코드 복사
          </button>
        </div>
      </div>

      {/* 커스텀 스크롤바 스타일 */}
      <style jsx global>{`
        .custom-scrollbar {
          overflow-x: auto;
          scrollbar-width: thin;
          scrollbar-color: rgba(255, 255, 255, 0.2) transparent;
        }
        
        .custom-scrollbar::-webkit-scrollbar {
          height: 6px;
          background: transparent;
        }
        
        .custom-scrollbar::-webkit-scrollbar-thumb {
          background-color: rgba(255, 255, 255, 0.2);
          border-radius: 3px;
        }
        
        .custom-scrollbar::-webkit-scrollbar-thumb:hover {
          background-color: rgba(255, 255, 255, 0.3);
        }
        
        .custom-scrollbar::-webkit-scrollbar-track {
          background: transparent;
        }
        
        /* 탭 영역의 스크롤바도 숨김 처리 */
        .hide-scrollbar {
          -ms-overflow-style: none;
          scrollbar-width: none;
        }
        
        .hide-scrollbar::-webkit-scrollbar {
          display: none;
        }
      `}</style>
    </div>
  );
}