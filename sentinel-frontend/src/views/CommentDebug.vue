<template>
  <div class="p-4 bg-gray-900 text-white rounded-lg max-w-md mx-auto">
    <h3 class="text-lg font-bold mb-2">Test Comment</h3>
    <div class="mb-2">
      <label class="block text-sm">Media ID (from database):</label>
      <input v-model="mediaId" type="number" class="w-full bg-gray-800 border rounded p-1" />
    </div>
    <div class="mb-2">
      <label class="block text-sm">Comment content:</label>
      <input v-model="content" type="text" class="w-full bg-gray-800 border rounded p-1" />
    </div>
    <button @click="postComment" class="bg-blue-600 hover:bg-blue-500 px-4 py-1 rounded">Post Comment</button>
    <div v-if="result" class="mt-2 text-sm">
      <pre>{{ result }}</pre>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import api from '../service/api'

const content = ref('')
const result = ref(null)

interface UserProfile {
  username: string;
  name: string;
  profilePicUrl: string;
  bannerPicUrl: string;
  description: string;
  email: string;
  friendCount: number;
  profilePicMediaId?: number;
  bannerMediaId?: number;
}

const userData = ref<UserProfile | null>(null);
const profilePicMediaId = ref<number | null>(null)
const profileBannerMediaId = ref<number | null>(null)

const gatherNecessaryUserMedia = async () => {
  const response = await api.get('/users/me/user-media')
  userData.value = response.data

  if (userData.value?.profilePicMediaId) {
    profilePicMediaId.value = userData.value.profilePicMediaId
  }

  if (userData.value?.bannerMediaId) {
    profileBannerMediaId.value = userData.value.bannerMediaId
  }
}

onMounted(() => {
  gatherNecessaryUserMedia()
});
</script>