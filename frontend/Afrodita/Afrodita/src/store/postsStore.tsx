import { create } from "zustand";
import axios from "axios";
import { useAuthStore } from "./authStore"; // ✅ Import auth store

interface Post {
  id: number;
  content: string;
  imageUrl?: string;
  createdAt: string;
  username: string;
  userAvatar?: string;
  likedByUser: boolean;
  likeCount: number;
}

interface PostsState {
  posts: Post[];
  fetchPosts: () => Promise<void>;
  likedPosts: Post[]; // ✅ Add liked posts
  fetchLikedPosts: () => Promise<void>; // ✅ New function
  createPost: (content: string, imageUrl?: string) => Promise<void>;
  deletePost: (postId: number) => Promise<void>;
  likePost: (postId: number) => Promise<void>;
  fetchUserPosts: () => Promise<void>;
}

export const usePostsStore = create<PostsState>((set) => ({
  posts: [],
  likedPosts: [], // ✅ Initialize liked posts

  fetchLikedPosts: async () => {
    try {
      const token = localStorage.getItem("token");
      if (!token) return;

      const response = await axios.get("http://localhost:8081/api/posts/liked", {
        headers: { Authorization: `Bearer ${token}` },
      });

      if (response.status === 200) {
        set({ likedPosts: response.data.content });
      }
    } catch (error) {
      console.error("❌ Failed to fetch liked posts:", error);
    }
  },

  fetchPosts: async () => {
    try {
      const token = localStorage.getItem("token");
      if (!token) return;
  
      const response = await axios.get("http://localhost:8081/api/posts/all", {
        headers: { Authorization: `Bearer ${token}` },
        params: { page: 0, size: 10 },
      });
  
      if (response.status === 200) {
        set({ posts: response.data.content });
      }
    } catch (error) {
      console.error("❌ Failed to fetch posts:", error);
    }
  },  

  createPost: async (content, imageUrl) => {
    try {
      const token = localStorage.getItem("token");
      if (!token) throw new Error("No token available");

      const response = await axios.post(
        "http://localhost:8081/api/posts/create",
        { content, imageUrl },
        {
          headers: { Authorization: `Bearer ${token}` },
        }
      );

      if (response.status === 200) {
        set((state) => ({ posts: [response.data, ...state.posts] }));
      }
    } catch (error) {
      console.error("❌ Failed to create post:", error);
    }
  },

  deletePost: async (postId) => {
    try {
      const token = localStorage.getItem("token");
      if (!token) throw new Error("No token available");

      const response = await axios.delete(
        `http://localhost:8081/api/posts/${postId}`,
        { headers: { Authorization: `Bearer ${token}` } }
      );

      if (response.status === 200) {
        set((state) => ({
          posts: state.posts.filter((post) => post.id !== postId),
        }));
      }
    } catch (error) {
      console.error("❌ Failed to delete post:", error);
    }
  },

  likePost: async (postId) => {
    try {
      const token = localStorage.getItem("token");
      if (!token) throw new Error("No token available");

      await axios.post(
        `http://localhost:8081/api/likes/${postId}`,
        {},
        { headers: { Authorization: `Bearer ${token}` } }
      );

      set((state) => ({
        posts: state.posts.map((post) =>
          post.id === postId ? { ...post, liked: !post.liked } : post
        ),
      }));
    } catch (error) {
      console.error("❌ Failed to like/unlike post:", error);
    }
  },

  fetchUserPosts: async () => {
    const token = localStorage.getItem("token");
    if (!token) return;

    // ✅ Get user from authStore
    const user = useAuthStore.getState().user;
    if (!user) return;

    try {
      const response = await axios.get(`http://localhost:8081/api/posts/user/${user.username}`, {
        headers: { Authorization: `Bearer ${token}` },
      });

      if (response.status === 200) {
        set({ posts: response.data.content });
      }
    } catch (error) {
      console.error("❌ Failed to fetch user posts", error);
    }
  },
}));
