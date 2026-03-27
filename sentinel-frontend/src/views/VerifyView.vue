<template>
  <div class="relative min-h-screen w-full flex items-center justify-center overflow-hidden bg-[#050b14] font-sans">
    <div class="fixed inset-0 z-0 pointer-events-none">
      <BackgroundCanvas />
    </div>
    <div class="relative z-10 text-white/70 text-lg">Verifying your email...</div>
  </div>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import BackgroundCanvas from '../components/common/BackgroundCanvas.vue'
import api from '../service/api.ts'

const route = useRoute()
const router = useRouter()

onMounted(async () => {
  const token = route.query.token as string

  if (!token) {
    router.replace({
      path: '/auth-result',
      query: { code: 'invalid', message: 'Missing verification token.' }
    })
    return
  }

  try {
    await api.get(`/users/verify?token=${token}`)
    router.replace({
      path: '/auth-result',
      query: { code: 'success', message: 'Email verified! You can now log in.' }
    })
  } catch (error: any) {
    let code = 'unknown'
    let message = 'An unexpected error occurred.'

    if (error.response?.data) {
      code = error.response.data.code || code
      message = error.response.data.message || message
    } else if (error.request) {
      code = 'network'
      message = 'Network error. Please check your connection.'
    }

    router.replace({
      path: '/auth-result',
      query: { code, message }
    })
  }
})
</script>