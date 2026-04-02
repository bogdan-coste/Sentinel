import type { Comment } from '@features/comment/types';

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