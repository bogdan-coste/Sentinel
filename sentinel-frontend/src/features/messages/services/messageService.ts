import api from '@shared/api/api';
import type { MessageDto, ConversationSummaryDto } from '@features/messages';

export const messageService = {
    getConversations: (): Promise<ConversationSummaryDto[]> =>
        api.get('/messages/all-conversations').then(res => res.data),

    getMessages: (publicId: string): Promise<MessageDto[]> =>
        api.get(`/messages/conversation/${publicId}`).then(res => res.data),

    sendMessage: (receiverPublicId: string, content: string): Promise<MessageDto> =>
        api.post('/messages/send-text', { receiverPublicId, content }).then(res => res.data),

    deleteConversation: (publicId: string) =>
        api.delete(`/messages/remove-conversation/${publicId}`)
};