import { useQuery } from '@tanstack/react-query';
import axios from 'axios';

export interface Article {
  articleId: string;
  title: string;
  mcps: string[];
}

const fetchMainArticles = async (): Promise<Article[]> => {
  const response = await axios.get('/api/main' );
  return response.data;
};

export const useMainPage = () => {
  return useQuery({
    queryKey: ['mainArticles'],
    queryFn: () => fetchMainArticles(),
  });
};
