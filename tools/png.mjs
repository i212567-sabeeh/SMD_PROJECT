// Side-by-side and crop for the visual check, with no dependencies (Node's zlib only).
// Usage (from LeagueX):
//   node tools/png.mjs vs <section slug>                    every frame: shots/<slug>/<id>-vs.png, Figma left, Android right
//   node tools/png.mjs sbs <out.png> <left.png> <right.png>  any two images side by side, tops aligned
//   node tools/png.mjs crop <in.png> <x> <y> <w> <h> <out.png>
// Reads 8-bit non-interlaced PNGs (greyscale, RGB, RGBA, grey+alpha), which is what the Figma export and
// Android's Bitmap.compress write.
import { readFileSync, writeFileSync, existsSync } from 'node:fs';
import { dirname, join } from 'node:path';
import { fileURLToPath } from 'node:url';
import { inflateSync, deflateSync, crc32 } from 'node:zlib';

const PROJECT = join(dirname(fileURLToPath(import.meta.url)), '..');
const FIGMA = join(PROJECT, '..', 'design', 'screens', 'shots');
const GAP = 12;
const GAP_COLOUR = [0xb8, 0xbf, 0xd3, 0xff];

function decode(file) {
  const b = readFileSync(file);
  let at = 8, width = 0, height = 0, depth = 0, type = 0, interlace = 0;
  const idat = [];
  while (at < b.length) {
    const len = b.readUInt32BE(at);
    const name = b.toString('latin1', at + 4, at + 8);
    const data = b.subarray(at + 8, at + 8 + len);
    if (name === 'IHDR') {
      width = data.readUInt32BE(0); height = data.readUInt32BE(4);
      depth = data[8]; type = data[9]; interlace = data[12];
    } else if (name === 'IDAT') idat.push(data);
    else if (name === 'IEND') break;
    at += 12 + len;
  }
  const channels = { 0: 1, 2: 3, 4: 2, 6: 4 }[type];
  if (depth !== 8 || interlace !== 0 || !channels) throw new Error(`${file}: only 8-bit non-interlaced grey/RGB/RGBA PNGs`);
  const raw = inflateSync(Buffer.concat(idat));
  const stride = width * channels;
  const px = Buffer.alloc(stride * height);
  for (let y = 0; y < height; y++) {
    const filter = raw[y * (stride + 1)];
    const src = y * (stride + 1) + 1;
    const row = y * stride;
    for (let x = 0; x < stride; x++) {
      const a = x >= channels ? px[row + x - channels] : 0;
      const up = y > 0 ? px[row - stride + x] : 0;
      const c = x >= channels && y > 0 ? px[row - stride + x - channels] : 0;
      let v = raw[src + x];
      if (filter === 1) v += a;
      else if (filter === 2) v += up;
      else if (filter === 3) v += (a + up) >> 1;
      else if (filter === 4) {
        const p = a + up - c, pa = Math.abs(p - a), pb = Math.abs(p - up), pc = Math.abs(p - c);
        v += pa <= pb && pa <= pc ? a : pb <= pc ? up : c;
      }
      px[row + x] = v & 0xff;
    }
  }
  const rgba = Buffer.alloc(width * height * 4);
  for (let i = 0; i < width * height; i++) {
    const s = i * channels, d = i * 4;
    if (channels >= 3) { rgba[d] = px[s]; rgba[d + 1] = px[s + 1]; rgba[d + 2] = px[s + 2]; }
    else { rgba[d] = rgba[d + 1] = rgba[d + 2] = px[s]; }
    rgba[d + 3] = channels === 4 ? px[s + 3] : channels === 2 ? px[s + 1] : 0xff;
  }
  return { width, height, rgba };
}

function encode(img, file) {
  const { width, height, rgba } = img;
  const raw = Buffer.alloc((width * 4 + 1) * height);
  for (let y = 0; y < height; y++) rgba.copy(raw, y * (width * 4 + 1) + 1, y * width * 4, (y + 1) * width * 4);
  const chunk = (name, data) => {
    const head = Buffer.alloc(8);
    head.writeUInt32BE(data.length, 0);
    head.write(name, 4, 'latin1');
    const crc = Buffer.alloc(4);
    crc.writeUInt32BE(crc32(Buffer.concat([head.subarray(4), data])) >>> 0, 0);
    return Buffer.concat([head, data, crc]);
  };
  const ihdr = Buffer.alloc(13);
  ihdr.writeUInt32BE(width, 0); ihdr.writeUInt32BE(height, 4);
  ihdr[8] = 8; ihdr[9] = 6;
  writeFileSync(file, Buffer.concat([
    Buffer.from([0x89, 0x50, 0x4e, 0x47, 0x0d, 0x0a, 0x1a, 0x0a]),
    chunk('IHDR', ihdr), chunk('IDAT', deflateSync(raw)), chunk('IEND', Buffer.alloc(0)),
  ]));
}

function blank(width, height, colour) {
  const rgba = Buffer.alloc(width * height * 4);
  for (let i = 0; i < width * height; i++) rgba.set(colour, i * 4);
  return { width, height, rgba };
}

function paste(dst, src, ox, oy) {
  for (let y = 0; y < src.height && oy + y < dst.height; y++) {
    const from = y * src.width * 4;
    src.rgba.copy(dst.rgba, ((oy + y) * dst.width + ox) * 4, from, from + Math.min(src.width, dst.width - ox) * 4);
  }
}

function sideBySide(left, right) {
  const out = blank(left.width + GAP + right.width, Math.max(left.height, right.height), GAP_COLOUR);
  paste(out, left, 0, 0);
  paste(out, right, left.width + GAP, 0);
  return out;
}

function crop(img, x, y, w, h) {
  w = Math.min(w, img.width - x); h = Math.min(h, img.height - y);
  const out = blank(w, h, [0, 0, 0, 0]);
  for (let r = 0; r < h; r++) img.rgba.copy(out.rgba, r * w * 4, ((y + r) * img.width + x) * 4, ((y + r) * img.width + x + w) * 4);
  return out;
}

const [cmd, ...args] = process.argv.slice(2);
if (cmd === 'vs') {
  const slug = args[0];
  const manifest = JSON.parse(readFileSync(join(PROJECT, 'app', 'src', 'test', 'resources', 'figma-frames.json'), 'utf8'));
  const section = manifest.sections.find(s => s.slug === slug);
  if (!section) { console.error(`No section "${slug}"`); process.exit(1); }
  const done = [], missing = [];
  for (const f of section.screens) {
    const android = join(PROJECT, 'shots', slug, `${f.id}.png`);
    const figma = join(FIGMA, `${f.id}.png`);
    if (!existsSync(android) || !existsSync(figma)) { missing.push(f.id); continue; }
    encode(sideBySide(decode(figma), decode(android)), join(PROJECT, 'shots', slug, `${f.id}-vs.png`));
    done.push(f.id);
  }
  console.log(`${done.length} side-by-side image(s) in shots/${slug}${missing.length ? `; missing ${missing.join(', ')}` : ''}`);
} else if (cmd === 'sbs') {
  encode(sideBySide(decode(args[1]), decode(args[2])), args[0]);
} else if (cmd === 'crop') {
  const [file, x, y, w, h, out] = args;
  encode(crop(decode(file), +x, +y, +w, +h), out);
} else {
  console.error('Usage: node tools/png.mjs vs <slug> | sbs <out> <left> <right> | crop <in> <x> <y> <w> <h> <out>');
  process.exit(1);
}
