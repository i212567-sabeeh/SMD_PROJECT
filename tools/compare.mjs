// Writes shots/<slug>/compare.html: every Figma frame of a section next to its Android screenshot (from
// FrameShotsTest), both 412px wide, under the frame's name. With `kit` it puts the Figma's style guide next to the
// Style kit screen, both at 1px = 1dp.
// Usage (from LeagueX): node tools/compare.mjs <section slug|kit>
// Open it through the comparison server (http://127.0.0.1:8766/LeagueX/shots/<slug>/compare.html) so both folders load.
import { existsSync, mkdirSync, readFileSync, writeFileSync } from 'node:fs';
import { dirname, join } from 'node:path';
import { fileURLToPath } from 'node:url';

const PROJECT = join(dirname(fileURLToPath(import.meta.url)), '..');
const FIGMA = '../../../design/screens/shots';

const slug = process.argv[2];
if (!slug) {
  console.error('Usage: node tools/compare.mjs <section slug|kit>');
  process.exit(1);
}

const esc = s => String(s).replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;').replace(/"/g, '&quot;');
const out = join(PROJECT, 'shots', slug);
mkdirSync(out, { recursive: true });

let title;
let pairs;
if (slug === 'kit') {
  title = 'Style kit';
  pairs = [{ name: 'Style guide (Figma) and Style kit (Android), 1px = 1dp', figma: `${FIGMA}/style-guide.png`, android: 'style-kit.png', width: null }];
} else {
  const manifest = JSON.parse(readFileSync(join(PROJECT, 'app', 'src', 'test', 'resources', 'figma-frames.json'), 'utf8'));
  const section = manifest.sections.find(s => s.slug === slug);
  if (!section) {
    console.error(`No section "${slug}". Sections: ${manifest.sections.map(s => s.slug).join(', ')}, or kit`);
    process.exit(1);
  }
  title = section.title;
  pairs = section.screens.map(f => ({ name: f.name, figma: `${FIGMA}/${f.id}.png`, android: `${f.id}.png`, width: 412 }));
}

const missing = pairs.filter(p => !existsSync(join(out, p.android))).map(p => p.android);
const img = (src, width) => `<img src="${esc(src)}"${width ? ` width="${width}"` : ''} alt="">`;
const figures = pairs.map(p => `<section>
  <h2>${esc(p.name)}</h2>
  <div class="pair">
    <figure><figcaption>Figma</figcaption>${img(p.figma, p.width)}</figure>
    <figure><figcaption>Android</figcaption>${existsSync(join(out, p.android)) ? img(p.android, p.width) : '<p class="none">No screenshot yet</p>'}</figure>
  </div>
</section>`).join('\n');

writeFileSync(join(out, 'compare.html'), `<!doctype html>
<html lang="en">
<head>
<meta charset="utf-8">
<title>${esc(title)}: Figma and Android</title>
<style>
  body { margin: 24px; font: 14px/20px system-ui, sans-serif; color: #0F1533; background: #DADFEA; }
  h1 { font-size: 20px; margin: 0 0 16px; }
  h2 { font-size: 15px; margin: 24px 0 8px; }
  .pair { display: flex; gap: 24px; align-items: flex-start; }
  figure { margin: 0; }
  figcaption { font-size: 12px; color: #5A6280; margin-bottom: 4px; }
  img { display: block; background: #fff; box-shadow: 0 0 0 1px #B8BFD3; }
  .none { width: 412px; color: #C62828; }
</style>
</head>
<body>
<h1>${esc(title)}: Figma and Android</h1>
${figures}
</body>
</html>
`);

console.log(`${join(out, 'compare.html')}: ${pairs.length} pair(s)${missing.length ? `; no Android shot yet for ${missing.join(', ')}` : ''}`);
