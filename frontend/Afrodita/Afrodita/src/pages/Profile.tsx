import { useEffect, useState } from "react";
import { useAuthStore } from "@/store/authStore";
import { usePostsStore } from "@/store/postsStore";
import { Card } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import { Skeleton } from "@/components/ui/skeleton";
import PostCard from "@/components/PostCard";
import EditProfileModal from "@/components/EditProfileModal";

export default function Profile() {
  const { user, fetchUser } = useAuthStore();
  const { posts, fetchUserPosts } = usePostsStore();
  const [loading, setLoading] = useState(true);
  const [isEditing, setIsEditing] = useState(false); // ✅ State for modal visibility

  useEffect(() => {
    const loadUserData = async () => {
      await fetchUser();
      await fetchUserPosts();
      setLoading(false);
    };

    loadUserData();
  }, []);

  if (loading) {
    return (
      <div className="flex flex-col items-center min-h-screen bg-gray-100 p-4">
        <Card className="w-[600px] p-6 shadow-lg rounded-lg bg-white mb-6">
          <div className="flex flex-col items-center">
            <Skeleton className="w-24 h-24 rounded-full mb-4" />
            <Skeleton className="w-32 h-6 mb-2" />
            <Skeleton className="w-24 h-4" />
          </div>
          <div className="mt-4 space-y-2">
            <Skeleton className="w-full h-4" />
            <Skeleton className="w-full h-4" />
            <Skeleton className="w-1/2 h-4" />
          </div>
        </Card>
      </div>
    );
  }

  return (
    <div className="flex flex-col items-center min-h-screen bg-gray-100">
      {/* User Info Card */}
      <Card className="w-[600px] p-6 shadow-lg rounded-lg bg-white mb-6">
        <div className="flex flex-col items-center">
          <Avatar className="w-24 h-24 mb-4">
            <AvatarImage src={user?.avatarUrl || "/default-avatar.png"} alt="User Avatar" />
            <AvatarFallback>{user?.username?.charAt(0).toUpperCase()}</AvatarFallback>
          </Avatar>
          <h2 className="text-xl font-semibold">{user?.fullName}</h2>
          <p className="text-gray-500">@{user?.username}</p>
        </div>

        <div className="mt-4 space-y-2">
          <p><strong>Correo electrónico:</strong> {user?.email}</p>
          <p><strong>Fecha de registro:</strong> {user?.createdAt ? new Date(user.createdAt).toLocaleDateString() : "N/A"}</p>
          {user?.updatedAt && <p><strong>Última actualización:</strong> {new Date(user.updatedAt).toLocaleDateString()}</p>}
        </div>

        {/* Edit Profile Button */}
        <div className="mt-4 flex justify-center">
          <Button variant="secondary" onClick={() => setIsEditing(true)}>
            ✏️ Editar Perfil
          </Button>
        </div>
      </Card>

      <EditProfileModal isOpen={isEditing} onClose={() => setIsEditing(false)} />

      {/* User Posts */}
      <div className="w-full max-w-8xl px-4">
        <h2 className="text-lg font-semibold mb-10">Publicaciones</h2>

        {Array.isArray(posts) && posts.length === 0 ? (
          <p className="text-gray-500 text-center">No hay publicaciones aún.</p>
        ) : (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6 ml-30">
            {Array.isArray(posts) &&
              posts.map((post) => (
                <PostCard
                  key={post.id}
                  id={post.id}
                  content={post.content}
                  imageUrl={post.imageUrl}
                  createdAt={post.createdAt}
                  username={post.createdBy}
                  userAvatar={user?.avatarUrl}
                  isOwner={post.createdBy === user?.username}
                  likedByUser={post.likedByUser} // ✅ Fix: Pass the likedByUser value
                  likeCount={post.likeCount} // ✅ Fix: Pass the likeCount value
                />
              ))}
          </div>
        )}
      </div>
    </div>
  );
}
