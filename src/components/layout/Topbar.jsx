import {
  Search,
  Bell,
  ChevronDown,
} from "lucide-react";

function Topbar() {
  return (
    <header className="topbar">
      <div className="topbar-left">
        <div className="breadcrumb">
          <span>Teacher Workspace</span>
          <span className="breadcrumb-separator">/</span>
          <strong>Dashboard</strong>
        </div>
      </div>

      <div className="topbar-right">
        <div className="search-box">
          <Search size={17} />
          <input
            type="text"
            placeholder="Search your workspace"
          />
          <span className="search-shortcut">⌘ K</span>
        </div>

        <button className="icon-button notification-button">
          <Bell size={19} />
          <span className="notification-dot"></span>
        </button>

        <button className="profile-button">
          <div className="topbar-avatar">MS</div>

          <div className="topbar-user">
            <strong>Ms. Maria Santos</strong>
            <span>Teacher</span>
          </div>

          <ChevronDown size={16} />
        </button>
      </div>
    </header>
  );
}

export default Topbar;