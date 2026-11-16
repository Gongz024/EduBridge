import { useEffect, useState } from "react";
import {
  announcementsApi,
  classesApi,
  familyMissionsApi,
  homeworkApi,
  studentClassesApi,
} from "../api";
import { useAuth } from "../context/useAuth";
import "./Dashboard.css";

function getGreeting(date) {
  const hour = date.getHours();

  if (hour < 12) return "Good morning";
  if (hour < 18) return "Good afternoon";
  return "Good evening";
}

async function loadStats(teacherId) {
  const classes = await classesApi.list({ teacherId });

  const perClass = await Promise.all(
    classes.map(async (schoolClass) => {
      const [enrollments, homework, missions, announcements] = await Promise.all([
        studentClassesApi.list({ classId: schoolClass.id }),
        homeworkApi.list({ classId: schoolClass.id }),
        familyMissionsApi.list({ classId: schoolClass.id }),
        announcementsApi.list({ classId: schoolClass.id }),
      ]);

      return { enrollments, homework, missions, announcements };
    }),
  );

  const total = (key) => perClass.reduce((sum, item) => sum + item[key].length, 0);

  return {
    classes: classes.length,
    students: new Set(
      perClass.flatMap((item) => item.enrollments.map((entry) => entry.student?.id)),
    ).size,
    homework: total("homework"),
    missions: total("missions"),
    announcements: total("announcements"),
  };
}

const STAT_CARDS = [
  { key: "classes", label: "My Classes" },
  { key: "students", label: "Students" },
  { key: "homework", label: "Homework & Activities" },
  { key: "missions", label: "Family Missions" },
  { key: "announcements", label: "Announcements" },
];

function Dashboard() {
  const { user } = useAuth();
  const [stats, setStats] = useState(null);
  const [error, setError] = useState("");

  useEffect(() => {
    let cancelled = false;

    loadStats(user.id)
      .then((result) => {
        if (!cancelled) setStats(result);
      })
      .catch((err) => {
        if (!cancelled) setError(err.message);
      });

    return () => {
      cancelled = true;
    };
  }, [user.id]);

  const now = new Date();

  const dateLabel = now
    .toLocaleDateString("en-US", {
      weekday: "long",
      month: "long",
      day: "numeric",
      year: "numeric",
    })
    .toUpperCase();

  return (
    <div>
      <div className="page-heading">
        <div>
          <p className="eyebrow">{dateLabel}</p>
          <h1>
            {getGreeting(now)}, {user.firstName}!
          </h1>
          <p className="page-description">
            Here's an overview of your classes and learning activities.
          </p>
        </div>
      </div>

      {error && <p className="form-error">{error}</p>}

      <div className="stat-grid">
        {STAT_CARDS.map((card) => (
          <div className="stat-card" key={card.key}>
            <div className="stat-label">{card.label}</div>
            <div className="stat-value">{stats ? stats[card.key] : "—"}</div>
          </div>
        ))}
      </div>
    </div>
  );
}

export default Dashboard;
