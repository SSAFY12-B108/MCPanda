"use client";

import { useCallback, useEffect } from "react";
import { useRouter } from "next/navigation";
import Header from "@/components/Layout/Header";
import Chatbot from "@/components/Layout/Chatbot";
import Image from "next/image";

import useAuthStore from "@/stores/authStore";

export default function LoginPage() {
  const isLoggedIn = useAuthStore((state) => state.isLoggedIn);
  const router = useRouter();

  useEffect(() => {
    if (isLoggedIn) {
      router.push("/");
    }
  }, [isLoggedIn, router]);

  const handleLogin = useCallback((provider: "google" | "github") => {
    window.location.href = `/api/auth/oauth2/authorization/${provider}`;
  }, []);

  return (
    <div>
      <Header />
      <div className="flex flex-col items-center mt-10">
        <Image
          src="/loginCharacter.png"
          alt="loginCharacter"
          width={160}
          height={160}
          className="w-40 mb-8"
        />
        <div className="text-2xl font-bold text-center mb-6">
          MCP 사용법을 쉽게 알려드려요
        </div>
        <Image
          src="/googleLogin.png"
          alt="googleLoginBttn"
          width={280}
          height={280}
          className="w-70 mb-2 cursor-pointer"
          onClick={() => handleLogin("google")}
        />
        <Image
          src="/githubLogin.png"
          alt="githubLoginBttn"
          width={280}
          height={280}
          className="w-70 cursor-pointer"
          onClick={() => handleLogin("github")}
        />
      </div>
      <Chatbot />
    </div>
  );
}
