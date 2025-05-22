"use client";

import { useEffect } from "react";
import { useRouter } from "next/navigation";
import useAuthStore from "@/stores/authStore";

export default function CallbackPage() {
  const router = useRouter();
  const login = useAuthStore((state) => state.login);

  useEffect(() => {
    const fetchUser = async () => {
      try {
        const res = await fetch(`/api/members/me`, {
          method: "GET",
          credentials: "include", // ✅ 쿠키 포함해서 요청
        });

        console.log(res)

        if (!res.ok) throw new Error("유저 정보를 불러오지 못했습니다");

        const data = await res.json();
        console.log("유저 로그인 성공:", data);


        login(data); // zustand에 로그인 정보 저장
        router.replace("/"); // 홈으로 이동 (또는 다른 페이지)
      } catch (error) {
        console.error("로그인 실패:", error);
        router.replace("/auth/login?error=true"); // 실패 시 로그인 페이지로 이동
      }
    };

    fetchUser();
  }, [login, router]);

  return (
    <div className="flex justify-center items-center h-screen text-xl font-semibold">
      로그인 처리 중입니다...
    </div>
  );
}
