# 4.0.6 release checks

**4.0.6 = 4.0.6a19 without the 2026 Army Reference Cards, Combat Tables and Terrain Tile Effects Chart, which are deferred to 4.0.7 (#32).**
- Held back until 4.0.7: the a03 national reference cards (French, British, Russian, Austrian, Prussian, Spanish, Spanish Late War, Suvorov) and the Combat Tables; the Spanish/Portuguese split (Portuguese card); the Suvorov page 2 overlay (`Alternate Russian Chart 2` slot, `Set Up Variants` back to 4.0.5); and the a04, a12 and a13 card edits, and the a04 Terrain Tile Effects Chart update. These charts show their 2023 (4.0.5) versions, with corrections:
  - Terrain p1, Sand Quarry: Inf Into 0/-1³ (was -1/0⁷), Inf Out 0 (was -1), Arty Into -1 (was -1/0).
  - Terrain p2, Marsh: Must Stop with note 8, Out Arty NA (was -1).
  - Terrain p2: Arty Into for Fordable River, Marsh and Frozen Lake reads -1/0⁷ (was -1⁷), matching the Inf column; every note-7 entry is now -1/0.
  - Terrain p1 (both versions): Hill with Field Works hexsides shows Into -1 / -2 / 0 and Out 0 / -2 / 0 (was Up/Down/Hill to Hill), and Line of Sight Yes² — as the 2026 chart and the other Field Works rows.
  - Terrain notes, both pages: note 3 now reads "Hill to Hill and Sand Quarry have no effect on melee only Ranged fire is effected." (the 2023 convention: -1/0 goes with note 7, 0/-1 with note 3); note 7 now reads "Fordable River, Marsh and Frozen Lake" (Sand Quarry removed); note 8 reads "Marsh and Sand Quarry are impassable for artillery."
  - Russian (and Suvorov) Light Infantry movement 1 / 2 NB.
  - Russian Heavy Cuirassier Cavalry is (CU), not (CC).
  - British Rocket Artillery movement 1-2 NB, and Moving column None (was See Chart Below).
  - Russian Heavy Cuirassier notes: line 2 is "Ignore 1 hit from ranged Inf fire." (it repeated the melee die line).
  - French Heavy Cuirassier notes: "Roll 1 additional die in melee combat." added (the Melee column already showed it).
  - Prussian Militia Lancer melee: stray asterisk removed.
  - Combat Tables: Note 3 "...one hit from Infantry ranged combat."; Note 5 "Rifle Light, Grenzer and all militia units...".
  - Each corrected page has "Errata corrected November 2026" in small italics, right-justified on the GMT footer line (French page 2 has no GMT footer, so the note sits at the same height). Unchanged pages have no mark.
  - All chart and overlay images keep their file names, so games in progress and set-ups show the corrected images without a Refresh. That includes the Suvorov overlay (RussianPA-SV.jpeg), and the Spanish Late War overlay, which is unchanged from 2023.
  The edits reuse glyphs cut from the same scans, so the type matches.
- Everything else from a19 is in: the community Special Rules charts (a05-a11), the Hill Line of Sight and Field Works Diagrams tabs, and fixes #12, #13, #14, #16, #17, #19, #23 and #29.
- The five accented leader portraits (Blücher, Bülow, Röder, Württemberg, Dupont de l'Étang) are stored under the precomposed names the buildFile uses.

## Sand Quarry option (#33)
- **The option:** Scenario Options > Variants > Sand Quarry, either **Updated (4th Edition)** (the default) or **Original (2016)**. It is set before game start, like the Spanish Late War variant.
- **The tiles:** the option switches every Sand Quarry tile on the main map. Updated uses `Quarry4thEd`: Inf 0 melee / -1 ranged into, 0 out; Arty -1 into, may not leave; Cav -2. Original uses `Quarry`: Inf -1 into and out. A tile placed later adopts the setting when it is dropped (`mapMove`). The tiles no longer have a public rules menu.
- **The chart:** Original overlays Terrain page 1 with `TerrainPA-SQ2016.jpg`, which carries © 2016 and "Errata corrected November 2026 / except Sand Quarry". The overlay is placed through the Game Setup helper, like the Suvorov chart. The corrected Terrain pages carry © 2023.
- **The lock:** once the game is locked, a change is reverted to the saved setting (`gSandQuarry`). That required fixing the generic resetOption, which had restored locked options blank (#34). Suvorov is now restored correctly too.
- **Headless test** (`SQTest`, on GBWaterloo after Refresh):
  - Updated to Original to Updated: the tile and the chart switch each time.
  - A new palette tile adopts Original.
  - After lock, a change to Updated is reverted to Original.
  - Suvorov after lock stays false.

## Refresh restores the variant flags (#35)
`isSpanishLateWarVar` is now restored on Refresh from `setupSpanishLateWarVar`; before, it was copied from the Suvorov option. `tools/harness/LWTest.java` sets each combination of the two options, fires `VassalPostRefreshGHK`, and checks both flags follow their own option: all four combinations pass.

## Self-contained check
Every image name in the buildFile (piece traits, boards, charts, HTML) was listed and checked against `dist/images`. Only VASSAL's own icons and a few parsing artefacts are not found, and the same list comes up for 4.0.5 and a19. Nothing new is missing.

## Set-up refresh (on 4.0.6, headless)
`tools/harness/LoadTest.java` (VASSAL 3.7.30, under Xvfb) loads each save, runs GameRefresher with the Refresh GHK, and saves the result:
`java -cp ".:VASSAL-3.7.30/lib/*" LoadTest CCNapoEpic4.vmod out.log dist/*.vsav`
- All 71 saves load and refresh. The only chat error is the "Set Name for side" Bad Data on the first load. That comes from the harness having no player side, and a19 gives the same line.
- Every stack and piece is at the same position before and after the refresh. The re-snaps (#12) are unchanged.
- **All 71 saves committed refreshed.**
  - MD09 Borodino Shevardino & Utitza gains its missing Game Setup piece; it was the only set-up without one.
  - EP609 Sorauren, MM14 EpicHeilsburg and MM15 Smolensk 1812 are old saves not on the menu. Their Counter-Attack card is renamed Counter-attack.
- **6 blank boards** (GBBorodinoBoard, GBWaterlooBoard, LGM26x11_empty, XGBF2, XGBL2, epic20x11_empty): refreshed, then the New Scenario Helper that Refresh places on them was taken out of the save again, because it belongs to starting a new scenario, not to the template. They now match the originals piece for piece and load the same way.
- No refreshed save still carries the Suvorov page 2 placement.

## By hand
1. File > New Game > EPIC13 Eylau - 8 February 1807: the scenario opens (#29).
2. Player Aids: the national cards, Combat Tables and Spanish & Portuguese tab are the 4.0.5 versions. Terrain has four tabs, and Special Rules has the new community charts.
3. A Russian scenario with the Suvorov variant: Russian page 1 gets the 4.0.5 Suvorov overlay, with no error.
4. Prussian leaders Blücher, Bülow and Röder show their portraits.

## XML check
Before every build, run `python3 -c "import xml.dom.minidom; xml.dom.minidom.parse('dist/buildFile.xml')"`. Change log text must not contain plain double quotes.
