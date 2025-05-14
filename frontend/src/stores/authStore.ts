import { create } from 'zustand';
import { persist, createJSONStorage } from 'zustand/middleware';

interface User {
  id: string;
  email: string;
  name: string;
  profileImage: string;
  createdAt: string;
}

interface AuthState {
  user: { id: string } | null;
  isLoggedIn: boolean;
  login: (userData: User) => void;
  logout: () => void;
}

const useAuthStore = create<AuthState>()(
  persist(
    (set) => ({
      user: null,
      isLoggedIn: false,
      login: (userData: User) => set({ user: { id: userData.id }, isLoggedIn: true }),
      logout: () => set({ user: null, isLoggedIn: false }),
    }),
    {
      name: 'auth-storage', // unique name
      storage: createJSONStorage(() => localStorage), // use localStorage
      partialize: (state) => ({ user: state.user, isLoggedIn: state.isLoggedIn }), // specify which parts of the state to store
    }
  )
);

export default useAuthStore;
