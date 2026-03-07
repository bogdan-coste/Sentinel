import { ref } from 'vue';
import { postService } from '@features/post';

export function usePostCreator() {
    const content = ref('');
    const selectedFile = ref<File | null>(null);
    const previewUrl = ref<string | null>(null);
    const isSubmitting = ref(false);

    const handleFile = (file: File) => {
        selectedFile.value = file;
        previewUrl.value = URL.createObjectURL(file);
    };

    const clearAttachment = () => {
        if (previewUrl.value) URL.revokeObjectURL(previewUrl.value);
        previewUrl.value = null;
        selectedFile.value = null;
    };

    const submitPost = async () => {
        if ((!content.value.trim() && !selectedFile.value) || isSubmitting.value) return null;
        isSubmitting.value = true;

        const formData = new FormData();
        formData.append('content', content.value);
        if (selectedFile.value) formData.append('file', selectedFile.value);

        try {
            const newPost = await postService.createPost(formData);

            content.value = '';
            clearAttachment();

            return newPost;
        } catch (err) {
            console.error('Failed to create post', err);
            return null;
        } finally {
            isSubmitting.value = false;
        }
    };

    return {
        content,
        selectedFile,
        previewUrl,
        isSubmitting,
        handleFile,
        clearAttachment,
        submitPost
    };
}