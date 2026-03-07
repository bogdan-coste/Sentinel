import { ref } from 'vue';

export function useToast() {
    const toastVisible = ref(false);
    const toastMessage = ref('');
    const toastType = ref<'success' | 'error'>('success');

    const showToast = (message: string, type: 'success' | 'error' = 'success') => {
        toastMessage.value = message;
        toastType.value = type;
        toastVisible.value = true;

        setTimeout(() => {
            toastVisible.value = false;
        }, 5000);
    };

    return {
        toastVisible,
        toastMessage,
        toastType,
        showToast
    };
}