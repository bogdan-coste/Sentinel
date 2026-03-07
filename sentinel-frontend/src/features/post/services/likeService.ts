import api from '@shared/api/api';

export const likeService = {
    toggleLike: (mediaId: number) =>
        api.post('/likes', { mediaId }).then(res => res.data),

    getLikers: (mediaId: number) =>
        api.get(`/likes/media/${mediaId}/likes`).then(res => res.data)
};