<template>
  <AppCanvas>
    <div class="h-screen w-full text-white font-sans overflow-hidden flex flex-col selection:bg-blue-500/30">

      <AppNavbar />

      <div class="flex-1 w-full max-w-350 mx-auto overflow-hidden flex flex-row gap-6 px-4 lg:px-8 py-6 relative">

        <aside class="w-[320px] xl:w-90 h-full flex flex-col shrink-0 bg-white/2 backdrop-blur-2xl rounded-4xl shadow-2xl border border-white/5 overflow-hidden md:flex">

          <div class="p-6 pb-4 border-b border-white/5 shrink-0">
            <div class="flex items-center justify-between mb-5">
              <h2 class="text-2xl font-black text-white tracking-tight">Messages</h2>
              <button @click="openNewChatModal" class="p-2 rounded-full bg-white/5 hover:bg-white/10 active:scale-95 transition-all text-white/70 hover:text-white border border-transparent hover:border-white/10" title="New Message">
                <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4"></path></svg>
              </button>
            </div>

            <div class="relative w-full group">
              <svg class="w-4 h-4 absolute left-4 top-1/2 -translate-y-1/2 text-white/40 group-focus-within:text-blue-400 transition-colors" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"></path></svg>
              <input
                  type="text"
                  placeholder="Search messages..."
                  class="w-full bg-black/20 hover:bg-black/30 focus:bg-white/4 border border-white/5 rounded-2xl py-2.5 pl-10 pr-4 text-[13px] text-white placeholder-white/40 focus:outline-none focus:border-white/20 transition-all"
              />
            </div>
          </div>

          <div class="flex-1 overflow-y-auto custom-scrollbar p-3 flex flex-col gap-1">
            <div v-if="loadingConversations" class="flex justify-center py-10">
              <div class="animate-spin rounded-full h-8 w-8 border-t-2 border-b-2 border-blue-400"></div>
            </div>

            <div v-else-if="conversations.length === 0" class="text-center py-10 text-white/40 text-sm">
              No conversations yet.<br/>Click the + icon to start chatting.
            </div>

            <div
                v-else
                v-for="chat in conversations"
                :key="chat.user.publicId"
                @click="selectChat(chat.user.publicId)"
                class="flex items-center gap-3 p-3 rounded-[20px] cursor-pointer transition-all duration-200 border border-transparent"
                :class="activeChatPublicId === chat.user.publicId ? 'bg-white/6 border-white/10 shadow-lg' : 'hover:bg-white/[0.03]'"
            >
              <div class="relative shrink-0">
                <div class="w-12 h-12 rounded-full overflow-hidden bg-[#0A192F] border-2 border-transparent flex items-center justify-center" :class="activeChatPublicId === chat.user.publicId ? 'border-blue-400/50' : ''">
                  <img v-if="getAvatarUrl(chat.user)" :src="getAvatarUrl(chat.user)!" class="w-full h-full object-cover" />
                  <svg v-else class="w-6 h-6 text-white/30" fill="currentColor" viewBox="0 0 24 24"><path d="M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm0 2c-2.67 0-8 1.34-8 4v2h16v-2c0-2.66-5.33-4-8-4z"/></svg>
                </div>
              </div>

              <div class="flex-1 min-w-0 flex flex-col justify-center">
                <div class="flex items-center justify-between mb-1">
                  <span class="text-[14px] font-bold text-white/95 truncate">{{ chat.user.name }}</span>
                  <span class="text-[10px] text-white/40 shrink-0" v-if="chat.lastMessage">{{ formatTime(chat.lastMessage.createdAt) }}</span>
                </div>
                <div class="flex items-center justify-between gap-2">
                  <p class="text-[12px] truncate" :class="chat.unreadCount > 0 ? 'text-white font-semibold' : 'text-white/50 font-light'">
                    {{ chat.lastMessage ? chat.lastMessage.content : 'New conversation' }}
                  </p>
                  <span v-if="chat.unreadCount > 0" class="w-4 h-4 rounded-full bg-blue-500 shadow-[0_0_8px_rgba(59,130,246,0.8)] shrink-0 flex items-center justify-center text-[9px] font-bold text-white">
                    {{ chat.unreadCount > 9 ? '9+' : chat.unreadCount }}
                  </span>
                </div>
              </div>
            </div>
          </div>
        </aside>

        <main class="flex-1 h-full flex flex-col bg-white/2 backdrop-blur-2xl rounded-[32px] shadow-2xl border border-white/[0.05] overflow-hidden relative">

          <template v-if="activeChatData">
            <div class="h-20 shrink-0 w-full px-6 flex items-center justify-between border-b border-white/[0.05] bg-white/[0.01]">
              <div class="flex items-center gap-4">
                <button class="md:hidden p-2 rounded-full bg-white/5 hover:bg-white/10 active:scale-95 text-white/70 border border-transparent hover:border-white/10">
                  <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7"></path></svg>
                </button>

                <div @click="goToProfile(activeChatData.user.publicId)" class="relative shrink-0 w-11 h-11 rounded-full overflow-hidden bg-[#0A192F] border border-white/10 flex items-center justify-center cursor-pointer hover:opacity-80 transition-opacity">
                  <img v-if="getAvatarUrl(activeChatData.user)" :src="getAvatarUrl(activeChatData.user)!" class="w-full h-full object-cover" />
                  <svg v-else class="w-6 h-6 text-white/30" fill="currentColor" viewBox="0 0 24 24"><path d="M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm0 2c-2.67 0-8 1.34-8 4v2h16v-2c0-2.66-5.33-4-8-4z"/></svg>
                </div>
                <div class="flex flex-col">
                  <h3 @click="goToProfile(activeChatData.user.publicId)" class="text-[16px] font-bold text-white tracking-wide cursor-pointer hover:text-blue-400 transition-colors">{{ activeChatData.user.name }}</h3>
                  <span class="text-[11px] font-medium text-white/40 tracking-widest uppercase">
                    @{{ activeChatData.user.username }}
                  </span>
                </div>
              </div>

              <div class="flex items-center gap-2">
                <button class="p-2.5 rounded-full hover:bg-white/10 active:scale-95 transition-all text-white/60 hover:text-blue-400 border border-transparent hover:border-white/10">
                  <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 5a2 2 0 012-2h3.28a1 1 0 01.948.684l1.498 4.493a1 1 0 01-.502 1.21l-2.257 1.13a11.042 11.042 0 005.516 5.516l1.13-2.257a1 1 0 011.21-.502l4.493 1.498a1 1 0 01.684.949V19a2 2 0 01-2 2h-1C9.716 21 3 14.284 3 6V5z"></path></svg>
                </button>
                <button class="p-2.5 rounded-full hover:bg-white/10 active:scale-95 transition-all text-white/60 hover:text-blue-400 border border-transparent hover:border-white/10">
                  <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 10l4.553-2.276A1 1 0 0121 8.618v6.764a1 1 0 01-1.447.894L15 14M5 18h8a2 2 0 002-2V8a2 2 0 00-2-2H5a2 2 0 00-2 2v8a2 2 0 002 2z"></path></svg>
                </button>
                <div class="w-px h-6 bg-white/10 mx-1"></div>

                <div class="relative">
                  <button @click="isChatMenuOpen = !isChatMenuOpen" class="p-2.5 rounded-full transition-all border border-transparent relative z-[60]" :class="isChatMenuOpen ? 'bg-white/10 text-white border-white/10' : 'text-white/60 hover:text-white hover:bg-white/10 hover:border-white/10'">
                    <svg class="w-5 h-5" fill="currentColor" viewBox="0 0 24 24"><path d="M12 8c1.1 0 2-.9 2-2s-.9-2-2-2-2 .9-2 2 .9 2 2 2zm0 2c-1.1 0-2 .9-2 2s.9 2 2 2 2-.9 2-2-.9-2-2-2zm0 6c-1.1 0-2 .9-2 2s.9 2 2 2 2-.9 2-2-.9-2-2-2z"></path></svg>
                  </button>

                  <div v-if="isChatMenuOpen" @click="isChatMenuOpen = false" class="fixed inset-0 z-[50]"></div>

                  <div v-if="isChatMenuOpen" class="absolute right-0 top-12 w-52 bg-[#0A192F]/95 backdrop-blur-2xl border border-white/10 rounded-[16px] shadow-2xl py-1.5 z-[120] animate-fade-in flex flex-col">
                    <button @click="goToProfile(activeChatData.user.publicId)" class="w-full flex items-center gap-3 px-4 py-3 text-[12px] font-bold text-white/80 hover:text-white hover:bg-white/[0.06] transition-colors">
                      <svg class="w-4 h-4 text-white/50" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"></path></svg>
                      View Profile
                    </button>
                    <div class="w-full h-px bg-white/5 my-1"></div>
                    <button @click="handleDeleteConversation" class="w-full flex items-center gap-3 px-4 py-3 text-[12px] font-bold text-red-400 hover:text-red-300 hover:bg-red-500/10 transition-colors">
                      <svg class="w-4 h-4 text-red-400/70" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"></path></svg>
                      Delete Chat
                    </button>
                  </div>
                </div>
              </div>
            </div>

            <div ref="messagesContainer" class="flex-1 overflow-y-auto custom-scrollbar p-6 flex flex-col gap-6">

              <div v-if="loadingMessages" class="w-full h-full flex items-center justify-center">
                <div class="animate-spin rounded-full h-8 w-8 border-t-2 border-b-2 border-blue-400"></div>
              </div>

              <template v-else>
                <div class="w-full text-center mb-6 mt-4">
                  <span class="text-[10px] uppercase tracking-widest font-bold text-white/30 bg-white/5 px-3 py-1 rounded-full border border-white/5">Start of Conversation</span>
                </div>

                <div
                    v-for="msg in messages"
                    :key="msg.id"
                    class="flex flex-col max-w-[75%]"
                    :class="isMine(msg) ? 'self-end items-end' : 'self-start items-start'"
                >
                  <div
                      class="px-5 py-3 shadow-lg relative group"
                      :class="isMine(msg) ? 'bg-blue-600 text-white rounded-[24px] rounded-br-sm' : 'bg-white/[0.04] backdrop-blur-md border border-white/[0.05] text-white/90 rounded-[24px] rounded-bl-sm'"
                  >
                    <p class="text-[14.5px] leading-relaxed font-light whitespace-pre-wrap">{{ msg.content }}</p>

                    <span
                        class="absolute top-1/2 -translate-y-1/2 text-[10px] text-white/30 font-semibold opacity-0 group-hover:opacity-100 transition-opacity whitespace-nowrap"
                        :class="isMine(msg) ? '-left-14' : '-right-14'"
                    >
                      {{ formatTime(msg.createdAt) }}
                    </span>
                  </div>
                </div>
              </template>
            </div>

            <div class="p-5 bg-white/[0.01] border-t border-white/[0.05] shrink-0">
              <div class="w-full bg-black/20 focus-within:bg-white/[0.03] border border-white/[0.05] focus-within:border-white/10 rounded-[32px] p-2 pr-2.5 flex items-end gap-2 shadow-inner transition-all">

                <button class="w-10 h-10 shrink-0 rounded-full hover:bg-white/10 flex items-center justify-center text-white/40 hover:text-blue-400 transition-colors mb-0.5">
                  <svg class="w-5 h-5" fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" d="M15.172 7l-6.586 6.586a2 2 0 102.828 2.828l6.414-6.586a4 4 0 00-5.656-5.656l-6.415 6.585a6 6 0 108.486 8.486L20.5 13"></path></svg>
                </button>

                <textarea
                    v-model="newMessage"
                    placeholder="Message..."
                    rows="1"
                    class="flex-grow bg-transparent text-[14px] text-white placeholder-white/40 focus:outline-none py-3 px-1 resize-none custom-scrollbar max-h-[120px]"
                    @keydown.enter.prevent="sendMessage"
                ></textarea>

                <div class="flex items-center gap-1 mb-0.5 shrink-0">
                  <button
                      @click="sendMessage"
                      class="w-10 h-10 rounded-full flex items-center justify-center shadow-lg transition-all"
                      :class="newMessage.trim() && !isSending ? 'bg-blue-600 hover:bg-blue-500 text-white active:scale-95' : 'bg-white/5 text-white/30 cursor-not-allowed'"
                      :disabled="!newMessage.trim() || isSending"
                  >
                    <div v-if="isSending" class="w-4 h-4 border-2 border-white/40 border-t-white rounded-full animate-spin"></div>
                    <svg v-else class="w-4 h-4 -mr-0.5 mt-0.5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2.5" d="M12 19l9 2-9-18-9 18 9-2zm0 0v-8"></path></svg>
                  </button>
                </div>
              </div>
            </div>
          </template>

          <div v-else class="flex-1 flex flex-col items-center justify-center text-center p-8">
            <div class="w-20 h-20 rounded-full bg-white/5 flex items-center justify-center mb-6">
              <svg class="w-10 h-10 text-white/20" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M8 12h.01M12 12h.01M16 12h.01M21 12c0 4.418-4.03 8-9 8a9.863 9.863 0 01-4.255-.949L3 20l1.395-3.72C3.512 15.042 3 13.574 3 12c0-4.418 4.03-8 9-8s9 3.582 9 8z"></path></svg>
            </div>
            <h3 class="text-[20px] font-bold text-white mb-2">Your Messages</h3>
            <p class="text-[13px] text-white/50 max-w-sm">Select a conversation from the sidebar or click the + icon to start chatting with your friends.</p>
          </div>

        </main>
      </div>

      <Teleport to="body">
        <div v-if="isNewChatModalOpen" class="fixed inset-0 z-[100] flex items-center justify-center p-4 sm:p-6 animate-fade-in">
          <div class="absolute inset-0 bg-[#0A192F]/80 backdrop-blur-md cursor-pointer" @click="closeNewChatModal"></div>

          <div class="relative w-full max-w-md bg-gradient-to-b from-[#0A192F] to-[#051021] border border-white/10 rounded-[32px] shadow-[0_0_40px_rgba(0,0,0,0.5)] flex flex-col max-h-[85vh] overflow-hidden z-[105]">

            <div class="flex items-center justify-between p-6 border-b border-white/5 shrink-0 bg-white/[0.02]">
              <div>
                <h3 class="text-[18px] font-black text-white tracking-tight">New Message</h3>
                <p class="text-[11px] font-bold text-blue-400 uppercase tracking-widest mt-1">Select a friend</p>
              </div>
              <button @click="closeNewChatModal" class="p-2.5 text-white/40 hover:text-white bg-white/5 hover:bg-white/10 rounded-full transition-all active:scale-95 border border-transparent hover:border-white/10">
                <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path></svg>
              </button>
            </div>

            <div class="p-4 border-b border-white/5 shrink-0 bg-black/20">
              <div class="relative group">
                <svg class="w-4 h-4 absolute left-4 top-1/2 -translate-y-1/2 text-white/40 group-focus-within:text-blue-400 transition-colors" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"></path></svg>
                <input
                    v-model="friendSearchQuery"
                    type="text"
                    placeholder="Search friends..."
                    class="w-full bg-white/[0.03] hover:bg-white/[0.06] focus:bg-white/[0.08] border border-white/5 rounded-2xl py-3.5 pl-11 pr-4 text-[13px] text-white placeholder-white/40 focus:outline-none focus:border-white/20 transition-all"
                />
              </div>
            </div>

            <div class="flex-1 overflow-y-auto custom-scrollbar p-3 relative">
              <div v-if="loadingFriends" class="flex justify-center py-10">
                <div class="animate-spin rounded-full h-6 w-6 border-t-2 border-b-2 border-blue-400"></div>
              </div>

              <ul v-else-if="filteredFriends.length > 0" class="flex flex-col gap-2 pb-6">
                <li v-for="friend in filteredFriends" :key="friend.publicId">

                  <button
                      @click="startChatWith(friend)"
                      class="w-full flex items-center justify-between p-2.5 rounded-[20px] hover:bg-white/[0.04] border border-transparent hover:border-white/[0.05] transition-all group"
                  >
                    <div class="flex items-center gap-4 flex-1 overflow-hidden px-1 text-left">
                      <div class="w-12 h-12 rounded-full overflow-hidden shrink-0 border-[2px] border-[#0A192F] shadow-sm bg-[#051021]">
                        <img v-if="friend.image" :src="friend.image" class="w-full h-full object-cover" alt="Avatar"/>
                        <div v-else class="w-full h-full flex items-center justify-center bg-white/5">
                          <svg class="w-6 h-6 text-white/30" fill="currentColor" viewBox="0 0 24 24"><path d="M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm0 2c-2.67 0-8 1.34-8 4v2h16v-2c0-2.66-5.33-4-8-4z"/></svg>
                        </div>
                      </div>
                      <div class="flex flex-col flex-1 overflow-hidden">
                        <span class="text-[14px] font-bold text-white/95 group-hover:text-blue-400 transition-colors truncate">{{ friend.name }}</span>
                        <span class="text-[11px] text-white/40 font-medium truncate mt-0.5">@{{ friend.username }}</span>
                      </div>
                    </div>
                  </button>

                </li>
              </ul>

              <div v-else class="flex flex-col items-center justify-center py-16 text-center relative z-10">
                <div class="w-16 h-16 rounded-3xl bg-white/5 border border-white/5 flex items-center justify-center mb-4 transform -rotate-6">
                  <svg class="w-8 h-8 text-white/20" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197M13 7a4 4 0 11-8 0 4 4 0 018 0z"></path></svg>
                </div>
                <p class="text-white/60 text-[14px] font-bold">No friends found</p>
                <p class="text-white/30 text-[12px] mt-1">Make sure you have added friends first.</p>
              </div>
            </div>

          </div>
        </div>
      </Teleport>

    </div>
  </AppCanvas>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import AppNavbar from "../components/common/AppNavbar.vue"
import AppCanvas from "../components/common/AppCanvas.vue"
import api from "../service/api.ts"

const router = useRouter()

interface UserDto {
  publicId: string;
  name: string;
  username: string;
  profilePicUrl?: string | null;
  currentProfilePicURL?: string | null;
  currentProfilePicUrl?: string | null;
  image?: string | null;
  _tempAvatarUrl?: string | null;
}

interface MessageDto {
  id: number;
  sender: UserDto;
  receiver: UserDto;
  content: string;
  type: string;
  createdAt: string | number[];
  readAt: string | null;
  status: string;
}

interface ConversationSummaryDto {
  user: UserDto;
  lastMessage: MessageDto | null;
  unreadCount: number;
}

interface Friend {
  publicId: string;
  name: string;
  username: string;
  image: string | null;
}

// --- Core State ---
const conversations = ref<ConversationSummaryDto[]>([])
const activeChatPublicId = ref<string | null>(null)
const messages = ref<MessageDto[]>([])

const newMessage = ref('')
const loadingConversations = ref(true)
const loadingMessages = ref(false)
const isSending = ref(false)
const isChatMenuOpen = ref(false)

const messagesContainer = ref<HTMLElement | null>(null)

// --- New Chat Modal State ---
const isNewChatModalOpen = ref(false)
const myFriends = ref<Friend[]>([])
const loadingFriends = ref(false)
const friendSearchQuery = ref('')

// --- Computed ---
const activeChatData = computed(() => {
  return conversations.value.find(c => c.user.publicId === activeChatPublicId.value) || null
})

const filteredFriends = computed(() => {
  if (!friendSearchQuery.value) return myFriends.value;
  const lowerQuery = friendSearchQuery.value.toLowerCase();
  return myFriends.value.filter(f =>
      (f.name || '').toLowerCase().includes(lowerQuery) ||
      (f.username || '').toLowerCase().includes(lowerQuery)
  );
});

const isMine = (msg: MessageDto) => {
  return msg.sender.publicId !== activeChatPublicId.value;
}

const getAvatarUrl = (user: any) => {
  if (!user) return null;
  if (user._tempAvatarUrl) return user._tempAvatarUrl;

  let pic = user.currentProfilePicURL || user.currentProfilePicUrl || user.profilePicUrl || user.image;

  if (pic) {
    if (pic.includes('/uploads/public/')) {
      return '/' + pic.replace(/^\/+/, '');
    }

    pic = pic.replace(/^\/+/, '');
    if (user.publicId) {
      return `/uploads/public/${user.publicId}/profile/${pic}`;
    }
  }
  return null;
}
const formatTime = (timeData: string | number[] | undefined | null) => {
  if (!timeData) return '';
  let date;

  if (Array.isArray(timeData)) {
    const [
      year = 0,
      month = 1,
      day = 1,
      hour = 0,
      minute = 0,
      second = 0
    ] = timeData;

    date = new Date(year, month - 1, day, hour || 0, minute || 0, second || 0);
  } else {
    date = new Date(timeData);
  }

  return date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
}

const scrollToBottom = () => {
  nextTick(() => {
    if (messagesContainer.value) {
      messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
    }
  })
}

const goToProfile = (publicId: string) => {
  isChatMenuOpen.value = false;
  router.push(`/profile/${publicId}`);
}

const openNewChatModal = async () => {
  isNewChatModalOpen.value = true;
  document.body.style.overflow = 'hidden';
  if (myFriends.value.length === 0) {
    loadingFriends.value = true;
    try {
      const res = await api.get('/friends/my-friends');
      myFriends.value = res.data;
    } catch (err) {
      console.error('Failed to load friends', err);
    } finally {
      loadingFriends.value = false;
    }
  }
}

const closeNewChatModal = () => {
  isNewChatModalOpen.value = false;
  friendSearchQuery.value = '';
  document.body.style.overflow = '';
}

const startChatWith = (friend: Friend) => {
  closeNewChatModal();

  const existingConvo = conversations.value.find(c => c.user.publicId === friend.publicId);

  if (!existingConvo) {
    const newUserDto: UserDto = {
      publicId: friend.publicId,
      name: friend.name,
      username: friend.username,
      _tempAvatarUrl: friend.image
    };

    conversations.value.unshift({
      user: newUserDto,
      lastMessage: null,
      unreadCount: 0
    });
  }

  selectChat(friend.publicId);
}

const fetchConversations = async () => {
  loadingConversations.value = true
  try {
    const res = await api.get('/messages/all-conversations')
    conversations.value = res.data
    const firstChat = conversations.value[0];
    if (firstChat?.user?.publicId && !activeChatPublicId.value) {
      await selectChat(firstChat.user.publicId);
    }
  } catch (err) {
    console.error('Failed to load conversations', err)
  } finally {
    loadingConversations.value = false
  }
}

const selectChat = async (publicId: string) => {
  activeChatPublicId.value = publicId
  loadingMessages.value = true
  messages.value = []
  try {
    const res = await api.get(`/messages/conversation/${publicId}`)

    messages.value = res.data.sort((a: MessageDto, b: MessageDto) => a.id - b.id);

    const chat = conversations.value.find(c => c.user.publicId === publicId)
    if (chat) chat.unreadCount = 0

    scrollToBottom()
  } catch (err) {
    console.error('Failed to load messages', err)
  } finally {
    loadingMessages.value = false
  }
}

const sendMessage = async () => {
  if (!newMessage.value.trim() || !activeChatPublicId.value || isSending.value) return;

  const content = newMessage.value.trim();
  newMessage.value = '';
  isSending.value = true;

  try {
    const res = await api.post('/messages/send-text', {
      receiverPublicId: activeChatPublicId.value,
      content: content
    });

    messages.value.push(res.data);

    const convoIndex = conversations.value.findIndex(c => c.user.publicId === activeChatPublicId.value);
    if (convoIndex !== -1) {
      const convo = conversations.value[convoIndex];
      if (convo) {
        convo.lastMessage = res.data;
        conversations.value.splice(convoIndex, 1);
        conversations.value.unshift(convo);
      }
    }

    scrollToBottom();
  } catch (err) {
    console.error('Failed to send message', err);
    alert("Failed to send message.");
  } finally {
    isSending.value = false;
  }
}

const handleDeleteConversation = async () => {
  isChatMenuOpen.value = false;
  if (!activeChatPublicId.value) return;

  if (!confirm(`Are you sure you want to delete your conversation with ${activeChatData.value?.user.name}? This action cannot be undone.`)) return;

  try {
    await api.delete(`/messages/remove-conversation/${activeChatPublicId.value}`);

    conversations.value = conversations.value.filter(c => c.user.publicId !== activeChatPublicId.value);
    activeChatPublicId.value = null;
    messages.value = [];

    if (conversations.value.length > 0) {
      const nextChat = conversations.value[0];
      if (nextChat && nextChat.user.publicId) {
        selectChat(nextChat.user.publicId);
      }
    }
  } catch (err) {
    console.error('Failed to delete conversation', err);
    alert('Failed to delete conversation.');
  }
}

onMounted(() => {
  fetchConversations()
})
</script>

<style scoped>
.animate-fade-in {
  animation: fadeIn 0.15s cubic-bezier(0.16, 1, 0.3, 1);
}
@keyframes fadeIn {
  from { opacity: 0; transform: scale(0.96) translateY(5px); }
  to { opacity: 1; transform: scale(1) translateY(0); }
}

.custom-scrollbar::-webkit-scrollbar {
  width: 5px;
}
.custom-scrollbar::-webkit-scrollbar-track {
  background: transparent;
}
.custom-scrollbar::-webkit-scrollbar-thumb {
  background: rgba(255, 255, 255, 0.1);
  border-radius: 10px;
}
.custom-scrollbar::-webkit-scrollbar-thumb:hover {
  background: rgba(255, 255, 255, 0.2);
}
</style>