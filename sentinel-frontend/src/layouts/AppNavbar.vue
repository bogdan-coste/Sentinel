<template>
  <header class="h-20 shrink-0 w-full px-6 md:px-10 flex items-center justify-between border-b border-white/5 bg-[#0A192F]/40 backdrop-blur-md z-40">

    <div class="flex items-center gap-6 w-full max-w-md">
      <router-link to="/home" class="w-11 h-11 rounded-full bg-white flex items-center justify-center shrink-0 shadow-[0_0_15px_rgba(255,255,255,0.2)] hover:scale-105 transition-transform">
        <svg class="w-5 h-5 text-[#0A192F]" viewBox="0 0 24 24" fill="currentColor">
          <path d="M21 3v12a6 6 0 01-6 6H3V9a6 6 0 016-6h12z"/>
        </svg>
      </router-link>

      <div class="relative w-full group">
        <svg class="w-4 h-4 absolute left-4 top-1/2 -translate-y-1/2 text-white/50 group-focus-within:text-blue-400 transition-colors z-10" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"></path></svg>
        <input
            v-model="textInput"
            type="text"
            placeholder="Search userEntities..."
            class="w-full bg-white/4 hover:bg-white/6 focus:bg-white/8 border border-white/5 rounded-full py-2.5 pl-10 pr-4 text-sm text-white placeholder-white/50 focus:outline-none focus:border-white/20 transition-all relative z-10"
        />

        <div
            v-if="textInput.length > 0"
            class="absolute top-[120%] left-0 w-full bg-[#0A192F]/95 backdrop-blur-xl border border-white/10 rounded-2xl shadow-2xl overflow-hidden z-50 flex flex-col"
        >
          <div v-if="isSearching" class="p-5 flex justify-center items-center gap-3 text-white/50 text-sm font-medium">
            <div class="w-4 h-4 border-2 border-white/20 border-t-blue-400 rounded-full animate-spin"></div>
            Searching...
          </div>

          <ul v-else-if="searchResults.length > 0" class="max-h-80 overflow-y-auto custom-scrollbar py-2">
            <li v-for="userEntity in searchResults" :key="userEntity.publicId">
              <router-link
                  :to="`/profile/${userEntity.publicId}`"
                  @click="clearSearch"
                  class="flex items-center gap-3 px-4 py-2.5 hover:bg-white/5..."
              >
                <div class="relative w-10 h-10 rounded-full overflow-hidden shrink-0 border border-white/10 bg-black/20">
                  <img
                      v-if="userEntity.profilePicUrl"
                      :src="userEntity.profilePicUrl"
                      alt="avatar"
                      class="w-full h-full object-cover"
                  />
                  <div v-else class="w-full h-full flex items-center justify-center text-blue-400 bg-blue-500/10">
                    <svg class="w-5 h-5" fill="currentColor" viewBox="0 0 20 20"><path d="M10 9a3 3 0 100-6 3 3 0 000 6zm-7 9a7 7 0 1114 0H3z"></path></svg>
                  </div>
                </div>

                <div class="flex flex-col truncate">
                  <span class="text-white text-[13px] font-semibold truncate">{{ userEntity.name }}</span>
                  <span class="text-white/40 text-[11px] truncate">@{{ userEntity.username }}</span>
                </div>
              </router-link>
            </li>
          </ul>

          <div v-else class="p-5 text-center text-white/50 text-sm">
            No userEntities found for "{{ textInput }}"
          </div>
        </div>
      </div>
    </div>

    <div class="flex items-center justify-end gap-5">
      <router-link to="/home" class="relative flex flex-col items-center group">
        <button class="text-white group-hover:scale-110 transition-all p-2 bg-white/10 rounded-full shadow-lg border border-white/5 flex items-center justify-center">
          <svg class="w-5 h-5" fill="currentColor" viewBox="0 0 20 20"><path d="M10.707 2.293a1 1 0 00-1.414 0l-7 7a1 1 0 001.414 1.414L4 10.414V17a1 1 0 001 1h2a1 1 0 001-1v-2a1 1 0 011-1h2a1 1 0 011 1v2a1 1 0 001 1h2a1 1 0 001-1v-6.586l.293.293a1 1 0 001.414-1.414l-7-7z"></path></svg>
        </button>
        <span class="absolute -bottom-2 w-1 h-1 bg-white rounded-full shadow-[0_0_8px_rgba(255,255,255,0.8)]"></span>
      </router-link>

      <router-link to="/messages" class="text-white/60 hover:text-white hover:scale-110 transition-all p-2 bg-white/5 rounded-full hover:bg-white/10 border border-transparent flex items-center justify-center">
        <svg class="w-5 h-5" fill="currentColor" viewBox="0 0 20 20"><path fill-rule="evenodd" d="M18 10c0 3.866-3.582 7-8 7a8.841 8.841 0 01-4.083-.98L2 17l1.338-3.123C2.493 12.767 2 11.434 2 10c0-3.866 3.582-7 8-7s8 3.134 8 7zM7 9H5v2h2V9zm8 0h-2v2h2V9zM9 9h2v2H9V9z" clip-rule="evenodd"></path></svg>
      </router-link>

      <router-link to="/settings" class="text-white/60 hover:text-white hover:scale-110 transition-all p-2 bg-white/5 rounded-full hover:bg-white/10 border border-transparent flex items-center justify-center">
        <svg class="w-5 h-5" fill="currentColor" viewBox="0 0 20 20"><path fill-rule="evenodd" d="M11.49 3.17c-.38-1.56-2.6-1.56-2.98 0a1.532 1.532 0 01-2.286.948c-1.372-.836-2.942.734-2.106 2.106.54.886.061 2.042-.947 2.287-1.561.379-1.561 2.6 0 2.978a1.532 1.532 0 01.947 2.287c-.836 1.372.734 2.942 2.106 2.106a1.532 1.532 0 012.287.947c.379 1.561 2.6 1.561 2.978 0a1.533 1.533 0 012.287-.947c1.372.836 2.942-.734 2.106-2.106a1.533 1.533 0 01.947-2.287c1.561-.379 1.561-2.6 0-2.978a1.532 1.532 0 01-.947-2.287c.836-1.372-.734-2.942-2.106-2.106a1.532 1.532 0 01-2.287-.947zM10 13a3 3 0 100-6 3 3 0 000 6z" clip-rule="evenodd"></path></svg>
      </router-link>
    </div>
  </header>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue';
import api from '@shared/api/api';

const textInput = ref('');
const searchResults = ref<any[]>([]);
const isSearching = ref(false);
let debounceTimer: number | undefined;

const clearSearch = () => {
  textInput.value = '';
  searchResults.value = [];
};

watch(textInput, (query) => {
  if (!query || query.trim() === '') {
    if (debounceTimer) clearTimeout(debounceTimer);
    searchResults.value = [];
    isSearching.value = false;
    return;
  }

  isSearching.value = true;
  if (debounceTimer) {
    clearTimeout(debounceTimer);
  }

  debounceTimer = window.setTimeout(async () => {
    try {
      const response = await api.get(`/users/searchAccounts?query=${query}`)
      searchResults.value = response.data;
      console.log(searchResults)
    } catch (err) {
      console.error('Search failed', err);
      searchResults.value = [];
    } finally {
      isSearching.value = false;
    }
  }, 300);
});
</script>

<style scoped>
.custom-scrollbar::-webkit-scrollbar { width: 4px; }
.custom-scrollbar::-webkit-scrollbar-track { background: transparent; }
.custom-scrollbar::-webkit-scrollbar-thumb { background: rgba(255, 255, 255, 0.1); border-radius: 10px; }
.custom-scrollbar::-webkit-scrollbar-thumb:hover { background: rgba(255, 255, 255, 0.2); }
</style>