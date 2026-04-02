<script setup lang="ts">
import { sentinelContent } from './store/data/main_page/content'
</script>

<template>
  <div class="min-h-screen w-full">
    <router-view v-slot="{ Component }">
      <component :is="Component" :pageContent="sentinelContent" />
    </router-view>
  </div>
</template>
