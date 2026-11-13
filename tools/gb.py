from PIL import Image, ImageDraw, ImageFont, ImageFilter
D='/home/claude/ccnapoleonicsepic/dist/images/'
L=lambda f: Image.open(D+f).convert('RGBA')
S=4
def at(c,im,cx,cy,dx=0,dy=0):
    im=im.resize((im.width*S,im.height*S),Image.LANCZOS)
    c.alpha_composite(im,(int((cx+dx)*S-im.width/2),int((cy+dy)*S-im.height/2)))
hexi=L('Tile_Clear.png'); blk=L('fre_FA.png'); fa=L('unit_FA.png'); s3=L('str3.png')
atk=L('marker_attacker_CCN25x25.png'); add=L('marker_attackerART.png')
H1=(43,50); H2=(86,125)   # pointy-top hexes, diagonal neighbours
W,Hh=129,175
c=Image.new('RGBA',(W*S,Hh*S),(0,0,0,0)); d=ImageDraw.Draw(c)
for cx,cy in (H1,H2):
    at(c,hexi,cx,cy)
for cx,cy in (H1,H2):
    pts=[(cx,cy-50),(cx+43,cy-25),(cx+43,cy+25),(cx,cy+50),(cx-43,cy+25),(cx-43,cy-25)]
    d.line([(min(x*S,W*S-1),min(y*S,Hh*S-1)) for x,y in pts+[pts[0]]],fill=(110,130,80,255),width=S)
for (cx,cy),m in ((H1,atk),(H2,add)):
    sh=Image.new('RGBA',((69+10)*S,(55+10)*S),(0,0,0,0)); ImageDraw.Draw(sh).rectangle([5*S,5*S,74*S,60*S],fill=(0,0,0,100)); sh=sh.filter(ImageFilter.GaussianBlur(2*S))
    c.alpha_composite(sh,(int((cx+2)*S-sh.width/2),int((cy+2)*S-sh.height/2)))
    at(c,blk,cx,cy); at(c,fa,cx,cy,0,-22); at(c,s3,cx,cy,22,20); at(c,m,cx,cy)
c=c.resize((W,Hh),Image.LANCZOS); c.save('gb_hexes2.png')
ch=Image.open('SpecialRules2_orig.jpg').convert('RGB') if False else Image.open(D+'SpecialRules2.jpg').convert('RGB')
ch.paste((255,255,255),(62,786,262,992))
gx,gy=72,800
ch.paste(c,(gx,gy),c)
dr=ImageDraw.Draw(ch)
f=ImageFont.truetype('/usr/share/fonts/truetype/dejavu/DejaVuSerifCondensed.ttf',12)
fi=ImageFont.truetype('/usr/share/fonts/truetype/dejavu/DejaVuSerifCondensed-Italic.ttf',11)
dr.text((gx+92,gy+42),'Attack',font=f,fill=(40,40,40))
for i,t in enumerate(('Add','Artillery')):
    w=dr.textlength(t,font=f); dr.text((gx+40-w,gy+118+i*15),t,font=f,fill=(40,40,40))
for i,t in enumerate(('French','Foot Artillery')):
    dr.text((gx+133,gy+150+i*13) if False else (gx+92,gy+8+i*13),t,font=fi,fill=(110,110,110)) if False else None
ch.save('SpecialRules2_v2.jpg',quality=92)
ch.crop((40,720,1240,1010)).save('preview2.png')
