import { api } from "./client";

export { ApiError } from "./client";

// Every resource exposes the same CRUD calls:
//   list(params)  GET    /<path>?<filters>
//   get(id)       GET    /<path>/{id}
//   create(data)  POST   /<path>
//   update(id, data) PUT /<path>/{id}
//   remove(id)    DELETE /<path>/{id}
function resource(path) {
  return {
    list: (params) => api.get(path, params),
    get: (id) => api.get(`${path}/${id}`),
    create: (data) => api.post(path, data),
    update: (id, data) => api.put(`${path}/${id}`, data),
    remove: (id) => api.delete(`${path}/${id}`),
  };
}

// login and signup respond with { token, user }; me returns the user for the stored token
export const authApi = {
  login: (email, password) => api.post("/auth/login", { email, password }),
  signup: (data) => api.post("/auth/signup", data),
  me: () => api.get("/auth/me"),
};

// filters: ?role=
export const usersApi = resource("/users");
export const badgesApi = resource("/badges");
// filters: ?userId=
export const userBadgesApi = resource("/user-badges");
// filters: ?parentId= | ?studentId=
export const parentStudentsApi = resource("/parent-students");
// filters: ?teacherId=
export const classesApi = resource("/classes");
// filters: ?classId= | ?studentId=
export const studentClassesApi = resource("/student-classes");
// filters: ?classId=
export const lessonsApi = resource("/lessons");
// filters: ?lessonId= | ?studentId=
export const lessonProgressApi = resource("/lesson-progress");
// filters: ?lessonId=
export const learningMaterialsApi = resource("/learning-materials");
// filters: ?classId=
export const homeworkApi = resource("/homework");
// filters: ?homeworkId= | ?studentId=
export const homeworkSubmissionsApi = resource("/homework-submissions");
// filters: ?classId=
export const announcementsApi = resource("/announcements");
// filters: ?userId=
export const notificationsApi = resource("/notifications");
// filters: ?classId=
export const familyMissionsApi = resource("/family-missions");
// filters: ?missionId= | ?parentStudentId=
export const missionParticipationsApi = resource("/mission-participations");
// filters: ?userId= | ?missionId=
export const pointTransactionsApi = resource("/point-transactions");
// filters: ?studentClassId=
export const attendanceApi = resource("/attendance");
// filters: ?studentClassId=
export const gradesApi = resource("/grades");
