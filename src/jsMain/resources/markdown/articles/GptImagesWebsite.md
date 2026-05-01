---
nav-title: GPT Images
title: Building GPT Images (gpt-images.ayfri.com)
description: How and why I built a SvelteKit client for OpenAI image and video generation, how storage and pricing evolved, and what the project taught me.
keywords: svelte, sveltekit, openai, gpt-image, images api, opfs, indexeddb, cloudflare, side project, typescript
date-created: 2026-05-01
date-modified: 2026-05-01
root: .layouts.ArticleLayout
routeOverride: /articles/gpt-images-website/index
---

[GPT Images](https://gpt-images.ayfri.com) is a small site I use to generate and browse images (and more recently video) with OpenAI's APIs. Source is on GitHub: [Ayfri/GPT-Images](https://github.com/Ayfri/GPT-Images), with the [main branch](https://github.com/Ayfri/GPT-Images/tree/main) and [full history](https://github.com/Ayfri/GPT-Images/commits/main) (about **112** commits at the time of writing). This post covers why I built it, how it works, how the repo grew, and what I took away from it.

## What it is and why it exists

I wasn't trying to compete with full design suites. I wanted a **quick personal panel** for image generation: pick a model and size, write a prompt, see history, get a rough cost estimate, and iterate without leaving the browser. Shipping it at [gpt-images.ayfri.com](https://gpt-images.ayfri.com) made it easy to share and to use myself on random devices.

[![GPT Images, images page (full viewport)](/images/gpt-images/screenshot-full.png)](https://gpt-images.ayfri.com)

A few choices fall out of that:

- **No server-side API key.** The key stays in **your** browser. The app talks to OpenAI from the client with the [official JavaScript SDK](https://github.com/openai/openai-node) and browser usage turned on. Hosting stays boring and I never store other people's keys. The flip side is the usual client-side warning: if someone gets into DevTools or you're on a shared machine, a leaked key is on you, so treat it like any other secret that can leave the machine.
- **Metadata in IndexedDB, blobs elsewhere.** Generated media gets big fast. Huge `data:` strings in IndexedDB work until they don't. Binary payloads moved to [OPFS](https://developer.mozilla.org/en-US/docs/Web/API/File_System_API/Origin_private_file_system) (Origin Private File System) with structured fields still in IndexedDB, plus **versioned migrations** so upgrades don't wipe galleries. The glue is in [`opfsStore.ts`](https://github.com/Ayfri/GPT-Images/blob/main/src/lib/db/opfsStore.ts), [`migrations.ts`](https://github.com/Ayfri/GPT-Images/blob/main/src/lib/db/migrations.ts), and [`imageStore.ts`](https://github.com/Ayfri/GPT-Images/blob/main/src/lib/db/imageStore.ts).
- **Pricing you can trust in the UI.** List prices move, and `gpt-image-2` is not billed like the older image models. The UI keeps explicit tables and helpers in [`types/image.ts`](https://github.com/Ayfri/GPT-Images/blob/main/src/lib/types/image.ts) so the stats area stays useful instead of pretty but wrong. When OpenAI updates numbers, that's still the first file I touch.

## Stack and deployment

It's **SvelteKit**, **TypeScript**, [Tailwind 4](https://tailwindcss.com/) with [`@tailwindcss/vite`](https://github.com/tailwindlabs/tailwindcss), and **Vite**. Icons are [Lucide](https://lucide.dev/) via [`@lucide/svelte`](https://www.npmjs.com/package/@lucide/svelte). Production goes through [`@sveltejs/adapter-cloudflare`](https://svelte.dev/docs/kit/adapter-cloudflare), same idea as my other small sites: static shell on the edge, no Node server for this app. [`wrangler.toml`](https://github.com/Ayfri/GPT-Images/blob/main/wrangler.toml) lives in the repo.

I switched package managers from **pnpm** to **bun** ([`package.json`](https://github.com/Ayfri/GPT-Images/blob/main/package.json)) mostly for speed and cleaner lockfiles on my machine. A lot of commits are routine dependency bumps; some came from Dependabot, e.g. [PR #4](https://github.com/Ayfri/GPT-Images/pull/4) (Kit) and [PR #5](https://github.com/Ayfri/GPT-Images/pull/5) (Svelte).

## How generation works (images)

The flow is straight line stuff, close to OpenAI's [Images API](https://platform.openai.com/docs/api-reference/images) and their [image generation guide](https://platform.openai.com/docs/guides/image-generation):

![API key field, browser only](/images/gpt-images/api-key-box.png)

1. You paste an API key. It persists in **localStorage** through a tiny store ([`apiKeyStore.ts`](https://github.com/Ayfri/GPT-Images/blob/main/src/lib/stores/apiKeyStore.ts)) so you don't have to retype it every visit.
2. **`ImageGenerator`** ([component](https://github.com/Ayfri/GPT-Images/blob/main/src/lib/components/ImageGenerator.svelte)) picks **generate** vs **edit** from context: no reference image means generation; attachments (and optional **mask** for inpaint-style work) mean edit. The client calls **`images.generate`** or **`images.edit`** with `response_format` aimed at **base64** so the UI can stash results without an extra download step.
3. **`openai.ts`** ([service](https://github.com/Ayfri/GPT-Images/blob/main/src/lib/services/openai.ts)) is the one place that maps UI fields to API parameters (quality, size, `background`, `moderation`, `output_format`, compression, plus things like **no transparent background on `gpt-image-2`**) and turns errors into something readable.
4. **IndexedDB** holds one record per image (prompt, timestamps, model, quality, size, fidelity, format, ...). **Pixels** go to OPFS via **`opfsStore`** (`writeMediaFile`); the row can leave `imageData` empty and **reads** build a **`blob:` URL** with `readMediaObjectUrl` for the grid and lightbox. That way you're not hauling giant strings through memory on every scroll.

The advanced block (input fidelity, output format, compression, moderation) sits behind an accordion so the default screen stays quiet, but you can still mirror most API tutorials field by field if you want.

**Prompt length** is capped in the UI against the documented max (`GPT_IMAGE_MODEL_PROMPT_MAX_CHARS` in [`types/image.ts`](https://github.com/Ayfri/GPT-Images/blob/main/src/lib/types/image.ts)) so you get a warning before the API says no. That landed in [`4c76737`](https://github.com/Ayfri/GPT-Images/commit/4c76737).

## Videos

There's a [`/videos`](https://github.com/Ayfri/GPT-Images/blob/main/src/routes/videos/+page.svelte) route on top of the [Videos API](https://platform.openai.com/docs/guides/video-generation): enqueue a job, poll, then store the result with the same **OPFS + IndexedDB** split as images. Types and video pricing helpers are in [`video.ts`](https://github.com/Ayfri/GPT-Images/blob/main/src/lib/types/video.ts) and [`videoPrice.ts`](https://github.com/Ayfri/GPT-Images/blob/main/src/lib/utils/videoPrice.ts). The header switches Images / Videos and links out to OpenAI's docs so I don't have to maintain a second copy of every parameter description.

## Usage stats and pricing

**`UsageStats`** and **`VideoUsageStats`** ([`UsageStats.svelte`](https://github.com/Ayfri/GPT-Images/blob/main/src/lib/components/UsageStats.svelte), [`VideoUsageStats.svelte`](https://github.com/Ayfri/GPT-Images/blob/main/src/lib/components/VideoUsageStats.svelte)) roll up what's already on disk. Image pricing in **`types/image.ts`** still has tables for **`gpt-image-1`**, **`gpt-image-1-mini`**, **`gpt-image-1.5`**, and separate logic for **`gpt-image-2`** around **image output tokens**, with reasonable guesses when a resolution doesn't match a published row exactly. That part was annoyingly finicky: the product feels like "pick a size," but the invoice sometimes looks like tokens.

![Video usage and cost from locally stored jobs](/images/gpt-images/videos-usage.png)

## Interface evolution

Lately the UI settled on **`MediaGrid`**, **`MediaCard`**, and **`MediaLightbox`** ([`MediaGrid.svelte`](https://github.com/Ayfri/GPT-Images/blob/main/src/lib/components/MediaGrid.svelte), [`MediaCard.svelte`](https://github.com/Ayfri/GPT-Images/blob/main/src/lib/components/MediaCard.svelte), [`MediaLightbox.svelte`](https://github.com/Ayfri/GPT-Images/blob/main/src/lib/components/MediaLightbox.svelte)) so images and video share one pattern: grid, open, swipe in the lightbox, same price badges. Swipes use [`carouselSwipe.ts`](https://github.com/Ayfri/GPT-Images/blob/main/src/lib/utils/carouselSwipe.ts). Before that I had separate **`ImageGrid`** and **`VideoGrid`**; folding them together in [`aa4808b`](https://github.com/Ayfri/GPT-Images/commit/aa4808b) stopped fixes from only landing on one side.

[![Lightbox with metadata (full viewport)](/images/gpt-images/image-preview-full.png)](https://gpt-images.ayfri.com)

The shell (**animated background**, sticky header, footer) is there so the thing feels intentional instead of "default Svelte template gray."

## Evolution in Git history (what actually happened)

If you've shipped a small API client before, the commit story will feel familiar. Here's a **rough timeline** against the [full log](https://github.com/Ayfri/GPT-Images/commits/main). None of this was a master plan on day one.

### April 2025: prove the loop, ship hosting

First commit [`c17314d`](https://github.com/Ayfri/GPT-Images/commit/c17314d) is **2025-04-24**. Same day the app went from layout/header to [**real generation + storage**](https://github.com/Ayfri/GPT-Images/commit/9a2822d), then picked up multi-image runs, quality/size fields, a big viewer, [**SEO / Open Graph**](https://github.com/Ayfri/GPT-Images/commit/a22791a), [**`robots.txt`**](https://github.com/Ayfri/GPT-Images/commit/aa820d3), and [**Cloudflare adapter**](https://github.com/Ayfri/GPT-Images/commit/c0e0e38). Order mattered: "prompt, API, thumbnail" had to work before I cared about polish.

### Mid 2025: depth on images

**Editing**, **masks**, **advanced options**, and **infinite scroll** landed in a burst through late April and May 2025 (e.g. [**editing + upload**](https://github.com/Ayfri/GPT-Images/commit/20ab484), [**mask + backgrounds**](https://github.com/Ayfri/GPT-Images/commit/a0d9f67), [**infinite scroll**](https://github.com/Ayfri/GPT-Images/commit/fcc579c)). Then [**localStorage for form defaults**](https://github.com/Ayfri/GPT-Images/commit/1fd7e8b) and a [**GitHub link in the header**](https://github.com/Ayfri/GPT-Images/commit/677ca28). Model pick and multi-model pricing showed up in [**3f3f849**](https://github.com/Ayfri/GPT-Images/commit/3f3f849) as the API grew.

### October 2025: Tailwind 4 and video

[**Tailwind 4**](https://github.com/Ayfri/GPT-Images/commit/2d908f9) on **2025-10-06**, then [**video (Sora 2)**](https://github.com/Ayfri/GPT-Images/commit/66e1adf) the day after: jobs, polling, storage parallel to images.

### December 2025: Svelte 5 and GPT Image 1.5

**2025-12-16** was [**Svelte 5 + runes**](https://github.com/Ayfri/GPT-Images/commit/60e085c) and follow-up [**migration work**](https://github.com/Ayfri/GPT-Images/commit/04da284) (handlers, `$effect`, a11y). [**GPT Image 1.5**](https://github.com/Ayfri/GPT-Images/commit/8371724) landed the same day as the framework jump. Classic week of platform churn stacked on vendor churn.

### Early 2026: one panel, new storage, bun

**2026-03-02**: [**merged generate/edit UI**](https://github.com/Ayfri/GPT-Images/commit/e0f7f5a) with paste support, then [**videos and images aligned**](https://github.com/Ayfri/GPT-Images/commit/716b97b), pricing and stats passes, and a [**full UI revamp**](https://github.com/Ayfri/GPT-Images/commit/561689c). **2026-03-11**: [**pnpm to bun**](https://github.com/Ayfri/GPT-Images/commit/26a6ecc), then [**OPFS helpers**](https://github.com/Ayfri/GPT-Images/commit/19f25ac) and a [**versioned migration engine**](https://github.com/Ayfri/GPT-Images/commit/784b990) almost in the same breath. That's the point where it stopped being "fine on my laptop" and started being "won't murder IndexedDB once the gallery is real." [**Object URL caching**](https://github.com/Ayfri/GPT-Images/commit/82f633d) came right after and made scrolling feel lighter.

### Spring 2026: GPT Image 2 and shared media UI

**2026-04-27**: [**GPT Image 2 options + pricing**](https://github.com/Ayfri/GPT-Images/commit/9d64919), [**pricing fixes**](https://github.com/Ayfri/GPT-Images/commit/f21ae2f), and [PR #10](https://github.com/Ayfri/GPT-Images/pull/10) (worked from a Copilot branch called "add image generation API," which is about how it was built, not a secret backend). [**MediaGrid / MediaLightbox**](https://github.com/Ayfri/GPT-Images/commit/aa4808b) merged the grids. Prompt character checks closed the loop ([`4c76737`](https://github.com/Ayfri/GPT-Images/commit/4c76737)).

So no, I didn't plan all of that upfront. It's the usual path from demo to tool: when OpenAI shipped something new, the app either adopted it or said "not here" with a reason.

## What I learned

- **Client-only keys are a real product choice.** They keep ops simple and keys with the user, but the docs have to spell the threat model so nobody thinks the site is a vault.
- **Browser storage isn't one blob store.** IndexedDB is great for structured rows and indexes. **OPFS** fits big binaries you **read back** a lot. A small migration runner earns its keep the first time you change that split without asking people to nuke site data.
- **For paid APIs, bad price estimates are a UX bug.** If the app lies, people blame the app. Keeping numbers and `getImagePrice` (plus the `gpt-image-2` token guesses) next to the types keeps the UI and the math aligned.
- **Svelte 5 runes fit this UI shape.** Derived **`mode`**, **`MODEL_SUPPORT`**, and bindable props for "edit this again" flows meant less glue than the older "put everything in stores" style I would have used a few years back.
- **Wait on shared abstractions.** Separate grids were fine until the behaviors matched; only then was one grid worth the churn. Git history makes that order obvious.

## Conclusion

[GPT Images](https://gpt-images.ayfri.com) stayed fun to work on because three things keep moving under it: **OpenAI's surface area** (models, token-ish pricing, video jobs), **what browsers can do** (OPFS, IndexedDB limits, SDK in the browser), and **framework releases** (Svelte 5, Tailwind 4, adapters). The [repo](https://github.com/Ayfri/GPT-Images) isn't a mini SaaS. It's something I actually use, plus a reference layout for small SvelteKit clients: thin routes, pricing next to types, storage you can migrate, commits that read like a log of API changes.

If you open the site, assume **bring your own key**, and treat the code as support for the official docs, not a replacement for them. The interesting bits are the sharp edges: [**migrations**](https://github.com/Ayfri/GPT-Images/blob/main/src/lib/db/migrations.ts), [**OPFS**](https://github.com/Ayfri/GPT-Images/blob/main/src/lib/db/opfsStore.ts), [**pricing tables**](https://github.com/Ayfri/GPT-Images/blob/main/src/lib/types/image.ts), and **`openai.ts`** as the single choke point for parameters. The rest is mostly there to give those pieces a home that doesn't look half broken.

The practical lesson: **if you ship a client-only tool that lasts, version your data and own your pricing copy** as seriously as you own the visuals. Everything else you can replay from [`c17314d`](https://github.com/Ayfri/GPT-Images/commit/c17314d) to [today's commits](https://github.com/Ayfri/GPT-Images/commits/main), one step at a time.
