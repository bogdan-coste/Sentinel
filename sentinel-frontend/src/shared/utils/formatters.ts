export const formatChatTime = (timeData: any): string => {
    if (!timeData) return '';
    let date = Array.isArray(timeData)
        ? new Date(timeData[0], timeData[1] - 1, timeData[2], timeData[3], timeData[4])
        : new Date(timeData);
    return date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
};

export const resolveAvatarUrl = (user: any): string | null => {
    if (!user) return null;
    if (user._tempAvatarUrl) return user._tempAvatarUrl;
    let pic = user.currentProfilePicURL || user.currentProfilePicUrl || user.profilePicUrl || user.image;
    if (pic) {
        if (pic.includes('/uploads/')) return pic.startsWith('/') ? pic : '/' + pic;
        return `/uploads/public/${user.publicId}/profile/${pic.replace(/^\/+/, '')}`;
    }
    return null;
};