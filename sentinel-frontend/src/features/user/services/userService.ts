import api from '@shared/api/api';
import type { Post } from '@features/post';

export const userService = {
    async getProfile() {
        const response = await api.get('/users/me');
        return response.data;
    },

    async updateProfile(payload: { name: string, description: string }) {
        await api.put('/users/me', payload);
    },

    async getMyFriends() {
        const response = await api.get('/friends/my-friends');
        return response.data;
    },

    async getMyPosts(): Promise<Post[]> {
        const response = await api.get('/users/me/user-media');

        return response.data
            .filter((item: any) => item.type === 'POST')
            .map((item: any) => ({
                id: item.id,
                time: new Date(item.createdAt).toLocaleString(),
                content: item.content || '',
                image: item.imageUrl || null,
                likes: item.likeCount || 0,
                comments: undefined,
                shares: 0,
                isLiked: item.likedByCurrentUser,
                showComments: false,
                showLikes: false,
                commentInput: ''
            }));
    },

    async getGuestProfile(publicId: string) {
        const response = await api.get(`/users/${publicId}`);
        const data = response.data;

        const guestProfile = {
            id: data.userInformation.publicId || publicId,
            username: data.userInformation.username,
            name: data.userInformation.name,
            profilePicUrl: data.userInformation.profilePicUrl || null,
            bannerPicUrl: data.userInformation.bannerPicUrl || null,
            description: data.userInformation.description || null,
            role: data.userInformation.role || '',
            location: data.userInformation.location || '',
            friendCount: data.userInformation.friendCount || 0,
            mutualCount: data.userInformation.mutualCount || 0,
            privacy: data.userInformation.privacy || { timeline: 'Friends', friendsList: 'Friends', details: 'Public' }
        };

        const profilePosts = (data.media || [])
            .filter((item: any) => item.type === 'POST' || !item.type)
            .map((item: any) => ({
                id: item.id,
                time: item.createdAt ? new Date(item.createdAt).toLocaleString() : 'Recently',
                content: item.content || '',
                image: item.url || item.imageUrl || null,
                likes: item.likesCount || item.likeCount || 0,
                isLiked: item.isLiked || item.likedByCurrentUser || false,
                showComments: false,
            }));

        const friends = (data.friends || []).map((f: any) => ({
            publicId: f.publicId, username: f.username, name: f.name, image: f.profilePicUrl || undefined
        }));

        return {
            guestProfile,
            profilePosts,
            friends,
            friendshipStatus: data.friendshipStatus
        };
    }
};