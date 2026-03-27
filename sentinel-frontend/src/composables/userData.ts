import { ref } from 'vue'
import api from '../service/api'

export function useUserMedia() {
    const userData = ref<any>(null)
    const userPosts = ref<any[]>([])
    const loading = ref(false)
    const error = ref<string | null>(null)

    const fetchUserMedia = async () => {
        loading.value = true
        error.value = null
        try {
            const profileRes = await api.get('/users/me')
            userData.value = profileRes.data

            const mediaRes = await api.get('/users/me/user-media')
            userPosts.value = mediaRes.data
        } catch (err: any) {
            console.error('Failed to fetch user media', err)
            error.value = err.message
        } finally {
            loading.value = false
        }
    }

    return {
        userData,
        userPosts,
        loading,
        error,
        fetchUserMedia
    }
}