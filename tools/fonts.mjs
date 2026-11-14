// Downloads the five static TTFs and the OFL licences. Re-runnable.
// Usage (from LeagueX): node tools/fonts.mjs
import { mkdirSync, writeFileSync } from 'node:fs';
import { dirname, join } from 'node:path';
import { fileURLToPath } from 'node:url';

const PROJECT = join(dirname(fileURLToPath(import.meta.url)), '..');
const FONTS = join(PROJECT, 'app', 'src', 'main', 'res', 'font');
const LICENSES = join(PROJECT, 'licenses');
mkdirSync(FONTS, { recursive: true });
mkdirSync(LICENSES, { recursive: true });

const get = async (url, ua) => {
  const res = await fetch(url, ua ? { headers: { 'User-Agent': ua } } : undefined);
  if (!res.ok) throw new Error(`${url}: HTTP ${res.status}`);
  return res;
};

const css = await (await get('https://fonts.googleapis.com/css2?family=Anton&family=Archivo:wght@400;500;600;700', 'node')).text();
const faces = [...css.matchAll(/@font-face\s*\{([^}]*)\}/g)].map(m => m[1]).map(b => ({
  family: /font-family:\s*'([^']+)'/.exec(b)?.[1],
  weight: /font-weight:\s*(\d+)/.exec(b)?.[1],
  url: /url\(([^)]+)\)\s*format\('truetype'\)/.exec(b)?.[1],
}));
const wanted = [['Anton', '400', 'anton_regular'], ['Archivo', '400', 'archivo_regular'], ['Archivo', '500', 'archivo_medium'],
  ['Archivo', '600', 'archivo_semibold'], ['Archivo', '700', 'archivo_bold']];
if (!faces.some(f => f.url)) throw new Error('Google Fonts returned no format(\'truetype\') URLs:\n' + css.slice(0, 600));

for (const [family, weight, file] of wanted) {
  const face = faces.find(f => f.family === family && f.weight === weight && f.url);
  if (!face) throw new Error(`no truetype face for ${family} ${weight}`);
  writeFileSync(join(FONTS, `${file}.ttf`), Buffer.from(await (await get(face.url)).arrayBuffer()));
  console.log(`${file}.ttf`);
}
for (const [dir, file] of [['anton', 'OFL-Anton.txt'], ['archivo', 'OFL-Archivo.txt']]) {
  writeFileSync(join(LICENSES, file), await (await get(`https://raw.githubusercontent.com/google/fonts/main/ofl/${dir}/OFL.txt`)).text());
}
console.log('5 fonts, 2 licences');
