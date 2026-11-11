# 4.0.6 release checks

**4.0.6 = 4.0.6a19 without the 2026 Army Reference Cards and Combat Tables, which are deferred to 4.0.7 (#32).**
- Held back until 4.0.7: the a03 national reference cards (French, British, Russian, Austrian, Prussian, Spanish, Spanish Late War, Suvorov) and the Combat Tables; the Spanish/Portuguese split (Portuguese card); the Suvorov page 2 overlay (`Alternate Russian Chart 2` slot, `Set Up Variants` back to 4.0.5); and the a04, a12 and a13 card edits. These charts show their 4.0.5 versions.
- Everything else from a19 is in: the community Special Rules charts (a05-a11), the 2026 Terrain chart, the Hill Line of Sight and Field Works Diagrams tabs, and fixes #12, #13, #14, #16, #17, #19, #23 and #29.
- The five accented leader portraits (Blücher, Bülow, Röder, Württemberg, Dupont de l'Étang) are stored under the precomposed names the buildFile uses.

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
