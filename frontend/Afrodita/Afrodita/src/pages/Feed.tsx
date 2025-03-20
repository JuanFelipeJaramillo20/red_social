import { useEffect, useState } from "react";
import { usePostsStore } from "@/store/postsStore";
import { useAuthStore } from "@/store/authStore";
import PostCard from "@/components/PostCard";
import { Skeleton } from "@/components/ui/skeleton";

export default function Feed() {
  const { posts, fetchPosts } = usePostsStore();
  const { user } = useAuthStore();
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const loadPosts = async () => {
      await fetchPosts();
      setLoading(false);
    };

    loadPosts();
  }, []);

  return (
    <div className="flex flex-col items-center min-h-screen bg-gray-100 px-4 py-6">
      <h2 className="text-2xl font-semibold mb-6">📢 Feed</h2>

      {loading ? (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {[...Array(6)].map((_, index) => (
            <Skeleton key={index} className="w-[600px] h-72 rounded-lg" />
          ))}
        </div>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {posts.length === 0 ? (
            <p className="text-gray-500 text-center">No hay publicaciones aún.</p>
          ) : (
            posts.map((post) => (
              <PostCard
                key={post.id}
                id={post.id}
                content={post.content}
                imageUrl={post.imageUrl}
                createdAt={post.createdAt}
                username={post.createdBy}
                userAvatar={post.userAvatar}
                isOwner={post.createdBy === user?.username}
                likedByUser={post.likedByUser}
                likeCount={post.likeCount}
              />
            ))
          )}
        </div>
      )}
    </div>
  );
}
