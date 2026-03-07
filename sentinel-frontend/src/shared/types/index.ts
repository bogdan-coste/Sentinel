export interface UserDto {
    publicId: string;
    name: string;
    username: string;
    profilePicUrl?: string | null;
    currentProfilePicURL?: string | null;
    image?: string | null;
    _tempAvatarUrl?: string | null;
}