export interface UserProfile {
    username: string;
    name: string;
    profilePicUrl: string;
    bannerPicUrl: string;
    description: string;
    email: string;
    friendCount: number;
    profilePicMediaId?: number;
    bannerMediaId?: number;
}

export interface GuestProfile {
    id: string;
    username: string;
    name: string;
    profilePicUrl: string | null;
    bannerPicUrl: string | null;
    description: string | null;
    role?: string;
    location?: string;
    friendCount: number;
    mutualCount: number;
    privacy: {
        timeline: 'Public' | 'Friends' | 'Only Me';
        friendsList: 'Public' | 'Friends' | 'Only Me';
        details: 'Public' | 'Friends' | 'Only Me';
    }
}