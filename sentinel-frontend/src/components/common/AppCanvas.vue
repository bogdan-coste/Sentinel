<template>
  <div class="min-h-screen w-full relative">

    <div
        class="fixed inset-0 bg-[radial-gradient(circle_at_top_left,#1A4B84_0%,#051A30_100%)] -z-50"
    ></div>

    <div class="fixed inset-0 bg-black/10 pointer-events-none -z-40"></div>

    <div class="relative z-10 w-full min-h-screen overflow-x-hidden">
      <slot />
    </div>
  </div>
</template>

<script setup lang="ts">
// Slot-based layout component
</script>

<style>
/* Smooth scrollbar styles */
::-webkit-scrollbar {
  width: 6px;
}
::-webkit-scrollbar-track {
  background: transparent;
}
::-webkit-scrollbar-thumb {
  background: rgba(255, 255, 255, 0.1);
  border-radius: 10px;
}
::-webkit-scrollbar-thumb:hover {
  background: rgba(255, 255, 255, 0.2);
}
</style>