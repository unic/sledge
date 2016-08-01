import org.scalatest.selenium.HtmlUnit
import org.scalatest.{FlatSpec, ShouldMatchers}

/**
  * Simple smoke test for Sledge main view.
  *
  * @author oliver.burkhalter
  */
class SledgeSmokeSpec extends FlatSpec with ShouldMatchers with HtmlUnit {

  val host = "http://localhost"
  val port = "8080"
  val urlBase = host + ":" + port

  "The Sledge main view" should "be displayed correctly" in {
    go to (urlBase + "/etc/sledge/packages.html")

    pageTitle should be("Sledge Webapp - A Sling Application Manager")
    findAll(cssSelector("a.add-package-button")) should have length 1
  }
}
