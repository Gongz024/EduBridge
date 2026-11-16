const TOKEN_KEY = "edubridge.token";

let unauthorizedHandler = null;

export function getToken() {
  try {
    return localStorage.getItem(TOKEN_KEY);
  } catch {
    return null;
  }
}

export function setToken(token) {
  localStorage.setItem(TOKEN_KEY, token);
}

export function clearToken() {
  try {
    localStorage.removeItem(TOKEN_KEY);
  } catch {
    // storage unavailable; nothing to clear
  }
}

// The auth provider registers a callback here so an expired or invalid
// token (HTTP 401) logs the user out from anywhere in the app.
export function setUnauthorizedHandler(handler) {
  unauthorizedHandler = handler;
}

export function notifyUnauthorized() {
  if (unauthorizedHandler) unauthorizedHandler();
}
