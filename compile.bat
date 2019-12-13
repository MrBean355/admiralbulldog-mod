mkdir compile\pak01_dir

..\..\..\game\bin\win64\resourcecompiler -r -i sounds\*

cp -r ..\..\..\game\dota_addons\admiralbulldog\sounds compile\pak01_dir\

compile\vpk compile\pak01_dir

mv compile\pak01_dir.vpk .

rmdir /s /q compile\pak01_dir
