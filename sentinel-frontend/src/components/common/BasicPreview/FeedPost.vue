<template>
  <div class="bg-white/[0.02] hover:bg-white/[0.03] transition-colors duration-500 backdrop-blur-2xl border border-white/[0.04] rounded-[32px] p-5 sm:p-6 shadow-2xl flex flex-col gap-4 shrink-0 relative group text-white">

    <div class="flex items-center justify-between">
      <div class="flex items-center gap-3.5">
        <div class="w-11 h-11 rounded-full bg-[#0A192F] border border-white/10 overflow-hidden shrink-0 cursor-pointer transition-transform hover:scale-105 active:scale-95" @click="$emit('view-image', 'avatar')">
          <img v-if="userProfile?.profilePicUrl" :src="userProfile.profilePicUrl" class="w-full h-full object-cover" alt="Avatar"/>
          <div v-else class="w-full h-full bg-white/5 flex items-center justify-center">
            <svg class="w-5 h-5 text-white/30" fill="currentColor" viewBox="0 0 24 24"><path d="M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm0 2c-2.67 0-8 1.34-8 4v2h16v-2c0-2.66-5.33-4-8-4z"/></svg>
          </div>
        </div>
        <div class="flex flex-col justify-center">
          <div class="flex items-center gap-1.5">
            <h4 class="text-[15px] font-bold text-white/95 cursor-pointer hover:text-blue-400 transition-colors tracking-tight">{{ userProfile?.name || 'Unknown User' }}</h4>
            <span v-if="post.isTemporary" class="ml-1 text-[10px] font-bold text-blue-400 animate-pulse bg-blue-500/10 px-2 py-0.5 rounded-full uppercase tracking-wider">Posting</span>
          </div>
          <span class="text-[12px] font-medium text-white/40 mt-0.5">{{ post.time }}</span>
        </div>
      </div>

      <div class="flex items-center gap-2">
        <div v-if="isRefreshing" class="w-4 h-4 border-2 border-white/30 border-t-white rounded-full animate-spin"></div>
        <button @click="$emit('toggle-menu', post.id)" class="text-white/30 hover:text-white transition-colors p-2 rounded-full hover:bg-white/10 active:scale-95 focus:outline-none">
          <svg class="w-5 h-5" fill="currentColor" viewBox="0 0 20 20"><path d="M6 10a2 2 0 11-4 0 2 2 0 014 0zM12 10a2 2 0 11-4 0 2 2 0 014 0zM16 12a2 2 0 100-4 2 2 0 000 4z"></path></svg>
        </button>
      </div>
    </div>

    <div
        v-if="post.image"
        @dblclick="$emit('toggle-like', post)"
        @click="handleImageClick"
        class="w-full rounded-[24px] overflow-hidden bg-[#051021] mt-1 cursor-pointer transition-all duration-300 hover:brightness-110 shadow-lg border border-white/[0.03]"
        :class="{ 'opacity-60': post.isTemporary }"
    >
      <img :src="post.image" loading="lazy" class="w-full h-auto object-cover max-h-[500px]" alt="Post attachment"/>
    </div>

    <InteractionPanel
        :post="post"
        :user-profile="userProfile"
        @toggle-like="$emit('toggle-like', post)"
    >
      <template #right-action>
        <button :disabled="post.isTemporary" class="flex items-center gap-2 text-white/40 hover:text-white transition-colors active:scale-95 ml-auto" :class="{ 'opacity-50 cursor-not-allowed': post.isTemporary }">
          <svg class="w-[22px] h-[22px]" fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" d="M8.684 13.342C8.886 12.938 9 12.482 9 12c0-.482-.114-.938-.316-1.342m0 2.684a3 3 0 110-2.684m0 2.684l6.632 3.316m-6.632-6l6.632-3.316m0 0a3 3 0 105.367-2.684 3 3 0 00-5.367 2.684zm0 9.316a3 3 0 105.368 2.684 3 3 0 00-5.368-2.684z"></path>
          </svg>
        </button>
      </template>

      <template #description>
        <p v-if="post.content" class="text-[14px] sm:text-[15px] text-white/85 leading-relaxed font-normal px-1 mt-3 mb-1" :class="{ 'opacity-60': post.isTemporary }">
          <span class="font-bold mr-1.5 cursor-pointer hover:text-blue-400 transition-colors text-white">{{ userProfile?.name || 'User' }}</span>
          {{ post.content }}
        </p>
      </template>
    </InteractionPanel>

  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from "vue";
import api from "../../../service/api.ts";
import InteractionPanel from "./InteractionPanel.vue";

const props = defineProps({
  post: {
    type: Object,
    required: true
  },
  userProfile: {
    type: Object,
    default: null
  }
});

const emit = defineEmits(['toggle-like', 'toggle-menu', 'view-image', 'open-preview']);

const isRefreshing = ref(false);

const handleImageClick = () => {
  if (!props.post.image || props.post.isTemporary) return;

  const previewObject = {
    id: props.post.id,
    url: props.post.image,
    type: 'post',
    likes: props.post.likes || 0,
    comments: props.post.comments || [],
    isLiked: props.post.isLiked || false,
    showComments: false,
    content: props.post.content,
    authorName: props.userProfile?.name
  };

  emit('open-preview', previewObject);
};

const postEndpoints = {
  details: () => `/media/${props.post.id}`,
  likes: () => `/media/${props.post.id}/likes`,
  comments: () => `/comments/media/${props.post.id}`
};

const fetchPostData = async (includeNestedData = false) => {
  if (props.post.isTemporary) return;

  isRefreshing.value = true;
  try {
    const postRes = await api.get(postEndpoints.details());

    if (postRes.data) {
      props.post.content = postRes.data.content || props.post.content;
      props.post.likes = postRes.data.likesCount ?? props.post.likes;

      if (postRes.data.path && props.userProfile?.publicId) {
        props.post.image = `/uploads/public/${props.userProfile.publicId}/media/${postRes.data.path}`;
      }
    }

    if (includeNestedData) {
      const [commentsRes, likesRes] = await Promise.all([
        api.get(postEndpoints.comments()),
        api.get(postEndpoints.likes())
      ]);

      props.post.comments = commentsRes.data || [];
      props.post.likers = likesRes.data || [];
    }
  } catch (err) {
    console.error(`[FeedPost] Failed to retrieve data for post ${props.post.id}:`, err);
  } finally {
    isRefreshing.value = false;
  }
};

onMounted(() => {
  fetchPostData();
});
</script>