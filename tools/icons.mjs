// Collects the icon names used by the HTML screens, fetches Material Symbols Rounded (400, grade 0, 24px)
// and writes res/drawable/ic_<name>.xml (+ ic_<name>_fill.xml for the five bottom-bar icons). Re-runnable.
// Usage (from LeagueX): node tools/icons.mjs
import { execSync } from 'node:child_process';
import { mkdirSync, readdirSync, readFileSync, writeFileSync } from 'node:fs';
import { dirname, join } from 'node:path';
import { fileURLToPath } from 'node:url';

const PROJECT = join(dirname(fileURLToPath(import.meta.url)), '..');
const DIST = join(PROJECT, '..', 'design', 'screens', 'dist');
const DRAWABLE = join(PROJECT, 'app', 'src', 'main', 'res', 'drawable');
const LICENSES = join(PROJECT, 'licenses');
const FILLED = ['home', 'sports_soccer', 'leaderboard', 'groups', 'person'];
mkdirSync(DRAWABLE, { recursive: true });
mkdirSync(LICENSES, { recursive: true });

const names = new Set();
for (const f of readdirSync(DIST).filter(f => f.endsWith('.html'))) {
  const html = readFileSync(join(DIST, f), 'utf8').replace(/<div class="status-bar[^"]*"[^>]*>[\s\S]*?<\/div>/g, '');
  for (const m of html.matchAll(/class="icon[^"]*"[^>]*>([a-z0-9_]+)</g)) names.add(m[1]);
}
const icons = [...names].sort();
writeFileSync(join(PROJECT, 'tools', 'icons.txt'), icons.join('\n') + '\n');
for (const n of FILLED) if (!names.has(n)) throw new Error(`nav icon ${n} is not used by any screen`);

const version = execSync('npm view @material-symbols/svg-400 version', { encoding: 'utf8', shell: true }).trim();
const get = async url => { const r = await fetch(url); if (!r.ok) throw new Error(`${url}: HTTP ${r.status}`); return r.text(); };
async function svg(name, fill) {
  try {
    return await get(`https://cdn.jsdelivr.net/npm/@material-symbols/svg-400@${version}/rounded/${name}${fill ? '-fill' : ''}.svg`);
  } catch {
    return await get(`https://raw.githubusercontent.com/google/material-design-icons/master/symbols/web/${name}/materialsymbolsrounded/${name}_${fill ? 'fill1_' : ''}24px.svg`);
  }
}

function toVector(source, name) {
  const vb = /viewBox="\s*(-?[\d.]+)[ ,]+(-?[\d.]+)[ ,]+([\d.]+)[ ,]+([\d.]+)\s*"/.exec(source);
  if (!vb) throw new Error(`${name}: no viewBox`);
  const [, minX, minY, w, h] = vb;
  const paths = [...source.matchAll(/<path\b[^>]*?\sd="([^"]+)"/g)].map(m => m[1]);
  if (!paths.length) throw new Error(`${name}: no path`);
  const num = v => String(-Number(v) || 0);
  const body = paths.map(d => `        <path\n            android:fillColor="@color/ink"\n            android:pathData="${d}" />`).join('\n');
  return `<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp"
    android:height="24dp"
    android:viewportWidth="${w}"
    android:viewportHeight="${h}">
    <group
        android:translateX="${num(minX)}"
        android:translateY="${num(minY)}">
${body}
    </group>
</vector>
`;
}

let filled = 0;
for (const n of icons) {
  writeFileSync(join(DRAWABLE, `ic_${n}.xml`), toVector(await svg(n, false), n));
  if (FILLED.includes(n)) { writeFileSync(join(DRAWABLE, `ic_${n}_fill.xml`), toVector(await svg(n, true), `${n}_fill`)); filled++; }
}
writeFileSync(join(LICENSES, 'Apache-2.0-Material-Symbols.txt'),
  await get('https://raw.githubusercontent.com/google/material-design-icons/master/LICENSE'));
console.log(`${icons.length} icons, ${filled} filled`);
