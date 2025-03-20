import { create } from "zustand";
import axios from "axios";

interface DecodedUser {
  id: number;
  username: string;
  email: string;
  fullName: string;
  avatarUrl: string;
  createdAt: string;
  updatedAt?: string | null;
}

interface Post {
  id: number;
  content: string;
  imageUrl: string | null;
  createdAt: string;
}

interface AuthState {
  token: string | null;
  user: DecodedUser | null;
  posts: Post[];
  login: (username: string, password: string) => Promise<void>;
  fetchUser: () => Promise<void>;
  logout: () => void;
  updateProfile: (profileData: { fullName: string; avatarUrl: string }) => Promise<void>; // ✅ Corrected type
}

export const useAuthStore = create<AuthState>((set, get) => ({
  token: localStorage.getItem("token") || null,
  user: localStorage.getItem("user") ? JSON.parse(localStorage.getItem("user")!) : null,
  posts: [],

  login: async (username, password) => {
    try {
      const response = await axios.post("http://localhost:8081/api/auth/login", {
        username,
        password,
      });

      if (response.status === 200 && response.data.token) {
        const token = response.data.token;
        localStorage.setItem("token", token);
        set({ token });

        await get().fetchUser();
      }
    } catch (error) {
      console.error("❌ Login failed", error);
      throw new Error("Invalid credentials");
    }
  },

  fetchUser: async () => {
    const token = localStorage.getItem("token");
    if (!token) return;

    try {
      const response = await axios.get("http://localhost:8080/api/users/me", {
        headers: { Authorization: `Bearer ${token}` },
      });

      if (response.status === 200) {
        localStorage.setItem("user", JSON.stringify(response.data));
        set({ user: response.data });
      }
    } catch (error) {
      console.error("❌ Failed to fetch user profile", error);
    }
  },

  logout: () => {
    localStorage.removeItem("token");
    localStorage.removeItem("user");
    set({ token: null, user: null, posts: [] });
  },
  updateProfile: async (profileData) => {
    try {
      const token = localStorage.getItem("token");
      if (!token) throw new Error("No token available");
  
      const response = await axios.put("http://localhost:8080/api/users/me", profileData, {
        headers: { Authorization: `Bearer ${token}` },
      });
  
      if (response.status === 200) {
        set((state) => ({
          user: {
            ...state.user!, // ✅ Ensure all existing properties are retained
            fullName: profileData.fullName,
            avatarUrl: profileData.avatarUrl,
          },
        }));
      }
    } catch (error) {
      console.error("❌ Failed to update profile:", error);
    }
  }  
}));
