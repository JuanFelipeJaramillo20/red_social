import { Link, Outlet, useNavigate } from "react-router-dom";
import { Button } from "@/components/ui/button";
import { DropdownMenu, DropdownMenuContent, DropdownMenuItem, DropdownMenuTrigger } from "@/components/ui/dropdown-menu";
import { NavigationMenu, NavigationMenuItem, NavigationMenuList } from "@/components/ui/navigation-menu";
import { useAuthStore } from "@/store/authStore";

export default function Layout() {
  const navigate = useNavigate();
  const logout = useAuthStore((state) => state.logout);

  const handleLogout = () => {
    logout();
    navigate("/");
  };

  return (
    <div className="flex flex-col min-h-screen">
      <nav className="bg-white shadow-md p-4 flex justify-between items-center">
        <Link to="/feed" className="text-lg font-bold">
          Afrodita
        </Link>

        <NavigationMenu>
          <NavigationMenuList className="flex gap-4">
          <NavigationMenuItem>
              <Link to="/feed">
                <Button variant="outline">Inicio</Button>
              </Link>
            </NavigationMenuItem>
            <NavigationMenuItem>
              <Link to="/profile">
                <Button variant="outline">Mi Perfil</Button>
              </Link>
            </NavigationMenuItem>
            <NavigationMenuItem>
              <Link to="/create-post">
                <Button variant="outline">Crear Post</Button>
              </Link>
            </NavigationMenuItem>
            <NavigationMenuItem>
              <Link to="/liked-posts">
                <Button variant="outline">Posts Que Me Han Gustado</Button>
              </Link>
            </NavigationMenuItem>
            <NavigationMenuItem>
              <DropdownMenu>
                <DropdownMenuTrigger asChild>
                  <Button variant="destructive">Salir</Button>
                </DropdownMenuTrigger>
                <DropdownMenuContent align="end">
                  <DropdownMenuItem onClick={handleLogout}>
                    Confirmar
                  </DropdownMenuItem>
                </DropdownMenuContent>
              </DropdownMenu>
            </NavigationMenuItem>
          </NavigationMenuList>
        </NavigationMenu>
      </nav>

      <main className="flex-grow p-6">
        <Outlet />
      </main>
    </div>
  );
}
