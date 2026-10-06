import {
  GraduationCap,
  Users,
  ClipboardList,
  TrendingUp,
} from "lucide-react";

import StatCard from "../components/dashboard/StatCard";
import UpcomingLessons from "../components/dashboard/UpcomingLessons";
import ProgressOverview from "../components/dashboard/ProgressOverview";
import AttendanceCard from "../components/dashboard/AttendanceCard";
import FamilyMissions from "../components/dashboard/FamilyMissions";
import QuickActions from "../components/dashboard/QuickActions";

function Dashboard() {
  return (
    <div className="dashboard-page">
      <div className="page-heading dashboard-heading">
        <div>
          <p className="eyebrow">MONDAY, OCTOBER 5, 2026</p>

          <h1>Good morning, Ms. Santos!</h1>

          <p className="page-description">
            Here's an overview of your classes and learning activities.
          </p>
        </div>
      </div>

      <div className="stats-grid">
        <StatCard
          label="Active Classes"
          value="4"
          description="Classes you're teaching"
          icon={GraduationCap}
        />

        <StatCard
          label="Students"
          value="126"
          description="Across all your classes"
          icon={Users}
        />

        <StatCard
          label="Pending Activities"
          value="18"
          description="Waiting for your review"
          icon={ClipboardList}
          tone="warning"
        />

        <StatCard
          label="Average Progress"
          value="89%"
          description="Overall student progress"
          icon={TrendingUp}
          tone="success"
        />
      </div>

      <div className="dashboard-main-grid">
        <UpcomingLessons />
        <div className="dashboard-side-column">
          <AttendanceCard />
        </div>
      </div>

      <div className="dashboard-two-column">
        <ProgressOverview />
        <QuickActions />
      </div>

      <FamilyMissions />
    </div>
  );
}

export default Dashboard;