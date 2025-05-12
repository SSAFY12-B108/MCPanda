import Header from "@/components/Layout/Header"
import Chatbot from "@/components/Layout/Chatbot"
import Image from "next/image"

function page() {
  return (
    <div>
      <Header />
      <div className="flex flex-col items-center mt-10">
        <Image src="/loginCharacter.png" alt="loginCharacter" width={160} height={160} className="w-40 mb-8" />
        <div className="text-2xl font-bold text-center mb-6">MCP 사용법을 쉽게 알려드려요</div>
        <Image src="/googleLogin.png" alt="googleLoginBttn" width={280} height={280} className="w-70 mb-2" />
        <Image src="/githubLogin.png" alt="githubLoginBttn" width={280} height={280} className="w-70" />
      </div>
      <Chatbot />
    </div>
  )
}

export default page
