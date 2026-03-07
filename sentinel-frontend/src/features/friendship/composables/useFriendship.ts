import { ref, computed } from 'vue';
import { friendService } from '@features/friendship';

export function useFriendship(guestProfileRef: any) {
    const friendshipState = ref<'none' | 'pending' | 'friends'>('none');
    const isTogglingFriend = ref(false);

    const friendButtonText = computed(() => {
        if (friendshipState.value === 'friends') return 'Friends';
        if (friendshipState.value === 'pending') return 'Requested';
        return 'Add Friend';
    });

    const toggleFriendStatus = async () => {
        if (!guestProfileRef.value || isTogglingFriend.value) return;
        const publicId = guestProfileRef.value.id;
        isTogglingFriend.value = true;

        try {
            if (friendshipState.value === 'none') {
                await friendService.addFriend(publicId);
                friendshipState.value = 'pending';
            }
            else if (friendshipState.value === 'pending') {
                await friendService.removeFriend(publicId);
                friendshipState.value = 'none';
            }
            else if (friendshipState.value === 'friends') {
                if (confirm(`Remove ${guestProfileRef.value.name} from your friends?`)) {
                    await friendService.removeFriend(publicId);
                    friendshipState.value = 'none';
                    guestProfileRef.value.friendCount = Math.max(0, guestProfileRef.value.friendCount - 1);
                }
            }
        } catch (error) {
            console.error('Failed to update friendship status', error);
        } finally {
            isTogglingFriend.value = false;
        }
    };

    return { friendshipState, isTogglingFriend, friendButtonText, toggleFriendStatus };
}