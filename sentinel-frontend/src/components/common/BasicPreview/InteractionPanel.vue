<template>
  <div class="w-full flex flex-col">

    <div class="flex items-center gap-6 mt-2 pt-2 border-t border-white/[0.02] px-2">

      <div class="flex items-center gap-1.5">
        <button
            @click="emit('toggle-like')"
            :disabled="post.isTemporary"
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

      <div class="ml-auto">
        <slot name="right-action"></slot>
      </div>

    </div>

    <slot name="description"></slot>

    <div class="w-full mt-1">
      <LikeBasic
          v-if="post.showLikes && !post.isTemporary"
          :post="post"
      />

      <CommentBasic
          v-if="post.showComments && !post.isTemporary"
          :post="post"
          :user-profile="userProfile"
      />
    </div>

  </div>
</template>

<script setup lang="ts">
import LikeBasic from "./LikeBasic.vue";
import CommentBasic from "./CommentBasic.vue";

defineProps({
  post: {
    type: Object,
    required: true
  },
  userProfile: {
    type: Object,
    default: null
  }
});

const emit = defineEmits(['toggle-like']);
</script>