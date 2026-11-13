from PIL import Image,ImageDraw,ImageFont
import numpy as np
P='/tmp/VB_preTP.jpg'
FD='/usr/share/fonts/type1/texlive-fonts-recommended/'
SZ=18.3
F={k:ImageFont.truetype(FD+f,SZ) for k,f in dict(r='bchr8a.pfb',b='bchb8a.pfb',i='bchri8a.pfb').items()}
COL=(50,50,50); LH=24
segs=[('Only','r'),('one','b'),('banner per turn, and only one Command card is drawn, however many','r'),('Scout','i'),('cards are played. The conditions apply to','r'),('every','b'),('Scout','i'),('card played that turn: none of their Corps Command Stars may be used.','r')]
x0,maxx=663,1206; lines=[[]]; x=x0; sp=F['r'].getlength(' ')
for t,st in segs:
    for j,w in enumerate(t.split(' ')):
        if not w: continue
        wl=F[st].getlength(w)
        if j==0 and w[0] in '.,;:': x-=sp
        if x>x0 and x+wl>maxx: lines.append([]); x=x0
        lines[-1].append((x,w,st)); x+=wl+sp
n=len(lines); A=np.array(Image.open(P).convert('RGB'))
ins=1458; hgt=n*LH+4
B=np.concatenate([A[:ins],np.repeat(A[ins:ins+1],hgt,axis=0),A[ins:]]); im=Image.fromarray(B); d=ImageDraw.Draw(im)
for k,ln in enumerate(lines):
    for x,w,st in ln: d.text((x,ins+2+k*LH-5),w,font=F[st],fill=COL)
im.save('VictoryBanner_tp.jpg',quality=93); im.crop((40,1180,1240,1600)).save('tp_prev.png'); print(n,im.size)
