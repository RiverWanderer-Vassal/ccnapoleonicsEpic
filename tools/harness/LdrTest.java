import VASSAL.build.GameModule;
import VASSAL.build.module.GameRefresher;
import VASSAL.command.Command;
import VASSAL.command.NullCommand;
import VASSAL.counters.GamePiece;
import VASSAL.counters.Stack;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;

/** java LdrTest <module.vmod> <out.log> <save1.vsav> [save2 ...]
 *  For each save: load, list leaders (BasicName, LeaderNameV4, LoHname, LeaderName, dName),
 *  run GameRefresher (as Mark's set-up refresh, incl. VassalPostRefreshGHK), list again, save as <save>.refreshed.vsav. */
public class LdrTest {
  static PrintStream log;
  public static void main(String[] a) throws Exception {
    log = new PrintStream(new FileOutputStream(a[1]), true, "UTF-8");
    dialogKiller();
    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      log.println("SHUTDOWN");
      for (java.util.Map.Entry<Thread, StackTraceElement[]> e : Thread.getAllStackTraces().entrySet()) {
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement x : e.getValue()) sb.append("\n   ").append(x);
        if (sb.indexOf("exit") >= 0 || sb.indexOf("Shutdown") >= 0) log.println("THREAD " + e.getKey().getName() + sb);
      }
    }));
    new Thread(() -> { try { VASSAL.launch.Player.main(new String[]{"--load", a[0], a[2]}); } catch (Throwable t) { t.printStackTrace(log); } }).start();
    long t0 = System.currentTimeMillis();
    while (GameModule.getGameModule() == null || GameModule.getGameModule().getPlayerWindow() == null || !GameModule.getGameModule().getPlayerWindow().isVisible()) {
      Thread.sleep(500); if (System.currentTimeMillis() - t0 > 600000) { log.println("TIMEOUT"); Runtime.getRuntime().halt(2); }
    }
    Thread.sleep(8000);
    final GameModule gm = GameModule.getGameModule();
    log.println("MODULE " + gm.getGameVersion());
    for (int i = 2; i < a.length; i++) {
      final File f = new File(a[i]);
      log.println("== " + f.getName());
      if (i > 2) SwingUtilities.invokeAndWait(() -> gm.getGameState().loadGameInForeground(f));
      idle();
      list(gm, "BEFORE");
      SwingUtilities.invokeAndWait(() -> {
        try {
          Set<String> o = new HashSet<>(Arrays.asList("UseName","UseLabelerName","UseLayerName","UseRotateName","DeleteNoMap","RefreshDecks","UseHotkey","RefreshPieces"));
          new GameRefresher(gm).execute(o, new NullCommand());
        } catch (Throwable t) { t.printStackTrace(log); }
      });
      idle();
      list(gm, "AFTER");
      final File out = new File(f.getName().replace(".vsav", ".refreshed.vsav"));
      SwingUtilities.invokeAndWait(() -> { try { gm.getGameState().saveGame(out); } catch (Throwable t) { t.printStackTrace(log); } });
      idle();
    }
    Runtime.getRuntime().halt(0);
  }
  static void list(GameModule gm, String tag) throws Exception {
    SwingUtilities.invokeAndWait(() -> {
      int n = 0, named = 0;
      for (GamePiece top : gm.getGameState().getAllPieces()) {
        List<GamePiece> l = new ArrayList<>();
        if (top instanceof Stack) l.addAll(((Stack) top).asList()); else l.add(top);
        for (GamePiece p : l) {
          if (!"LDR".equals(p.getProperty("Class"))) continue;
          n++;
          Object ln = p.getProperty("LeaderName");
          if (ln != null && !ln.toString().isEmpty()) named++;
          log.println(tag + " " + p.getProperty("BasicName") + " | V4=" + p.getProperty("LeaderNameV4") + " | LoH=" + p.getProperty("LoHname")
            + " | LeaderName=" + ln + " | dName=" + p.getProperty("dName") + " | map=" + (p.getMap() == null ? "-" : p.getMap().getMapName()));
        }
      }
      log.println(tag + " TOTAL leaders=" + n + " withLeaderName=" + named);
    });
  }
  static void idle() throws Exception { for (int i = 0; i < 8; i++) { SwingUtilities.invokeAndWait(() -> {}); Thread.sleep(250); } }
  static void dialogKiller() {
    Thread t = new Thread(() -> {
      while (true) {
        try {
          Thread.sleep(300);
          for (Window w : Window.getWindows()) {
            if (w instanceof JDialog && w.isVisible() && ((JDialog) w).isModal()) {
              JDialog d = (JDialog) w;
              log.println("DIALOG [" + d.getTitle() + "]");
              if ("Welcome".equals(d.getTitle())) { SwingUtilities.invokeLater(() -> d.setVisible(false)); continue; }
              JButton b = btn(d.getContentPane());
              SwingUtilities.invokeLater(() -> { if (b != null) b.doClick(); else d.dispose(); });
            }
          }
        } catch (Throwable e) { }
      }
    });
    t.setDaemon(true); t.start();
  }
  static JButton btn(Container c) {
    for (Component k : c.getComponents()) {
      if (k instanceof JButton) { String s = ((JButton) k).getText(); if (s != null && s.matches("(?i)ok|close|continue|ignore|no|yes")) return (JButton) k; }
      if (k instanceof Container) { JButton r = btn((Container) k); if (r != null) return r; }
    }
    return null;
  }
}
