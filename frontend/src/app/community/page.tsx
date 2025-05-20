// app/community/page.tsx
import { QueryClient, dehydrate, HydrationBoundary } from '@tanstack/react-query';
import { ArticlesResponse } from '@/hooks/useArticle';
import CommunityClient from '@/components/community/CommunityClient';
import { Metadata } from 'next';

// SEO를 위한 정적 메타데이터 (기본 설정)
export const metadata: Metadata = {
  title: '커뮤니티 | 개발자 MCP 조합 공유 플랫폼',
  description: '개발자들을 위한 MCP 조합 공유 커뮤니티입니다. 최신 트렌드와 기술 정보, 개발 팁을 공유하고 토론해보세요.',
  keywords: ['개발자 커뮤니티', '프로그래밍', '코딩', '기술 공유', '개발 팁', 'MCP', 'MCP 조합'],
  openGraph: {
    title: '커뮤니티 | 개발자 MCP 조합 공유 플랫폼',
    description: '개발자들을 위한 MCP 조합 공유 커뮤니티입니다. 최신 트렌드와 기술 정보, 개발 팁을 공유하고 토론해보세요.',
    type: 'website',
    // 도메인 URL 설정
    url: 'https://mcpanda.co.kr/community',
    siteName: '개발자 커뮤니티',
  },
  twitter: {
    card: 'summary_large_image',
    title: '커뮤니티 | 개발자 MCP 조합 공유 플랫폼',
    description: '개발자들을 위한 MCP 조합 공유 커뮤니티입니다. 최신 트렌드와 기술 정보, 개발 팁을 공유하고 토론해보세요.',
  },
};

// 동적으로 메타데이터를 생성하려면 아래 함수 사용 (선택적)
// export async function generateMetadata(): Promise<Metadata> {
//   try {
//     // 인기 게시글 데이터 가져오기
//     const articlesData = await fetchInitialArticles();
//     const popularTopics = articlesData.articles
//       .slice(0, 3)
//       .map(article => article.title)
//       .join(', ');
    
//     return {
//       title: '커뮤니티 | 개발자 지식 공유 플랫폼',
//       description: `개발자들을 위한 지식 공유 커뮤니티입니다. 인기 주제: ${popularTopics}`,
//       // 나머지 메타데이터는 정적 메타데이터와 동일
//     };
//   } catch (error) {
//     // 오류 시 기본 메타데이터 반환
//     return metadata;
//   }
// }

// 서버 사이드에서 초기 게시글 목록 데이터 가져오기
async function fetchInitialArticles(): Promise<ArticlesResponse> {
  const res = await fetch(`/api/articles?type=latest&page=1`, {
    cache: 'no-store',
  });
  
  if (!res.ok) throw new Error('게시글 데이터를 불러오지 못했습니다.');
  return res.json();
}

export default async function Page() {
  const queryClient = new QueryClient();
  const initialParams = { type: 'latest', page: 1 };

  try {
    await queryClient.prefetchQuery({
      queryKey: ['articles', initialParams],
      queryFn: () => fetchInitialArticles(),
    });
  } catch (error) {
    console.error('초기 게시글 데이터 로딩 실패:', error);
    // 에러 처리는 클라이언트 컴포넌트에 위임
  }

  const dehydratedState = dehydrate(queryClient);

  return (
    <HydrationBoundary state={dehydratedState}>
      <CommunityClient />
    </HydrationBoundary>
  );
}
