resolvers += "jitpack" at "https://jitpack.io"
sys.props.get("plugin.version") match {
  case Some(v) => addSbtPlugin("dev.atedeg" % "sbt-ubiquitous-scaladoc" % v)
  case _ => sys.error("No version specified")
}