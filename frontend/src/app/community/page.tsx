// app/community/page.tsx
import { QueryClient, dehydrate, HydrationBoundary } from '@tanstack/react-query';
import { ArticlesResponse } from '@/hooks/useArticle';
import CommunityClient from '@/components/community/CommunityClient';

async function fetchInitialArticles(): Promise<ArticlesResponse> {
  const res = await fetch(`${process.env.NEXT_PUBLIC_API_URL}/api/articles`, {
    cache: 'no-store',
  });
  if (!res.ok) throw new Error('게시글 데이터를 불러오지 못했습니다.');
  return res.json();
}

export default async function Page() {
  const queryClient = new QueryClient();
  const initialParams = { type: 'recommend', page: 1 };

  await queryClient.prefetchQuery({
    queryKey: ['articles', initialParams],
    queryFn: () => fetchInitialArticles(),
  });

  const dehydratedState = dehydrate(queryClient);
  

  return (
    <HydrationBoundary state={dehydratedState}>
      <CommunityClient />
    </HydrationBoundary>
  );
}
