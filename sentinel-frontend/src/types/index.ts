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

export interface Post {
    id: number;
    time: string;
    content: string;
    image: string | null;
    likes: number;
    comments: Comment[];
    shares: number;
    isLiked: boolean;
    showComments: boolean;
    commentInput?: string;
}