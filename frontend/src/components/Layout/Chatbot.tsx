// components/Chatbot.tsx
"use client"
import { useState } from "react";

const Chatbot = () => {
    const [messages, setMessages] = useState([
        { type: "bot", text: "안녕하세요! MCPanda 챗봇입니다. 무엇을 도와드릴까요?" },
        { type: "user", text: "MCP 초보자인데 추천 조합 알려주세요." }
    ]);
    const [input, setInput] = useState("");
    //열고 닫기 
    const [isOpen, setIsOpen] = useState(false)
    const [isClosed, setIsClosed] = useState(false)


    const handleSend = () => {
        if (!input.trim()) return;
        setMessages((prev) => [...prev, { type: "user", text: input }]);
        setInput("");
    };

    const openModal = () => {
        setIsOpen(!isOpen)
    }


    return (
        <>
            {isOpen ?

                <div className="w-[360px] h-[600px] bg-[#F8F9FA] shadow-md rounded-lg flex flex-col overflow-hidden border border-gray-200 fixed bottom-1 right-1">
                    {/* Header */}
                    <div className="bg-[#0095FF] text-white px-4 py-3 flex items-center justify-between">
                        <div className="font-bold text-md flex items-center gap-1">
                            <img src="chatbot.png" alt="chatbot_img" className='w-8' />
                            <span>MCPanda 챗봇</span>
                        </div>
                        <button onClick={openModal} className="text-xl font-bold">X</button>
                    </div>

                    {/* Quick buttons */}
                    <div className="flex justify-center gap-4 p-3">
                        {["Json 파일 작성법", "초보자 가이드", "자주 묻는 질문"].map((text) => (
                            <button
                                key={text}
                                className="text-xs border border-[#0095FF] text-[#0095FF] rounded-full px-2 py-1"
                            >
                                {text}
                            </button>
                        ))}
                    </div>

                    {/* Message list */}
                    <div className="flex-1 p-4 space-y-3 text-sm">
                        {messages.map((msg, i) => (
                            <div
                                key={i}
                                className={`max-w-[80%] px-3 py-2 rounded-lg ${msg.type === "bot"
                                    ? "bg-white border border-gray-200 text-gray-800 self-start"
                                    : "bg-blue-100 text-gray-900 self-end ml-auto"
                                    }`}
                            >
                                {msg.text}
                            </div>
                        ))}
                    </div>

                    {/* Input area */}
                    <div className="flex items-center border-t border-gray-200 px-2 py-3 gap-2">
                        <textarea
                            rows={1}
                            className="flex-1 text-sm px-3 py-2 rounded-md border border-gray-300 focus:outline-none"
                            placeholder="질문을 입력하세요."

                            onChange={(e) => setInput(e.target.value)}
                        />
                        <button
                            className="bg-blue-100 p-2 rounded-md"
                            onClick={handleSend}
                        >
                            🐼
                        </button>
                    </div>
                </div>
                : <img src="chatbot.png" alt="chatbot_img" className="w-20 fixed bottom-6 right-6 cursor-pointer z-50"
                    onClick={openModal} />
            }


        </>
    );
};

export default Chatbot;
