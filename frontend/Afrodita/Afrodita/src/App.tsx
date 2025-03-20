import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import Layout from "@/components/Layout";
import Login from "@/pages/Login";
import Profile from "@/pages/Profile";
import Feed from "@/pages/Feed";
import CreatePost from "@/pages/CreatePost";
import LikedPosts from "./pages/LikedPosts";

export default function App() {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<Login />} />

        <Route path="/" element={<Layout />}>
          <Route path="profile" element={<Profile />} />
          <Route path="create-post" element={<CreatePost />} />
          <Route path="liked-posts" element={<LikedPosts />} />
          <Route path="feed" element={<Feed />} />
        </Route>
      </Routes>
    </Router>
  );
}
