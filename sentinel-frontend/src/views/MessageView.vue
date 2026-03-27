<template>
  <AppCanvas>
    <div class="h-screen w-full text-white font-sans overflow-hidden flex flex-col selection:bg-blue-500/30">

      <AppNavbar />

      <div class="flex-1 w-full max-w-[1400px] mx-auto overflow-hidden flex flex-row gap-6 px-4 lg:px-8 py-6">

        <aside class="w-[320px] xl:w-[360px] h-full flex flex-col shrink-0 bg-white/[0.02] backdrop-blur-2xl rounded-[32px] shadow-2xl border border-white/[0.05] overflow-hidden hidden md:flex">

          <div class="p-6 pb-4 border-b border-white/[0.05] shrink-0">
            <div class="flex items-center justify-between mb-5">
              <h2 class="text-2xl font-black text-white tracking-tight">Messages</h2>
              <button class="p-2 rounded-full bg-white/5 hover:bg-white/10 active:scale-95 transition-all text-white/70 hover:text-white border border-transparent hover:border-white/10">
                <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4"></path></svg>
              </button>
            </div>

            <div class="relative w-full group">
              <svg class="w-4 h-4 absolute left-4 top-1/2 -translate-y-1/2 text-white/40 group-focus-within:text-blue-400 transition-colors" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"></path></svg>
              <input
                  type="text"
                  placeholder="Search messages..."
                  class="w-full bg-black/20 hover:bg-black/30 focus:bg-white/[0.04] border border-white/[0.05] rounded-2xl py-2.5 pl-10 pr-4 text-[13px] text-white placeholder-white/40 focus:outline-none focus:border-white/20 transition-all"
              />
            </div>
          </div>

          <div class="flex-1 overflow-y-auto custom-scrollbar p-3 flex flex-col gap-1">
            <div
                v-for="chat in mockConversations"
                :key="chat.id"
                @click="activeChat = chat.id"
                class="flex items-center gap-3 p-3 rounded-[20px] cursor-pointer transition-all duration-200 border border-transparent"
                :class="activeChat === chat.id ? 'bg-white/[0.06] border-white/10 shadow-lg' : 'hover:bg-white/[0.03]'"
            >
              <div class="relative shrink-0">
                <div class="w-12 h-12 rounded-full overflow-hidden bg-[#0A192F] border-2 border-transparent" :class="activeChat === chat.id ? 'border-blue-400/50' : ''">
                  <img :src="chat.avatar" class="w-full h-full object-cover" />
                </div>
                <div v-if="chat.isOnline" class="absolute bottom-0 right-0 w-3.5 h-3.5 bg-green-500 rounded-full border-2 border-[#0A192F]"></div>
              </div>

              <div class="flex-1 min-w-0 flex flex-col justify-center">
                <div class="flex items-center justify-between mb-1">
                  <span class="text-[14px] font-bold text-white/95 truncate">{{ chat.name }}</span>
                  <span class="text-[10px] text-white/40 shrink-0">{{ chat.time }}</span>
                </div>
                <div class="flex items-center justify-between gap-2">
                  <p class="text-[12px] truncate" :class="chat.unread ? 'text-white font-semibold' : 'text-white/50 font-light'">
                    {{ chat.lastMessage }}
                  </p>
                  <span v-if="chat.unread" class="w-2 h-2 rounded-full bg-blue-500 shadow-[0_0_8px_rgba(59,130,246,0.8)] shrink-0"></span>
                </div>
              </div>
            </div>
          </div>
        </aside>

        <main class="flex-1 h-full flex flex-col bg-white/[0.02] backdrop-blur-2xl rounded-[32px] shadow-2xl border border-white/[0.05] overflow-hidden relative">

          <div class="h-20 shrink-0 w-full px-6 flex items-center justify-between border-b border-white/[0.05] bg-white/[0.01]">
            <div class="flex items-center gap-4">
              <button class="md:hidden p-2 rounded-full bg-white/5 hover:bg-white/10 active:scale-95 text-white/70 border border-transparent hover:border-white/10">
                <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7"></path></svg>
              </button>

              <div class="relative shrink-0">
                <img :src="activeChatData.avatar" class="w-11 h-11 rounded-full object-cover border border-white/10" />
                <div v-if="activeChatData.isOnline" class="absolute bottom-0 right-0 w-3 h-3 bg-green-500 rounded-full border-2 border-[#0A192F]"></div>
              </div>
              <div class="flex flex-col">
                <h3 class="text-[16px] font-bold text-white tracking-wide">{{ activeChatData.name }}</h3>
                <span class="text-[11px] font-medium text-blue-400 tracking-widest uppercase">
                  {{ activeChatData.isOnline ? 'Online Now' : 'Last seen ' + activeChatData.time }}
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
              <button class="p-2.5 rounded-full hover:bg-white/10 active:scale-95 transition-all text-white/60 hover:text-white border border-transparent hover:border-white/10">
                <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"></path></svg>
              </button>
            </div>
          </div>

          <div class="flex-1 overflow-y-auto custom-scrollbar p-6 flex flex-col gap-6">

            <div class="w-full text-center mb-2">
              <span class="text-[10px] uppercase tracking-widest font-bold text-white/30 bg-white/5 px-3 py-1 rounded-full border border-white/5">Today</span>
            </div>

            <div
                v-for="msg in mockMessages"
                :key="msg.id"
                class="flex flex-col max-w-[75%]"
                :class="msg.isMine ? 'self-end items-end' : 'self-start items-start'"
            >
              <div
                  class="px-5 py-3 shadow-lg relative group"
                  :class="msg.isMine ? 'bg-blue-600 text-white rounded-[24px] rounded-br-sm' : 'bg-white/[0.04] backdrop-blur-md border border-white/[0.05] text-white/90 rounded-[24px] rounded-bl-sm'"
              >
                <p class="text-[14.5px] leading-relaxed font-light">{{ msg.text }}</p>

                <span
                    class="absolute top-1/2 -translate-y-1/2 text-[10px] text-white/30 font-semibold opacity-0 group-hover:opacity-100 transition-opacity whitespace-nowrap"
                    :class="msg.isMine ? '-left-14' : '-right-14'"
                >
                  {{ msg.time }}
                </span>
              </div>
            </div>

            <div v-if="isTyping" class="self-start flex items-center gap-2 px-5 py-3.5 bg-white/[0.02] backdrop-blur-md border border-white/[0.05] rounded-[24px] rounded-bl-sm w-fit shadow-lg">
              <div class="flex gap-1.5">
                <div class="w-1.5 h-1.5 rounded-full bg-white/40 animate-bounce"></div>
                <div class="w-1.5 h-1.5 rounded-full bg-white/40 animate-bounce" style="animation-delay: 0.1s"></div>
                <div class="w-1.5 h-1.5 rounded-full bg-white/40 animate-bounce" style="animation-delay: 0.2s"></div>
              </div>
            </div>

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
                <button class="w-10 h-10 rounded-full hover:bg-white/10 flex items-center justify-center text-white/40 hover:text-yellow-400 transition-colors border border-transparent hover:border-white/10">
                  <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M14.828 14.828a4 4 0 01-5.656 0M9 10h.01M15 10h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"></path></svg>
                </button>
                <button
                    @click="sendMessage"
                    class="w-10 h-10 rounded-full flex items-center justify-center shadow-lg active:scale-95 transition-all"
                    :class="newMessage.trim() ? 'bg-blue-600 hover:bg-blue-500 text-white' : 'bg-white/5 text-white/30 cursor-not-allowed'"
                >
                  <svg class="w-4 h-4 -mr-0.5 mt-0.5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2.5" d="M12 19l9 2-9-18-9 18 9-2zm0 0v-8"></path></svg>
                </button>
              </div>
            </div>
          </div>

        </main>
      </div>

    </div>
  </AppCanvas>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import AppNavbar from "../components/common/AppNavbar.vue"
import AppCanvas from "../components/common/AppCanvas.vue"

// Purely Mock Data
const mockConversations = ref([
  {
    id: 1,
    name: 'Elena Rossi',
    avatar: 'https://i.pravatar.cc/150?img=47',
    lastMessage: 'The new glassmorphism UI is stunning! ✨',
    time: '10:42 AM',
    unread: true,
    isOnline: true
  },
  {
    id: 2,
    name: 'David Chen',
    avatar: 'https://i.pravatar.cc/150?img=14',
    lastMessage: 'Did you push the latest commit?',
    time: 'Yesterday',
    unread: false,
    isOnline: true
  },
  {
    id: 3,
    name: 'Sarah Jenkins',
    avatar: 'https://i.pravatar.cc/150?img=32',
    lastMessage: 'Let\'s catch up over coffee this weekend.',
    time: 'Tue',
    unread: false,
    isOnline: false
  },
  {
    id: 4,
    name: 'Marcus Johnson',
    avatar: 'https://i.pravatar.cc/150?img=59',
    lastMessage: 'Haha, exactly what I was thinking! 😂',
    time: 'Mon',
    unread: false,
    isOnline: true
  },
  {
    id: 5,
    name: 'Design Team',
    avatar: 'https://images.unsplash.com/photo-1522071820081-009f0129c71c?q=80&w=200&auto=format&fit=crop',
    lastMessage: 'Alice: Updated the Figma files.',
    time: 'Last Week',
    unread: false,
    isOnline: false
  }
])

const activeChat = ref(1)

const activeChatData = computed(() => {
  return mockConversations.value.find(c => c.id === activeChat.value) || mockConversations.value[0]
})

const mockMessages = ref([
  { id: 1, text: 'Hey Bogdan! Have you seen the new design updates?', time: '10:30 AM', isMine: false },
  { id: 2, text: 'Yes! I just pulled the repo. The new AppCanvas wrapper is working perfectly.', time: '10:35 AM', isMine: true },
  { id: 3, text: 'Awesome. I love how the gradient sits behind everything now instead of cutting off.', time: '10:38 AM', isMine: false },
  { id: 4, text: 'Exactly. Makes the whole app feel like a native desktop client rather than a website.', time: '10:40 AM', isMine: true },
  { id: 5, text: 'The new glassmorphism UI is stunning! ✨', time: '10:42 AM', isMine: false },
])

const newMessage = ref('')
const isTyping = ref(false)

const sendMessage = () => {
  if (!newMessage.value.trim()) return;

  mockMessages.value.push({
    id: Date.now(),
    text: newMessage.value,
    time: 'Now',
    isMine: true
  })

  newMessage.value = ''

  isTyping.value = true
  setTimeout(() => {
    isTyping.value = false
    mockMessages.value.push({
      id: Date.now(),
      text: 'Haha, completely agree. Great work!',
      time: 'Now',
      isMine: false
    })
  }, 2000)
}
</script>

<style scoped>
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