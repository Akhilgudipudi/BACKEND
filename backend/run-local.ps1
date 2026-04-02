$ErrorActionPreference = "Stop"

$projectRoot = Split-Path -Parent $MyInvocation.MyCommand.Path
$sourceRoot = Join-Path $projectRoot "src\main\java"
$classesDir = Join-Path $projectRoot "build\classes"
$resourcesDir = Join-Path $projectRoot "src\main\resources"
$m2Root = Join-Path $env:USERPROFILE ".m2\repository"

if (-not (Test-Path $m2Root)) {
    throw "Maven repository cache was not found at $m2Root"
}

New-Item -ItemType Directory -Force -Path $classesDir | Out-Null

$classpathEntries = Get-ChildItem -Path $m2Root -Recurse -Filter *.jar |
    Where-Object {
        $_.Name -notlike "*-sources.jar" `
        -and $_.Name -notlike "*-javadoc.jar" `
        -and $_.FullName -notlike "*\org\apache\maven\*" `
        -and $_.FullName -notlike "*\org\codehaus\plexus\*" `
        -and $_.FullName -notlike "*\org\slf4j\slf4j-api\1.7.36\*"
    } |
    ForEach-Object { $_.FullName }
$classpath = (($classpathEntries + $classesDir + $resourcesDir) -join ";")
$sources = Get-ChildItem -Path $sourceRoot -Recurse -Filter *.java | ForEach-Object { $_.FullName }

if (-not $sources) {
    throw "No Java sources were found in $sourceRoot"
}

Write-Host "Compiling Spring Boot backend..."
javac -cp $classpath -d $classesDir $sources
if ($LASTEXITCODE -ne 0) {
    throw "Compilation failed."
}

Write-Host "Starting backend on http://localhost:8080 ..."
java -cp $classpath com.skillcert.tracker.SkillCertTrackerApplication
