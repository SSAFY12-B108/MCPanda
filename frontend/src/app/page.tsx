
import CombinationBox from "@/components/Home/CombinationBox";
import Header from "@/components/Layout/Header";
import Chatbot from '@/components/Layout/Chatbot';


export default function Home() {
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

              <img
                src="/logo.png"
                alt="MCPanda_Logo"
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
            <CombinationBox title="이거 추천함용ㅇㅅㅇㅇㅇ멍너얌너얀머얌넝" tags={['react', 'spring', 'LLM Chain of']} likes={1} />
          </div>
          <Chatbot />
        </div>
      </div>
    </>
  );
}