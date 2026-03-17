import './assets/css/main.css'

import { createApp } from 'vue'
import App from './App.vue'
import route from './router/router.ts'

const app = createApp(App);
app.use(route)
app.mount('#app')