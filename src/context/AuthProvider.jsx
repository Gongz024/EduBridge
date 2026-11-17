import { useCallback, useEffect, useMemo, useState } from "react";
import { authApi } from "../api";
import {
  clearToken,
  getToken,
  setToken,
  setUnauthorizedHandler,
} from "../api/token";
import { AuthContext } from "./AuthContext";

const TEACHER_ONLY_MESSAGE = "This workspace is for teacher accounts only.";

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null);
  // true while a stored token is being checked against the server
  const [loading, setLoading] = useState(() => Boolean(getToken()));

  const logout = useCallback(() => {
    clearToken();
    setUser(null);
  }, []);

  // Log out automatically when the API rejects the token
  useEffect(() => {
    setUnauthorizedHandler(logout);
    return () => setUnauthorizedHandler(null);
  }, [logout]);

  // Restore the session from a stored token
  useEffect(() => {
    if (!getToken()) return undefined;

    let cancelled = false;

    authApi
      .me()
      .then((currentUser) => {
        if (cancelled) return;

        if (currentUser.role === "TEACHER") {
          setUser(currentUser);
        } else {
          clearToken();
        }
      })
      .catch(() => {
        // a 401 already cleared the token; other errors leave the user signed out
      })
      .finally(() => {
        if (!cancelled) setLoading(false);
      });

    return () => {
      cancelled = true;
    };
  }, []);

  const completeAuth = useCallback(({ token, user: authUser }) => {
    if (authUser.role !== "TEACHER") {
      throw new Error(TEACHER_ONLY_MESSAGE);
    }

    setToken(token);
    setUser(authUser);

    return authUser;
  }, []);

  const login = useCallback(
    async (email, password) => completeAuth(await authApi.login(email, password)),
    [completeAuth],
  );

  const signup = useCallback(
    async (details) =>
      completeAuth(await authApi.signup({ ...details, role: "TEACHER" })),
    [completeAuth],
  );

  const value = useMemo(
    () => ({ user, loading, login, signup, logout }),
    [user, loading, login, signup, logout],
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}
