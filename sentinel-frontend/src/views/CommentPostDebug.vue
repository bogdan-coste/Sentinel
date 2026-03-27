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

<script setup>
import { ref } from 'vue'
import api from '../service/api'

const mediaId = ref(null)
const content = ref('')
const result = ref(null)

const postComment = async () => {
  if (!mediaId.value || !content.value) {
    result.value = 'Please fill both fields.'
    return
  }
  try {
    const response = await api.post('/comments', {
      mediaId: Number(mediaId.value),
      content: content.value
    })
    result.value = response.data
  } catch (err) {
    console.error(err)
    result.value = err.response?.data || err.message
  }
}
</script>