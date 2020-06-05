@REM
@REM     AsyncMC - A fully async, non blocking, thread safe and open source Minecraft server implementation
@REM     Copyright (C) 2020 joserobjr@gamemods.com.br
@REM
@REM     This program is free software: you can redistribute it and/or modify
@REM     it under the terms of the GNU Affero General Public License as published
@REM     by the Free Software Foundation, either version 3 of the License, or
@REM     (at your option) any later version.
@REM
@REM     This program is distributed in the hope that it will be useful,
@REM     but WITHOUT ANY WARRANTY; without even the implied warranty of
@REM     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
@REM     GNU Affero General Public License for more details.
@REM
@REM     You should have received a copy of the GNU Affero General Public License
@REM     along with this program.  If not, see https://www.gnu.org/licenses/
@REM

@REM Thanks to: https://gist.github.com/slavafomin/08670ec0c0e75b500edbaa5d43a5c93c

@REM Initialize submodules after regular cloning:
git submodule update --init

@REM Make submodules to track their respective remote branches (instead of being in detached HEAD state)
git submodule foreach -q --recursive "git checkout $(git config -f $toplevel/.gitmodules submodule.$name.branch || echo master)"

@REM Display status of submodules when git status is invoked:
git config status.submoduleSummary true
