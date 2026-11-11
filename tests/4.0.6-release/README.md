# 4.0.6 release checks

**4.0.6 = 4.0.6a19 without the GMT player aid art (#8), which is deferred to 4.0.7.**
- Player Aids window as in 4.0.5 (4.0.5 chart images and tabs), plus the Terrain tabs **Hill Line of Sight Diagrams** (#22) and **Field Works Diagrams**.
- Out until 4.0.7: 2026 national reference cards, Combat Tables, Terrain Tile Effects pages, Special Rules / Grand Battles charts, the Portuguese tab, and the Suvorov page 2 overlay (`Alternate Russian Chart 2` slot; `Set Up Variants` prototype back to its 4.0.5 form).
- Fixes kept: #29 (EPIC13 Eylau menu entry), #12, #13, #14, #16, #17, #19, #23.

## Headless load test
`tools/harness/LoadTest.java` (VASSAL 3.7.30, under Xvfb):
`java -cp ".:VASSAL-3.7.30/lib/*" LoadTest CCNapoEpic4.vmod out.log dist/*.vsav`
It loads every save, logs chat lines reporting errors, runs GameRefresher (with the Refresh GHK), and saves `<save>.refreshed.vsav`.

Result on 4.0.6: all 71 set-ups load and refresh. The only chat error is the "Set Name for side" Bad Data on the first load. That comes from the harness having no player side, and 4.0.6a19 gives the same line. Piece counts match 4.0.6a19 save for save.

## Set-ups need Mark's Refresh before release
The set-ups were last saved on the a19 line. They still carry the Suvorov page 2 placement (`Alternate Russian Chart 2`) and the Portuguese chart board. Refresh removes both: 0 of 71 refreshed saves still contain the Chart 2 placement. Without a Refresh, choosing the Suvorov variant would try to place a chart that is not in 4.0.6.

## By hand
1. File > New Game > EPIC13 Eylau - 8 February 1807: the scenario opens (#29).
2. Player Aids > Terrain: Page 1, Page 2, Hill Line of Sight Diagrams and Field Works Diagrams tabs are present; the other tabs show the 4.0.5 charts.
3. A Russian scenario with the Suvorov variant: page 1 overlay placed, no error.
