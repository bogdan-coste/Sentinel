import { ref } from 'vue';
import { likeService } from '@features/post';

export function useLikes(itemRef: any) {
    const isLiking = ref(false);
    const isFetchingLikers = ref(false);
    const showLikesModal = ref(false);
    const likersList = ref<any[]>([]);

    const handleToggleLike = async () => {
        if (!itemRef.value || isLiking.value) return;

        const mediaId = itemRef.value.id;
        const wasLiked = itemRef.value.isLiked;
        const oldLikesCount = itemRef.value.likes;

        itemRef.value.isLiked = !wasLiked;
        itemRef.value.likes += itemRef.value.isLiked ? 1 : -1;
        isLiking.value = true;

        try {
            const data = await likeService.toggleLike(mediaId);
            itemRef.value.likes = data.likeCount;
            itemRef.value.isLiked = data.liked;
        } catch (err) {
            itemRef.value.isLiked = wasLiked;
            itemRef.value.likes = oldLikesCount;
            console.error("Like failed", err);
        } finally {
            isLiking.value = false;
        }
    };

    const fetchLikers = async () => {
        if (!itemRef.value?.id) return;
        isFetchingLikers.value = true;
        try {
            const data = await likeService.getLikers(itemRef.value.id);
            likersList.value = data.map((l: any) => l.user || l.userEntity || l);
        } catch (err) {
            console.error("Failed to fetch likers", err);
        } finally {
            isFetchingLikers.value = false;
        }
    };

    // --- NEW: Sync initial data & verify identity for the Red Heart ---
    const syncLikes = async (myPublicId?: string, myUsername?: string) => {
        if (!itemRef.value?.id) return false;
        try {
            const data = await likeService.getLikers(itemRef.value.id);
            itemRef.value.likes = data.length;

            let foundMatch = false;
            if (myPublicId || myUsername) {
                foundMatch = data.some((item: any) => {
                    const targetUser = item?.user || item?.userEntity || item;
                    const likedPublicId = String(targetUser?.publicId || '');
                    const likedUsername = String(targetUser?.username || '');

                    return (myPublicId && likedPublicId === myPublicId) ||
                        (myUsername && likedUsername === myUsername);
                });
            }

            itemRef.value.isLiked = foundMatch;
            return foundMatch; // Return so the component can update its local UI state
        } catch (err) {
            console.error("Failed to sync likes", err);
            return false;
        }
    };

    let holdTimer: any = null;
    let longPressTriggered = false;

    const onMouseDown = () => {
        longPressTriggered = false;
        holdTimer = setTimeout(() => {
            longPressTriggered = true;
            showLikesModal.value = true;
            fetchLikers();
        }, 500);
    };

    const onMouseUp = () => { if (holdTimer) clearTimeout(holdTimer); };

    return {
        isLiking,
        isFetchingLikers,
        showLikesModal,
        likersList,
        handleToggleLike,
        fetchLikers,
        syncLikes, // Export the new sync function
        longPressHandlers: {
            mousedown: onMouseDown,
            mouseup: onMouseUp,
            mouseleave: onMouseUp,
            touchstart: onMouseDown,
            touchend: onMouseUp
        },
        isLongPress: () => longPressTriggered
    };
}