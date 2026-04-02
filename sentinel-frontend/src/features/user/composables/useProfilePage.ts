import { ref, computed, onMounted } from 'vue';
import { userService } from '../services/userService.ts';
import type { UserProfile, Post } from '../../../shared/types'

export function useProfilePage() {
    const userProfile = ref<UserProfile | null>(null);
    const profilePosts = ref<Post[]>([]);
    const friends = ref<any[]>([]);
    const loading = ref(true);

    const fetchAllData = async () => {
        loading.value = true;
        try {
            userProfile.value = await userService.getProfile();
            profilePosts.value = await userService.getMyPosts();
            friends.value = await userService.getMyFriends();
        } catch (err) {
            console.error(err);
        } finally {
            loading.value = false;
        }
    };

    const recentPhotos = computed(() => {
        return profilePosts.value.map(p => p.image).filter(Boolean).slice(0, 6);
    });

    onMounted(fetchAllData);

    return {
        userProfile, profilePosts, friends, loading, recentPhotos, fetchAllData
    };
}