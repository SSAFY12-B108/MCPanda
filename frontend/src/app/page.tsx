'use client';

import CombinationBox from "@/components/Home/CombinationBox";
import Header from "@/components/Layout/Header";
import Chatbot from '@/components/Layout/Chatbot';
import { useMainPage } from '@/hooks/useMainPage';
import Image from "next/image";

export default function Home() {
  const { data: articles, isLoading, isError } = useMainPage();

  return (
    <>
      <div>
        <Header />
        <div className=''>
          <div className="mx-auto w-[920px] px-20">

            <div className="flex justify-between items-center mb-4">
              {/* 왼쪽: 타이틀 텍스트 */}
              <div className="font-bold text-[25px] leading-tight text-gray-900">
                <div>MCP 조합,</div>
                <div>MCPanda와 함께</div>
                <div>쉽게 시작하세요</div>
              </div>

              <Image
                src="/logo.png"
                alt="MCPanda_Logo"
                width={160}
                height={160}
                className="w-40"
              />
            </div>

            <div className="flex justify-between items-center mb-6">
              <h1 className="text-lg font-bold text-gray-900">유저들의 추천 조합</h1>
              <select className="text-sm  p-1 text-gray-600 rounded-md border"
                style={{ borderColor: '#0095FF' }}>
                <option value="">툴 선택</option>
                <option value="react">React</option>
                <option value="notion">Notion</option>
                <option value="docker">Docker</option>
              </select>
            </div>
            <ul className="space-y-4">
              {articles?.map((article) => (
                <li key={article.articleId} className="p-4 border rounded-lg shadow-sm hover:shadow-md transition">
                  <CombinationBox title={article.title} tags={article.mcps} likes={1} />
                </li>
              ))}
            </ul>
          </div>
          <Chatbot />
        </div>
      </div>
    </>
  );
}
