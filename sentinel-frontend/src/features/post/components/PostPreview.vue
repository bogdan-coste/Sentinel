<template>
  <div class="fixed inset-0 z-[9999] flex items-center justify-center p-4 lg:p-10 animate-fade-in">
    <div class="absolute inset-0 bg-[#051021]/90 backdrop-blur-xl cursor-pointer" @click="$emit('close')"></div>

    <div class="bg-[#0A192F] border border-white/10 rounded-4xl overflow-hidden max-w-4xl w-full shadow-2xl flex flex-col relative z-10" @click.stop>

      <button @click="$emit('close')" class="absolute top-4 right-4 z-[70] p-2 bg-black/40 hover:bg-black/60 rounded-full text-white/70 hover:text-white backdrop-blur-md transition-all border border-white/10">
        <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path></svg>
      </button>

      <div class="w-full bg-[#051021] flex items-center justify-center min-h-[300px] max-h-[70vh] overflow-hidden relative">
        <div v-if="isLoadingData" class="absolute inset-0 flex items-center justify-center bg-black/40 backdrop-blur-md z-50">
          <div class="animate-spin rounded-full h-10 w-10 border-t-2 border-b-2 border-blue-400"></div>
        </div>
        <img :src="previewImage.url" class="w-full h-full object-contain" alt="Preview" />
      </div>

      <div class="px-6 py-4 border-t border-white/5 flex items-center gap-6 bg-white/2">

        <button
            v-on="likeHandlers"
            @click="handleLikeClick"
            class="flex items-center gap-2 transition-all active:scale-95 group/btn"
            :class="localIsLiked ? 'text-red-500' : 'text-white/60 hover:text-white'"
        >
          <svg
              class="w-6 h-6 transition-transform group-hover/btn:scale-110"
              :fill="localIsLiked ? '#ef4444' : 'none'"
              :stroke="localIsLiked ? '#ef4444' : 'currentColor'"
              stroke-width="2"
              viewBox="0 0 24 24"
          >
            <path stroke-linecap="round" stroke-linejoin="round" d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z"></path>
          </svg>
          <span class="text-[14px] font-bold" :class="localIsLiked ? 'text-red-500' : ''">
            {{ localLikesCount }}
          </span>
        </button>

        <button
            @click="isCommentsOpen = true"
            class="flex items-center gap-2 transition-all active:scale-95 group/btn text-white/60 hover:text-white"
        >
          <svg class="w-6 h-6 transition-transform group-hover/btn:scale-110" fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" d="M8 12h.01M12 12h.01M16 12h.01M21 12c0 4.418-4.03 8-9 8a9.863 9.863 0 01-4.255-.949L3 20l1.395-3.72C3.512 15.042 3 13.574 3 12c0-4.418 4.03-8 9-8s9 3.582 9 8z"></path>
          </svg>
          <span class="text-[14px] font-bold">{{ previewImage.comments?.length || 0 }}</span>
        </button>

        <button
            v-if="previewImage.type === 'avatar' || previewImage.type === 'banner'"
            @click="triggerUpload"
            :disabled="isUploadingAvatar || isUploadingBanner"
            class="ml-auto px-6 py-2.5 rounded-full bg-blue-600/10 hover:bg-blue-600/20 text-[11px] font-black uppercase tracking-widest text-blue-400 border border-blue-500/20 transition-all active:scale-95 disabled:opacity-50 disabled:cursor-not-allowed"
        >
          <span v-if="isUploadingAvatar || isUploadingBanner" class="flex items-center gap-2">
            <div class="w-3 h-3 border-2 border-blue-400/30 border-t-blue-400 rounded-full animate-spin"></div>
            Uploading
          </span>
          <span v-else>Update {{ previewImage.type }}</span>
        </button>
      </div>
    </div>

    <LikeModal v-if="showLikesModal" :likers="likersList" :is-loading="isFetchingLikers" @close="showLikesModal = false" />
    <CommentsModal v-if="isCommentsOpen" :post="previewImage" :user-profile="userProfile" :is-loading="isFetchingComments" :is-submitting="isSubmittingComment" @submit-comment="submitComment" @close="isCommentsOpen = false" />

    <input type="file" ref="fileInput" class="hidden" accept="image/*" @change="handleFileSelect" />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch, toRef } from 'vue';
import LikeModal from "./LikeModal.vue";
import { CommentsModal } from "@features/comment";
import { useLikes } from "@features/post";
import { useComments } from "@features/comment";
import { useMediaUpload } from "@features/user";

const props = defineProps({
  previewImage: { type: Object, required: true },
  userProfile: { type: Object, default: null }
});

const emit = defineEmits(['close', 'refresh-userEntity']);

const previewRef = toRef(props, 'previewImage');

const {
  handleToggleLike, syncLikes, showLikesModal, likersList,
  isFetchingLikers, longPressHandlers: likeHandlers, isLongPress
} = useLikes(previewRef);

const {
  isCommentsOpen, isFetchingComments, isSubmittingComment,
  syncComments, submitComment
} = useComments(previewRef);

const { uploadAvatar, uploadBanner, isUploadingAvatar, isUploadingBanner } = useMediaUpload();

const localIsLiked = ref(props.previewImage.isLiked === true);
const localLikesCount = ref(props.previewImage.likes || 0);
const isLoadingData = ref(false);
const fileInput = ref<HTMLInputElement | null>(null);

const triggerUpload = () => fileInput.value?.click();

const handleFileSelect = async (event: Event) => {
  const target = event.target as HTMLInputElement;
  const file = target.files?.[0];
  if (!file) return;
  const res = props.previewImage.type === 'avatar' ? await uploadAvatar(file) : await uploadBanner(file);
  if (res.success) emit('refresh-userEntity');
  target.value = '';
};

const handleLikeClick = async (e: Event) => {
  if (isLongPress()) { e.preventDefault(); return; }

  localIsLiked.value = !localIsLiked.value;
  localLikesCount.value += localIsLiked.value ? 1 : -1;

  try {
    await handleToggleLike();
    localIsLiked.value = props.previewImage.isLiked;
    localLikesCount.value = props.previewImage.likes;
  } catch (err) {
    localIsLiked.value = !localIsLiked.value;
    localLikesCount.value += localIsLiked.value ? 1 : -1;
  }
};

const syncAllData = async () => {
  if (!props.previewImage?.id) return;
  isLoadingData.value = true;

  const myPublicId = props.userProfile?.publicId ? String(props.userProfile.publicId) : undefined;
  const myUsername = props.userProfile?.username ? String(props.userProfile.username) : undefined;

  await Promise.all([
    syncLikes(myPublicId, myUsername).then((isLiked) => {
      localIsLiked.value = isLiked;
      localLikesCount.value = props.previewImage.likes;
    }),
    syncComments()
  ]);

  isLoadingData.value = false;
};

onMounted(syncAllData);
watch(() => props.previewImage.id, syncAllData);
watch(() => props.userProfile, (val) => { if (val) syncAllData(); }, { deep: true });
</script>

<style scoped>
.animate-fade-in { animation: fadeIn 0.2s ease-out; }
@keyframes fadeIn {
  from { opacity: 0; transform: scale(0.98) translateY(10px); }
  to { opacity: 1; transform: scale(1) translateY(0); }
}
.custom-scrollbar::-webkit-scrollbar { width: 4px; }
.custom-scrollbar::-webkit-scrollbar-track { background: transparent; }
.custom-scrollbar::-webkit-scrollbar-thumb { background: rgba(255, 255, 255, 0.1); border-radius: 10px; }
</style>