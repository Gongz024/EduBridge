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

export const navigation = [
  {
    section: "Overview",
    items: [{ label: "Dashboard", path: "/", icon: LayoutDashboard }],
  },
  {
    section: "Teaching",
    items: [
      { label: "My Classes", path: "/classes", icon: GraduationCap },
      { label: "Students", path: "/students", icon: Users },
      { label: "Lessons", path: "/lessons", icon: BookOpen },
      { label: "Learning Materials", path: "/materials", icon: FolderOpen },
      { label: "Homework & Activities", path: "/homework", icon: ClipboardList },
    ],
  },
  {
    section: "Academic",
    items: [
      { label: "Attendance", path: "/attendance", icon: CalendarCheck },
      { label: "Grades", path: "/grades", icon: ClipboardCheck },
      { label: "Progress", path: "/progress", icon: TrendingUp },
    ],
  },
  {
    section: "Engagement",
    items: [
      { label: "Family Missions", path: "/family-missions", icon: HeartHandshake },
      { label: "Announcements", path: "/announcements", icon: Megaphone },
    ],
  },
];

export const settingsItem = { label: "Settings", path: "/settings", icon: Settings };

export function getPageTitle(pathname) {
  const items = [...navigation.flatMap((group) => group.items), settingsItem];

  const match = items.find((item) =>
    item.path === "/" ? pathname === "/" : pathname.startsWith(item.path),
  );

  return match ? match.label : "";
}
