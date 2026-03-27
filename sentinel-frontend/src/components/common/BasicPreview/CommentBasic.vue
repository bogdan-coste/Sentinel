<template>
  <div class="flex flex-col gap-4 animate-fade-in border-t border-white/5 pt-4 px-2 w-full">

    <div v-if="isLoading" class="flex justify-center py-2">
      <div class="animate-spin rounded-full h-5 w-5 border-t-2 border-b-2 border-blue-400"></div>
    </div>

    <div v-else-if="errorMsg" class="text-center text-[12px] text-red-400 py-2">
      {{ errorMsg }}
    </div>

    <div v-else class="flex flex-col gap-4 max-h-75 overflow-y-auto custom-scrollbar pr-2">
      <div v-for="comment in post.comments" :key="comment.id" class="flex gap-3">
        <img v-if="comment.author?.profilePicUrl" :src="comment.author.profilePicUrl" class="w-8 h-8 rounded-full bg-[#0A192F] object-cover shrink-0 border border-white/10"  alt=""/>
        <div v-else class="w-8 h-8 rounded-full bg-white/5 flex items-center justify-center shrink-0 border border-white/10">
          <svg class="w-4 h-4 text-white/30" fill="currentColor" viewBox="0 0 24 24"><path d="M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm0 2c-2.67 0-8 1.34-8 4v2h16v-2c0-2.66-5.33-4-8-4z"/></svg>
        </div>

        <div class="flex flex-col bg-white/3 rounded-2xl rounded-tl-sm px-4 py-2.5 max-w-[85%] border border-white/5">
          <div class="flex items-center gap-2 mb-0.5">
            <span class="text-[12px] font-bold text-white/90">{{ comment.author?.name || 'Unknown User' }}</span>
            <span class="text-[10px] text-white/40">{{ new Date(comment.createdAt).toLocaleDateString() }}</span>
          </div>
          <p class="text-[13px] text-white/80 leading-relaxed font-light">{{ comment.content }}</p>
        </div>
      </div>

      <div v-if="!post.comments?.length" class="text-center text-[12px] text-white/40 py-2">
        No comments yet. Be the first to reply!
      </div>
    </div>

    <div class="flex items-center gap-3 mt-2 pb-2">
      <img v-if="userProfile?.profilePicUrl" :src="userProfile.profilePicUrl" class="w-8 h-8 rounded-full object-cover shrink-0 border border-white/10"  alt=""/>
      <div v-else class="w-8 h-8 rounded-full bg-white/5 flex items-center justify-center shrink-0 border border-white/10">
        <svg class="w-4 h-4 text-white/30" fill="currentColor" viewBox="0 0 24 24"><path d="M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm0 2c-2.67 0-8 1.34-8 4v2h16v-2c0-2.66-5.33-4-8-4z"/></svg>
      </div>

      <div class="flex-1 bg-black/20 focus-within:bg-white/3 border border-white/5 focus-within:border-white/10 rounded-full flex items-center pr-1 transition-all shadow-inner">
        <input
            v-model="commentInput"
            @keydown.enter="submit"
            type="text"
            placeholder="Write a comment..."
            :disabled="isSubmitting"
            class="flex-1 bg-transparent text-[13px] text-white placeholder-white/40 focus:outline-none py-2 px-4 disabled:opacity-50"
        />
        <button
            @click="submit"
            :disabled="!commentInput.trim() || isSubmitting"
            class="w-7 h-7 rounded-full flex items-center justify-center transition-all"
            :class="commentInput.trim() && !isSubmitting ? 'bg-blue-600 hover:bg-blue-500 text-white shadow-lg' : 'bg-white/5 text-white/30 cursor-not-allowed'"
        >
          <div v-if="isSubmitting" class="w-3 h-3 border-2 border-white/30 border-t-white rounded-full animate-spin"></div>
          <svg v-else class="w-3 h-3 -mr-0.5 mt-0.5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2.5" d="M12 19l9 2-9-18-9 18 9-2zm0 0v-8"></path></svg>
        </button>
      </div>
    </div>

  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import api from "../../../service/api.ts"

const props = defineProps({
  post: {
    type: Object,
    required: true
  },
  userProfile: {
    type: Object,
    default: null
  }
})

const commentInput = ref('')
const isLoading = ref(false)
const isSubmitting = ref(false)
const errorMsg = ref<string | null>(null)

onMounted(async () => {
  if (!props.post?.id) {
    isLoading.value = false;
    return;
  }

  if (!props.post.comments || props.post.comments.length === 0) {
    isLoading.value = true;
    errorMsg.value = null;
    try {
      const response = await api.get(`/comments/media/${props.post.id}`);
      props.post.comments = response.data || [];
    } catch (err) {
      console.error('Failed to load comments', err);
      errorMsg.value = "Couldn't load comments. Try again.";
      props.post.comments = [];
    } finally {
      isLoading.value = false;
    }
  }
});

const submit = async () => {
  const content = commentInput.value.trim()
  if (!content || isSubmitting.value) return

  isSubmitting.value = true
  errorMsg.value = null

  try {
    const response = await api.post('/comments', {
      mediaId: props.post.id,
      content: content
    })
    if (!props.post.comments) props.post.comments = []
    props.post.comments.push(response.data)
    commentInput.value = ''
  } catch (err) {
    console.error('Failed to add comment', err)
    errorMsg.value = "Failed to post comment. Please try again."
  } finally {
    isSubmitting.value = false
  }
}
</script>

<style scoped>
.custom-scrollbar::-webkit-scrollbar { width: 5px; }
.custom-scrollbar::-webkit-scrollbar-track { background: transparent; }
.custom-scrollbar::-webkit-scrollbar-thumb { background: rgba(255, 255, 255, 0.1); border-radius: 10px; }
.custom-scrollbar::-webkit-scrollbar-thumb:hover { background: rgba(255, 255, 255, 0.2); }
.animate-fade-in { animation: fadeIn 0.15s ease-out; }
@keyframes fadeIn {
  from { opacity: 0; transform: translateY(-10px); }
  to { opacity: 1; transform: translateY(0); }
}
</style>