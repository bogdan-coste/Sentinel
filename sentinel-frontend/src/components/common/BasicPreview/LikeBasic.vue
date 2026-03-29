<template>
  <div class="flex items-center gap-3 py-3 px-4 bg-white/2 border-t border-white/5 w-full animate-fade-in rounded-b-4xl">

    <div class="flex items-center -space-x-2 shrink-0">
      <img v-for="(liker, index) in displayLikers" :key="index" :src="liker.image" class="w-6 h-6 rounded-full border-2 border-[#0A192F] object-cover bg-[#051021]"  alt=""/>
    </div>

    <span class="text-[12px] text-white/70 truncate">
      <template v-if="post.likes > 0">
        <span class="font-bold text-white/90">{{ displayLikers[0]?.name || 'Someone' }}</span>
        <span v-if="post.likes > 1"> and {{ post.likes - 1 }} others</span>
      </template>
      <template v-else>
        Be the first to like this
      </template>
    </span>

  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue';

const props = defineProps({
  post: {
    type: Object,
    required: true
  },
  likers: {
    type: Array as () => any[],
    default: () => []
  }
});

const displayLikers = computed(() => {
  return props.likers.slice(0, Math.min(props.post.likes, 3)).map(likeObj => ({
    name: likeObj.user?.name || 'User',
    image: likeObj.user?.profilePicUrl || ''
  }));
});
</script>

<style scoped>
.animate-fade-in {
  animation: fadeIn 0.15s ease-out;
}
@keyframes fadeIn {
  from { opacity: 0; transform: translateY(-10px); }
  to { opacity: 1; transform: translateY(0); }
}
</style>