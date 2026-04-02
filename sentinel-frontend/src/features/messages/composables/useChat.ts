import { ref, computed } from 'vue';
import { messageService } from '@features/messages';
import type { ConversationSummaryDto, MessageDto } from '@features/messages';
import type { Friend } from '@features/friendship';

export function useChat() {
    const conversations = ref<ConversationSummaryDto[]>([]);
    const activeChatPublicId = ref<string | null>(null);
    const messages = ref<MessageDto[]>([]);

    const loadingConversations = ref(true);
    const loadingMessages = ref(false);
    const isSending = ref(false);

    const validConversations = computed(() =>
        conversations.value.filter(c => c && c.userEntity)
    );

    const activeChatData = computed(() =>
        conversations.value.find(c => c.userEntity?.publicId === activeChatPublicId.value) || null
    );

    const isMine = (msg: MessageDto) => msg.sender.publicId !== activeChatPublicId.value;

    const fetchConversations = async () => {
        loadingConversations.value = true;
        try {
            const data = await messageService.getConversations();
            conversations.value = data || [];

            const firstChat = validConversations.value[0];
            if (firstChat && !activeChatPublicId.value) {
                await selectChat(firstChat.userEntity.publicId);
            }
        } catch (err) {
            console.error('Failed to load conversations', err);
        } finally {
            loadingConversations.value = false;
        }
    };

    const selectChat = async (publicId: string, onChatLoaded?: () => void) => {
        activeChatPublicId.value = publicId;
        loadingMessages.value = true;
        messages.value = [];
        try {
            const data = await messageService.getMessages(publicId);
            messages.value = data.sort((a: any, b: any) => a.id - b.id);

            const chat = conversations.value.find(c => c.userEntity?.publicId === publicId);
            if (chat) chat.unreadCount = 0;

            if (onChatLoaded) onChatLoaded();
        } catch (err) {
            console.error('Failed to load messages', err);
        } finally {
            loadingMessages.value = false;
        }
    };

    const sendMessage = async (content: string, onMessageSent?: () => void) => {
        if (!content.trim() || !activeChatPublicId.value || isSending.value) return;

        isSending.value = true;
        const id = activeChatPublicId.value;

        try {
            const res = await messageService.sendMessage(id, content);
            messages.value.push(res);

            const index = conversations.value.findIndex(c => c.userEntity?.publicId === id);
            if (index !== -1) {
                const [convo] = conversations.value.splice(index, 1);
                if (convo) {
                    convo.lastMessage = res;
                    conversations.value.unshift(convo);
                }
            }
            if (onMessageSent) onMessageSent();
        } catch (err) {
            console.error('Failed to send message', err);
        } finally {
            isSending.value = false;
        }
    };

    const deleteConversation = async () => {
        if (!activeChatPublicId.value) return;
        try {
            await messageService.deleteConversation(activeChatPublicId.value);
            conversations.value = conversations.value.filter(c => c.userEntity?.publicId !== activeChatPublicId.value);
            activeChatPublicId.value = null;
            messages.value = [];
        } catch (err) {
            console.error('Failed to delete conversation', err);
            throw err;
        }
    };

    const startNewChat = (friend: Friend) => {
        const existing = conversations.value.find(c => c.userEntity?.publicId === friend.publicId);

        if (!existing) {
            conversations.value.unshift({
                userEntity: {
                    publicId: friend.publicId,
                    name: friend.name,
                    username: friend.username,
                    currentProfilePicURL: friend.image
                },
                lastMessage: null,
                unreadCount: 0
            } as unknown as ConversationSummaryDto);
        }
        selectChat(friend.publicId);
    };

    return {
        activeChatPublicId,
        messages,
        loadingConversations,
        loadingMessages,
        isSending,
        validConversations,
        activeChatData,
        isMine,
        fetchConversations,
        selectChat,
        sendMessage,
        deleteConversation,
        startNewChat
    };
}