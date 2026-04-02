import { ref } from 'vue';
import { commentService } from '@features/comment';

export function useComments(itemRef: any) {
    const isCommentsOpen = ref(false);
    const isFetchingComments = ref(false);
    const isSubmittingComment = ref(false);

    const syncComments = async () => {
        if (!itemRef.value?.id) return;
        isFetchingComments.value = true;
        try {
            const data = await commentService.getComments(itemRef.value.id);
            itemRef.value.comments = data || [];
        } catch (err) {
            console.error("Failed to fetch comments", err);
        } finally {
            isFetchingComments.value = false;
        }
    };

    const submitComment = async (content: string) => {
        if (!content || !itemRef.value?.id || isSubmittingComment.value) return;
        isSubmittingComment.value = true;
        try {
            const newComment = await commentService.addComment(itemRef.value.id, content);
            if (!itemRef.value.comments) itemRef.value.comments = [];
            itemRef.value.comments.push(newComment);
        } catch (err) {
            console.error("Failed to submit comment", err);
        } finally {
            isSubmittingComment.value = false;
        }
    };

    return {
        isCommentsOpen,
        isFetchingComments,
        isSubmittingComment,
        syncComments,
        submitComment
    };
}