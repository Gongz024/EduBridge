import { Navigate, Outlet, useLocation } from "react-router-dom";
import Sidebar from "../components/layout/Sidebar";
import Topbar from "../components/layout/Topbar";
import { useAuth } from "../context/useAuth";

function TeacherLayout() {
  const { user, loading } = useAuth();
  const location = useLocation();

  if (loading) return null;

  if (!user) {
    return <Navigate to="/login" replace state={{ from: location }} />;
  }

  return (
    <div className="app-shell">
      <Sidebar />

      <div className="main-area">
        <Topbar />

        <main className="page-content">
          <Outlet />
        </main>
      </div>
    </div>
  );
}

export default TeacherLayout;
