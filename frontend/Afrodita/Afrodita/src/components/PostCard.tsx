import { useState, useEffect } from "react";
import { Card } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import { usePostsStore } from "@/store/postsStore";

interface PostProps {
    id: number;
    content: string;
    imageUrl?: string;
    createdAt: string;
    username: string;
    userAvatar?: string;
    isOwner: boolean;
    likedByUser: boolean;
    likeCount: number; // ✅ New field
  }  

  export default function PostCard({
    id,
    content,
    imageUrl,
    createdAt,
    username,
    userAvatar,
    isOwner,
    likedByUser,
    likeCount, // ✅ Include like count
  }: PostProps) {
    const { deletePost, likePost } = usePostsStore();
    const [liked, setLiked] = useState(likedByUser);
    const [likes, setLikes] = useState(likeCount); // ✅ Use `likeCount`
  
    useEffect(() => {
      setLiked(likedByUser);
      setLikes(likeCount); // ✅ Update count when reloading
    }, [likedByUser, likeCount]);
  
    const handleLike = async () => {
      await likePost(id);
      setLiked(!liked);
      setLikes((prev) => (liked ? prev - 1 : prev + 1)); // ✅ Update count
    };
  
    return (
      <Card className="w-[600px] p-6 shadow-lg rounded-lg bg-white mb-6">
        <div className="flex items-center gap-4">
          <Avatar className="w-12 h-12">
            <AvatarImage src={userAvatar || "/default-avatar.png"} alt="User Avatar" />
            <AvatarFallback>{username.charAt(0).toUpperCase()}</AvatarFallback>
          </Avatar>
          <div>
            <h2 className="text-lg font-semibold">{username}</h2>
            <p className="text-sm text-gray-500">{new Date(createdAt).toLocaleString()}</p>
          </div>
        </div>
  
        {imageUrl && (
          <div className="mt-2 w-full h-48 overflow-hidden rounded-lg">
            <img src={imageUrl} alt="Post Image" className="w-full h-full object-cover" />
          </div>
        )}

        <p className="mt-3 text-gray-900">{content}</p>
  
        {/* Actions */}
        <div className="flex justify-between mt-3">
          <Button onClick={handleLike} variant={liked ? "secondary" : "outline"}>
            {liked ? "❤️ Liked" : "🤍 Like"} ({likes}) {/* ✅ Display like count */}
          </Button>
  
          {isOwner && (
            <Button onClick={() => deletePost(id)} variant="destructive">
              🗑 Delete
            </Button>
          )}
        </div>
      </Card>
    );
  }
  