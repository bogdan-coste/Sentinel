import api from '@shared/api/api';

export const postService = {
    createPost: (formData: FormData) =>
        api.post('/users/me/create-post', formData, {
            headers: { 'Content-Type': 'multipart/form-data' }
        }).then(res => res.data)
};