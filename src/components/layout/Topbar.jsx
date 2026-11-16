import { useEffect, useState } from "react";
import { useLocation } from "react-router-dom";
import { Search, Bell, ChevronDown } from "lucide-react";
import { notificationsApi } from "../../api";
import { getPageTitle } from "../../config/navigation";
import { useAuth } from "../../context/useAuth";
import { getFullName, getInitials, getRoleLabel } from "../../utils/user";

function Topbar() {
  const { user } = useAuth();
  const { pathname } = useLocation();
  const [hasUnread, setHasUnread] = useState(false);

  useEffect(() => {
    let cancelled = false;

    notificationsApi
      .list({ userId: user.id })
      .then((notifications) => {
        if (!cancelled) {
          setHasUnread(
            notifications.some(
              (notification) => notification.status?.toUpperCase() === "UNREAD",
            ),
          );
        }
      })
      .catch(() => {
        if (!cancelled) setHasUnread(false);
      });

    return () => {
      cancelled = true;
    };
  }, [user.id]);

  const pageTitle = getPageTitle(pathname);

  return (
    <header className="topbar">
      <div className="topbar-left">
        <div className="breadcrumb">
          <span>Teacher Workspace</span>

          {pageTitle && (
            <>
              <span className="breadcrumb-separator">/</span>
              <strong>{pageTitle}</strong>
            </>
          )}
        </div>
      </div>

      <div className="topbar-right">
        <div className="search-box">
          <Search size={17} />
          <input type="text" placeholder="Search your workspace" />
          <span className="search-shortcut">⌘ K</span>
        </div>

        <button className="icon-button notification-button">
          <Bell size={19} />
          {hasUnread && <span className="notification-dot"></span>}
        </button>

        <button className="profile-button">
          <div className="topbar-avatar">{getInitials(user)}</div>

          <div className="topbar-user">
            <strong>{getFullName(user)}</strong>
            <span>{getRoleLabel(user)}</span>
          </div>

          <ChevronDown size={16} />
        </button>
      </div>
    </header>
  );
}

export default Topbar;
