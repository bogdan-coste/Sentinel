<template>
  <div class="fixed inset-0 z-9999 flex items-center justify-center p-4 lg:p-10 animate-fade-in">

    <div class="absolute inset-0 bg-[#051021]/90 backdrop-blur-xl cursor-pointer" @click="$emit('close')"></div>

    <div class="bg-[#0A192F] border border-white/10 rounded-4xl overflow-hidden max-w-4xl w-full shadow-2xl flex flex-col relative z-10" @click.stop>

      <button @click="$emit('close')" class="absolute top-4 right-4 z-10 p-2 bg-black/40 hover:bg-black/60 rounded-full text-white/70 hover:text-white backdrop-blur-md transition-all border border-white/10">
        <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path></svg>
      </button>

      <div class="w-full bg-[#051021] flex items-center justify-center min-h-75 max-h-[70vh] overflow-hidden">
        <img :src="previewImage.url" class="w-full h-full object-contain" alt="" />
      </div>

      <div class="px-6 py-4 border-t border-white/5 flex items-center gap-6 bg-white/2">

        <button
            @mousedown="handleMouseDown"
            @mouseup="handleMouseUp"
            @mouseleave="handleMouseUp"
            @touchstart="handleMouseDown"
            @touchend="handleMouseUp"
            @click="handleLikeClick"
            class="flex items-center gap-2 transition-colors active:scale-95 group/btn"
            :class="previewImage.isLiked ? 'text-red-500' : 'text-white/60 hover:text-white'"
        >
          <svg class="w-6 h-6 transition-transform group-hover/btn:scale-110" :fill="previewImage.isLiked ? 'currentColor' : 'none'" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z"></path></svg>
          <span class="text-[13px] font-semibold">{{ previewImage.likes }}</span>
        </button>

        <button
            @mousedown="handleCommentMouseDown"
            @mouseup="handleCommentMouseUp"
            @mouseleave="handleCommentMouseUp"
            @touchstart="handleCommentMouseDown"
            @touchend="handleCommentMouseUp"
            @click="handleCommentClick"
            class="flex items-center gap-2 transition-colors active:scale-95 group/btn"
            :class="previewImage.showComments ? 'text-blue-400' : 'text-white/60 hover:text-white'"
        >
          <svg class="w-6 h-6 transition-transform group-hover/btn:scale-110" fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" d="M8 12h.01M12 12h.01M16 12h.01M21 12c0 4.418-4.03 8-9 8a9.863 9.863 0 01-4.255-.949L3 20l1.395-3.72C3.512 15.042 3 13.574 3 12c0-4.418 4.03-8 9-8s9 3.582 9 8z"></path></svg>
          <span class="text-[13px] font-semibold">{{ previewImage.comments?.length || 0 }}</span>
        </button>

        <button
            @click="triggerUpload"
            :disabled="isUploadingAvatar || isUploadingBanner"
            class="ml-auto px-5 py-2.5 rounded-full bg-white/10 hover:bg-white/20 text-[11px] font-bold uppercase tracking-widest text-white transition-all shadow-sm active:scale-95 disabled:opacity-50"
        >
          <span v-if="isUploadingAvatar || isUploadingBanner">Uploading...</span>
          <span v-else>Update {{ previewImage.type }}</span>
        </button>

      </div>

    </div>

    <input type="file" ref="fileInput" class="hidden" accept="image/*" @change="handleFileSelect" />

    <LikeModal
        v-if="showLikesModal"
        :likers="likesList"
        :is-loading="isFetchingLikes"
        :error-msg="likesErrorMsg"
        @close="showLikesModal = false"
    />

    <CommentsModal
        v-if="showCommentsModal"
        :post="previewImage"
        :user-profile="userProfile"
        :is-loading="isFetchingComments"
        :is-submitting="isSubmittingComment"
        :error-msg="commentErrorMsg"
        @submit-comment="submitComment"
        @close="showCommentsModal = false"
    />

  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue';
import LikeModal from "./LikeModal.vue";
import CommentsModal from "./CommentsModal.vue";
import { useMediaUpload } from '../../../composables/userMediaUpload.ts';
import api from "../../../service/api.ts";

const props = defineProps({
  previewImage: {
    type: Object,
    required: true
  },
  userProfile: {
    type: Object,
    default: null
  }
});

const emit = defineEmits(['close', 'refresh-user']);

const { uploadAvatar, uploadBanner, isUploadingAvatar, isUploadingBanner } = useMediaUpload();
const fileInput = ref<HTMLInputElement | null>(null);

const triggerUpload = () => fileInput.value?.click();

const handleFileSelect = async (event: Event) => {
  const target = event.target as HTMLInputElement;
  const file = target.files?.[0];
  if (!file) return;

  const isAvatar = props.previewImage.type === 'avatar';
  const result = isAvatar ? await uploadAvatar(file) : await uploadBanner(file);

  if (result.success) emit('refresh-user');
  target.value = '';
};

// --- Likes Logic ---
const showLikesModal = ref(false);
const likesList = ref<any[]>([]);
const isFetchingLikes = ref(false);
const likesErrorMsg = ref<string | undefined>(undefined);

let holdTimer: ReturnType<typeof setTimeout> | null = null;
let isLongPress = false;

const fetchLikes = async () => {
  if (!props.previewImage?.id) return;

  isFetchingLikes.value = true;
  likesErrorMsg.value = undefined;

  try {
    const response = await api.get(`/likes/media/${props.previewImage.id}/likes`);

    likesList.value = (response.data || []).map((likeDto: any) => likeDto.user);

  } catch (err) {
    console.error('Failed to load likers', err);
    likesErrorMsg.value = "Failed to load likes. Please try again.";

    likesList.value = [];
  } finally {
    isFetchingLikes.value = false;
  }
};

const handleMouseDown = () => {
  isLongPress = false;
  holdTimer = setTimeout(async () => {
    isLongPress = true;
    showLikesModal.value = true;
    await fetchLikes(); // Trigger fetch on long press
  }, 500);
};

const handleMouseUp = () => {
  if (holdTimer) {
    clearTimeout(holdTimer);
    holdTimer = null;
  }
};

const handleLikeClick = async (e: Event) => {
  if (isLongPress) {
    e.preventDefault();
    return;
  }

  const wasLiked = props.previewImage.isLiked;
  const oldLikes = props.previewImage.likes;
  props.previewImage.isLiked = !wasLiked;
  props.previewImage.likes += props.previewImage.isLiked ? 1 : -1;

  try {
    const response = await api.post('/likes', { mediaId: props.previewImage.id });
    props.previewImage.likes = response.data.likeCount;
    props.previewImage.isLiked = response.data.liked;
  } catch (err) {
    props.previewImage.isLiked = wasLiked;
    props.previewImage.likes = oldLikes;
    console.error('Failed to toggle like', err);
  }
};

// --- Comments Logic ---
const showCommentsModal = ref(false);
const isFetchingComments = ref(false);
const isSubmittingComment = ref(false);
const commentErrorMsg = ref<string | undefined>(undefined);
let commentHoldTimer: ReturnType<typeof setTimeout> | null = null;
let isCommentLongPress = false;

const fetchComments = async () => {
  if (!props.previewImage?.id) return;
  if (props.previewImage.comments?.length > 0) return;

  isFetchingComments.value = true;
  try {
    const response = await api.get(`/comments/media/${props.previewImage.id}/comments`);
    props.previewImage.comments = response.data || [];
  } catch (err) {
    console.error('Failed to load comments', err);
    props.previewImage.comments = [];
  } finally {
    isFetchingComments.value = false;
  }
};

const submitComment = async (content: string) => {
  if (!content) return;

  isSubmittingComment.value = true;
  commentErrorMsg.value = undefined;

  try {
    const response = await api.post('/comments', {
      mediaId: props.previewImage.id,
      content: content
    });

    if (!props.previewImage.comments) {
      props.previewImage.comments = [];
    }

    props.previewImage.comments.push(response.data);
  } catch (err) {
    console.error('Failed to add comment', err);
    commentErrorMsg.value = "Failed to post comment. Please try again.";
  } finally {
    isSubmittingComment.value = false;
  }
};

const handleCommentMouseDown = () => {
  isCommentLongPress = false;
  commentHoldTimer = setTimeout(async () => {
    isCommentLongPress = true;
    showCommentsModal.value = true;
    await fetchComments();
  }, 500);
};

const handleCommentMouseUp = () => {
  if (commentHoldTimer) {
    clearTimeout(commentHoldTimer);
    commentHoldTimer = null;
  }
};

const handleCommentClick = (e: Event) => {
  if (isCommentLongPress) {
    e.preventDefault();
    return;
  }

  props.previewImage.showComments = !props.previewImage.showComments;

  if (props.previewImage.showComments) {
    fetchComments();
  }
};

const fetchedLikesData = ref<any[]>([]);

const updateLikeStatus = () => {
  if (props.userProfile && props.userProfile.id) {
    const currentUserIdStr = String(props.userProfile.id);

    props.previewImage.isLiked = fetchedLikesData.value.some((like: any) => {
      return like.user && String(like.user.id) === currentUserIdStr;
    });
  }
};

watch(() => props.userProfile, () => {
  if (fetchedLikesData.value.length > 0) {
    updateLikeStatus();
  }
}, { deep: true, immediate: true });


onMounted(async () => {
  if (!props.previewImage?.id) return;

  try {
    const likesRes = await api.get(`/likes/media/${props.previewImage.id}/likes`);
    const likesData = likesRes.data || [];

    props.previewImage.likes = likesData.length;

    if (props.userProfile) {
      const myId = String(props.userProfile.id);
      const myPublicId = String(props.userProfile.publicId);

      props.previewImage.isLiked = likesData.some((likeItem: any) => {
        if (!likeItem.user) return false;

        const likedUserId = String(likeItem.user.id);
        const likedUserPublicId = String(likeItem.user.publicId);

        return likedUserId === myId || likedUserPublicId === myPublicId;
      });
    }

  } catch (err) {
    console.error('Failed to fetch initial likes', err);
  }

  try {
    const commentsRes = await api.get(`/comments/media/${props.previewImage.id}/comments`);
    props.previewImage.comments = commentsRes.data || [];
  } catch (err) {
    console.error('Failed to fetch initial comments', err);
  }
});
</script>

<style scoped>
.animate-fade-in { animation: fadeIn 0.15s ease-out; }
@keyframes fadeIn {
  from { opacity: 0; transform: translateY(4px); }
  to { opacity: 1; transform: translateY(0); }
}
</style>