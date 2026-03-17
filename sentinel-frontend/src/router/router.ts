import { createRouter, createWebHistory } from "vue-router";
import HomeView from "../views/HomeView.vue"
import AuthenticateView from "../views/AuthenticateView.vue"

const routes = [
    { path: '/home', component: HomeView },
    { path: '/authenticate', component: AuthenticateView }
];

const router = createRouter({
    history: createWebHistory(),
    routes
})

export default router