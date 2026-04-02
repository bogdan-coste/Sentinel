import api from '@shared/api/api';

export const commentService = {
    getComments: (mediaId: number) =>
        api.get(`/comments/media/${mediaId}`).then(res => res.data),

    addComment: (mediaId: number, content: string) =>
        api.post('/comments', { mediaId, content }).then(res => res.data)
};