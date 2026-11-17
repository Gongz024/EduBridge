import { NavLink } from "react-router-dom";
import { LogOut } from "lucide-react";
import { navigation, settingsItem } from "../../config/navigation";
import { useAuth } from "../../context/useAuth";
import { getFullName, getInitials, getRoleLabel } from "../../utils/user";

function navClassName({ isActive }) {
  return `nav-item ${isActive ? "active" : ""}`;
}

function Sidebar() {
  const { user, logout } = useAuth();
  const SettingsIcon = settingsItem.icon;

  return (
    <aside className="sidebar">
      <div className="sidebar-brand">
        <div className="brand-mark">E</div>

        <div>
          <div className="brand-name">EduBridge</div>
          <div className="brand-role">Teacher Workspace</div>
        </div>
      </div>

      <nav className="sidebar-navigation">
        {navigation.map((group) => (
          <div className="nav-group" key={group.section}>
            <div className="nav-section-title">{group.section}</div>

            {group.items.map((item) => {
              const Icon = item.icon;

              return (
                <NavLink
                  to={item.path}
                  end={item.path === "/"}
                  className={navClassName}
                  key={item.path}
                >
                  <Icon size={18} strokeWidth={1.8} />
                  <span>{item.label}</span>
                </NavLink>
              );
            })}
          </div>
        ))}
      </nav>

      <div className="sidebar-bottom">
        <NavLink to={settingsItem.path} className={navClassName}>
          <SettingsIcon size={18} strokeWidth={1.8} />
          <span>{settingsItem.label}</span>
        </NavLink>

        <button type="button" className="nav-item nav-button" onClick={logout}>
          <LogOut size={18} strokeWidth={1.8} />
          <span>Log out</span>
        </button>

        <div className="teacher-profile">
          <div className="teacher-avatar">{getInitials(user)}</div>

          <div className="teacher-info">
            <strong>{getFullName(user)}</strong>
            <span>{getRoleLabel(user)}</span>
          </div>
        </div>
      </div>
    </aside>
  );
}

export default Sidebar;
