<template>
  <div class="bg-white/2 hover:bg-white/3 transition-colors duration-500 backdrop-blur-2xl border border-white/4 rounded-4xl p-5 sm:p-6 shadow-2xl flex flex-col gap-4 shrink-0 relative group text-white">

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
        <div v-if="isFetchingComments" class="w-4 h-4 border-2 border-white/30 border-t-white rounded-full animate-spin"></div>
        <button @click="$emit('toggle-menu', post.id)" class="text-white/30 hover:text-white transition-colors p-2 rounded-full hover:bg-white/10 active:scale-95 focus:outline-none">
          <svg class="w-5 h-5" fill="currentColor" viewBox="0 0 20 20"><path d="M6 10a2 2 0 11-4 0 2 2 0 014 0zM12 10a2 2 0 11-4 0 2 2 0 014 0zM16 12a2 2 0 100-4 2 2 0 000 4z"></path></svg>
        </button>
      </div>
    </div>

    <div
        v-if="post.image"
        @dblclick="onLikeClick"
        @click="handleImageClick"
        class="w-full rounded-3xl overflow-hidden bg-[#051021] mt-1 cursor-pointer transition-all duration-300 hover:brightness-110 shadow-lg border border-white/3"
        :class="{ 'opacity-60': post.isTemporary }"
    >
      <img :src="post.image" loading="lazy" class="w-full h-auto object-cover max-h-125" alt="Post attachment"/>
    </div>

    <div class="flex items-center gap-6 mt-1 pt-2 border-t border-white/2 px-2">

      <div class="flex items-center gap-1.5">
        <button
            @click="onLikeClick"
            :disabled="post.isTemporary || isLiking"
            class="flex items-center gap-2 transition-colors active:scale-95 group/btn"
            :class="[post.isLiked ? 'text-red-500' : 'text-white/50 hover:text-white', post.isTemporary ? 'opacity-50 cursor-not-allowed' : '']"
        >
          <svg class="w-5 h-5 transition-transform group-hover/btn:scale-110" :fill="post.isLiked ? 'currentColor' : 'none'" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z"></path>
          </svg>
        </button>
        <button
            @click="post.showLikes = !post.showLikes; post.showComments = false"
            class="text-[13px] font-semibold text-white/50 hover:text-white transition-colors"
        >
          {{ post.likes }}
        </button>
      </div>

      <button
          @click="post.showComments = !post.showComments; post.showLikes = false"
          :disabled="post.isTemporary"
          class="flex items-center gap-2 transition-colors active:scale-95 group/btn"
          :class="[post.showComments ? 'text-blue-400' : 'text-white/50 hover:text-white', post.isTemporary ? 'opacity-50 cursor-not-allowed' : '']"
      >
        <svg class="w-5 h-5 transition-transform group-hover/btn:scale-110" fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" d="M8 12h.01M12 12h.01M16 12h.01M21 12c0 4.418-4.03 8-9 8a9.863 9.863 0 01-4.255-.949L3 20l1.395-3.72C3.512 15.042 3 13.574 3 12c0-4.418 4.03-8 9-8s9 3.582 9 8z"></path>
        </svg>
        <span class="text-[13px] font-semibold">{{ post.comments?.length || 0 }}</span>
      </button>

      <button :disabled="post.isTemporary" class="flex items-center gap-2 text-white/40 hover:text-white transition-colors active:scale-95 ml-auto" :class="{ 'opacity-50 cursor-not-allowed': post.isTemporary }">
        <svg class="w-5.5 h-5.5" fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" d="M8.684 13.342C8.886 12.938 9 12.482 9 12c0-.482-.114-.938-.316-1.342m0 2.684a3 3 0 110-2.684m0 2.684l6.632 3.316m-6.632-6l6.632-3.316m0 0a3 3 0 105.367-2.684 3 3 0 00-5.367 2.684zm0 9.316a3 3 0 105.368 2.684 3 3 0 00-5.368-2.684z"></path>
        </svg>
      </button>
    </div>

    <p v-if="post.content" class="text-[14px] sm:text-[15px] text-white/85 leading-relaxed font-normal px-1 mt-1 mb-1" :class="{ 'opacity-60': post.isTemporary }">
      <span class="font-bold mr-1.5 cursor-pointer hover:text-blue-400 transition-colors text-white">{{ userProfile?.name || 'User' }}</span>
      {{ post.content }}
    </p>

    <div class="w-full mt-1">
      <LikeBasic
          v-if="post.showLikes && !post.isTemporary"
          :post="post"
          :likers="likersList"
      />

      <CommentBasic
          v-if="post.showComments && !post.isTemporary"
          :comments="post.comments || []"
          :user-profile="userProfile"
          :is-loading="isFetchingComments"
          :is-submitting="isSubmittingComment"
          @submit-comment="submitComment"
      />
    </div>

  </div>
</template>

<script setup lang="ts">
import { onMounted, watch, toRef } from "vue";

import LikeBasic from "./LikeBasic.vue";
import { CommentBasic } from "@features/comment";
import { useLikes } from "@features/post";
import { useComments } from "@features/comment";

const props = defineProps({
  post: { type: Object, required: true },
  userProfile: { type: Object, default: null }
});

const emit = defineEmits(['toggle-menu', 'view-image', 'open-preview']);

const postRef = toRef(props, 'post');

const { handleToggleLike, likersList, fetchLikers, isLiking } = useLikes(postRef);
const { syncComments, submitComment, isFetchingComments, isSubmittingComment } = useComments(postRef);

const handleImageClick = () => {
  if (!props.post.image || props.post.isTemporary) return;

  emit('open-preview', {
    id: props.post.id,
    url: props.post.image,
    type: 'post',
    likes: props.post.likes || 0,
    comments: props.post.comments || [],
    isLiked: props.post.isLiked || false,
    showComments: false,
    content: props.post.content,
    authorName: props.userProfile?.name
  });
};

const onLikeClick = async () => {
  if (props.post.isTemporary || isLiking.value) return;

  await handleToggleLike();

  if (props.post.showLikes) fetchLikers();
};

watch(() => props.post.showComments, (isShowing) => {
  if (isShowing && (!props.post.comments || props.post.comments.length === 0)) syncComments();
});

watch(() => props.post.showLikes, (isShowing) => {
  if (isShowing && (!likersList.value || likersList.value.length === 0)) fetchLikers();
});

onMounted(() => {
  if (!props.post.isTemporary) syncComments();
});
</script>