from PIL import Image,ImageDraw,ImageFont
import numpy as np
SRC='/home/claude/ccnapoleonicsepic/dist/images/SpecialRules.jpg'
FD='/usr/share/fonts/type1/texlive-fonts-recommended/'
SZ=18.8
FR=ImageFont.truetype(FD+'bchr8a.pfb',SZ); FB=ImageFont.truetype(FD+'bchb8a.pfb',SZ)
COL=(40,40,40)
def rich(img,x,y,maxw,segs,lh=24.4):
    # segs: list of (text,bold); word-wrap
    d=ImageDraw.Draw(img); words=[]
    for t,b in segs:
        for i,w in enumerate(t.split(' ')):
            if w=='' : continue
            words.append((w,b))
    cx=x; cy=y; sp=FR.getlength(' ')
    for w,b in words:
        f=FB if b else FR; wl=f.getlength(w)
        if cx>x and cx+wl>x+maxw: cx=x; cy+=lh
        d.text((cx,cy),w,font=f,fill=COL); cx+=wl+sp
    return cy
HUNG=[("A Hungarian unit is an Austrian Line Infantry unit named by the scenario. It deploys with",0),("5 blocks",1),("and a +1 marker (kept until the last block is lost), and battles with",0),("1 extra die",1),("in all ranged and melee combat.",0)]
def hung(img):
    img.paste((255,255,255),(652,1352,1206,1462))
    # baseline match: original first row ink top 1357 ; Charter bbox top offset ~6 at this size
    rich(img,663,1357-5,525,HUNG)
src=Image.open(SRC).convert('RGB')
# Epic version: Hungarian generic
ep=src.copy(); hung(ep); ep.save('SpecialRules_epic.jpg',quality=93)
# Standard version
st=src.copy(); hung(st)
d=ImageDraw.Draw(st); d.rectangle((301,25,760,78),fill=(255,255,255))
A=np.array(st)
parts=[A[0:370],A[538:925],A[949:1469],A[1931:]]
B=np.concatenate(parts); im=Image.fromarray(B)
# footer rewrite
fy=B.shape[0]-(2019-1950)
d=ImageDraw.Draw(im); im.paste((255,255,255),(45,fy,1240,fy+45))
FS=ImageFont.truetype(FD+'bchr8a.pfb',14.5); FI=ImageFont.truetype(FD+'bchri8a.pfb',14.5); g=(110,110,110)
x=50;y=fy+4
for t,f in (("Sources: ",FS),("Grand Battles",FI),(" rules (GMT 2025); previous module chart (Mother Russia, Battalion Mass, Iron Will, Conscripts).",FS)):
    d.text((x,y),t,font=f,fill=g); x+=f.getlength(t)
t="Standard modules"; d.text((1222-FS.getlength(t),y),t,font=FS,fill=g)
im.save('SpecialRules_std.jpg',quality=93)
print(im.size)
