from PIL import Image,ImageDraw,ImageFont
P='/home/claude/ccnapoleonicsepic/dist/images/SpecialRules.jpg'
FD='/usr/share/fonts/type1/texlive-fonts-recommended/'
SZ=18.5
F={k:ImageFont.truetype(FD+f,SZ) for k,f in dict(r='bchr8a.pfb',b='bchb8a.pfb',i='bchri8a.pfb',bi='bchbi8a.pfb').items()}
COL=(45,45,45); TAG=(150,40,30)
im=Image.open(P).convert('RGB'); d=ImageDraw.Draw(im)
im.paste((255,255,255),(70,414,1205,541))
def bullet(y,segs,maxx=1196,lh=24.5):
    d.text((76,y-5),'•',font=F['r'],fill=COL)
    x=93; sp=F['r'].getlength(' ')
    for t,st in segs:
        for j,w in enumerate(t.split(' ')):
            if not w: continue
            f=F[st]; wl=f.getlength(w)
            if j==0 and w[0] in '.,;:)': x-=sp
            if x>93 and x+wl>maxx: x=93; y+=lh
            d.text((x,y-5),w,font=f,fill=TAG if st=='bi' else COL); x+=wl+sp
    return y
bullet(417,[('EPIC (2 players):','bi'),('Gaining','b'),('a counter, after playing a','r'),('Scout','i'),('card: the CIC draws','r'),('one','b'),('Command card instead of two, the','r'),('Scout','i'),('player still gains his Tactician card, and the 3 Corps Command Stars must not have been used (no March Move, no Tactician draw). The section the','r'),('Scout','i'),('was played in must have','r'),('at least four Spanish units','b'),('. The counter is gained in that section.','r')])
bullet(493,[('Grand Battles (multiplayer):','bi'),('A counter held in a section may only target a Command card played in that','r'),('same section','b'),('.','r')])
bullet(519,[('Both:','bi'),('Multi-Section cards and Initiative Rolls cannot be stopped.','r')])
im.save('SpecialRules_gue.jpg',quality=93)
im.crop((60,380,1215,545)).save('gue2.png')
