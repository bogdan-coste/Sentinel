<template>
  <div class="bg-gradient-to-b from-white/[0.04] to-transparent hover:from-white/[0.06] backdrop-blur-3xl border border-white/[0.05] rounded-[32px] p-4 shadow-[0_8px_32px_rgba(0,0,0,0.2)] flex flex-col gap-3 focus-within:from-white/[0.06] focus-within:border-white/[0.1] focus-within:shadow-[0_12px_40px_rgba(0,0,0,0.4)] transition-all duration-500">

    <div class="flex items-center gap-4 w-full">
      <div class="w-11 h-11 rounded-full bg-[#0A192F] border border-white/10 overflow-hidden shrink-0 cursor-pointer transition-transform hover:scale-105 active:scale-95 shadow-inner" @click="$emit('view-avatar')">
        <img v-if="userProfile?.profilePicUrl" :src="userProfile.profilePicUrl" class="w-full h-full object-cover" />
        <div v-else class="w-full h-full flex items-center justify-center bg-gradient-to-br from-white/5 to-transparent">
          <svg class="w-5 h-5 text-white/30" fill="currentColor" viewBox="0 0 24 24"><path d="M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm0 2c-2.67 0-8 1.34-8 4v2h16v-2c0-2.66-5.33-4-8-4z"/></svg>
        </div>
      </div>

      <input
          type="text"
          v-model="content"
          @keydown.enter="submit"
          placeholder="What's new?"
          class="flex-grow bg-transparent text-[15px] font-medium text-white placeholder-white/30 focus:outline-none px-1 tracking-wide"
      />

      <div class="flex items-center gap-2.5 shrink-0">

        <button
            @click="triggerFileInput"
            class="w-10 h-10 rounded-full flex items-center justify-center transition-all duration-300 border border-transparent active:scale-95"
            :class="previewUrl
              ? 'bg-blue-500/20 text-blue-400 border-blue-500/30 shadow-[0_0_15px_rgba(59,130,246,0.2)]'
              : 'bg-white/[0.03] text-white/50 hover:bg-white/[0.08] hover:text-white border-white/[0.02] hover:border-white/10'"
        >
          <svg class="w-[20px] h-[20px]" fill="none" stroke="currentColor" stroke-width="2.2" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" d="M15.172 7l-6.586 6.586a2 2 0 102.828 2.828l6.414-6.586a4 4 0 00-5.656-5.656l-6.415 6.585a6 6 0 108.486 8.486L20.5 13" />
          </svg>
        </button>

        <button
            @click="submit"
            :disabled="isSubmitting || (!content.trim() && !selectedFile)"
            class="w-10 h-10 rounded-full flex items-center justify-center transition-all duration-300 active:scale-95 relative overflow-hidden group disabled:opacity-40 disabled:active:scale-100"
            :class="(!content.trim() && !selectedFile)
              ? 'bg-white/5 text-white/30 cursor-not-allowed'
              : 'bg-gradient-to-tr from-blue-600 to-indigo-500 hover:from-blue-500 hover:to-indigo-400 text-white shadow-[0_0_20px_rgba(59,130,246,0.4)] cursor-pointer'"
        >
          <div v-if="content.trim() || selectedFile" class="absolute inset-0 bg-white/20 opacity-0 group-hover:opacity-100 transition-opacity"></div>

          <div v-if="isSubmitting" class="w-4 h-4 border-2 border-white/40 border-t-white rounded-full animate-spin relative z-10"></div>
          <svg v-else class="w-[18px] h-[18px] -mr-0.5 mt-0.5 relative z-10" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2.5" d="M12 19l9 2-9-18-9 18 9-2zm0 0v-8"></path></svg>
        </button>
      </div>
    </div>

    <div v-if="previewUrl" class="w-full pl-[60px] pr-2 pb-1 animate-fade-in">
      <div class="relative rounded-[24px] overflow-hidden border border-white/10 bg-[#030914] shadow-[0_10px_40px_rgba(0,0,0,0.5)] inline-block group/preview">

        <div class="absolute inset-0 bg-gradient-to-t from-black/20 to-transparent pointer-events-none"></div>

        <img :src="previewUrl" class="w-auto max-h-[300px] object-contain transition-transform duration-700 group-hover/preview:scale-[1.02]" alt="Upload preview" />

        <button
            @click="removeImage"
            class="absolute top-3 right-3 p-2 bg-black/50 backdrop-blur-xl rounded-full text-white/80 hover:text-white hover:bg-red-500 border border-white/10 hover:border-red-400 hover:shadow-[0_0_15px_rgba(239,68,68,0.5)] transition-all duration-300 active:scale-90"
        >
          <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2.5" d="M6 18L18 6M6 6l12 12"></path></svg>
        </button>
      </div>
    </div>

    <input type="file" ref="fileInput" class="hidden" accept="image/*" @change="handleFileSelected" />
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

const removeImage = () => {
  if (previewUrl.value) URL.revokeObjectURL(previewUrl.value);
  previewUrl.value = null;
  selectedFile.value = null;
  if (fileInput.value) fileInput.value.value = '';
};

const submit = async () => {
  if ((!content.value.trim() && !selectedFile.value) || isSubmitting.value) return;
  isSubmitting.value = true;

  const formData = new FormData();
  formData.append('content', content.value);
  if (selectedFile.value) formData.append('file', selectedFile.value);

  try {
    const response = await api.post('/users/me/create-post', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    });

    content.value = '';
    removeImage();

    emit('post-created', response.data);
  } catch (err) {
    console.error('Failed to create post', err);
  } finally {
    isSubmitting.value = false;
  }
};
</script>

<style scoped>
.animate-fade-in {
  animation: fadeIn 0.3s cubic-bezier(0.16, 1, 0.3, 1);
}
@keyframes fadeIn {
  from { opacity: 0; transform: translateY(-8px) scale(0.98); }
  to { opacity: 1; transform: translateY(0) scale(1); }
}
</style>