export function getFullName(user) {
  return [user?.firstName, user?.lastName].filter(Boolean).join(" ");
}

export function getInitials(user) {
  return `${user?.firstName?.[0] ?? ""}${user?.lastName?.[0] ?? ""}`.toUpperCase();
}

export function getRoleLabel(user) {
  const role = user?.role ?? "";
  return role.charAt(0) + role.slice(1).toLowerCase();
}
