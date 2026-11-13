# Build the release-notes PDF for the VASSAL library page from release-notes/*.md (newest first).
# Usage: python3 tools/release_notes_pdf.py release-notes  &&  google-chrome --headless=new --no-pdf-header-footer --print-to-pdf='CCNEpicv4 Release Notes.pdf' notes.html
# Issue numbers (#NN) link to this repo's issues; each version heading links to its GitHub Release.
import markdown,re,sys
R='https://github.com/RiverWanderer-Vassal/ccnapoleonicsEpic'
L='https://vassalengine.org/library/projects/Commands__Colors_Napoleonics_EPIC__Grand_Battles'
SRC=sys.argv[1] if len(sys.argv)>1 else '/home/claude/ccnapoleonicsepic/release-notes'
import os
vers=sorted([f[:-3] for f in os.listdir(SRC) if f.endswith('.md')],key=lambda v:[int(x) for x in v.split('.')],reverse=True)
parts=[]
for v in vers:
    lines=open(f'{SRC}/{v}.md',encoding='utf-8').read().splitlines()
    body='\n'.join(lines[1:]).strip()
    body=re.sub(r'^## ',r'### ',body,flags=re.M)
    body=re.sub(r'^  - ',r'    - ',body,flags=re.M)
    body=re.sub(r'(?<![\w/\[])#(\d{1,3})\b',lambda m:f'[#{m.group(1)}]({R}/issues/{m.group(1)})',body)
    h=markdown.markdown(body,extensions=['sane_lists'])
    parts.append(f'<section><h2><a href="{R}/releases/tag/v{v}">Version {v}</a></h2>{h}</section>')
doc=f'''<!DOCTYPE html><html><head><meta charset="utf-8"><title>CCN Epic &amp; Grand Battles v4 — Release Notes</title>
<style>
@page {{ size: A4; margin: 16mm 16mm 18mm; }}
body {{ font-family: Georgia,"DejaVu Serif",serif; font-size: 10pt; line-height: 1.4; color:#222; }}
h1 {{ font-size: 18pt; margin: 0 0 4pt; }}
.sub {{ color:#555; margin-bottom: 10pt; }}
h2 {{ font-size: 13.5pt; border-bottom: 1.5px solid #b08a50; padding-bottom: 2pt; margin: 16pt 0 6pt; break-after: avoid; }}
h3 {{ font-size: 11pt; margin: 9pt 0 3pt; break-after: avoid; }}
a {{ color:#1f4e8c; text-decoration: none; }} h2 a {{ color:#222; }}
ul {{ margin: 2pt 0 4pt; padding-left: 16pt; }} li {{ margin: 1pt 0; }} p {{ margin: 3pt 0; }}
</style></head><body>
<h1>Commands &amp; Colors: Napoleonics — Epic &amp; Grand Battles</h1>
<div class="sub">Version 4 release notes · Download: <a href="{L}">VASSAL Module Library</a> · Full history and issues: <a href="{R}/releases">GitHub releases</a></div>
{''.join(parts)}
</body></html>'''
open('notes.html','w',encoding='utf-8').write(doc)
