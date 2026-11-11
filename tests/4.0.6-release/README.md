# 4.0.6 release checks

**4.0.6 = 4.0.6a19 without the Expansion 7 (Grand Battles) player aids, which are deferred to 4.0.7.**
- Out until 4.0.7: the Special Rules charts redrawn from the Grand Battles rules (a05-a11): Victory Banner Objectives, EPIC, Grand Battles 1st/2nd Edition, National Rules & Iconic Commanders, and Garrisons, Grand Battery & Leaders. The Special Rules tab is as in 4.0.5, and the GrandBattles1/2 images are removed.
- Everything else from a19 is in: 2026 national reference cards and Combat Tables, the Spanish/Portuguese split, the Suvorov overlay on both Russian pages, the 2026 Terrain chart, the Hill Line of Sight and Field Works Diagrams tabs, and fixes #12, #13, #14, #16, #17, #19, #23 and #29.
- The five accented leader portraits (Blücher, Bülow, Röder, Württemberg, Dupont de l'Étang) are stored under the precomposed names the buildFile uses. They were stored decomposed, which VASSAL's exact-name lookup misses.

## Self-contained check
Every image name in the buildFile (piece traits, boards, charts, HTML) was listed and checked against `dist/images`. Only VASSAL's own icons and a few parsing artefacts are not found, and the same list comes up for 4.0.5 and a19. Nothing new is missing.

## Set-up refresh (on 4.0.6, headless)
`tools/harness/LoadTest.java` (VASSAL 3.7.30, under Xvfb) loads each save, runs GameRefresher with the Refresh GHK, and saves the result:
`java -cp ".:VASSAL-3.7.30/lib/*" LoadTest CCNapoEpic4.vmod out.log dist/*.vsav`
- All 71 saves load and refresh. The only chat error is the "Set Name for side" Bad Data on the first load. That comes from the harness having no player side, and a19 gives the same line.
- Every stack and piece is at the same position before and after the refresh. The re-snaps (#12) are unchanged.
- **65 saves committed refreshed.**
  - MD09 Borodino Shevardino & Utitza gains its missing Game Setup piece; it was the only set-up without one.
  - EP609 Sorauren, MM14 EpicHeilsburg and MM15 Smolensk 1812 are old saves not on the menu. Their Counter-Attack card is renamed Counter-attack.
- **6 blank boards left as they were:** GBBorodinoBoard, GBWaterlooBoard, LGM26x11_empty, XGBF2, XGBL2, epic20x11_empty. Refresh places a New Scenario Helper on them, which belongs to starting a new scenario, not to the template.

## By hand
1. File > New Game > EPIC13 Eylau - 8 February 1807: the scenario opens (#29).
2. Player Aids: the new national cards, Combat Tables, Terrain (four tabs) and Portuguese tab are there, and Special Rules shows the 4.0.5 charts.
3. A Russian scenario with the Suvorov variant: both Russian pages get the overlay.
4. Prussian leaders Blücher, Bülow and Röder show their portraits.
