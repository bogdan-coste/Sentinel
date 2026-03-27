<template>
  <header class="h-20 shrink-0 w-full px-6 md:px-10 flex items-center justify-between border-b border-white/[0.05] bg-[#0A192F]/40 backdrop-blur-md z-40">

    <div class="flex items-center gap-6 w-full max-w-md">
      <router-link to="/home" class="w-11 h-11 rounded-full bg-white flex items-center justify-center shrink-0 shadow-[0_0_15px_rgba(255,255,255,0.2)] hover:scale-105 transition-transform">
        <svg class="w-5 h-5 text-[#0A192F]" viewBox="0 0 24 24" fill="currentColor">
          <path d="M21 3v12a6 6 0 01-6 6H3V9a6 6 0 016-6h12z"/>
        </svg>
      </router-link>

      <div class="relative w-full group">
        <svg class="w-4 h-4 absolute left-4 top-1/2 -translate-y-1/2 text-white/50 group-focus-within:text-blue-400 transition-colors" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"></path></svg>
        <input
            type="text"
            placeholder=""
            class="w-full bg-white/[0.04] hover:bg-white/[0.06] focus:bg-white/[0.08] border border-white/[0.05] rounded-full py-2.5 pl-10 pr-4 text-sm text-white placeholder-white/50 focus:outline-none focus:border-white/20 transition-all"
        />
      </div>
    </div>

    <div class="flex items-center justify-end gap-5">

      <router-link to="/home" class="relative flex flex-col items-center group">
        <button class="text-white group-hover:scale-110 transition-all p-2 bg-white/10 rounded-full shadow-lg border border-white/5 flex items-center justify-center">
          <svg class="w-5 h-5" fill="currentColor" viewBox="0 0 20 20"><path d="M10.707 2.293a1 1 0 00-1.414 0l-7 7a1 1 0 001.414 1.414L4 10.414V17a1 1 0 001 1h2a1 1 0 001-1v-2a1 1 0 011-1h2a1 1 0 011 1v2a1 1 0 001 1h2a1 1 0 001-1v-6.586l.293.293a1 1 0 001.414-1.414l-7-7z"></path></svg>
        </button>
        <span class="absolute -bottom-2 w-1 h-1 bg-white rounded-full shadow-[0_0_8px_rgba(255,255,255,0.8)]"></span>
      </router-link>

      <router-link to="/messages" class="text-white/60 hover:text-white hover:scale-110 transition-all p-2 bg-white/5 rounded-full hover:bg-white/10 border border-transparent flex items-center justify-center">
        <svg class="w-5 h-5" fill="currentColor" viewBox="0 0 20 20"><path fill-rule="evenodd" d="M18 10c0 3.866-3.582 7-8 7a8.841 8.841 0 01-4.083-.98L2 17l1.338-3.123C2.493 12.767 2 11.434 2 10c0-3.866 3.582-7 8-7s8 3.134 8 7zM7 9H5v2h2V9zm8 0h-2v2h2V9zM9 9h2v2H9V9z" clip-rule="evenodd"></path></svg>
      </router-link>

      <router-link to="/settings" class="text-white/60 hover:text-white hover:scale-110 transition-all p-2 bg-white/5 rounded-full hover:bg-white/10 border border-transparent flex items-center justify-center">
        <svg class="w-5 h-5" fill="currentColor" viewBox="0 0 20 20"><path fill-rule="evenodd" d="M11.49 3.17c-.38-1.56-2.6-1.56-2.98 0a1.532 1.532 0 01-2.286.948c-1.372-.836-2.942.734-2.106 2.106.54.886.061 2.042-.947 2.287-1.561.379-1.561 2.6 0 2.978a1.532 1.532 0 01.947 2.287c-.836 1.372.734 2.942 2.106 2.106a1.532 1.532 0 012.287.947c.379 1.561 2.6 1.561 2.978 0a1.533 1.533 0 012.287-.947c1.372.836 2.942-.734 2.106-2.106a1.533 1.533 0 01.947-2.287c1.561-.379 1.561-2.6 0-2.978a1.532 1.532 0 01-.947-2.287c.836-1.372-.734-2.942-2.106-2.106a1.532 1.532 0 01-2.287-.947zM10 13a3 3 0 100-6 3 3 0 000 6z" clip-rule="evenodd"></path></svg>
      </router-link>

    </div>
  </header>
</template>

<script setup lang="ts">
</script>