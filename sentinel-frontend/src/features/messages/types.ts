import type { UserDto } from '@shared/types';

export interface MessageDto {
    id: number;
    sender: UserDto;
    receiver: UserDto;
    content: string;
    createdAt: string | number[];
}

export interface ConversationSummaryDto {
    userEntity: UserDto;
    lastMessage: MessageDto | null;
    unreadCount: number;
}