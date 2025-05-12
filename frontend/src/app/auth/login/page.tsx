import Header from "@/components/Layout/Header"
import Chatbot from "@/components/Layout/Chatbot"

function page() {
    return (
        <div>
            <Header />
            <div className="flex flex-col items-center mt-10">
                <img src="/loginCharacter.png" alt="loginCharacter" className="w-40 mb-8"/>
                <div className="text-xl font-bold text-center mb-6">MCP 사용법을 쉽게 알려드려요</div>
                <img src="/googleLogin.png" alt="googleLoginBttn" className="w-70 mb-2"/>
                <img src="/githubLogin.png" alt="githubLoginBttn" className="w-70"/>
            </div>
            <Chatbot/>
        </div>
    )
}

export default page