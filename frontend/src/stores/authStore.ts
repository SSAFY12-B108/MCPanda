import { create } from 'zustand';

interface User {
  _id: string;
  email: string;
  name: string;
  profileImage: string;
  createdAt: string;
}

interface AuthState {
  user: User | null;
  isLoggedIn: boolean;
  login: (userData: User) => void;
  logout: () => void;
}

const useAuthStore = create<AuthState>((set: (newState: Partial<AuthState>) => void) => ({
  user: null,
  isLoggedIn: false,
  login: (userData: User) => set({ user: userData, isLoggedIn: true }),
  logout: () => set({ user: null, isLoggedIn: false }),
}));

export default useAuthStore;
