package com.it342g02.edubridge.mobile.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.Assignment
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.automirrored.outlined.TrendingUp
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.School
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.it342g02.edubridge.mobile.data.model.Role
import com.it342g02.edubridge.mobile.data.model.User
import com.it342g02.edubridge.mobile.data.session.SessionState
import com.it342g02.edubridge.mobile.ui.auth.LoginScreen
import com.it342g02.edubridge.mobile.ui.auth.SignupScreen
import com.it342g02.edubridge.mobile.ui.components.Avatar
import com.it342g02.edubridge.mobile.ui.components.BrandMark
import com.it342g02.edubridge.mobile.ui.components.CenteredLoading
import com.it342g02.edubridge.mobile.ui.components.LocalAppContainer
import com.it342g02.edubridge.mobile.ui.components.LocalSnackbar
import com.it342g02.edubridge.mobile.ui.components.rememberLoader
import com.it342g02.edubridge.mobile.ui.parent.ParentHomeScreen
import com.it342g02.edubridge.mobile.ui.parent.ParentMissionsScreen
import com.it342g02.edubridge.mobile.ui.parent.ParentProgressScreen
import com.it342g02.edubridge.mobile.ui.parent.rememberParentState
import com.it342g02.edubridge.mobile.ui.shared.NotificationsScreen
import com.it342g02.edubridge.mobile.ui.shared.ProfileScreen
import com.it342g02.edubridge.mobile.ui.student.StudentHomeScreen
import com.it342g02.edubridge.mobile.ui.student.StudentHomeworkScreen
import com.it342g02.edubridge.mobile.ui.student.StudentLessonsScreen
import com.it342g02.edubridge.mobile.ui.student.StudentProgressScreen
import com.it342g02.edubridge.mobile.ui.teacher.AttendanceScreen
import com.it342g02.edubridge.mobile.ui.teacher.ClassDetailScreen
import com.it342g02.edubridge.mobile.ui.teacher.ClassesScreen
import com.it342g02.edubridge.mobile.ui.teacher.MissionReviewScreen
import com.it342g02.edubridge.mobile.ui.teacher.StudentRecordScreen
import com.it342g02.edubridge.mobile.ui.teacher.SubmissionsScreen
import com.it342g02.edubridge.mobile.ui.teacher.TeacherHomeScreen
import com.it342g02.edubridge.mobile.ui.theme.EduColors

object Routes {
    const val NOTIFICATIONS = "notifications"
    const val PROFILE = "profile"

    const val T_HOME = "teacher/home"
    const val T_CLASSES = "teacher/classes"
    const val T_CLASS = "teacher/class/{classId}"
    const val T_ATTENDANCE = "teacher/attendance/{classId}"
    const val T_STUDENT = "teacher/student/{enrollmentId}"
    const val T_SUBMISSIONS = "teacher/homework/{homeworkId}"
    const val T_MISSION = "teacher/mission/{missionId}"

    const val S_HOME = "student/home"
    const val S_HOMEWORK = "student/homework"
    const val S_LESSONS = "student/lessons"
    const val S_PROGRESS = "student/progress"

    const val P_HOME = "parent/home"
    const val P_MISSIONS = "parent/missions"
    const val P_PROGRESS = "parent/progress"

    fun teacherClass(id: Long) = "teacher/class/$id"
    fun teacherAttendance(classId: Long) = "teacher/attendance/$classId"
    fun teacherStudent(enrollmentId: Long) = "teacher/student/$enrollmentId"
    fun teacherSubmissions(homeworkId: Long) = "teacher/homework/$homeworkId"
    fun teacherMission(missionId: Long) = "teacher/mission/$missionId"
}

private data class Tab(val route: String, val label: String, val icon: ImageVector)

private fun tabsFor(role: Role?): List<Tab> = when (role) {
    Role.PARENT -> listOf(
        Tab(Routes.P_HOME, "Home", Icons.Outlined.Dashboard),
        Tab(Routes.P_MISSIONS, "Missions", Icons.Outlined.FavoriteBorder),
        Tab(Routes.P_PROGRESS, "Progress", Icons.AutoMirrored.Outlined.TrendingUp),
        Tab(Routes.PROFILE, "Profile", Icons.Outlined.Person),
    )
    Role.STUDENT -> listOf(
        Tab(Routes.S_HOME, "Home", Icons.Outlined.Dashboard),
        Tab(Routes.S_HOMEWORK, "Homework", Icons.AutoMirrored.Outlined.Assignment),
        Tab(Routes.S_LESSONS, "Lessons", Icons.AutoMirrored.Outlined.MenuBook),
        Tab(Routes.S_PROGRESS, "Progress", Icons.AutoMirrored.Outlined.TrendingUp),
        Tab(Routes.PROFILE, "Profile", Icons.Outlined.Person),
    )
    else -> listOf(
        Tab(Routes.T_HOME, "Home", Icons.Outlined.Dashboard),
        Tab(Routes.T_CLASSES, "Classes", Icons.Outlined.School),
        Tab(Routes.NOTIFICATIONS, "Alerts", Icons.Outlined.Notifications),
        Tab(Routes.PROFILE, "Profile", Icons.Outlined.Person),
    )
}

/** Matches the web sidebar's "Teacher Workspace" sub-label for each role. */
private fun workspaceLabel(role: Role?) = when (role) {
    Role.PARENT -> "Parent Portal"
    Role.STUDENT -> "Student Space"
    else -> "Teacher Workspace"
}

private fun detailTitle(route: String?): String = when (route) {
    Routes.T_CLASS -> "Class"
    Routes.T_ATTENDANCE -> "Take Attendance"
    Routes.T_STUDENT -> "Student Record"
    Routes.T_SUBMISSIONS -> "Submissions"
    Routes.T_MISSION -> "Mission Review"
    Routes.NOTIFICATIONS -> "Notifications"
    else -> ""
}

/** Top of the tree: show auth screens or the role's workspace based on the session. */
@Composable
fun EduBridgeRoot() {
    val session = LocalAppContainer.current.session
    val state by session.state.collectAsStateWithLifecycle()

    when (val s = state) {
        SessionState.Restoring -> CenteredLoading()
        SessionState.SignedOut -> AuthFlow()
        // keyed by user id so signing in as someone else starts from a clean slate
        is SessionState.SignedIn -> key(s.user.id) { MainScaffold(s.user) }
    }
}

@Composable
private fun AuthFlow() {
    val nav = rememberNavController()
    NavHost(nav, startDestination = "login") {
        composable("login") { LoginScreen(onCreateAccount = { nav.navigate("signup") }) }
        composable("signup") {
            SignupScreen(onSignIn = { if (!nav.popBackStack()) nav.navigate("login") })
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainScaffold(user: User) {
    val nav = rememberNavController()
    val snackbar = remember { SnackbarHostState() }
    val repo = LocalAppContainer.current.repository
    val tabs = tabsFor(user.role)
    val backStack by nav.currentBackStackEntryAsState()
    val route = backStack?.destination?.route
    val isTopLevel = tabs.any { it.route == route } || route == null

    // unread dot on the bell; refreshed whenever the destination changes
    val unread = rememberLoader(user.id, route) {
        repo.notifications(user.id!!).count { it.isUnread }
    }

    // shared, role-level state so switching tabs doesn't refetch everything
    val studentOverview = if (user.role == Role.STUDENT) {
        rememberLoader(user.id) { repo.studentOverview(user) }
    } else null
    val parentState = if (user.role == Role.PARENT) rememberParentState(user) else null

    CompositionLocalProvider(LocalSnackbar provides snackbar) {
        Scaffold(
            containerColor = EduColors.Background,
            snackbarHost = { SnackbarHost(snackbar) },
            topBar = {
                Column {
                    if (isTopLevel) {
                        TopAppBar(
                            title = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    BrandMark(32.dp)
                                    Spacer(Modifier.width(10.dp))
                                    Column {
                                        Text("EduBridge", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                                        Text(workspaceLabel(user.role), style = MaterialTheme.typography.bodySmall)
                                    }
                                }
                            },
                            actions = {
                                if (user.role != Role.TEACHER) {
                                    BellButton(hasUnread = (unread.data ?: 0) > 0) { nav.navigate(Routes.NOTIFICATIONS) }
                                }
                                Box(
                                    Modifier
                                        .padding(end = 12.dp, start = 4.dp)
                                        .clip(CircleShape)
                                        .clickable { nav.navigateToTab(Routes.PROFILE) },
                                ) { Avatar(user.initials, 34.dp) }
                            },
                            colors = TopAppBarDefaults.topAppBarColors(containerColor = EduColors.Surface),
                        )
                    } else {
                        CenterAlignedTopAppBar(
                            title = {
                                Text(
                                    detailTitle(route),
                                    style = MaterialTheme.typography.titleMedium,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            },
                            navigationIcon = {
                                IconButton(onClick = { nav.popBackStack() }) {
                                    Icon(Icons.AutoMirrored.Outlined.ArrowBack, "Back")
                                }
                            },
                            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = EduColors.Surface),
                        )
                    }
                    HorizontalDivider(color = EduColors.Border)
                }
            },
            bottomBar = {
                if (isTopLevel) {
                    Column {
                        HorizontalDivider(color = EduColors.Border)
                        NavigationBar(containerColor = EduColors.Surface, tonalElevation = 0.dp) {
                            tabs.forEach { tab ->
                                val selected = route == tab.route
                                NavigationBarItem(
                                    selected = selected,
                                    onClick = { nav.navigateToTab(tab.route) },
                                    icon = {
                                        if (tab.route == Routes.NOTIFICATIONS && (unread.data ?: 0) > 0) {
                                            Box {
                                                Icon(tab.icon, null)
                                                UnreadDot(Modifier.align(Alignment.TopEnd))
                                            }
                                        } else {
                                            Icon(tab.icon, null)
                                        }
                                    },
                                    label = { Text(tab.label, style = MaterialTheme.typography.labelMedium) },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = EduColors.PrimaryDark,
                                        selectedTextColor = EduColors.PrimaryDark,
                                        indicatorColor = EduColors.PrimaryLight,
                                        unselectedIconColor = EduColors.TextMuted,
                                        unselectedTextColor = EduColors.TextMuted,
                                    ),
                                )
                            }
                        }
                    }
                }
            },
        ) { padding ->
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(padding),
            ) {
                NavHost(nav, startDestination = tabs.first().route) {
                    composable(Routes.NOTIFICATIONS) { NotificationsScreen(user) }
                    composable(Routes.PROFILE) { ProfileScreen(user) }

                    when (user.role) {
                        Role.STUDENT -> {
                            val overview = studentOverview!!
                            composable(Routes.S_HOME) {
                                StudentHomeScreen(user, overview, onOpen = { nav.navigateToTab(it) })
                            }
                            composable(Routes.S_HOMEWORK) { StudentHomeworkScreen(user, overview) }
                            composable(Routes.S_LESSONS) { StudentLessonsScreen(user, overview) }
                            composable(Routes.S_PROGRESS) { StudentProgressScreen(overview) }
                        }

                        Role.PARENT -> {
                            val parent = parentState!!
                            composable(Routes.P_HOME) {
                                ParentHomeScreen(user, parent, onOpen = { nav.navigateToTab(it) })
                            }
                            composable(Routes.P_MISSIONS) { ParentMissionsScreen(parent) }
                            composable(Routes.P_PROGRESS) { ParentProgressScreen(parent) }
                        }

                        else -> teacherGraph(nav, user)
                    }
                }
            }
        }
    }
}

private fun androidx.navigation.NavGraphBuilder.teacherGraph(nav: NavHostController, user: User) {
    composable(Routes.T_HOME) {
        TeacherHomeScreen(
            user = user,
            onOpenClasses = { nav.navigateToTab(Routes.T_CLASSES) },
            onOpenClass = { nav.navigate(Routes.teacherClass(it)) },
        )
    }
    composable(Routes.T_CLASSES) {
        ClassesScreen(user, onOpenClass = { nav.navigate(Routes.teacherClass(it)) })
    }
    composable(Routes.T_CLASS, listOf(navArgument("classId") { type = NavType.LongType })) { entry ->
        ClassDetailScreen(
            classId = entry.arguments!!.getLong("classId"),
            onTakeAttendance = { nav.navigate(Routes.teacherAttendance(it)) },
            onOpenStudent = { nav.navigate(Routes.teacherStudent(it)) },
            onOpenHomework = { nav.navigate(Routes.teacherSubmissions(it)) },
            onOpenMission = { nav.navigate(Routes.teacherMission(it)) },
        )
    }
    composable(Routes.T_ATTENDANCE, listOf(navArgument("classId") { type = NavType.LongType })) { entry ->
        AttendanceScreen(entry.arguments!!.getLong("classId"), onDone = { nav.popBackStack() })
    }
    composable(Routes.T_STUDENT, listOf(navArgument("enrollmentId") { type = NavType.LongType })) { entry ->
        StudentRecordScreen(entry.arguments!!.getLong("enrollmentId"))
    }
    composable(Routes.T_SUBMISSIONS, listOf(navArgument("homeworkId") { type = NavType.LongType })) { entry ->
        SubmissionsScreen(entry.arguments!!.getLong("homeworkId"))
    }
    composable(Routes.T_MISSION, listOf(navArgument("missionId") { type = NavType.LongType })) { entry ->
        MissionReviewScreen(entry.arguments!!.getLong("missionId"))
    }
}

/** Standard bottom-nav behaviour: single copy of each tab, state saved between switches. */
fun NavHostController.navigateToTab(route: String) {
    navigate(route) {
        popUpTo(graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

@Composable
private fun BellButton(hasUnread: Boolean, onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        Box {
            Icon(Icons.Outlined.Notifications, "Notifications", tint = EduColors.TextMuted)
            if (hasUnread) UnreadDot(Modifier.align(Alignment.TopEnd))
        }
    }
}

/** .notification-dot */
@Composable
private fun UnreadDot(modifier: Modifier = Modifier) {
    Box(
        modifier
            .offset(x = 2.dp, y = (-2).dp)
            .size(8.dp)
            .clip(CircleShape)
            .background(EduColors.Danger),
    )
}
