<template>
  <div class="fixed inset-0 z-9999 flex items-center justify-center p-4 sm:p-6 animate-fade-in">

    <div class="absolute inset-0 bg-[#051021]/80 backdrop-blur-sm cursor-pointer" @click="$emit('close')"></div>

    <div class="bg-[#0A192F] border border-white/10 rounded-4xl overflow-hidden max-w-sm w-full shadow-2xl flex flex-col relative z-10" @click.stop>

      <div class="px-6 py-4 border-b border-white/5 flex items-center justify-between bg-white/2">
        <h3 class="text-base font-bold text-white tracking-wide">Liked by</h3>
        <button @click="$emit('close')" class="p-2 bg-white/5 hover:bg-white/10 rounded-full text-white/70 hover:text-white transition-all active:scale-95">
          <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path></svg>
        </button>
      </div>

      <div class="p-2 max-h-[60vh] min-h-50 overflow-y-auto custom-scrollbar flex flex-col">

        <div v-if="isLoading" class="flex justify-center items-center h-40">
          <div class="animate-spin rounded-full h-8 w-8 border-t-2 border-b-2 border-blue-400"></div>
        </div>

        <div v-else-if="errorMsg" class="flex justify-center items-center h-40 text-[13px] text-red-400 text-center px-4">
          {{ errorMsg }}
        </div>

        <template v-else>
          <div v-if="likers.length === 0" class="flex justify-center items-center h-40 text-[13px] text-white/40">
            No likes yet.
          </div>

          <div v-for="user in likers" :key="user.id" class="flex items-center gap-3 p-3 rounded-2xl hover:bg-white/5 transition-colors cursor-pointer group">

            <img v-if="user.profilePicUrl" :src="user.profilePicUrl" class="w-10 h-10 rounded-full object-cover bg-[#0A192F] border border-white/10 shrink-0"  alt=""/>
            <div v-else class="w-10 h-10 rounded-full bg-white/5 flex items-center justify-center border border-white/10 shrink-0">
              <svg class="w-5 h-5 text-white/30" fill="currentColor" viewBox="0 0 24 24"><path d="M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm0 2c-2.67 0-8 1.34-8 4v2h16v-2c0-2.66-5.33-4-8-4z"/></svg>
            </div>

            <div class="flex flex-col flex-1 min-w-0">
              <span class="text-[14px] font-bold text-white/90 group-hover:text-blue-400 transition-colors truncate">
                {{ user.name }}
              </span>
              <span v-if="user.username" class="text-[12px] text-white/40 truncate">
                @{{ user.username }}
              </span>
            </div>

          </div>
        </template>

      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import type {PropType} from 'vue';

interface Liker {
  id: number | string;
  name: string;
  username?: string;
  profilePicUrl?: string | null;
}

defineProps({
  likers: {
    type: Array as PropType<Liker[]>,
    default: () => []
  },
  isLoading: {
    type: Boolean,
    default: false
  },
  errorMsg: {
    type: String,
    default: null
  }
});

defineEmits(['close']);
</script>

<style scoped>
.custom-scrollbar::-webkit-scrollbar { width: 5px; }
.custom-scrollbar::-webkit-scrollbar-track { background: transparent; }
.custom-scrollbar::-webkit-scrollbar-thumb {
  background: rgba(255, 255, 255, 0.1);
  border-radius: 10px;
}
.custom-scrollbar::-webkit-scrollbar-thumb:hover { background: rgba(255, 255, 255, 0.2); }

.animate-fade-in { animation: fadeIn 0.15s ease-out; }
@keyframes fadeIn {
  from { opacity: 0; transform: translateY(4px); }
  to { opacity: 1; transform: translateY(0); }
}
</style>