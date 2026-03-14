# Desktop Deeplink

Desktop deeplink support in this project has two parts:

1. The app can consume `demo03://...` from startup arguments.
2. The app can handle URI open events while already running via `Desktop.setOpenURIHandler()` when the platform supports it.

## macOS

The packaged app declares the `demo03` URL scheme in the generated `Info.plist`.

Build:

```bash
./gradlew :composeApp:packageDmg
```

Run the packaged app after installation, then open:

```bash
open "demo03://app/login"
```

## Linux

Register the installed desktop executable as a scheme handler:

```bash
./scripts/register-demo03-scheme-linux.sh /absolute/path/to/app-executable
```

Then test:

```bash
xdg-open "demo03://app/login"
```

## Windows

Register the installed `.exe` as a scheme handler:

```powershell
powershell -ExecutionPolicy Bypass -File .\scripts\register-demo03-scheme-windows.ps1 -AppExecutable "C:\Path\To\Demo03.exe"
```

Then test:

```powershell
start "demo03://app/login"
```
