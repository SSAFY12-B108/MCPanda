'use client';

import CombinationBox from '@/components/Home/CombinationBox';
import Header from '@/components/Layout/Header';
import Chatbot from '@/components/Layout/Chatbot';
import Image from 'next/image';
import { useState } from 'react';
import { useMainArticles } from '@/hooks/useMainPage';
import { useRouter } from "next/navigation";


// ✅ 여기에 props 타입 선언
interface HomeClientProps {
  defaultCategory: string;
  defaultPage: number;
}

export default function HomeClient({ defaultCategory, defaultPage }: HomeClientProps) {
  const router = useRouter();

  const [category, setCategory] = useState<string>(defaultCategory);
  const [page, setPage] = useState<number>(defaultPage);
  const { data } = useMainArticles(category, page);

  //페이지 네이션 
  const handlePrev = () => {
    if (page > 1) setPage((prev) => prev - 1);
  };

  const handleNext = () => {
    if (data && page < data.totalPages) setPage((prev) => prev + 1);
  };


  console.log('category:', category);
  console.log('data:', data);

  return (
    <div>
      <Header />
      <div className="mx-auto w-[920px] px-20">
        <div className="flex justify-between items-center mb-4">
          <div className="font-bold text-[25px] leading-tight text-gray-900">
            <div>MCP 조합,</div>
            <div>MCPanda와 함께</div>
            <div>쉽게 시작하세요</div>
          </div>
          <Image src="/logo.png" alt="MCPanda_Logo" width={160} height={160} className="w-40" />
        </div>

        <div className="flex justify-between items-center mb-6">
          <h1 className="text-lg font-bold text-gray-900">유저들의 추천 조합</h1>
          <select
            value={category}
            onChange={(e) => {
              setCategory(e.target.value);
              setPage(1); // ✅ 카테고리 바뀌면 페이지 초기화
            }}
            className="text-sm p-1 text-gray-600 rounded-md border"
            style={{ borderColor: '#0095FF' }}
          >
            <option value="">MCP 선택</option>
            <option value="frontend">Frontend</option>
            <option value="backend">Backend</option>
            <option value="infra">Infra</option>
            <option value="ETC">기타</option>
          </select>
        </div>

        <ul className="flex flex-wrap">
          {data?.articles?.map((article) => (
            //	3등분 너비 (33.3333%)
            <li key={article.id} className="w-1/3">
              <CombinationBox
                title={article.title}
                tags={Object.keys(article.mcps)}
                recommendCount={article.recommendCount}
                onClick={() => router.push(`/community/${article.id}`)}

              />
            </li>
          ))}
        </ul>


        {/* ✅ 페이지네이션 */}
        <div className="flex justify-center items-center gap-4 mt-6">
          <button
            onClick={handlePrev}
            disabled={page === 1}
            className="px-4 py-2 bg-gray-200 rounded disabled:opacity-50"
          >
            이전
          </button>
          <span className="text-sm text-gray-600">페이지 {page} / {data?.totalPages}</span>
          <button
            onClick={handleNext}
            disabled={page === data?.totalPages}
            className="px-4 py-2 bg-gray-200 rounded disabled:opacity-50"
          >
            다음
          </button>
        </div>

      </div>
      <Chatbot />
    </div>
  );
}
