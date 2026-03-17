<script setup lang="ts">
import { onMounted } from 'vue'
import BackgroundCanvas from '../components/common/BackgroundCanvas.vue'

const props = defineProps<{
  pageContent: any
}>()

const content = props.pageContent

onMounted(() => {
  const observer = new IntersectionObserver((entries) => {
    entries.forEach((entry) => {
      if (entry.isIntersecting) {
        entry.target.classList.add('opacity-100', 'scale-100')
        entry.target.classList.remove('opacity-0', 'scale-95')
      } else {
        entry.target.classList.add('opacity-0', 'scale-95')
        entry.target.classList.remove('opacity-100', 'scale-100')
      }
    })
  }, { threshold: 0.5 })

  document.querySelectorAll('.reveal-zone').forEach((el) => {
    observer.observe(el)
  })
})
</script>

<template>
  <div class="relative w-full h-screen bg-[#050b14] font-sans text-white selection:bg-[#40c09d] selection:text-[#050b14] overflow-y-scroll snap-y snap-mandatory scrollbar-hide">

    <div class="fixed inset-0 z-0 pointer-events-none">
      <BackgroundCanvas />
    </div>

    <section class="relative z-10 w-full h-screen snap-center flex flex-col justify-center items-center px-6">
      <div class="w-full max-w-7xl mx-auto flex flex-col items-center text-center transition-all duration-1200 ease-in-out reveal-zone opacity-0 scale-95">

        <h1 class="text-6xl md:text-[8rem] lg:text-[10rem] font-black tracking-tighter leading-none mb-8 relative">
          <span class="absolute inset-0 bg-[#40c09d] blur-[150px] opacity-10 rounded-full pointer-events-none"></span>
          <span class="text-white animate-[pulse_3s_ease-in-out_infinite] [text-shadow:0_0_20px_rgba(64,192,157,0.5)]">
            {{ content.hero.titleStart }}
          </span>
          <span class="text-[#40c09d] animate-[pulse_3s_ease-in-out_1.5s_infinite] [text-shadow:0_0_30px_rgba(64,192,157,0.8)]">
            {{ content.hero.titleEnd }}
          </span>
        </h1>

        <p class="text-xl md:text-2xl text-white/80 max-w-3xl mx-auto font-light leading-relaxed mb-12 drop-shadow-lg">
          {{ content.hero.subtitle }}
        </p>

        <div class="flex flex-col md:flex-row gap-6 justify-center items-center w-full md:w-auto mb-20">
          <router-link :to="content.hero.pathPrimary" class="w-full md:w-72 bg-linear-to-r from-[#40c09d] to-[#26856a] text-[#050b14] font-black py-5 rounded-2xl text-xl tracking-widest text-center transition-all duration-300 shadow-[0_0_40px_rgba(64,192,157,0.3)] hover:shadow-[0_0_60px_rgba(64,192,157,0.5)] hover:-translate-y-1 active:scale-95">
            {{ content.hero.ctaPrimary }}
          </router-link>
          <router-link :to="content.hero.pathSecondary" class="w-full md:w-72 bg-[#050b14]/50 hover:bg-[#0d1335]/80 text-white font-bold py-5 rounded-2xl text-xl border border-white/20 backdrop-blur-xl text-center transition-all duration-300 hover:border-[#40c09d]/50 hover:-translate-y-1 active:scale-95 uppercase tracking-widest">
            {{ content.hero.ctaSecondary }}
          </router-link>
        </div>

        <div class="grid grid-cols-3 gap-4 md:gap-12 border-t border-white/10 pt-10 w-full max-w-4xl">
          <div v-for="(stat, i) in content.stats" :key="i" class="text-center">
            <div class="text-3xl md:text-5xl font-black text-[#40c09d] tracking-tighter mb-2">{{ stat.value }}</div>
            <div class="text-white/60 text-[10px] md:text-xs font-bold uppercase tracking-widest">{{ stat.label }}</div>
          </div>
        </div>
      </div>
      <div class="absolute bottom-6 left-1/2 -translate-x-1/2 animate-bounce text-[#40c09d]/50 text-2xl pointer-events-none">↓</div>
    </section>

    <section class="relative z-10 w-full h-screen snap-center flex flex-col justify-center items-center px-6">
      <div class="w-full max-w-7xl mx-auto grid grid-cols-1 md:grid-cols-6 gap-6 transition-all duration-1200 ease-in-out reveal-zone opacity-0 scale-95">

        <div class="md:col-span-3 bg-[#050b14]/85 backdrop-blur-2xl rounded-3xl p-8 md:p-12 border border-white/10 shadow-2xl relative overflow-hidden group hover:border-white/30 transition-colors duration-500">
          <h2 class="text-[#40c09d] font-mono tracking-[0.2em] uppercase text-xs mb-6 flex items-center gap-4">
            <span class="w-8 h-0.5 bg-[#40c09d]/50"></span> {{ content.manifesto.label }}
          </h2>
          <p class="text-2xl md:text-3xl text-white/90 font-light leading-snug relative z-10">
            {{ content.manifesto.text }}
          </p>
        </div>

        <div class="md:col-span-3 bg-linear-to-br from-[#0d1335]/90 to-[#050b14]/90 backdrop-blur-2xl rounded-3xl p-8 md:p-12 border border-[#40c09d]/30 shadow-2xl flex flex-col md:flex-row items-center gap-8 group hover:border-[#40c09d]/60 transition-colors duration-500 overflow-hidden relative">
          <div class="absolute bottom-0 right-0 w-full h-1/2 bg-linear-to-t from-[#40c09d]/5 to-transparent pointer-events-none"></div>
          <div class="shrink-0 w-20 h-20 bg-[#40c09d]/10 border border-[#40c09d]/30 rounded-2xl flex items-center justify-center relative z-10 shadow-inner">
            <svg class="w-10 h-10 text-[#40c09d]" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M15 12a3 3 0 11-6 0 3 3 0 016 0zM2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" />
            </svg>
          </div>
          <div class="relative z-10 text-center md:text-left">
            <h3 class="text-2xl md:text-3xl font-black text-white mb-3 tracking-tight">{{ content.highlight.title }}</h3>
            <p class="text-white/70 text-sm md:text-base font-light">{{ content.highlight.desc }}</p>
          </div>
        </div>

        <div v-for="(feat) in content.features" :key="feat.id"
             class="md:col-span-2 bg-[#050b14]/85 backdrop-blur-2xl rounded-3xl p-8 border border-white/10 shadow-lg hover:border-[#40c09d]/40 hover:-translate-y-2 transition-all duration-500 group">
          <div class="w-14 h-14 bg-[#40c09d]/10 border border-[#40c09d]/20 rounded-2xl flex items-center justify-center mb-6">
            <svg class="w-7 h-7 text-[#40c09d]" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" :d="feat.icon" />
            </svg>
          </div>
          <h3 class="text-xl font-bold mb-3 text-white tracking-wide uppercase">{{ feat.title }}</h3>
          <p class="text-white/60 text-sm leading-relaxed font-light">{{ feat.desc }}</p>
        </div>
      </div>
    </section>

    <section class="relative z-10 w-full h-screen snap-center flex flex-col justify-center items-center px-6">
      <div class="w-full max-w-4xl mx-auto flex flex-col items-center text-center transition-all duration-1200 ease-in-out reveal-zone opacity-0 scale-95">

        <div class="mb-10 w-24 h-24 bg-[#0d1335]/80 border border-white/20 rounded-3xl flex items-center justify-center shadow-[0_0_50px_rgba(64,192,157,0.2)]">
          <svg class="w-12 h-12 text-[#40c09d]" fill="currentColor" viewBox="0 0 24 24">
            <path :d="content.audit.iconPath" />
          </svg>
        </div>

        <h2 class="text-4xl md:text-6xl font-black mb-6 text-white">{{ content.audit.title }}</h2>
        <p class="text-xl md:text-2xl text-white/60 font-light mb-12 max-w-2xl">{{ content.audit.desc }}</p>

        <a :href="content.audit.repoUrl" target="_blank" class="px-10 py-5 bg-[#0d1335]/80 hover:bg-[#1a2347] border border-[#40c09d]/50 hover:border-[#40c09d] rounded-2xl text-white font-bold tracking-widest uppercase transition-all duration-300 hover:-translate-y-1 shadow-2xl">
          {{ content.audit.repoText }} →
        </a>

        <footer class="mt-24 pt-10 border-t border-white/10 text-center animate-[pulse_4s_ease-in-out_infinite] [text-shadow:0_0_10px_#40c09d] text-xs tracking-[0.2em] uppercase w-full font-mono text-[#40c09d]">
          {{ content.footerText }}
        </footer>
      </div>
    </section>

  </div>
</template>