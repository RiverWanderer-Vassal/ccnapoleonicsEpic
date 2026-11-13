from PIL import Image,ImageDraw,ImageFont
import numpy as np
P='/home/claude/ccnapoleonicsepic/dist/images/SpecialRules.jpg'
FD='/usr/share/fonts/type1/texlive-fonts-recommended/'
SZ=18.5
F={k:ImageFont.truetype(FD+f,SZ) for k,f in dict(r='bchr8a.pfb',b='bchb8a.pfb',i='bchri8a.pfb',bi='bchbi8a.pfb').items()}
H={k:ImageFont.truetype(FD+f,23) for k,f in dict(b='bchb8a.pfb',bi='bchbi8a.pfb').items()}
COL=(45,45,45); TAG=(150,40,30); LH=24.5
src=Image.open(P).convert('RGB'); A=np.array(src)
badge=src.crop((672,388,764,411))
BUL=[
 [('Starting counters','b'),('are assigned to battlefield sections, by the CIC or divided between the players commanding sections. A section with fewer than','r'),('four Spanish units','b'),('cannot be given a counter.','r')],
 [('Gaining','b'),('a counter: when a','r'),('Scout','i'),('card is played, one counter is gained at the end of the turn in the section the','r'),('Scout','i'),('was played in, provided that the CIC draws','r'),('one','b'),('Command card instead of two (the','r'),('Scout','i'),('player still gains his Tactician card), the 3 Corps Command Stars were not used (no March Move, no Tactician draw), and the section has','r'),('at least four Spanish units','b'),('.','r')],
 [('A counter held in a section may only target a Command card played in that','r'),('same section','b'),('. A','r'),('Take Command','i'),('card counts as played in the section it is played into, even when the units it orders extend into another section.','r')],
 [('Multi-Section cards and Initiative Rolls cannot be stopped.','r')],
]
GB2=[('Grand Battles (2nd edition):','bi'),('each Corps Commander holds the counters listed in the Battle Notes. A counter may only target a Command card his opponent plays in the','r'),('sections he commands','b'),(', and a','r'),('Scout','i'),('card gains a counter only if those sections have at least four Spanish units.','r')]
def layout(segs,x0,maxx):
    lines=[[]]; x=x0; sp=F['r'].getlength(' ')
    for t,st in segs:
        for j,w in enumerate(t.split(' ')):
            if not w: continue
            f=F[st]; wl=f.getlength(w)
            if j==0 and w[0] in '.,;:)': x-=sp
            if x>x0 and x+wl>maxx: lines.append([]); x=x0
            lines[-1].append((x,w,st)); x+=wl+sp
    return lines
blocks=[layout(b,93,1196) for b in BUL]; gb2=layout(GB2,69,1196)
TOP=383; y=389-6+2
h=int(6+32+sum(len(b)*LH+2 for b in blocks)+10+len(gb2)*LH+8)
# blank strip from a white box row (y=545) repeated
blank=np.repeat(A[545:546],h,axis=0)
blk=Image.fromarray(blank.copy()); d=ImageDraw.Draw(blk)
# heading
x=69; hy=3
for t,st in (('EPIC & Grand Battles (1st edition) clarifications (90-card ','b'),('GMT','bi'),(' deck)','b')):
    d.text((x,hy),t,font=H[st],fill=(25,25,25)); x+=H[st].getlength(t)
blk.paste(badge,(int(x+14),hy+5))
cy=38
for b in blocks:
    d.text((76,cy-5),'•',font=F['r'],fill=COL)
    for k,ln in enumerate(b):
        for x,w,st in ln: d.text((x,cy+k*LH-5),w,font=F[st],fill=TAG if st=='bi' else COL)
    cy+=len(b)*LH+2
cy+=10
for k,ln in enumerate(gb2):
    for x,w,st in ln: d.text((x,cy+k*LH-5),w,font=F[st],fill=TAG if st=='bi' else COL)
B=np.concatenate([A[:TOP],np.array(blk),A[545:]])
im=Image.fromarray(B); im.save('SpecialRules_ga.jpg',quality=93)
im.crop((40,90,1240,TOP+h+30)).save('ga_prev.png'); print(im.size,h)
