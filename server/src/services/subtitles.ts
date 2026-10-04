import fs from 'fs';

export interface CueItem {
  startSec: number;
  endSec: number;
  text: string;
}

export function generateAssFile(filePath: string, cues: CueItem[], style: string = 'neon_karaoke'): void {
  const assHeader = `[Script Info]
Title: ShortsForge ASS Subtitles
ScriptType: v4.00+
WrapStyle: 0
PlayResX: 1080
PlayResY: 1920

[V4+ Styles]
Format: Name, Fontname, Fontsize, PrimaryColour, SecondaryColour, OutlineColour, BackColour, Bold, Italic, Underline, StrikeOut, ScaleX, ScaleY, Spacing, Angle, BorderStyle, Outline, Shadow, Alignment, MarginL, MarginR, MarginV, Encoding
Style: Default,Arial,72,&H0000FFFF,&H000000FF,&H00000000,&H80000000,-1,0,0,0,100,100,0,0,1,4,2,2,80,80,340,1

[Events]
Format: Layer, Start, End, Style, Name, MarginL, MarginR, MarginV, Effect, Text
`;

  let events = '';
  for (const cue of cues) {
    const startStr = formatTime(cue.startSec);
    const endStr = formatTime(cue.endSec);
    const safeText = cue.text.replace(/{/g, '\\{').replace(/}/g, '\\}').toUpperCase();
    events += `Dialogue: 0,${startStr},${endStr},Default,,0,0,0,,${safeText}\n`;
  }

  fs.writeFileSync(filePath, assHeader + events, 'utf-8');
}

function formatTime(sec: number): string {
  const totalMs = Math.floor(sec * 1000);
  const h = Math.floor(totalMs / 3600000);
  const m = Math.floor((totalMs % 3600000) / 60000);
  const s = Math.floor((totalMs % 60000) / 1000);
  const cs = Math.floor((totalMs % 1000) / 10);
  return `${h}:${m.toString().padStart(2, '0')}:${s.toString().padStart(2, '0')}.${cs.toString().padStart(2, '0')}`;
}
