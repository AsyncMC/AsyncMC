@REM Thanks to: https://gist.github.com/slavafomin/08670ec0c0e75b500edbaa5d43a5c93c

@REM Initialize submodules after regular cloning:
git submodule update --init

@REM Make submodules to track their respective remote branches (instead of being in detached HEAD state)
git submodule foreach -q --recursive "git checkout $(git config -f $toplevel/.gitmodules submodule.$name.branch || echo master)"

@REM Display status of submodules when git status is invoked:
git config status.submoduleSummary true
