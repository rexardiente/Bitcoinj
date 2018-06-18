
// @GENERATOR:play-routes-compiler
// @SOURCE:/Volumes/Development/Projects/bitcoinj/conf/routes
// @DATE:Mon Jun 18 15:37:15 PHT 2018


package router {
  object RoutesPrefix {
    private var _prefix: String = "/"
    def setPrefix(p: String): Unit = {
      _prefix = p
    }
    def prefix: String = _prefix
    val byNamePrefix: Function0[String] = { () => prefix }
  }
}
