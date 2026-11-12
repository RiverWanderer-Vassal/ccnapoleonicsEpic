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
public class SQTest {
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
    chat(gm,"LOAD");
    SwingUtilities.invokeAndWait(() -> { try {
      Set<String> o = new HashSet<>(Arrays.asList("UseName","UseLabelerName","UseLayerName","UseRotateName","DeleteNoMap","RefreshDecks","UseHotkey","RefreshPieces"));
      new VASSAL.build.module.GameRefresher(gm).execute(o, new VASSAL.command.NullCommand()); log.println("REFRESHED"); } catch (Throwable t) { t.printStackTrace(log);} });
    idle(); idle();
    step(gm,"initial");
    set(gm,"Original (2016)"); step(gm,"after Original");
    set(gm,"Updated (4th Edition)"); step(gm,"after Updated");
    set(gm,"Original (2016)"); step(gm,"Original again");
    // new tile from palette while Original
    SwingUtilities.invokeAndWait(() -> { try {
      for (VASSAL.build.widget.PieceSlot ps : gm.getAllDescendantComponentsOf(VASSAL.build.widget.PieceSlot.class)) {
        if ("114".equals(ps.getGpId())) {
          VASSAL.counters.GamePiece p = VASSAL.counters.PieceCloner.getInstance().clonePiece(ps.getPiece());
          VASSAL.build.module.Map m = VASSAL.build.module.Map.getMapById("Main Map"); if (m==null) for (VASSAL.build.module.Map mm: VASSAL.build.module.Map.getMapList()) if (mm.getMapName().equals("Main Map")) m=mm;
          gm.sendAndLog(m.placeOrMerge(p, new java.awt.Point(400,400)));
          gm.sendAndLog(p.keyEvent(new VASSAL.tools.NamedKeyStroke("mapMove").getKeyStroke()));
          log.println("placed new palette tile (gpid 114) at 400,400 and sent mapMove");
        }
      }
    } catch (Throwable t) { t.printStackTrace(log);} });
    idle(); step(gm,"new tile while Original");
    // lock the game, try to change
    SwingUtilities.invokeAndWait(() -> { gm.getMutableProperty("gSetupLocked").setPropertyValue("true"); gm.getMutableProperty("gNotSetupLocked").setPropertyValue("false"); });
    set(gm,"Updated (4th Edition)"); step(gm,"locked, tried Updated");
    SwingUtilities.invokeAndWait(() -> { log.println("Suvorov before: setupSuvorovVar=" + gm.getProperty("setupSuvorovVar") + " isSuvorovVar=" + gm.getProperty("isSuvorovVar")); gm.getMutableProperty("setupSuvorovVar").setPropertyValue("true"); gm.fireKeyStroke(new VASSAL.tools.NamedKeyStroke("ghkSuvorovVar")); });
    idle(); idle();
    SwingUtilities.invokeAndWait(() -> log.println("Suvorov after locked change attempt: setupSuvorovVar=" + gm.getProperty("setupSuvorovVar") + " isSuvorovVar=" + gm.getProperty("isSuvorovVar")));
    Runtime.getRuntime().halt(0);
  }
  static void set(GameModule gm, String v) throws Exception {
    SwingUtilities.invokeAndWait(() -> { try {
      VASSAL.build.module.properties.MutableProperty mp = gm.getMutableProperty("setupSandQuarry");
      if (mp == null) { log.println("NO setupSandQuarry mutable"); return; }
      mp.setPropertyValue(v);
      gm.fireKeyStroke(new VASSAL.tools.NamedKeyStroke("ghkSetupSandQuarry"));
      log.println("SET setupSandQuarry=" + v + " and fired ghkSetupSandQuarry");
    } catch (Throwable t) { t.printStackTrace(log);} });
    idle(); idle();
  }
  static void step(GameModule gm, String tag) throws Exception {
    SwingUtilities.invokeAndWait(() -> {
      log.println("== " + tag + ": setupSandQuarry=" + gm.getProperty("setupSandQuarry") + " gSandQuarry=" + gm.getProperty("gSandQuarry"));
      for (GamePiece top : gm.getGameState().getAllPieces()) {
        List<GamePiece> l = new ArrayList<>(); if (top instanceof Stack) l.addAll(((Stack) top).asList()); else l.add(top);
        for (GamePiece p : l) {
          String bn = String.valueOf(p.getProperty("BasicName"));
          if (bn.equals("Game Setup")) log.println("   Game Setup at " + p.getPosition() + " map " + (p.getMap()==null?"-":p.getMap().getMapName()) + " zone " + p.getProperty("CurrentZone") + " type has sqChart=" + p.getType().contains("sqChart") + "; cMain=" + gm.getProperty("cMain"));
          if (bn.equals("Sand Quarry")) log.println("   tile at " + p.getPosition() + " INFinCC=" + p.getProperty("INFinCC") + " INFinRng=" + p.getProperty("INFinRng") + " INFout=" + p.getProperty("INFout") + " ARTinCC=" + p.getProperty("ARTinCC") + " hasMacro=" + p.getType().contains("sqAlign") + " map=" + p.getMap().getMapName());
          if (p.getMap()!=null && p.getMap().getMapName().equals("TerrainPA1")) log.println("   TerrainPA1 piece: " + bn + " at " + p.getPosition());
        }
      }
    });
    chat(gm, tag);
  }
  static int chatLen = 0;
  static void chat(GameModule gm, String tag) throws Exception {
    SwingUtilities.invokeAndWait(() -> {
      try {
        javax.swing.text.Document d = ((javax.swing.text.JTextComponent) find(gm.getChatter(), javax.swing.text.JTextComponent.class)).getDocument();
        String t = d.getText(0, d.getLength());
        String n = t.substring(Math.min(chatLen, t.length())); chatLen = t.length();
        for (String l : n.split("\n")) if (l.matches("(?i).*(bad data|error|exception|can't|cannot|not found|failed).*")) log.println(tag + " CHAT " + l);
        log.println(tag + " pieces=" + gm.getGameState().getAllPieces().size());
      } catch (Throwable e) { e.printStackTrace(log); }
    });
  }
  static Component find(Container c, Class<?> k) { for (Component x : c.getComponents()) { if (k.isInstance(x)) return x; if (x instanceof Container) { Component r = find((Container) x, k); if (r != null) return r; } } return null; }
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
