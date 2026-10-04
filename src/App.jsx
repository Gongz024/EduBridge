import { BrowserRouter, Routes, Route } from "react-router-dom";

import TeacherLayout from "./layouts/TeacherLayout";

import Dashboard from "./pages/Dashboard";
import Classes from "./pages/Classes";
import Students from "./pages/Students";
import Lessons from "./pages/Lessons";
import LearningMaterials from "./pages/LearningMaterials";
import Homework from "./pages/Homework";
import Attendance from "./pages/Attendance";
import Grades from "./pages/Grades";
import Progress from "./pages/Progress";
import FamilyMissions from "./pages/FamilyMissions";
import Announcements from "./pages/Announcements";
import Settings from "./pages/Settings";

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route element={<TeacherLayout />}>
          <Route path="/" element={<Dashboard />} />
          <Route path="/classes" element={<Classes />} />
          <Route path="/students" element={<Students />} />
          <Route path="/lessons" element={<Lessons />} />
          <Route
            path="/materials"
            element={<LearningMaterials />}
          />
          <Route path="/homework" element={<Homework />} />
          <Route path="/attendance" element={<Attendance />} />
          <Route path="/grades" element={<Grades />} />
          <Route path="/progress" element={<Progress />} />
          <Route
            path="/family-missions"
            element={<FamilyMissions />}
          />
          <Route
            path="/announcements"
            element={<Announcements />}
          />
          <Route path="/settings" element={<Settings />} />
        </Route>
      </Routes>
    </BrowserRouter>
  );
}

export default App;