<template>
  <Teleport to="body">
    <div v-if="show" class="fixed inset-0 z-100 flex items-center justify-center p-4 sm:p-6 animate-fade-in">
      <div class="absolute inset-0 bg-[#0A192F]/80 backdrop-blur-md cursor-pointer" @click="$emit('close')"></div>
      <div class="relative w-full max-w-md bg-linear-to-b from-[#0A192F] to-[#051021] border border-white/10 rounded-4xl shadow-2xl flex flex-col max-h-[85vh] overflow-hidden z-105">

        <div class="flex items-center justify-between p-6 border-b border-white/5 shrink-0 bg-white/2">
          <div>
            <h3 class="text-[18px] font-black text-white tracking-tight">New Message</h3>
            <p class="text-[11px] font-bold text-blue-400 uppercase tracking-widest mt-1">Select a friend</p>
          </div>
          <button @click="$emit('close')" class="p-2.5 text-white/40 hover:text-white bg-white/5 hover:bg-white/10 rounded-full transition-all border border-transparent">
            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path></svg>
          </button>
        </div>

        <div class="p-4 border-b border-white/5 bg-black/20">
          <input v-model="searchQuery" type="text" placeholder="Search friends..." class="w-full bg-white/3 border border-white/5 rounded-2xl py-3.5 px-4 text-[13px] text-white focus:outline-none focus:border-white/20" />
        </div>

        <div class="flex-1 overflow-y-auto custom-scrollbar p-3">
          <div v-if="loading" class="flex justify-center py-10"><div class="animate-spin rounded-full h-6 w-6 border-t-2 border-blue-400"></div></div>
          <ul v-else-if="filteredFriends.length > 0" class="flex flex-col gap-2">
            <li v-for="friend in filteredFriends" :key="friend.publicId">
              <button @click="$emit('start-chat', friend)" class="w-full flex items-center gap-4 p-2.5 rounded-[20px] hover:bg-white/4 text-left">
                <div class="w-12 h-12 rounded-full bg-white/5 overflow-hidden">
                  <img v-if="friend.image" :src="friend.image" class="w-full h-full object-cover"  alt=""/>
                </div>
                <div>
                  <div class="text-[14px] font-bold text-white">{{ friend.name }}</div>
                  <div class="text-[11px] text-white/40">@{{ friend.username }}</div>
                </div>
              </button>
            </li>
          </ul>
        </div>
      </div>
    </div>
  </Teleport>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue';
import api from "../../../shared/api/api.ts";

const props = defineProps<{ show: boolean }>();
const emit = defineEmits(['close', 'start-chat']);

const searchQuery = ref('');
const friends = ref<any[]>([]);
const loading = ref(false);

const filteredFriends = computed(() => {
  const query = searchQuery.value.toLowerCase();
  return friends.value.filter(f => f.name.toLowerCase().includes(query) || f.username.toLowerCase().includes(query));
});

watch(() => props.show, async (isOpen) => {
  if (isOpen && friends.value.length === 0) {
    loading.value = true;
    try {
      const res = await api.get('/friends/my-friends');
      friends.value = res.data;
    } finally { loading.value = false; }
  }
});
</script>