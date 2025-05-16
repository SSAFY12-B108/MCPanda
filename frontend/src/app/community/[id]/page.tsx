// app/community/[id]/page.tsx
import { QueryClient, dehydrate, HydrationBoundary } from '@tanstack/react-query';
import { ArticleDetail } from '@/hooks/useArticle';
import ArticleDetailClient from '@/components/community/ArticleDetailClient';
import { Metadata } from 'next';

// SEO를 위한 메타데이터 생성
export async function generateMetadata({ params }: { params: { id: string } }): Promise<Metadata> {
  try {
    const article = await fetchArticleDetail(params.id);
    // HTML 태그 제거하고 순수 텍스트만 추출
    const plainTextContent = article.content.replace(/<[^>]*>/g, '').substring(0, 160);
    
    return {
      title: `${article.title} | 커뮤니티`,
      description: plainTextContent,
      // Open Graph 태그 추가 (소셜 미디어 공유용)
      openGraph: {
        title: article.title,
        description: plainTextContent,
        type: 'article',
        // URL은 현재 페이지 URL로 자동 설정됨
        publishedTime: article.createdAt,
        // 작성자 정보가 있다면 추가
        authors: article.author?.nickname ? [article.author.nickname] : undefined,
      },
      // Twitter 카드 설정 (트위터 공유용)
      twitter: {
        card: 'summary',
        title: article.title,
        description: plainTextContent,
      },
    };
  } catch (error) {
    console.error('메타데이터 생성 중 오류:', error);
    // 기본 메타데이터 반환
    return {
      title: '게시글 | 커뮤니티',
      description: '커뮤니티 게시글 상세 페이지입니다.',
    };
  }
}

// 서버 사이드에서 게시글 상세 데이터 가져오기
async function fetchArticleDetail(articleId: string): Promise<ArticleDetail> {
  try {
    const res = await fetch(`${process.env.NEXT_PUBLIC_API_URL}/api/articles/${articleId}`, {
      cache: 'no-store',
      headers: {
        'Content-Type': 'application/json',
      },
    });

    if (!res.ok) {
      throw new Error(`게시글을 불러오는데 실패했습니다. 상태 코드: ${res.status}`);
    }

    return res.json();
  } catch (error) {
    console.error('게시글 데이터 불러오기 실패:', error);
    throw error;
  }
}

export default async function Page({ params }: { params: { id: string } }) {
  const queryClient = new QueryClient();
  
  try {
    // 게시글 데이터 미리 가져오기
    await queryClient.prefetchQuery({
      queryKey: ['article', params.id],
      queryFn: () => fetchArticleDetail(params.id),
    });
  } catch (error) {
    // 에러 로깅만 하고 계속 진행 - 클라이언트 컴포넌트에서 재시도하게 함
    console.error('SSR 데이터 로딩 실패:', error);
  }
    
  // 성공하든 실패하든 클라이언트 컴포넌트로 넘기기
  // ArticleDetailClient에 이미 에러 처리 로직이 있음
  const dehydratedState = dehydrate(queryClient);
  
  return (
    <HydrationBoundary state={dehydratedState}>
      <ArticleDetailClient articleId={params.id} />
    </HydrationBoundary>
  );
}