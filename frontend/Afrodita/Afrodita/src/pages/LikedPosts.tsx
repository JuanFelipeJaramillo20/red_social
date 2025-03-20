import { useEffect, useState } from "react";
import { usePostsStore } from "@/store/postsStore";
import { useAuthStore } from "@/store/authStore";
import PostCard from "@/components/PostCard";
import { Card } from "@/components/ui/card";
import { Skeleton } from "@/components/ui/skeleton";

export default function LikedPosts() {
  const { fetchLikedPosts, likedPosts } = usePostsStore();
  const { user } = useAuthStore();
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const loadLikedPosts = async () => {
      await fetchLikedPosts();
      setLoading(false);
    };

    loadLikedPosts();
  }, []);

  if (loading) {
    return (
      <div className="flex flex-col items-center min-h-screen bg-gray-100 p-4">
        <Card className="w-[600px] p-6 shadow-lg rounded-lg bg-white mb-6">
          <div className="flex flex-col items-center">
            <Skeleton className="w-32 h-6 mb-2" />
            <Skeleton className="w-24 h-4" />
          </div>
        </Card>
        <div className="w-[600px]">
          <Skeleton className="w-full h-20 mb-4" />
          <Skeleton className="w-full h-20 mb-4" />
          <Skeleton className="w-full h-20 mb-4" />
        </div>
      </div>
    );
  }

  return (
    <div className="flex flex-col items-center min-h-screen bg-gray-100">
      <h2 className="text-lg font-semibold mb-4">Posts Que Me Han Gustado</h2>

      {Array.isArray(likedPosts) && likedPosts.length === 0 ? (
        <p className="text-gray-500 text-center">No has dado me gusta a ningún post aún.</p>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6 px-4">
          {Array.isArray(likedPosts) &&
            likedPosts.map((post) => (
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
            ))}
        </div>
      )}
    </div>
  );
}
