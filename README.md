# AsyncMC

Welcome to AsyncMC, this project is being setup right now, information about it will be published soon.

To correctly check out the project, execute these commands:

Linux/MacOS/Unix/Windows-Using-Git-Bash:
```sh
git clone --recurse-submodules -j8 -b bleeding https://github.com/AsyncMC/AsyncMC.git
cd AsyncMC
sh setup-submodules.sh
```

Windows using CMD/PowerShell:
```bat
git clone --recurse-submodules -j8 -b bleeding https://github.com/AsyncMC/AsyncMC.git
cd AsyncMC
setup-submodules.bat
```

The `master` branch will hold only stable code while `bleeding` will hold all the work in progress.

If you have cloned using different commands, make sure you are using the `bleeding` branch and then run the `setup-submodules` utility to fix your submodules if necessary. **You may loose local changes, so make a backup!**
