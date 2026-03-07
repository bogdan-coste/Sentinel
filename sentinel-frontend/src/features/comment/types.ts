export interface Comment {
    id: number;
    author: {
        id: number;
        name: string;
        username: string;
        profilePicUrl?: string;
    };
    content: string;
    createdAt: string;
}