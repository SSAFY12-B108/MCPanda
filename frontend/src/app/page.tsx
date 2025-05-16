// app/page.tsx

import { dehydrate, HydrationBoundary, QueryClient } from '@tanstack/react-query';
import { fetchMainArticles } from '@/hooks/useMainPage';
import HomeClient from '@/components/Home/HomeClient';

export default async function Home() {
  const queryClient = new QueryClient();

  await queryClient.prefetchQuery({
    queryKey: ['mainArticles'],
    queryFn: fetchMainArticles,
  });

  return (
    <HydrationBoundary state={dehydrate(queryClient)}>
      <HomeClient /> 
    </HydrationBoundary>
  );
}
