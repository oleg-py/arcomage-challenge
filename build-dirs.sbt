sourceDirectory in Compile   := baseDirectory.value / "src"
sourceDirectory in Test      := baseDirectory.value / "tests"
resourceDirectory in Compile := baseDirectory.value / "resources"

scalaSource in Compile := (sourceDirectory in Compile).value
scalaSource in Test    := (sourceDirectory in Test).value
