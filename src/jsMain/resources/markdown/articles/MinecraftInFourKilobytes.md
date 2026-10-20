---
nav-title: 4k-minecraft Packing
title: "4k-minecraft: A Deflate Stream Without 10 Bytes"
description: How 4k-minecraft, a WebGPU Minecraft in one HTML file of 4096 bytes, carries its own code, and why it only fits thanks to a deflate encoder that never writes the 10 bytes the HTML parser would break.
keywords: 4k-minecraft, minecraft 4k, size coding, code golf, deflate, raw deflate, DecompressionStream, huffman coding, html packing, self-extracting html, iso-8859-15, webgpu, javascript golf, demoscene, 4kb game
date-created: 2026-10-20
date-modified: 2026-10-20
root: .layouts.ArticleLayout
routeOverride: /articles/minecraft-in-four-kilobytes/index
---

[4k-minecraft](https://4k-minecraft.ayfri.com) is Minecraft in one HTML file of at most 4096 bytes. You open it in Chrome, click, and you walk around a 1024x1024x128 world with lakes, a snowy massif, caves, trees and flowers, a day and night cycle, and blocks you can break and place. The source is on GitHub: [Ayfri/4k-minecraft](https://github.com/Ayfri/4k-minecraft).

![4k-minecraft at noon near the spawn, grass terraces with flowers and trees below a snowy mountain](/images/4k-minecraft/hero.avif)

It is a remake of [Minecraft 4k](https://minecraft.wiki/w/Minecraft_4k), the entry Notch made for the Java 4K contest in December 2009. His was 2581 bytes: a 64x64x63 world drawn by a CPU raycaster at about 214x120 pixels, and grass as the only block to place. Mine moves everything to the GPU. One WebGPU compute shader generates the world at load, then marches a ray through the voxels for every pixel and writes it straight into the canvas. Most of it got written in two days, 136 commits over four.

The rendering is the fun part to look at, but it is not what this article is about. This one is about the 202 bytes that wrap the game, and one number: with any normal compressor, the page is **4171 bytes**. That is 75 bytes over the limit. The page that ships is 4085 bytes, and the difference comes from 10 byte values my encoder refuses to write.

## The page is a decompressor with a payload

The minified game script is 8078 bytes. That never fits as is, so the page compresses it and carries it inside itself. Browsers ship [`DecompressionStream`](https://developer.mozilla.org/en-US/docs/Web/API/DecompressionStream), so I don't need to bring a decompressor: the page is a tiny bootstrap followed by a raw deflate stream.

![The 4096 byte budget split into 202 bytes of page and decoder, a 3883 byte deflate stream and 11 free bytes, with the page source colour coded above the first bytes of the stream](/images/4k-minecraft/page-anatomy.svg)

Here is the whole thing, minus the stream:

```html
<meta charset=l9><body onload='new Response(new Response(Uint8Array.from(c.textContent,c=>c.charCodeAt())).body.pipeThrough(new DecompressionStream("deflate-raw"))).text().then(eval)'><canvas id=c><xmp>
```

- `<meta charset=l9>` tells the browser to read the file as ISO-8859-15, one byte per character. More on why that one below.
- `<body onload>` runs after parsing, so the stream is already in the DOM when the decoder starts. No `<script>` tag, no closing tag.
- `<canvas id=c>` is the game canvas. An element with an `id` becomes a global, so the decoder and the game both just say `c`.
- `<xmp>` is an obsolete tag from the early web that the parser still treats as raw text: no entities, no tags, everything up to `</xmp>`. There is no `</xmp>`, so it runs to the end of the file. It sits inside the canvas, whose content never renders, so you never see the garbage.

The decoder turns each character back into its byte, wraps the bytes in a `Response` to get a stream, inflates it, reads it as text and passes it to `eval`. Raw deflate (`deflate-raw`) means no zlib header and no checksum, 6 bytes saved for free.

The [about page](https://4k-minecraft.ayfri.com/about) fetches the game and paints its bytes as a 64x64 grid, one pixel per byte. Since 4096 is 64 squared, the page fills a block, so of course I made it a grass block: stone for the bootstrap, grass then dirt for the stream, and the 11 free bytes in cyan at the bottom right.

![The 4085 bytes of the page drawn as a 64x64 grass block: a grey stone strip for the decoder, then grass and dirt for the deflate stream, and a short cyan line of free bytes at the end](/images/4k-minecraft/page-map.webp)

## Ten bytes the browser won't give back

This only works if `c.textContent` hands back exactly the bytes I wrote. It doesn't, for 10 of the 256 values.

The HTML parser normalizes newlines before anything else: a CR (`0x0D`) becomes a LF, alone or in a CRLF. A NUL (`0x00`) becomes U+FFFD. Those two are lost whatever the charset.

Then the charset itself. A single-byte charset where byte `n` reads as character `n` would be perfect, and you would think `latin1` is that. It isn't: in browsers, `latin1` and `l1` are labels for windows-1252, which puts curly quotes, the euro sign and friends in `0x80`-`0x9F`. I measured how many byte values come back changed in Chrome for every single-byte label I could find:

| Label | Bytes that don't round-trip (NUL included) |
|---|---|
| `l9` (ISO-8859-15) | 9 |
| `l1` (windows-1252) | 28 |
| ISO-8859-14, windows-1254 | 32 |
| ISO-8859-3 | 36 |
| windows-1258 | 38 |
| ISO-8859-16 | 41 |
| the others | 47 to 129 |

ISO-8859-15 is Latin-1 with 8 characters swapped for €, Š, š, Ž, ž, Œ, œ and Ÿ, at `A4 A6 A8 B4 B8 BC BD BE`. Everything else, including `0x80`-`0x9F`, comes back as is. And yes, I tried dropping the meta: on `file://`, Chrome sniffs the encoding from the content and picked IBM866 for a file with all 256 bytes and `macintosh` for random bytes. `x-user-defined`, the label that maps bytes one to one, turns into windows-1252 when you put it in a meta. So `l9` it is.

That leaves 10 forbidden values: `00`, `0D`, and the 8 bytes ISO-8859-15 remaps. It doesn't sound like much until you remember what a deflate stream looks like. It's dense, close to random, so about 10 bytes in 256 are bad. Here is libdeflate's stream of the game next to the one that ships, 3862 and 3883 bytes on a 64x64 grid, bad bytes in pink:

![Two 64x64 byte maps: libdeflate's stream has 148 forbidden bytes scattered everywhere, the shipped stream has none](/images/4k-minecraft/stream-compare.webp)

148 bad bytes for libdeflate, 159 for zlib. Every one of them would come out of `textContent` wrong and break the inflate.

One more rule: the stream must not contain `</xmp` followed by whitespace, a `/` or a `>`, or it closes its own container. That one is rare enough that I just check it and reject the stream.

## The obvious fix: escaping

The first versions escaped the bad bytes. CR becomes `E1`, the escape byte `E` itself becomes `E0`, and `E` is whatever safe byte appears the least in the stream. NUL and the 8 remapped characters are looked up in a 28-character string that spans `NUL` then `¤` to `¾`, so the index gives the byte back. The decoder got this extra work:

```js
Uint8Array.from(
	c.textContent.replace(/E./g, m => "E\r"[m[1]]),
	c => (i = "\0¤¥¦§¨...¾".indexOf(c)) < 0 ? c.charCodeAt() : i && i + 163
)
```

It works, and the page started like that, with the stream in an HTML comment read as `c.firstChild.data`. Moving it to `<xmp>` and the decoder to `onload` saved [7 bytes](https://github.com/Ayfri/4k-minecraft/commit/b4db047), switching from `l1` to `l9` with its shorter table saved [9 more](https://github.com/Ayfri/4k-minecraft/commit/d1e5292).

But the escaped head is 289 bytes against 202 now, and every CR in the stream costs one more byte on top. On today's game, I ran every encoder the build knows through that escaping:

![Page size per encoder against the 4096 byte limit: zlib level 9 is 4245 bytes, zopfli and libdeflate are 4171 to 4174 bytes, all escaped and over the limit, the search stream is 4085 bytes and fits](/images/4k-minecraft/over-the-limit.svg)

zopfli at 1000 iterations doesn't help, it lands on the same 4171 bytes as libdeflate. Without the search below, the game as it is today doesn't ship: I would have to cut 75 bytes of features to get it under the limit.

## Writing deflate that never writes them

You can't filter bad bytes out of a deflate stream after the fact, because deflate doesn't work in bytes. It's a stream of bits: Huffman codes from 1 to 15 bits, extra bits for lengths and distances, packed LSB first one after the other. A byte of output is the end of one code and the start of the next. Same text, two codes for the same symbols:

![Two encodings of the same six symbols: in code A the last bits of r and the first bits of the opening parenthesis form the byte 0x0D, in code B the o code is one bit longer, every following bit moves and no byte is forbidden](/images/4k-minecraft/bits.svg)

But one input has a huge number of valid deflate streams. The parse can pick a literal or a match, and which match distance. The Huffman code lengths can change. The block header, which is itself Huffman coded with run-length codes, can be written several ways. Even the padding bits of the last byte are free. Any of those choices moves every bit that follows, so the question becomes: among all the streams that inflate to my script, which is the smallest one with none of the 10 bytes?

So I wrote my own raw deflate encoder for that, [`scripts/lib/deflate.ts`](https://github.com/Ayfri/4k-minecraft/blob/main/scripts/lib/deflate.ts). It's a shortest path search over a graph:

- A node is a position in the input plus the bit phase: how many bits of the current byte are already written, and their values.
- An edge appends a literal or a match. When it completes a byte, the byte is checked, and a forbidden one kills the edge.
- The header gets the same treatment, including the choice of the run-length tokens 16, 17 and 18 for the code lengths. It sets the bit phase the data starts at.
- To keep it fast, a node only keeps the bit phases within 12 bits of its best one, and 4 distances per match length.

The function that appends bits is where a byte gets rejected:

```ts
/** Appends `count` bits to the bit phase `state` (`1 << k | partial byte`), -1 when a finished byte is forbidden. */
private append(state: number, bits: number, count: number) {
	let k = 31 - Math.clz32(state);
	let p = state ^ (1 << k);
	while (count) {
		const take = Math.min(count, 8 - k);
		p |= (bits & ((1 << take) - 1)) << k;
		bits >>>= take;
		count -= take;
		k += take;
		if (k === 8) {
			if (this.bad[p]) return -1;
			p = k = 0;
		}
	}
	return (1 << k) | p;
}
```

Around it, the search runs optimal parses under a Huffman code, rebuilds the code from what the parse used, and smooths the symbol counts so the header codes them cheaply, until the exact header plus data size stops falling. The build runs 12 seeds in parallel in a worker pool and keeps the smallest stream.

Unconstrained, the encoder made 3759 bytes on the build of that day, against 3760 for zopfli and 3757 for [ECT](https://github.com/fhanau/Efficient-Compression-Tool). With the 10 bytes forbidden it costs about 25 bytes more than unconstrained, roughly 2.6 bytes per extra value (just CR and NUL cost 4). Today's stream is 3883 bytes, 21 more than libdeflate. In exchange, the decoder is back to `Uint8Array.from(c.textContent,c=>c.charCodeAt())`, with no regex and no table. That commit took [83 bytes](https://github.com/Ayfri/4k-minecraft/commit/81e01d8) off the page, the biggest single win of the whole packer.

## The first bytes have no way around

Then the search started failing on some builds, and the page silently fell back to the escaped version. The one that caught me was the mouse wheel block cycling: a shorter form of the same JavaScript came out at 3961 bytes, where the slightly longer form I kept is 3832, because no seed found a valid stream for it.

The reason is the start of the input. A match needs 3 bytes seen earlier, so until the first repeat, the only way to encode the script is literal after literal. There is exactly one path, and the bits on it are fixed by the code. If any byte it makes is forbidden, every stream under that code is dead, whatever the search does after.

Today the first possible match is at byte 67 of `(async()=>{var L=[-1.7,-.02],t={};c.style="position:fixed;inset:0";var b=awai...`. As a rough model, each finished byte survives with a chance of 246 in 256, so a code gets through those 67 bytes about 7% of the time, 1 code in 14. An older build had about 140 bytes before its first match, which is 1 code in 250.

So the encoder now tries other codes when the best one dies:

- 8 variants of the header, swapping two code lengths of the code-length code.
- Up to 300 swaps of two literal code lengths, cheapest first. Each one is first probed on the start of the stream only (the literal run plus 256 bytes), and only the ones that get through run in full.
- If nothing works, a `;`, a space or a newline at the end of the script, which changes the stream without changing the program.

I tested it on 200 near copies of the game, each one edit away. Failures went from 117 to 0. And for input where no code can make it, like 2 KB of printable noise, it gives up right away:

```ts
/** A code gets through the literals before the first match with chance `(1 - avoid / 256) ** lead` and `search` tries `SWAPS + 1` codes: below 0.1 expected streams it is hopeless (2 KB of printable noise), and no suffix changes that. */
export const hopeless = (data: Uint8Array, avoid: number[]) => (1 - avoid.length / 256) ** leadOf(data) * (SWAPS + 1) < 0.1;
```

The escaped encoders still run next to it, and the build keeps whichever page is smallest. On today's build the search wins by 86 bytes.

## What didn't make it

I keep a notes file with every lead and its measured result, so I never retry a dead end. The packing ones:

| Lead | What happened |
|---|---|
| `DecompressionStream("brotli")` or `"zstd"` | The constructor throws in Chrome 155, even with experimental features on. Only `gzip`, `deflate` and `deflate-raw` exist |
| `fetch(location)` to read the raw bytes | No escapes at all, but `fetch` of a `file://` URL is blocked in Chrome |
| A PNG holding the code, read with `getImageData` | The canvas is tainted on `file://`, and the PNG container costs bytes on top of the same deflate |
| [Roadroller](https://github.com/lifthrasiir/roadroller) | Great for js13k, about 100 bytes worse here: its data plus its decoder deflate to 3870 bytes against 3763 |
| ECT through js13k-zip | 8 bytes better than libdeflate on the escaped page, beaten once the search landed |
| `<svg onload>` for the decoder | Fires before the data after it is parsed |
| The data in an attribute, or as text in the canvas | Quotes, `<` and `&` need escaping |
| `gzip` or `deflate` formats | Headers and checksums for nothing |

## Where it is now

The page is 4085 bytes, with 11 bytes free. The packer is only part of the size work: the minifier that picks variable names by deflate size and the WGSL renaming annealed against the whole script saved more, together. That one deserves its own article.

The stream sitting in that `<xmp>` is a weird thing when you think about it. It's a valid deflate stream, close to the smallest one the encoder can find, and it also happens to never contain a CR, a NUL or a €. The browser reads it as text, gives it back byte for byte, and inflates it into Minecraft.

It runs on Chrome 150 and later on Windows and macOS (the shader uses WebGPU immediates, which Firefox and Safari don't have yet). [Play it here](https://4k-minecraft.ayfri.com), check the [about page](https://4k-minecraft.ayfri.com/about) for the live byte map, and the [repo](https://github.com/Ayfri/4k-minecraft) has the whole packer.
