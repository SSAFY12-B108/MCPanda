import './globals.css'
import type { Metadata } from 'next'
import { Inter } from 'next/font/google'
import Providers from './providers'
import { Toaster } from 'react-hot-toast';

const inter = Inter({ subsets: ['latin'] })

export const metadata: Metadata = {
  title: 'MCPanda',
  description: 'MCP 커뮤니티',
}

export default function RootLayout({
  children,
}: {
  children: React.ReactNode
}) {
  return (
    <html lang="ko">
      <body className={inter.className}>
        <Providers>
          {children}
          <Toaster position="top-center" toastOptions={{
            success: {
              // iconTheme 설정으로 아이콘 색상 변경
              iconTheme: {
                primary: '#0091E2',  // 원형 배경 색상 (파란색)
                secondary: 'white',  // 체크마크 색상 (흰색)
              },
              // 추가로 토스트 전체 스타일도 설정 가능 (선택사항)
              style: {
                background: '#fff',  // 토스트 배경색 (흰색)
                color: '#333',       // 토스트 텍스트 색상
              },
            },
          }} />
        </Providers>
      </body>
    </html>
  )
}