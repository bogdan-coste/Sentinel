import { ref } from 'vue'
import api from '../service/api'

export function useMediaUpload() {
    const isUploadingAvatar = ref(false)
    const isUploadingBanner = ref(false)

    const uploadAvatar = async (file: File) => {
        isUploadingAvatar.value = true
        const formData = new FormData()
        formData.append('file', file)
        try {
            const response = await api.post('/users/me/profile-picture', formData, {
                headers: { 'Content-Type': 'multipart/form-data' }
            })
            return {
                success: true,
                profilePicUrl: response.data.profilePicUrl,
                profilePicMediaId: response.data.profilePicMediaId
            }
        } catch (err) {
            console.error('Avatar upload failed', err)
            return { success: false }
        } finally {
            isUploadingAvatar.value = false
        }
    }

    const uploadBanner = async (file: File) => {
        isUploadingBanner.value = true
        const formData = new FormData()
        formData.append('file', file)
        try {
            const response = await api.post('/users/me/profile-banner', formData, {
                headers: { 'Content-Type': 'multipart/form-data' }
            })
            return {
                success: true,
                bannerPicUrl: response.data.bannerPicUrl,
                bannerMediaId: response.data.bannerMediaId
            }
        } catch (err) {
            console.error('Banner upload failed', err)
            return { success: false }
        } finally {
            isUploadingBanner.value = false
        }
    }

    return {
        isUploadingAvatar,
        isUploadingBanner,
        uploadAvatar,
        uploadBanner
    }
}