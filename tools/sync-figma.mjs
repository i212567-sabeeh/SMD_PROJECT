// Copies what the tests compare against from the HTML build into app/src/test/resources:
//   figma-frames.json  the frame manifest (design/screens/dist/manifest.json, made by `node build.js`)
//   icons.txt          the icon names tools/icons.mjs collected (read by FoundationsTest)
//   figma-tokens.json  { "<android colour name>": "#AARRGGBB" } for every --color-* and --team-* token in kit.css :root
// Usage (from LeagueX): node tools/sync-figma.mjs
import { copyFileSync, existsSync, mkdirSync, readFileSync, writeFileSync } from 'node:fs';
import { dirname, join } from 'node:path';
import { fileURLToPath } from 'node:url';

const PROJECT = join(dirname(fileURLToPath(import.meta.url)), '..');
const SCREENS = join(PROJECT, '..', 'design', 'screens');
const OUT = join(PROJECT, 'app', 'src', 'test', 'resources');

mkdirSync(OUT, { recursive: true });

const manifestPath = join(SCREENS, 'dist', 'manifest.json');
copyFileSync(manifestPath, join(OUT, 'figma-frames.json'));
const manifest = JSON.parse(readFileSync(manifestPath, 'utf8'));
const frames = manifest.sections.reduce((n, s) => n + s.screens.length, 0);

// --color-on-blue-muted -> on_blue_muted, --team-mar -> team_mar
const androidName = token => token.replace(/^color-/, '').replace(/-/g, '_');

const hex2 = n => Math.round(n).toString(16).padStart(2, '0').toUpperCase();
function argb(value) {
  const v = value.trim();
  let m = /^#([0-9a-f]{6})$/i.exec(v);
  if (m) return `#FF${m[1].toUpperCase()}`;
  m = /^rgba?\(\s*(\d+)\s*,\s*(\d+)\s*,\s*(\d+)\s*(?:,\s*([\d.]+)\s*)?\)$/i.exec(v);
  if (m) {
    const alpha = m[4] === undefined ? 1 : Number(m[4]);
    return `#${hex2(alpha * 255)}${hex2(m[1])}${hex2(m[2])}${hex2(m[3])}`;
  }
  throw new Error(`Unsupported colour value: ${value}`);
}

const iconsPath = join(PROJECT, 'tools', 'icons.txt');
if (existsSync(iconsPath)) copyFileSync(iconsPath, join(OUT, 'icons.txt'));

const css = readFileSync(join(SCREENS, 'src', 'kit.css'), 'utf8');
const root = /:root\s*\{([^}]*)\}/.exec(css);
if (!root) throw new Error('kit.css has no :root block');
const tokens = {};
for (const [, token, value] of root[1].matchAll(/--((?:color|team)-[a-z0-9-]+)\s*:\s*([^;]+);/g)) {
  tokens[androidName(token)] = argb(value);
}
writeFileSync(join(OUT, 'figma-tokens.json'), JSON.stringify(tokens, null, 1) + '\n');

console.log(`figma-frames.json: ${frames} frames in ${manifest.sections.length} sections; figma-tokens.json: ${Object.keys(tokens).length} colours`);
