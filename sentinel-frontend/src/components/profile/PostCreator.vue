<template>
  <div class="bg-white/[0.02] backdrop-blur-xl border border-white/[0.05] rounded-[32px] p-3 shadow-lg flex items-center gap-3 focus-within:bg-white/[0.04] focus-within:border-white/10 transition-all">
    <div class="w-10 h-10 rounded-full bg-[#0A192F] border border-white/10 overflow-hidden shrink-0 ml-1 cursor-pointer" @click="$emit('view-avatar')">
      <img v-if="userProfile?.profilePicUrl" :src="userProfile.profilePicUrl" class="w-full h-full object-cover" />
      <div v-else class="w-full h-full flex items-center justify-center bg-white/5">
        <svg class="w-5 h-5 text-white/30" fill="currentColor" viewBox="0 0 24 24"><path d="M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm0 2c-2.67 0-8 1.34-8 4v2h16v-2c0-2.66-5.33-4-8-4z"/></svg>
      </div>
    </div>

    <input type="text" v-model="content" @keydown.enter="submit" placeholder="What's new?" class="flex-grow bg-transparent text-[14px] text-white placeholder-white/40 focus:outline-none px-2" />

    <div class="flex items-center gap-2 pr-1 shrink-0">
      <button @click="triggerFileInput" class="w-9 h-9 rounded-full hover:bg-white/10 flex items-center justify-center text-white/40 hover:text-blue-400 transition-colors">
        <svg class="w-5 h-5" fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" d="M15.172 7l-6.586 6.586a2 2 0 102.828 2.828l6.414-6.586a4 4 0 00-5.656-5.656l-6.415 6.585a6 6 0 108.486 8.486L20.5 13" />
        </svg>
      </button>
      <button @click="submit" :disabled="isSubmitting" class="w-9 h-9 rounded-full bg-blue-600 hover:bg-blue-500 flex items-center justify-center text-white shadow-lg active:scale-95 transition-all disabled:opacity-50">
        <div v-if="isSubmitting" class="w-4 h-4 border-2 border-white/30 border-t-white rounded-full animate-spin"></div>
        <svg v-else class="w-4 h-4 -mr-0.5 mt-0.5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2.5" d="M12 19l9 2-9-18-9 18 9-2zm0 0v-8"></path></svg>
      </button>
    </div>

    <input type="file" ref="fileInput" class="hidden" accept="image/*" @change="handleFileSelected" />
    <div v-if="previewUrl" class="absolute bottom-full left-0 mb-2 bg-black/80 rounded-lg p-2 text-xs text-white">Image selected</div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import api from '../../service/api';

const props = defineProps<{ userProfile: any }>();
const emit = defineEmits(['post-created', 'view-avatar']);

const content = ref('');
const fileInput = ref<HTMLInputElement | null>(null);
const selectedFile = ref<File | null>(null);
const previewUrl = ref<string | null>(null);
const isSubmitting = ref(false);

const triggerFileInput = () => fileInput.value?.click();

const handleFileSelected = (e: Event) => {
  const target = e.target as HTMLInputElement;
  const file = target.files?.[0];
  if (file) {
    selectedFile.value = file;
    previewUrl.value = URL.createObjectURL(file);
  }
  target.value = '';
};

const submit = async () => {
  if (!content.value.trim() && !selectedFile.value) return;
  isSubmitting.value = true;

  const formData = new FormData();
  formData.append('content', content.value);
  if (selectedFile.value) formData.append('file', selectedFile.value);

  try {
    const response = await api.post('/users/me/posts', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    });
    content.value = '';
    if (previewUrl.value) URL.revokeObjectURL(previewUrl.value);
    previewUrl.value = null;
    selectedFile.value = null;
    emit('post-created', response.data);
  } catch (err) {
    console.error('Failed to create post', err);
  } finally {
    isSubmitting.value = false;
  }
};
</script>