// app/page.tsx

import { dehydrate, HydrationBoundary, QueryClient } from '@tanstack/react-query';
import { fetchMainArticles } from '@/hooks/useMainPage';
import HomeClient from '@/components/Home/HomeClient';

export default async function Home() {
  const queryClient = new QueryClient();
  const defaultCategory = '';
  const defaultPage = 1;

  await queryClient.prefetchQuery({
    queryKey: ['mainArticles', defaultPage],
    queryFn: () => fetchMainArticles(defaultCategory,defaultPage),
  });

  return (
    <HydrationBoundary state={dehydrate(queryClient)}>
      <HomeClient defaultCategory={defaultCategory} defaultPage={defaultPage} />
    </HydrationBoundary>
  );
}
