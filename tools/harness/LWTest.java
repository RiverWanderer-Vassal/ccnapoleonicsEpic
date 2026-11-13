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
public class LWTest {
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
    for (String[] c : new String[][]{{"true","false"},{"false","true"},{"true","true"},{"false","false"}}) {
      SwingUtilities.invokeAndWait(() -> { gm.getMutableProperty("setupSpanishLateWarVar").setPropertyValue(c[0]); gm.getMutableProperty("setupSuvorovVar").setPropertyValue(c[1]);
        gm.getMutableProperty("isSpanishLateWarVar").setPropertyValue("unset"); gm.getMutableProperty("isSuvorovVar").setPropertyValue("unset");
        gm.fireKeyStroke(new VASSAL.tools.NamedKeyStroke("VassalPostRefreshGHK")); });
      idle(); idle();
      SwingUtilities.invokeAndWait(() -> log.println("setupLW=" + c[0] + " setupSV=" + c[1] + " -> isSpanishLateWarVar=" + gm.getProperty("isSpanishLateWarVar") + " isSuvorovVar=" + gm.getProperty("isSuvorovVar")));
    }
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
