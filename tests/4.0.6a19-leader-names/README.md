# 4.0.6a19: leader names converted on Refresh (#23)

**Test save:** `MM13 Epic Albuera - refreshed on a19.vsav`. This is `dist/MM13 - Epic Albuera.vsav` after a Refresh on 4.0.6a19. It includes Blake, the only Leader of Honor in the Epic set-ups.

**Before (a17, and a17 after Refresh):** all 15 leaders have an empty `LeaderName` and `dName`. Blake's set-up label reads "BlakeBlake", because the Leader of Honor label falls back to `BasicName`.

**After Refresh on a19:** all 15 leaders have `LeaderName` and `dName` set from the counter label (Latour-Maubourg, Werle, Cole, Godinot, Lumley, Loy, Pepin, Alten, Zayas, Girard, Lardizabal, Balesteros, Blake, Hamilton, Stewart). Blake's Leader of Honor label is blank, so the set-up label reads "Blake". EP601 Austerlitz gave the same result: 16 of 16 named.

**To check by hand:**
1. Open the save and hover over a leader: the trimmed name shows.
2. Make a leader take a hit: the chat report names the leader.
3. Before game start, Blake's set-up label shows his name once.

**Harness:** `tools/harness/LdrTest.java`, run headless under Xvfb with VASSAL 3.7.30:
`java -cp ".:VASSAL-3.7.30/lib/*" LdrTest module.vmod out.log save.vsav`
It loads the save, lists the leader name properties, runs GameRefresher (including the Refresh GHK), lists them again and saves `<save>.refreshed.vsav`.
