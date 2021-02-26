import sbt.url

publishMavenStyle in ThisBuild := true
publishConfiguration in ThisBuild := publishConfiguration.value.withOverwrite(true)

licenses in ThisBuild := Seq("APL2" -> url("http://www.apache.org/licenses/LICENSE-2.0.txt"))

homepage in ThisBuild := Some(url("https://github.com/valdo404/scala-openrtb"))

scmInfo in ThisBuild := Some(
  ScmInfo(
    url("https://github.com/valdo404/scala-openrtb"),
    "scm:git@github.com:valdo404/scala-openrtb.git"
  )
)

developers in ThisBuild := List(
  Developer(
    id = "waiter-melon",
    name = "Emanuele Pirro",
    email = "pirroemanuele@gmail.com",
    url = url("https://github.com/waiter-melon")),
  Developer(
    id = "rlebran",
    name = "Romain Lebran",
    email = "rlebran@gmail.com",
    url = url("https://github.com/rlebran")),
  Developer(
    id = "valdo404",
    name = "Laurent Valdes",
    email = "valderama@gmail.com",
    url = url("https://github.com/valdo404")),
  Developer(
    id = "Garnek20",
    name = "Pawel Gontarz",
    email = "garnek522@gmail.com",
    url = url("https://github.com/Garnek20"))
)

pomIncludeRepository  in ThisBuild:= { _ => false }
publishMavenStyle in ThisBuild := true
