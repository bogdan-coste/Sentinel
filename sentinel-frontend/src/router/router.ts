import { createRouter, createWebHistory } from "vue-router";
import api from "@shared/api/api";
import MainPageView from "@views/main/MainPageView.vue";
import DashboardView from "@views/dashboard/DashboardView.vue";
import AuthenticateView from "@views/auth/AuthenticateView.vue";
import AuthResultView from "@views/auth/AuthResultView.vue";
import VerifyView from "@views/auth/VerifyView.vue";
import ProfileView from "@views/profile/ProfileView.vue";
import GuestProfileView from "@views/profile/GuestProfileView.vue";
import MessageView from "@views/messages/MessageView.vue";
import SettingsView from "@views/settings/SettingsView.vue";

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