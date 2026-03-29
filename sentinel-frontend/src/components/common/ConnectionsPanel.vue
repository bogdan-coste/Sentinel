<template>
  <div class="w-full">
    <button
        @click="openModal"
        class="w-full flex items-center justify-between p-4 rounded-[30px] bg-black/40 hover:bg-black/60 border border-white/3 transition-all group active:scale-[0.98]"
    >
      <div class="flex items-center gap-4">
        <div class="flex items-center -space-x-3">
          <img v-for="friend in friends.slice(0, 3)" :key="friend.publicId" :src="friend.image" class="w-10 h-10 rounded-full border-[3px] border-[#09152a] object-cover bg-[#0A192F]" alt=""/>
          <div v-if="friends.length === 0" class="w-10 h-10 rounded-full border-[3px] border-[#09152a] bg-blue-600/20 flex items-center justify-center text-blue-400">
            <svg class="w-5 h-5" fill="currentColor" viewBox="0 0 20 20"><path d="M10 9a3 3 0 100-6 3 3 0 000 6zm-7 9a7 7 0 1114 0H3z"></path></svg>
          </div>
        </div>
        <div class="flex flex-col items-start gap-0.5">
          <span class="text-sm font-bold text-white group-hover:text-blue-400 transition-colors tracking-tight">Connections</span>
          <span class="text-[10px] text-white/30 uppercase tracking-[0.15em] font-bold">{{ totalCount }} total</span>
        </div>
      </div>
      <div class="w-9 h-9 rounded-full bg-white/3 flex items-center justify-center group-hover:bg-blue-600 transition-all duration-300">
        <svg class="w-5 h-5 text-white/30 group-hover:text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="3" d="M9 5l7 7-7 7"></path></svg>
      </div>
    </button>

    <Teleport to="body">
      <div v-if="isOpen" class="fixed inset-0 z-[100] flex items-center justify-center p-4 sm:p-6 animate-fade-in">

        <div class="absolute inset-0 bg-[#0A192F]/80 backdrop-blur-md cursor-pointer" @click="closeModal"></div>

        <div class="relative w-full max-w-md bg-gradient-to-b from-[#0A192F] to-[#051021] border border-white/10 rounded-[32px] shadow-[0_0_40px_rgba(0,0,0,0.5)] flex flex-col max-h-[85vh] overflow-hidden z-[105]">

          <div v-if="activeMenuId" @click="activeMenuId = null" class="absolute inset-0 z-[50]"></div>

          <div class="flex items-center justify-between p-6 border-b border-white/5 shrink-0 bg-white/[0.02]">
            <div>
              <h3 class="text-[18px] font-black text-white tracking-tight">Connections</h3>
              <p class="text-[11px] font-bold text-blue-400 uppercase tracking-widest mt-1">{{ totalCount }} friends</p>
            </div>
            <button @click="closeModal" class="p-2.5 text-white/40 hover:text-white bg-white/5 hover:bg-white/10 rounded-full transition-all active:scale-95 border border-transparent hover:border-white/10 relative z-[60]">
              <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path></svg>
            </button>
          </div>

          <div class="p-4 border-b border-white/5 shrink-0 bg-black/20">
            <div class="relative group">
              <svg class="w-4 h-4 absolute left-4 top-1/2 -translate-y-1/2 text-white/40 group-focus-within:text-blue-400 transition-colors" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"></path></svg>
              <input
                  v-model="searchQuery"
                  type="text"
                  placeholder="Search friends..."
                  class="w-full bg-white/[0.03] hover:bg-white/[0.06] focus:bg-white/[0.08] border border-white/5 rounded-2xl py-3.5 pl-11 pr-4 text-[13px] text-white placeholder-white/40 focus:outline-none focus:border-white/20 transition-all relative z-[60]"
              />
            </div>
          </div>

          <div class="flex-1 overflow-y-auto custom-scrollbar p-3 relative">
            <ul v-if="filteredFriends.length > 0" class="flex flex-col gap-2 pb-32">

              <li v-for="friend in filteredFriends" :key="friend.publicId" class="relative" :class="activeMenuId === friend.publicId ? 'z-[60]' : 'z-10'">

                <div class="w-full flex items-center justify-between p-2.5 rounded-[20px] hover:bg-white/[0.04] border border-transparent hover:border-white/[0.05] transition-all group">

                  <div @click="goToProfile(friend.publicId)" class="flex items-center gap-4 flex-1 cursor-pointer overflow-hidden px-1">
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

                  <button @click.stop="toggleMenu(friend.publicId)" :class="activeMenuId === friend.publicId ? 'bg-white/10 text-white' : 'text-white/40 hover:text-white hover:bg-white/10'" class="p-2.5 rounded-full transition-colors shrink-0 relative">
                    <svg class="w-5 h-5" fill="currentColor" viewBox="0 0 24 24"><path d="M12 8c1.1 0 2-.9 2-2s-.9-2-2-2-2 .9-2 2 .9 2 2 2zm0 2c-1.1 0-2 .9-2 2s.9 2 2 2 2-.9 2-2-.9-2-2-2zm0 6c-1.1 0-2 .9-2 2s.9 2 2 2 2-.9 2-2-.9-2-2-2z"></path></svg>
                  </button>

                  <div v-if="activeMenuId === friend.publicId" class="absolute right-12 top-10 w-48 bg-[#0A192F]/95 backdrop-blur-2xl border border-white/10 rounded-[16px] shadow-2xl py-1.5 animate-fade-in flex flex-col">
                    <button @click.stop="goToProfile(friend.publicId)" class="w-full flex items-center gap-3 px-4 py-3 text-[12px] font-bold text-white/80 hover:text-white hover:bg-white/[0.06] transition-colors">
                      <svg class="w-4 h-4 text-white/50" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"></path></svg>
                      View Profile
                    </button>

                    <template v-if="isOwner">
                      <div class="w-full h-px bg-white/5 my-1"></div>
                      <button @click.stop="handleRemoveFriend(friend.publicId, friend.name)" class="w-full flex items-center gap-3 px-4 py-3 text-[12px] font-bold text-red-400 hover:text-red-300 hover:bg-red-500/10 transition-colors">
                        <svg class="w-4 h-4 text-red-400/70" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 7a4 4 0 11-8 0 4 4 0 018 0zM9 14a6 6 0 00-6 6v1h12v-1a6 6 0 00-6-6zM21 12h-6"></path></svg>
                        Remove Friend
                      </button>
                    </template>
                  </div>

                </div>
              </li>
            </ul>

            <div v-else class="flex flex-col items-center justify-center py-16 text-center relative z-10">
              <div class="w-16 h-16 rounded-3xl bg-white/5 border border-white/5 flex items-center justify-center mb-4 transform -rotate-6">
                <svg class="w-8 h-8 text-white/20" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197M13 7a4 4 0 11-8 0 4 4 0 018 0z"></path></svg>
              </div>
              <p class="text-white/60 text-[14px] font-bold">No friends found</p>
              <p class="text-white/30 text-[12px] mt-1">Try a different search term.</p>
            </div>
          </div>

        </div>
      </div>
    </Teleport>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue';
import { useRouter } from 'vue-router';
import api from '../../service/api.ts';

interface Friend {
  publicId: string;
  name: string;
  username: string;
  image?: string;
}

// Added the isOwner prop (defaults to false for safety)
const props = withDefaults(defineProps<{
  friends: Friend[];
  totalCount: number;
  isOwner?: boolean;
}>(), {
  isOwner: false
});

const emit = defineEmits(['refresh-friends']);

const router = useRouter();
const isOpen = ref(false);
const searchQuery = ref('');
const activeMenuId = ref<string | null>(null);

const openModal = () => {
  isOpen.value = true;
  document.body.style.overflow = 'hidden';
};

const closeModal = () => {
  isOpen.value = false;
  searchQuery.value = '';
  activeMenuId.value = null;
  document.body.style.overflow = '';
};

const toggleMenu = (publicId: string) => {
  activeMenuId.value = activeMenuId.value === publicId ? null : publicId;
};

const filteredFriends = computed(() => {
  if (!searchQuery.value) return props.friends;
  const lowerQuery = searchQuery.value.toLowerCase();
  return props.friends.filter(f =>
      (f.name || '').toLowerCase().includes(lowerQuery) ||
      (f.username || '').toLowerCase().includes(lowerQuery)
  );
});

const goToProfile = (publicId: string) => {
  closeModal();
  router.push(`/profile/${publicId}`);
};

const handleRemoveFriend = async (publicId: string, name: string) => {
  // Extra security check in case UI state fails
  if (!props.isOwner) return;

  activeMenuId.value = null;
  if (!confirm(`Are you sure you want to remove ${name} from your friends?`)) return;

  try {
    await api.delete('/friends/remove-friend', { data: { publicId } });
    emit('refresh-friends');
  } catch (err) {
    console.error('Failed to remove friend', err);
    alert('Failed to remove friend.');
  }
};
</script>

<style scoped>
.animate-fade-in {
  animation: fadeIn 0.15s cubic-bezier(0.16, 1, 0.3, 1);
}
@keyframes fadeIn {
  from { opacity: 0; transform: scale(0.96) translateY(5px); }
  to { opacity: 1; transform: scale(1) translateY(0); }
}

.custom-scrollbar::-webkit-scrollbar { width: 5px; }
.custom-scrollbar::-webkit-scrollbar-track { background: transparent; }
.custom-scrollbar::-webkit-scrollbar-thumb { background: rgba(255, 255, 255, 0.1); border-radius: 10px; }
.custom-scrollbar::-webkit-scrollbar-thumb:hover { background: rgba(255, 255, 255, 0.2); }
</style>