import { createRouter, createWebHistory } from "vue-router";
import MainPageView from "../views/MainPageView.vue";
import DashboardView from "../views/DashboardView.vue";
import AuthenticateView from "../views/AuthenticateView.vue";
import AuthResultView from "../views/AuthResultView.vue";
import VerifyView from "../views/VerifyView.vue";
import ProfileView from "../views/ProfileView.vue";
import api from "../service/api";
import MessageView from "../views/MessageView.vue";
import SettingsView from "../views/SettingsView.vue";
import CommentDebug from "../views/CommentDebug.vue";
import GuestProfileView from "../views/GuestProfileView.vue";

const routes = [
    {
        path: '/',
        redirect: '/main'
    },
    {
        path: '/home',
        name: 'home',
        component: DashboardView,
        meta: { requiresAuth: true }
    },
    {
        path: '/profile',
        name: 'profile',
        component: ProfileView,
        meta: { requiresAuth: true }
    },
    {
        path: '/profile/:publicId',
        name: 'ViewGuest',
        component: GuestProfileView,
        meta: { requiresAuth: true }
    },
    {
        path: '/messages',
        name: 'messages',
        component: MessageView,
        meta: { requiresAuth: true }
    },
    {
        path: '/settings',
        name: 'settings',
        component: SettingsView,
        meta: { requiresAuth: true }
    },
    {
        path: '/main',
        name: 'main',
        component: MainPageView,
        meta: { requiresGuest: true }
    },
    {
        path: '/commentDebug',
        name: 'commentDebug',
        component: CommentDebug
    },
    {
        path: '/authenticate',
        name: 'authenticate',
        component: AuthenticateView,
        meta: { requiresGuest: true }
    },
    {
        path: '/auth-result',
        name: 'auth-result',
        component: AuthResultView,
        props: true,
        meta: { requiresGuest: true }
    },
    {
        path: '/verify',
        name: 'verify',
        component: VerifyView,
        meta: { requiresGuest: true }
    }
];

const router = createRouter({
    history: createWebHistory(),
    routes,
});

router.beforeEach(async (to, _from, next) => {
    if (to.matched.some(record => record.meta.requiresAuth)) {
        try {
            await api.get('/users/me');
            next();
        } catch {
            next({ name: 'main' });
        }
    }
    else if (to.matched.some(record => record.meta.requiresGuest)) {
        try {
            await api.get('/users/me');
            next({ name: 'home' });
        } catch {
            next();
        }
    }
    else {
        next();
    }
});

export default router;