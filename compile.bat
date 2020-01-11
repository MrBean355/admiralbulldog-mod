REM Compile resources:
..\..\..\game\bin\win64\resourcecompiler -r -i sounds\*
..\..\..\game\bin\win64\resourcecompiler -r -i panorama\*

REM Copy resources to the 'compile' directory:
mkdir compile\pak01_dir
cp -r ..\..\..\game\dota_addons\admiralbulldog\sounds compile\pak01_dir\
cp -r ..\..\..\game\dota_addons\admiralbulldog\panorama compile\pak01_dir\
cp -r resource compile\pak01_dir\

REM Build the VPK file:
compile\vpk compile\pak01_dir
mv compile\pak01_dir.vpk .

REM Clean up:
rmdir /s /q compile\pak01_dir
rmdir /s /q ..\..\..\game\dota_addons\admiralbulldog

PAUSE