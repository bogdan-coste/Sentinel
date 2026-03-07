import api from '@shared/api/api';

export const friendService = {
    addFriend: (publicId: string) => api.post('/friends/add-friend', { publicId }),
    removeFriend: (publicId: string) => api.delete('/friends/remove-friend', { data: { publicId } })
};