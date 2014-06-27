public class Logger {

  private static Logger mLogger = null;

  public static Logger getInstance() {
    if (mLogger == null) {
      mLogger = new Logger();
    }
    return mLogger;
  }

  protected static void updateInstance(Logger logger) {
    mLogger = logger;
  }

  public void info(String format, Object... argv) {

  }

  public void debug(String format, Object... argv) {

  }

  public void error(String format, Object... argv) {

  }

  public void verbose(String format, Object... argv) {

  }

}