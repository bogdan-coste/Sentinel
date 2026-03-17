import axios from 'axios'

const dynamicHost = window.location.hostname
const PORT = '8080'

const api = axios.create({
    baseURL: `http://${dynamicHost}:${PORT}/api`,
    timeout: 10000,
    headers: {
        'Content-Type': 'application/json'
    }
})

export default api