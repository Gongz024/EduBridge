import {
  LayoutDashboard,
  Users,
  GraduationCap,
  BookOpen,
  FolderOpen,
  ClipboardList,
  CalendarCheck,
  ClipboardCheck,
  TrendingUp,
  HeartHandshake,
  Megaphone,
  Settings,
} from "lucide-react";

const navigation = [
  {
    section: "Overview",
    items: [
      {
        label: "Dashboard",
        path: "/",
        icon: LayoutDashboard,
      },
    ],
  },
  {
    section: "Teaching",
    items: [
      {
        label: "My Classes",
        path: "/classes",
        icon: GraduationCap,
      },
      {
        label: "Students",
        path: "/students",
        icon: Users,
      },
      {
        label: "Lessons",
        path: "/lessons",
        icon: BookOpen,
      },
      {
        label: "Learning Materials",
        path: "/materials",
        icon: FolderOpen,
      },
      {
        label: "Homework & Activities",
        path: "/homework",
        icon: ClipboardList,
      },
    ],
  },
  {
    section: "Academic",
    items: [
      {
        label: "Attendance",
        path: "/attendance",
        icon: CalendarCheck,
      },
      {
        label: "Grades",
        path: "/grades",
        icon: ClipboardCheck,
      },
      {
        label: "Progress",
        path: "/progress",
        icon: TrendingUp,
      },
    ],
  },
  {
    section: "Engagement",
    items: [
      {
        label: "Family Missions",
        path: "/family-missions",
        icon: HeartHandshake,
      },
      {
        label: "Announcements",
        path: "/announcements",
        icon: Megaphone,
      },
    ],
  },
];

function Sidebar() {
  const currentPath = window.location.pathname;

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
              const isActive =
                item.path === "/"
                  ? currentPath === "/"
                  : currentPath.startsWith(item.path);

              return (
                <a
                  href={item.path}
                  className={`nav-item ${isActive ? "active" : ""}`}
                  key={item.path}
                >
                  <Icon size={18} strokeWidth={1.8} />
                  <span>{item.label}</span>
                </a>
              );
            })}
          </div>
        ))}
      </nav>

      <div className="sidebar-bottom">
        <a href="/settings" className="nav-item">
          <Settings size={18} strokeWidth={1.8} />
          <span>Settings</span>
        </a>

        <div className="teacher-profile">
          <div className="teacher-avatar">MS</div>

          <div className="teacher-info">
            <strong>Ms. Maria Santos</strong>
            <span>Teacher</span>
          </div>
        </div>
      </div>
    </aside>
  );
}

export default Sidebar;