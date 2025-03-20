import { useState, FormEvent } from "react";
import { usePostsStore } from "@/store/postsStore";
import { useNavigate } from "react-router-dom";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { Card } from "@/components/ui/card";
import { Label } from "@/components/ui/label";
import { Textarea } from "@/components/ui/textarea";
import { Skeleton } from "@/components/ui/skeleton";

export default function CreatePost() {
  const navigate = useNavigate();
  const { createPost } = usePostsStore();
  const [content, setContent] = useState<string>("");
  const [imageUrl, setImageUrl] = useState<string>("");
  const [loading, setLoading] = useState<boolean>(false);
  const [error, setError] = useState<string | null>(null);

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    setError(null);

    if (!content.trim()) {
      setError("El contenido no puede estar vacío.");
      return;
    }

    setLoading(true);
    try {
      await createPost(content, imageUrl || null);
      navigate("/profile"); // Redirect to profile after posting
    } catch (err) {
      setError("Hubo un error al publicar el post.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="flex justify-center items-center min-h-screen bg-gray-100">
      <Card className="w-[600px] p-6 shadow-lg rounded-lg bg-white">
        <h2 className="text-xl font-semibold text-center mb-4">Crear Nuevo Post</h2>
        {error && <p className="text-red-500 text-center">{error}</p>}

        <form onSubmit={handleSubmit} className="space-y-4">
          {/* Post Content */}
          <div>
            <Label htmlFor="content">Contenido</Label>
            <Textarea
              id="content"
              value={content}
              onChange={(e) => setContent(e.target.value)}
              placeholder="Escribe algo..."
              rows={4}
              required
            />
          </div>

          {/* Image URL (Optional) */}
          <div>
            <Label htmlFor="imageUrl">Imagen (opcional)</Label>
            <Input
              id="imageUrl"
              value={imageUrl}
              onChange={(e) => setImageUrl(e.target.value)}
              placeholder="URL de la imagen"
            />
          </div>

          {/* Image Preview */}
          {imageUrl && (
            <div className="mt-2 w-full h-48 overflow-hidden rounded-lg">
              <img src={imageUrl} alt="Post Preview" className="w-full h-full object-cover" />
            </div>
          )}

          {/* Submit Button */}
          <Button type="submit" className="w-full" disabled={loading}>
            {loading ? "Publicando..." : "Publicar"}
          </Button>

          {/* Loading State */}
          {loading && <Skeleton className="w-full h-8 mt-2" />}
        </form>
      </Card>
    </div>
  );
}
