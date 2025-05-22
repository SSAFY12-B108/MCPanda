import useAuthStore from '@/stores/authStore';
import client from '@/api/client';
import { useRouter } from 'next/navigation';

const useAuth = () => {
  const authStoreLogout = useAuthStore((state) => state.logout);
  const router = useRouter();

  const logout = async () => {
    try {
      await client.post('/auth/logout');
      authStoreLogout();
      router.push('/');
    } catch (error) {
      console.error('Logout failed:', error);
    }
  };

  return {
    logout,
  };
};

export default useAuth;
