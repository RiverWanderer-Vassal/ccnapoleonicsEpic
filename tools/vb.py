from PIL import Image,ImageDraw,ImageFont
import numpy as np
P='/home/claude/ccnapoleonicsepic/dist/images/VictoryBanner.jpg'
FD='/usr/share/fonts/type1/texlive-fonts-recommended/'
SZ=18.5
F={k:ImageFont.truetype(FD+f,SZ) for k,f in dict(r='bchr8a.pfb',b='bchb8a.pfb',i='bchri8a.pfb').items()}
COL=(45,40,34); LH=26
segs=[('Re-occupied the same turn:','b'),('the rulebook prints the','r'),('not lost','i'),('allowance only for a single Temporary Objective Hex. By implication it applies equally to each hex of a Temporary Group or Majority objective: a hex vacated by an ordered unit and entered by another ordered unit of the same side in the','r'),('same turn','b'),('counts as still occupied.','r')]
# layout
x0,maxx=68,1206; lines=[[]]; x=x0; sp=F['r'].getlength(' ')
for t,st in segs:
    for j,w in enumerate(t.split(' ')):
        if not w: continue
        wl=F[st].getlength(w)
        if j==0 and w[0] in '.,;:': x-=sp
        if x>x0 and x+wl>maxx: lines.append([]); x=x0
        lines[-1].append((x,w,st)); x+=wl+sp
n=len(lines); print('lines',n)
src=Image.open(P).convert('RGB'); A=np.array(src)
ins=1078; strip=np.repeat(A[ins:ins+1],n*LH,axis=0)
B=np.concatenate([A[:ins],strip,A[ins:]]); im=Image.fromarray(B); d=ImageDraw.Draw(im)
for k,ln in enumerate(lines):
    y=1082+k*LH-5   # first new line takes position of old line 3
    for x,w,st in ln: d.text((x,y),w,font=F[st],fill=COL)
im.save('VictoryBanner_new.jpg',quality=93)
im.crop((40,990,1240,1240+n*LH-26)).save('vb_prev.png')
print(im.size)
