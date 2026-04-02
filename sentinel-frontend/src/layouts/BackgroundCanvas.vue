<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'

const canvasRef = ref<HTMLCanvasElement | null>(null)

let animationFrameId: number
let mouse = { x: -1000, y: -1000 }
let time = 0

type Point = { x: number; y: number; ox: number; oy: number; angle: number; speed: number; vx: number; vy: number }
type Triangle = { p1: Point; p2: Point; p3: Point; hoverAlpha: number; colorOffset: number }
type Ripple = { x: number; y: number; radius: number; life: number }

let points: Point[] = []
let triangles: Triangle[] = []
let ripples: Ripple[] = []

const pointInTriangle = (pt: { x: number; y: number }, v1: Point, v2: Point, v3: Point) => {
  const sign = (p1: { x: number; y: number }, p2: { x: number; y: number }, p3: { x: number; y: number }) =>
      (p1.x - p3.x) * (p2.y - p3.y) - (p2.x - p3.x) * (p1.y - p3.y)
  const d1 = sign(pt, v1, v2)
  const d2 = sign(pt, v2, v3)
  const d3 = sign(pt, v3, v1)
  return !((d1 < 0 || d2 < 0 || d3 < 0) && (d1 > 0 || d2 > 0 || d3 > 0))
}

const generateTriangles = (width: number, height: number) => {
  points = []
  triangles = []
  const gap = width < 768 ? 80 : 130
  const cols = Math.ceil(width / gap) + 2
  const rows = Math.ceil(height / gap) + 2
  const pointsPerRow = cols + 1

  for (let y = 0; y <= rows; y++) {
    for (let x = 0; x <= cols; x++) {
      const ox = (x - 1) * gap + (Math.random() - 0.5) * gap * 0.5
      const oy = (y - 1) * gap + (Math.random() - 0.5) * gap * 0.5
      points.push({
        x: ox,
        y: oy,
        ox,
        oy,
        angle: Math.random() * Math.PI * 2,
        speed: 0.005 + Math.random() * 0.01,
        vx: 0,
        vy: 0,
      })
    }
  }

  for (let y = 0; y < rows; y++) {
    for (let x = 0; x < cols; x++) {
      const tl = y * pointsPerRow + x
      const tr = tl + 1
      const bl = (y + 1) * pointsPerRow + x
      const br = bl + 1
      const pTL = points[tl]
      const pTR = points[tr]
      const pBL = points[bl]
      const pBR = points[br]
      if (!pTL || !pTR || !pBL || !pBR) continue
      const colorOffset = (x + y) * 0.3
      if ((x + y) % 2 === 0) {
        triangles.push({ p1: pTL, p2: pTR, p3: pBL, hoverAlpha: 0, colorOffset })
        triangles.push({ p1: pTR, p2: pBR, p3: pBL, hoverAlpha: 0, colorOffset })
      } else {
        triangles.push({ p1: pTL, p2: pTR, p3: pBR, hoverAlpha: 0, colorOffset })
        triangles.push({ p1: pTL, p2: pBR, p3: pBL, hoverAlpha: 0, colorOffset })
      }
    }
  }
}

onMounted(() => {
  const canvas = canvasRef.value
  if (!canvas) return
  const ctx = canvas.getContext('2d')
  if (!ctx) return

  let width = window.innerWidth
  let height = window.innerHeight
  canvas.width = width
  canvas.height = height
  generateTriangles(width, height)

  const handleMouseMove = (e: MouseEvent) => {
    mouse.x = e.clientX
    mouse.y = e.clientY
  }
  const handleTouch = (e: TouchEvent) => {
    if (e.touches && e.touches[0]) {
      mouse.x = e.touches[0].clientX
      mouse.y = e.touches[0].clientY
    }
  }
  const handleClick = (e: MouseEvent) => {
    ripples.push({ x: e.clientX, y: e.clientY, radius: 0, life: 1 })
  }
  const handleResize = () => {
    width = window.innerWidth
    height = window.innerHeight
    canvas.width = width
    canvas.height = height
    generateTriangles(width, height)
  }

  window.addEventListener('mousemove', handleMouseMove)
  window.addEventListener('touchstart', handleTouch)
  window.addEventListener('touchmove', handleTouch)
  window.addEventListener('click', handleClick)
  window.addEventListener('resize', handleResize)

  const draw = () => {
    ctx.clearRect(0, 0, width, height)
    time += 1
    if (Math.random() < 0.005) {
      ripples.push({ x: Math.random() * width, y: Math.random() * height, radius: 0, life: 0.6 })
    }

    for (let i = ripples.length - 1; i >= 0; i--) {
      const r = ripples[i]
      if (r) {
        r.radius += 15
        r.life -= 0.02
        if (r.life <= 0) ripples.splice(i, 1)
      }
    }

    for (const p of points) {
      p.angle += p.speed
      const bx = p.ox + Math.cos(p.angle) * 35
      const by = p.oy + Math.sin(p.angle) * 35
      let tx = bx
      let ty = by

      const repulseR = width < 768 ? 100 : 200
      const dx = mouse.x - bx
      const dy = mouse.y - by
      const d = Math.sqrt(dx * dx + dy * dy)
      if (d < repulseR && d > 0) {
        const f = (repulseR - d) / repulseR
        const strength = width < 768 ? 60 : 120
        tx -= (dx / d) * f * strength
        ty -= (dy / d) * f * strength
      }

      for (const r of ripples) {
        const rdx = bx - r.x
        const rdy = by - r.y
        const rd = Math.sqrt(rdx * rdx + rdy * rdy)
        const dtr = Math.abs(rd - r.radius)
        if (dtr < 60 && rd > 0) {
          const f = ((60 - dtr) / 60) * r.life
          tx += (rdx / rd) * f * 80
          ty += (rdy / rd) * f * 80
        }
      }

      p.vx += (tx - p.x) * 0.08
      p.vx *= 0.75
      p.vy += (ty - p.y) * 0.08
      p.vy *= 0.75
      p.x += p.vx
      p.y += p.vy
    }

    for (const t of triangles) {
      const isHovered = pointInTriangle(mouse, t.p1, t.p2, t.p3)
      if (isHovered) t.hoverAlpha = Math.min(t.hoverAlpha + 0.1, 0.8)
      else t.hoverAlpha = Math.max(t.hoverAlpha - 0.02, 0)

      const hue = 190 + Math.sin(time * 0.08 + t.colorOffset) * 35
      ctx.beginPath()
      ctx.moveTo(t.p1.x, t.p1.y)
      ctx.lineTo(t.p2.x, t.p2.y)
      ctx.lineTo(t.p3.x, t.p3.y)
      ctx.closePath()

      const cx = (t.p1.x + t.p2.x + t.p3.x) / 3
      const cy = (t.p1.y + t.p2.y + t.p3.y) / 3
      const dm = Math.sqrt(Math.pow(mouse.x - cx, 2) + Math.pow(mouse.y - cy, 2))
      const proxA = Math.max(0, 1 - dm / 400)

      let ripA = 0
      for (const r of ripples) {
        const dr = Math.sqrt(Math.pow(r.x - cx, 2) + Math.pow(r.y - cy, 2))
        if (Math.abs(dr - r.radius) < 80) ripA = Math.max(ripA, r.life * 0.6)
      }

      const finalLineA = Math.min(1, 0.12 + proxA * 0.5 + ripA)
      ctx.strokeStyle = `hsla(${hue}, 90%, 60%, ${finalLineA})`
      ctx.lineWidth = ripA > 0 ? 1.5 : 1
      ctx.stroke()

      const idleFillA = (Math.sin(time * 0.03 + t.colorOffset) + 1) * 0.02
      ctx.fillStyle = `hsla(${hue}, 80%, 50%, ${idleFillA})`
      ctx.fill()

      if (t.hoverAlpha > 0) {
        ctx.save()
        ctx.shadowBlur = 30 * t.hoverAlpha
        ctx.shadowColor = `hsla(${hue}, 100%, 60%, 1)`
        ctx.fillStyle = `hsla(${hue}, 100%, 60%, ${t.hoverAlpha})`
        ctx.fill()
        ctx.restore()
      }
    }

    animationFrameId = requestAnimationFrame(draw)
  }

  draw()

  onUnmounted(() => {
    cancelAnimationFrame(animationFrameId)
    window.removeEventListener('mousemove', handleMouseMove)
    window.removeEventListener('touchstart', handleTouch)
    window.removeEventListener('touchmove', handleTouch)
    window.removeEventListener('click', handleClick)
    window.removeEventListener('resize', handleResize)
  })
})
</script>

<template>
  <canvas ref="canvasRef" class="absolute inset-0 z-0"></canvas>
</template>