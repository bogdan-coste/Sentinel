<template>
  <div class="relative min-h-screen w-full flex items-center justify-center overflow-hidden bg-[#050b14] font-sans">
    <div class="fixed inset-0 z-0 pointer-events-none">
      <BackgroundCanvas />
    </div>

    <div class="relative z-10 w-[92%] max-w-md bg-[#050b14]/85 backdrop-blur-2xl rounded-[2.5rem] shadow-[0_0_60px_rgba(64,192,157,0.15)] border border-white/10 overflow-hidden transition-all duration-700 group hover:border-[#40c09d]/30">
      <div class="relative bg-linear-to-b from-[#0d1335] to-transparent pt-12 pb-8 flex items-center justify-center border-b border-white/5">
        <div class="absolute top-0 left-0 w-full h-1 bg-linear-to-r from-transparent via-[#40c09d] to-transparent opacity-50"></div>
        <h1 class="text-white text-2xl md:text-3xl font-black tracking-[0.3em] uppercase drop-shadow-[0_0_15px_rgba(64,192,157,0.5)]">
          {{ headerTitle }}
        </h1>
      </div>

      <div class="px-8 py-10 md:px-12 md:py-12 text-center">
        <div class="mb-6 flex justify-center">
          <div class="w-20 h-20 rounded-2xl bg-opacity-10 flex items-center justify-center border shadow-[0_0_20px_rgba(0,0,0,0.1)]"
               :class="iconBackgroundClass">
            <svg v-if="resultType === 'success'" xmlns="http://www.w3.org/2000/svg" class="w-10 h-10 text-[#40c09d]" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
              <path stroke-linecap="round" stroke-linejoin="round" d="M5 13l4 4L19 7" />
            </svg>
            <svg v-else-if="resultType === 'warning'" xmlns="http://www.w3.org/2000/svg" class="w-10 h-10 text-yellow-400" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
              <path stroke-linecap="round" stroke-linejoin="round" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
            </svg>
            <svg v-else xmlns="http://www.w3.org/2000/svg" class="w-10 h-10 text-red-400" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
              <path stroke-linecap="round" stroke-linejoin="round" d="M6 18L18 6M6 6l12 12" />
            </svg>
          </div>
        </div>

        <h2 class="text-white text-2xl font-bold mb-3">{{ title }}</h2>
        <p class="text-white/60 text-base leading-relaxed mb-8">
          {{ errorMessage }}
        </p>

        <div class="flex flex-col sm:flex-row gap-4 justify-center">
          <template v-if="resultType === 'success'">
            <button
                @click="goToLogin"
                class="px-8 py-4 bg-[#40c09d] text-[#050b14] rounded-xl text-lg font-black tracking-widest transition-all duration-300 shadow-[0_0_30px_rgba(64,192,157,0.3)] hover:shadow-[0_0_50px_rgba(64,192,157,0.6)] hover:-translate-y-1 active:scale-95 uppercase"
            >
              GO TO LOGIN
            </button>
          </template>

          <template v-else-if="resultType === 'warning'">
            <button
                @click="goToLogin"
                class="px-8 py-4 bg-[#40c09d] text-[#050b14] rounded-xl text-lg font-black tracking-widest transition-all duration-300 shadow-[0_0_30px_rgba(64,192,157,0.3)] hover:shadow-[0_0_50px_rgba(64,192,157,0.6)] hover:-translate-y-1 active:scale-95 uppercase"
            >
              GO TO LOGIN
            </button>
            <button
                @click="goToHome"
                class="px-8 py-4 bg-transparent text-white border border-white/20 rounded-xl text-lg font-black tracking-widest transition-all duration-300 hover:bg-white/10 active:scale-95 uppercase"
            >
              GO HOME
            </button>
          </template>

          <template v-else>
            <button
                @click="goToRegister"
                class="px-8 py-4 bg-[#40c09d] text-[#050b14] rounded-xl text-lg font-black tracking-widest transition-all duration-300 shadow-[0_0_30px_rgba(64,192,157,0.3)] hover:shadow-[0_0_50px_rgba(64,192,157,0.6)] hover:-translate-y-1 active:scale-95 uppercase"
            >
              TRY AGAIN
            </button>
            <button
                @click="goToHome"
                class="px-8 py-4 bg-transparent text-white border border-white/20 rounded-xl text-lg font-black tracking-widest transition-all duration-300 hover:bg-white/10 active:scale-95 uppercase"
            >
              GO HOME
            </button>
          </template>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter, useRoute } from 'vue-router'
import { computed } from 'vue'

import { BackgroundCanvas } from '@layouts'

const router = useRouter()
const route = useRoute()

const errorCode = computed(() => route.query.code as string || 'unknown')
const errorMessage = computed(() => route.query.message as string || 'An unexpected error occurred.')

const resultType = computed(() => {
  if (errorCode.value === 'success') return 'success'
  if (errorCode.value === 'already_verified') return 'warning'
  return 'error'
})

const title = computed(() => {
  switch (errorCode.value) {
    case 'success':
      return 'Success!'
    case 'expired':
      return 'Link Expired'
    case 'already_verified':
      return 'Already Verified'
    case 'network':
      return 'Network Error'
    default:
      return 'Oops! Something went wrong'
  }
})

const headerTitle = computed(() => {
  switch (resultType.value) {
    case 'success':
      return 'SUCCESS'
    case 'warning':
      return 'WARNING'
    default:
      return 'ERROR'
  }
})

const iconBackgroundClass = computed(() => {
  switch (resultType.value) {
    case 'success':
      return 'bg-[#40c09d]/10 border-[#40c09d]/20'
    case 'warning':
      return 'bg-yellow-500/10 border-yellow-500/20'
    default:
      return 'bg-red-500/10 border-red-500/20'
  }
})

const goToRegister = () => {
  router.push('/register')
}

const goToHome = () => {
  router.push('/home')
}

const goToLogin = () => {
  router.push('/authenticate')
}
</script>

<style scoped>
</style>