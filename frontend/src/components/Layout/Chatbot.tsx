"use client";

import { useState, useRef, useEffect } from "react";
import Image from "next/image";
import ReactMarkdown from "react-markdown";
import toast from "react-hot-toast";

// ✅ 코드블럭 커스텀 렌더링
const markdownComponents = {
  code({ node, inline, className = "", children, ...props }: any) {
    const language = className.replace("language-", "") || "plaintext";
    const codeText = String(children).trim();

    if (inline) {
      return (
        <code className="bg-gray-100 text-sm px-1 rounded text-red-500">
          {children}
        </code>
      );
    }

    // ✅ json 코드블록만 스타일 적용
    if (language === "json") {
      return (
        <div className="relative bg-[#F8F9FA] border border-gray-200 rounded-md my-4 overflow-hidden group">
          <div className="flex justify-between items-center px-3 py-1 bg-[#6c757d] text-white text-xs font-mono">
            <span>{language}</span>
            <button
              onClick={() => {
                navigator.clipboard.writeText(codeText);
                toast.success("복사가 완료되었어요!");
              }}
              className="text-white hover:text-gray-200"
            >
              📋 복사
            </button>
          </div>
          <pre className="whitespace-pre-wrap break-words text-sm p-4 overflow-x-auto">
            <code className="text-gray-800 block">{children}</code>
          </pre>
        </div>
      );
    }

    // ❌ json이 아니면 그냥 일반 텍스트 출력
    return (
        <span>{children}</span>
    );
  },
};


const Chatbot = () => {
  const [messages, setMessages] = useState([
    {
      type: "bot",
      text: "안녕하세요! MCPanda 챗봇입니다. 무엇을 도와드릴까요?",
    },
  ]);
  const [input, setInput] = useState("");
  const [isOpen, setIsOpen] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const textareaRef = useRef<HTMLTextAreaElement | null>(null);

  const chatUrl = "https://mcpanda.ngrok.app/chat";

  // ✅ 입력값에 따라 textarea 높이 자동 조절
  useEffect(() => {
    const textarea = textareaRef.current;
    if (textarea) {
      textarea.style.height = "auto";
      textarea.style.height = textarea.scrollHeight + "px";
    }
  }, [input]);

  const handleSend = async () => {
    const trimmedInput = input.trim();
    if (!trimmedInput) return;

    setMessages((prev) => [...prev, { type: "user", text: trimmedInput }]);
    setInput("");
    setIsLoading(true);

    try {
      const resp = await fetch(chatUrl, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          message: trimmedInput,
          top_k: 3,
          max_tokens: 1024,
        }),
      });

      if (!resp.ok) {
        throw new Error(`서버 오류: ${resp.status} ${resp.statusText}`);
      }

      const data = await resp.json();
      const reply = data?.response || "죄송해요, 답변을 가져오지 못했어요.";
      setMessages((prev) => [...prev, { type: "bot", text: reply }]);
    } catch (err: any) {
      setMessages((prev) => [
        ...prev,
        { type: "bot", text: "⚠️ 잠시 후 다시 시도해주세요." },
      ]);
      console.error("Chat error:", err);
    } finally {
      setIsLoading(false);
    }
  };

  const openModal = () => {
    setIsOpen(!isOpen);
  };

  console.log('data', messages);
  return (
    <>
      {isOpen ? (
        <div className="w-[500px] h-[600px] bg-[#F8F9FA] shadow-md rounded-lg flex flex-col overflow-hidden border border-gray-200 fixed bottom-1 right-1 z-50">
          {/* Header */}
          <div className="bg-[#0095FF] text-white px-4 py-3 flex items-center justify-between">
            <div className="font-bold text-md flex items-center gap-1">
              <Image src="/chatbot.png" alt="chatbot_img" width={32} height={32} />
              <span>MCPanda 챗봇</span>
            </div>
            <button onClick={openModal} className="text-xl font-bold cursor-pointer">
              ×
            </button>
          </div>

          {/* Message list */}
          <div className="flex-1 p-4 space-y-3 text-sm overflow-y-auto overflow-x-hidden">
            {messages.map((msg, i) => (
              <div
                key={i}
                className={`w-fit max-w-full px-3 py-2 rounded-lg break-words whitespace-pre-wrap ${msg.type === "bot"
                    ? "bg-white border border-gray-200 text-gray-800"
                    : "bg-blue-100 text-gray-900 ml-auto text-right"
                  }`}
              >
                <ReactMarkdown components={markdownComponents}>
                  {msg.text}
                </ReactMarkdown>
              </div>
            ))}
            {isLoading && (
              <div className="text-gray-500 text-sm animate-pulse">
                답변을 생성 중입니판다...
              </div>
            )}
          </div>

          {/* Input area */}
          <div className="flex items-center border-t border-gray-200 px-2 py-3 gap-2">
            <textarea
              ref={textareaRef}
              value={input}
              rows={1}
              className="flex-1 text-sm px-3 py-2 rounded-md border border-gray-300 focus:outline-none resize-none overflow-hidden max-h-40 overflow-y-auto"
              placeholder="질문을 입력하세요."
              onChange={(e) => setInput(e.target.value)}
              onKeyDown={(e) => {
                if (e.key === "Enter" && !e.shiftKey) {
                  e.preventDefault();
                  handleSend();
                }
              }}
            />

            <button
              className="bg-blue-100 p-2 rounded-md disabled:opacity-50"
              onClick={handleSend}
              disabled={isLoading}
            >
              🐼
            </button>
          </div>
        </div>
      ) : (
        <Image
          src="/chatbot.png"
          alt="chatbot_img"
          width={80}
          height={80}
          className="w-20 fixed bottom-6 right-6 cursor-pointer z-40"
          onClick={openModal}
        />
      )}
    </>
  );
};

export default Chatbot;
