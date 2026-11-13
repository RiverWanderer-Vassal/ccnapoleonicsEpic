from PIL import Image, ImageDraw, ImageFont, ImageFilter
D='/home/claude/ccnapoleonicsepic/dist/images/'
L=lambda f: Image.open(D+f).convert('RGBA')
hexi=L('Tile_Clear.png'); blk=L('fre_FA.png'); s3=L('str3.png')
atk=L('marker_attacker_CCN25x25.png'); add=L('marker_attackerART.png')
S=3  # supersample
W,H=172,100
c=Image.new('RGBA',(W*S,H*S),(255,255,255,0))
hx=hexi.resize((86*S,100*S),Image.LANCZOS)
outline=Image.new('RGBA',hx.size,(0,0,0,0))
for i,x in enumerate((0,86)):
    c.alpha_composite(hx,(x*S,0))
# hex outlines
d=ImageDraw.Draw(c)
for x in (0,86):
    pts=[(x+43,0),(x+86,25),(x+86,75),(x+43,100),(x,75),(x,25)]
    d.line([(px*S,min(py*S,H*S-1)) for px,py in pts+[pts[0]]],fill=(110,130,80,255),width=S)
b=blk.resize((60*S,int(55*60/69)*S),Image.LANCZOS); bw,bh=b.size
sh=Image.new('RGBA',(bw+8*S,bh+8*S),(0,0,0,0)); ImageDraw.Draw(sh).rectangle([4*S,4*S,bw+4*S,bh+4*S],fill=(0,0,0,110)); sh=sh.filter(ImageFilter.GaussianBlur(2*S))
st=s3.resize((s3.width*S,s3.height*S),Image.LANCZOS)
for x,m in ((0,atk),(86,add)):
    bx=(x+43)*S-bw//2; by=50*S-bh//2+3*S
    c.alpha_composite(sh,(bx-2*S,by-2*S)); c.alpha_composite(b,(bx,by))
    mk=m.resize((22*S,22*S),Image.LANCZOS)
    c.alpha_composite(mk,(bx-7*S,by-12*S))
c=c.resize((W,H),Image.LANCZOS)
c.save('gb_hexes.png')
# patch into chart
ch=Image.open(D+'SpecialRules2.jpg').convert('RGB')
x0,y0,x1,y1=66,786,258,995
ch.paste((255,255,255),(x0,y0,x1,990))
sc=186/172; g=c.resize((186,round(100*sc)),Image.LANCZOS)
gx=69; gy=800
ch.paste(g,(gx,gy),g)
dr=ImageDraw.Draw(ch)
f=ImageFont.truetype('/usr/share/fonts/truetype/dejavu/DejaVuSerifCondensed.ttf',12)
fi=ImageFont.truetype('/usr/share/fonts/truetype/dejavu/DejaVuSerifCondensed-Italic.ttf',11)
for cx,t in ((gx+46,'Attack'),(gx+139,'Add Artillery')):
    w=dr.textlength(t,font=f); dr.text((cx-w/2,gy+g.height+4),t,font=f,fill=(40,40,40))
t='French Foot Artillery'; w=dr.textlength(t,font=fi); dr.text((gx+93-w/2,gy+g.height+20),t,font=fi,fill=(90,90,90))
ch.save('SpecialRules2.jpg',quality=92)
ch.crop((40,720,1240,1010)).save('preview.png')
