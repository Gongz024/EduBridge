import {
  CalendarCheck,
  BookOpen,
  ClipboardList,
  HeartHandshake,
} from "lucide-react";

const actions = [
  {
    label: "Take Attendance",
    icon: CalendarCheck,
    path: "/attendance",
  },
  {
    label: "Create Lesson",
    icon: BookOpen,
    path: "/lessons",
  },
  {
    label: "Create Homework",
    icon: ClipboardList,
    path: "/homework",
  },
  {
    label: "Create Family Mission",
    icon: HeartHandshake,
    path: "/family-missions",
  },
];

function QuickActions() {
  return (
    <section className="dashboard-card">
      <div className="dashboard-card-header">
        <div>
          <h2>Quick Actions</h2>
          <p>Common teaching tasks</p>
        </div>
      </div>

      <div className="quick-actions">
        {actions.map((action) => {
          const Icon = action.icon;

          return (
            <a
              href={action.path}
              className="quick-action"
              key={action.label}
            >
              <div className="quick-action-icon">
                <Icon size={19} />
              </div>

              <span>{action.label}</span>
            </a>
          );
        })}
      </div>
    </section>
  );
}

export default QuickActions;