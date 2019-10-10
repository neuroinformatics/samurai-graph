;Samurai Graph installer script for Win32
!include common.nsh

;--------------------------------
; General

!define SG_FILE_NAME "samurai-graph-win32-${SG_FULLVERSION}.exe"

SetCompressor /SOLID lzma
RequestExecutionLevel user

;-----------------------------------
; Version

VIProductVersion "${SG_PRODUCT_VERSION}"
VIAddVersionKey "ProductName"      "${SG_PRODUCT}"
VIAddVersionKey "Comments"         "${SG_COMMENT}"
VIAddVersionKey "CompanyName"      "${SG_COMPANY}"
VIAddVersionKey "ProductVersion"   "${SG_PRODUCT_VERSION}"
VIAddVersionKey "LegalCopyright"   "${SG_COPYRIGHT}"
VIAddVersionKey "FileDescription"  "Samurai Graph Setup Program"
VIAddVersionKey "FileVersion"      "1.0.0"
VIAddVersionKey "InternalName"     "${SG_FILE_NAME}"
VIAddVersionKey "OriginalFilename" "${SG_FILE_NAME}"

!define SG_REGKEY  "Software\${SG_PRODUCT}"
!define SG_UNINST_REGKEY  "Software\Microsoft\Windows\CurrentVersion\Uninstall\${SG_NAME}"

!define SG_PROPERTY_EXT_REGKEY         ".sgp"
!define SG_PROPERTY_EXT_BACKUP_REGKEY  "backup_val"
!define SG_PROPERTY_REGKEY             "SamuraiGraphPropertyFile"
!define SG_PROPERTY_LABEL              "Samurai Graph Property File"
!define SG_PROPERTY_ICONFILE           "samurai-graph-property.ico"

!define SG_ARCHIVE_EXT_REGKEY          ".sga"
!define SG_ARCHIVE_EXT_BACKUP_REGKEY   "backup_val"
!define SG_ARCHIVE_REGKEY              "SamuraiGraphArchiveFile"
!define SG_ARCHIVE_LABEL               "Samurai Graph Archive File"
!define SG_ARCHIVE_ICONFILE            "samurai-graph-archive.ico"

!define SG_SCRIPT_EXT_REGKEY          ".sgs"
!define SG_SCRIPT_EXT_BACKUP_REGKEY   "backup_val"
!define SG_SCRIPT_REGKEY              "SamuraiGraphScriptFile"
!define SG_SCRIPT_LABEL               "Samurai Graph Script File"
!define SG_SCRIPT_ICONFILE            "samurai-graph-script.ico"

;!define SG_SELECT_COMPONENTS  "YES"

!include "MUI.nsh"


;Name and file
Name "${SG_NAME}"
OutFile "${SG_FILE_NAME}"

;Default installation folder
InstallDir "$PROGRAMFILES\${SG_PRODUCT}"

;Get installation folder from registry if available
InstallDirRegKey HKLM "${SG_REGKEY}" ""

;------------------------------
; Interface Configuration

; Installer and uninstaller icon file
!define MUI_ICON   "resources\setup.ico"
!define MUI_UNICON "resources\remove.ico"
; welcome page bitmap
!define MUI_WELCOMEFINISHPAGE_BITMAP "resources\welcome.bmp"
!define MUI_WELCOMEFINISHPAGE_BITMAP_NOSTRETCH
!define MUI_UNWELCOMEFINISHPAGE_BITMAP "resources\unwelcome.bmp"
!define MUI_UNWELCOMEFINISHPAGE_BITMAP_NOSTRETCH

;--------------------------------
; Pages

!insertmacro MUI_PAGE_WELCOME
!ifdef SG_SELECT_COMPONENTS
  !insertmacro MUI_PAGE_COMPONENTS
!endif
!insertmacro MUI_PAGE_DIRECTORY
!insertmacro MUI_PAGE_INSTFILES
!insertmacro MUI_PAGE_FINISH

!insertmacro MUI_UNPAGE_WELCOME
!insertmacro MUI_UNPAGE_CONFIRM
!insertmacro MUI_UNPAGE_INSTFILES
!insertmacro MUI_UNPAGE_FINISH

;--------------------------------
; Interface Settings

!define MUI_ABORTWARNING
  
;--------------------------------
;Languages
!insertmacro MUI_LANGUAGE "English"
!insertmacro MUI_LANGUAGE "Japanese"
!include "languages\english.nsh"
!include "languages\japanese.nsh"
!include UAC.nsh

;-------------------------
;Installer Sections

Function .onInit

  ; -----------------------------------------------------------
  ; begin UAC plugin : elevete user privileges to admin
  ; -----------------------------------------------------------
  Push $R0
  Push $R1
  Push $R2
  Push $R3
  
  UAC_Elevate:
    UAC::RunElevated 
    StrCmp 1223 $0 UAC_ElevationAborted ; UAC dialog aborted by user?
    StrCmp 0 $0 0 UAC_Err ; Error?
    StrCmp 1 $1 0 UAC_Success ;Are we the real deal or just the wrapper?
    Quit
    
  UAC_Err:
    MessageBox MB_ICONSTOP "$(DESC_UAC_Error) - Error $0"
    Abort

  UAC_ElevationAborted:
    ; elevation was aborted, run as normal?
    MessageBox MB_ICONSTOP "$(DESC_UAC_ReqAdmin)$\r$\r$(DESC_UAC_Aborted)"
    Abort

  UAC_Success:
    StrCmp 1 $3 +4 ; is Admin?
    StrCmp 3 $1 0 UAC_ElevationAborted ; Try again?
    MessageBox MB_ICONSTOP "$(DESC_UAC_ReqAdmin)$\r$\r$(DESC_UAC_TryAgain)"
    goto UAC_Elevate

  Pop $R3
  Pop $R2
  Pop $R1
  Pop $R0
  ; -----------------------------------------------------------
  ; end of UAC plugin
  ; -----------------------------------------------------------

   ; If it is failed to extract the installation directory from command lines,
   ; find the keyword "/D=", extract the installation directory, and set it
   ; to the variable $INSTDIR.

   Push $R0
   Push $R1
   Push $R2
   Push $R3  ; length of commad line

   StrCpy $R2 0
   StrLen $R3 $CMDLINE

   loop:
     StrCpy $R0 $CMDLINE 3 $R2
     StrCpy $R1 "/D="
     StrCmp $R0 $R1 get
     StrCmp $R2 $R3 end
     IntOp $R2 $R2 + 1
     Goto loop

   get:
     IntOp $R2 $R2 + 3
     IntOp $R1 $R3 - $R2
     IntOp $R1 $R1 - 1
     StrCpy $R0 $CMDLINE $R1 $R2
     StrCpy $INSTDIR $R0

   end:

   Pop $R3
   Pop $R2
   Pop $R1
   Pop $R0
FunctionEnd

; -----------------------------------------------------------
; begin UAC plugin : elevete user privileges to admin
; -----------------------------------------------------------
Function .OnInstFailed
  UAC::Unload ; Must call unload!
FunctionEnd

Function .OnInstSuccess
  UAC::Unload ; Must call unload!
FunctionEnd
; -----------------------------------------------------------
; end of UAC plugin
; -----------------------------------------------------------


Section "Samura Graph" SecCopyUI
  call CheckVersion

  WriteRegStr HKLM "${SG_REGKEY}" "" $INSTDIR
  WriteRegDWORD HKLM "${SG_REGKEY}" "VersionMajor"   "${SG_VER_MAJOR}"
  WriteRegDWORD HKLM "${SG_REGKEY}" "VersionMinor"   "${SG_VER_MINOR}"
  WriteRegDWORD HKLM "${SG_REGKEY}" "VersionMicro"   "${SG_VER_MICRO}"

  ;Install Files

  ; main binaries
  SetOutPath "$INSTDIR"
  File samurai-graph.exe
;  File samurai-graph-console.exe
  File resources\samurai-graph.ini
  File ..\..\dist\COPYING.txt
  File ..\..\dist\LICENSE.txt
  File resources\${SG_PROPERTY_ICONFILE}
  File resources\${SG_ARCHIVE_ICONFILE}
  File resources\${SG_SCRIPT_ICONFILE}
  File ..\..\dist\samurai-graph.jar
;  File ..\..\dist\samurai-graph-client.jar

  ; update helper
  SetOutPath "$INSTDIR\lib"
  File ..\..\helper\lib\upgrade-helper.jar

  ; vectorgraphics
  SetOutPath "$INSTDIR\lib\vectorgraphics"
  File ..\..\dist\lib\vectorgraphics\freehep-export-2.1.1.jar
  File ..\..\dist\lib\vectorgraphics\freehep-graphics2d-2.1.1.jar
  File ..\..\dist\lib\vectorgraphics\freehep-graphicsio-2.1.1.jar
  File ..\..\dist\lib\vectorgraphics\freehep-graphicsio-cgm-2.1-SNAPSHOT.jar
  File ..\..\dist\lib\vectorgraphics\freehep-graphicsio-emf-2.1.1.jar
  File ..\..\dist\lib\vectorgraphics\freehep-graphicsio-java-2.1.1.jar
  File ..\..\dist\lib\vectorgraphics\freehep-graphicsio-pdf-2.1.1.jar
  File ..\..\dist\lib\vectorgraphics\freehep-graphicsio-ps-2.1.1.jar
  File ..\..\dist\lib\vectorgraphics\freehep-graphicsio-svg-2.1.1.jar
  File ..\..\dist\lib\vectorgraphics\freehep-graphicsio-swf-2.1.1.jar
  File ..\..\dist\lib\vectorgraphics\freehep-io-2.0.2.jar
  File ..\..\dist\lib\vectorgraphics\freehep-swing-2.0.3.jar
  File ..\..\dist\lib\vectorgraphics\freehep-util-2.0.2.jar
  File ..\..\dist\lib\vectorgraphics\freehep-xml-2.1.1.jar
  File ..\..\dist\lib\vectorgraphics\openide-lookup-1.9-patched-1.0.jar

  SetOutPath "$INSTDIR\doc\vectorgraphics"
  File ..\..\dist\doc\vectorgraphics\LGPL.txt
  File ..\..\dist\doc\vectorgraphics\LICENSE.txt
  File ..\..\dist\doc\vectorgraphics\README.txt
  File ..\..\dist\doc\vectorgraphics\SAMURAIGRAPH-ChangeLog.txt
  File ..\..\dist\doc\vectorgraphics\ReleaseNotes-2.1.1.html

  ; foxtrot
  SetOutPath "$INSTDIR\lib\foxtrot"
  File ..\..\dist\lib\foxtrot\foxtrot.jar

  SetOutPath "$INSTDIR\doc\foxtrot"
  File ..\..\dist\doc\foxtrot\README
  File ..\..\dist\doc\foxtrot\LICENSE
  File ..\..\dist\doc\foxtrot\SAMURAIGRAPH-ChangeLog.txt

  ; netCDF
  SetOutPath "$INSTDIR\lib\netCDF"
  File ..\..\dist\lib\netCDF\netcdf-4.2.jar
  File ..\..\dist\lib\netCDF\log4j-1.2.15.jar
  File ..\..\dist\lib\netCDF\slf4j-api-1.5.6.jar
  File ..\..\dist\lib\netCDF\slf4j-log4j12-1.5.6.jar
  File ..\..\dist\lib\netCDF\jdom.jar
  File ..\..\dist\lib\netCDF\commons-codec-1.3.jar
  File ..\..\dist\lib\netCDF\commons-httpclient-3.1.jar
  File ..\..\dist\lib\netCDF\commons-logging-1.1.jar

  SetOutPath "$INSTDIR\doc\netCDF"
  File ..\..\dist\doc\netCDF\README.txt
;  File ..\..\dist\doc\netCDF\LICENSE
  File ..\..\dist\doc\netCDF\SAMURAIGRAPH-ChangeLog.txt

  ; cisd-jhdf5
  SetOutPath "$INSTDIR\lib\cisd-jhdf5"
  File ..\..\dist\lib\cisd-jhdf5\cisd-args4j.jar
  File ..\..\dist\lib\cisd-jhdf5\cisd-base.jar
  File ..\..\dist\lib\cisd-jhdf5\cisd-jhdf5.jar
  File ..\..\dist\lib\cisd-jhdf5\cisd-jhdf5-batteries_included_lin_win_mac_sun.jar
  File ..\..\dist\lib\cisd-jhdf5\cisd-jhdf5-core.jar
  File ..\..\dist\lib\cisd-jhdf5\cisd-jhdf5-tools.jar
  File ..\..\dist\lib\cisd-jhdf5\commons-io.jar
  File ..\..\dist\lib\cisd-jhdf5\commons-lang.jar

  SetOutPath "$INSTDIR\doc\cisd-jhdf5"
  File ..\..\dist\doc\cisd-jhdf5\CONTENT
  File ..\..\dist\doc\cisd-jhdf5\COPYING
  File ..\..\dist\doc\cisd-jhdf5\COPYING-args4j
  File ..\..\dist\doc\cisd-jhdf5\COPYING-commons
  File ..\..\dist\doc\cisd-jhdf5\COPYING-hdf5
  File ..\..\dist\doc\cisd-jhdf5\LICENSE

  ; jmatio
  SetOutPath "$INSTDIR\lib\jmatio"
  File ..\..\dist\lib\jmatio\jamtio.jar

  SetOutPath "$INSTDIR\doc\jmatio"
  File ..\..\dist\doc\jmatio\license.txt
  File ..\..\dist\doc\jmatio\readme.txt

  ; jna
  SetOutPath "$INSTDIR\lib\jna"
  File ..\..\dist\lib\jna\jna.jar
  File ..\..\dist\lib\jna\platform.jar

  SetOutPath "$INSTDIR\doc\jna"
  File ..\..\dist\doc\jna\license.txt
  File ..\..\dist\doc\jna\LGPL.txt

  ; juniversalchardet
  SetOutPath "$INSTDIR\lib\juniversalchardet"
  File ..\..\dist\lib\juniversalchardet\juniversalchardet-1.0.3.jar

  SetOutPath "$INSTDIR\doc\juniversalchardet"
  File ..\..\dist\doc\juniversalchardet\license.txt
  File ..\..\dist\doc\juniversalchardet\MPL.txt

  ; joda-time
  SetOutPath "$INSTDIR\lib\joda-time"
  File ..\..\dist\lib\joda-time\joda-time-2.1.jar

  SetOutPath "$INSTDIR\doc\joda-time"
  File ..\..\dist\doc\joda-time\LICENSE.txt
  File ..\..\dist\doc\joda-time\NOTICE.txt

  ; examples
  SetOutPath "$INSTDIR\examples"
  File ..\..\examples\data\ReadMe.txt
  File ..\..\examples\data\Example1.txt
  File ..\..\examples\data\Example2.txt
  File ..\..\examples\data\Example3.txt
  File ..\..\examples\data\Example4.txt
  File ..\..\examples\data\Example5.txt
  File ..\..\examples\data\Example6.txt
  File ..\..\examples\data\Example7.txt
  File ..\..\examples\data\Example8.txt
  File ..\..\examples\data\Example9.txt
  File ..\..\examples\data\Example10.txt
  File ..\..\examples\data\Example11.txt
  File ..\..\examples\data\Example12.txt
  File ..\..\examples\data\Example13.txt
  File ..\..\examples\data\Example14.txt
  File ..\..\examples\data\Example15.nc
  File ..\..\examples\data\Example16.nc
  File ..\..\examples\data\Property1.sgp
  File ..\..\examples\data\Property2.sgp
  File ..\..\examples\data\Property3.sgp
  File ..\..\examples\data\Property4.sgp
  File ..\..\examples\data\Property5.sgp
  File ..\..\examples\data\Property6.sgp
  File ..\..\examples\data\Property7.sgp
  File ..\..\examples\data\Property8.sgp

  ;Create uninstaller
  WriteUninstaller "$INSTDIR\Uninstall.exe"

SectionEnd

;--------------------------------
; Section for Create shortcut on Desktop

Section "Desktop Shortcut" DesktopIcon
  SetShellVarContext all
  SetOutPath "$INSTDIR\"
  CreateShortCut  "$DESKTOP\${SG_NAME}.lnk" "$INSTDIR\samurai-graph.exe"
SectionEnd

;--------------------------------
; Section for Create shortcut on Start Menu

Section "Start Menu Shortcuts" StartMenu
  SetShellVarContext all
  SetOutPath "$INSTDIR\"
  CreateDirectory "$SMPROGRAMS\${SG_NAME}"
  CreateShortCut  "$SMPROGRAMS\${SG_NAME}\${SG_PRODUCT}.lnk" "$INSTDIR\samurai-graph.exe"
  CreateShortCut  "$SMPROGRAMS\${SG_NAME}\${SG_PRODUCT} Examples Directory.lnk" "$INSTDIR\examples"
  WriteINIStr "$SMPROGRAMS\${SG_NAME}\${SG_PRODUCT} Site.url" "InternetShortcut" "URL" "${SG_URL}"
  CreateShortCut  "$SMPROGRAMS\${SG_NAME}\Uninstall.lnk" "$INSTDIR\Uninstall.exe" "" "$INSTDIR\Uninstall.exe" 0
  Pop $R0
SectionEnd

;--------------------------------
; Section for Write Registry
Section -post
  Call RegistKeys
SectionEnd


!ifdef SG_SELECT_COMPONENTS
;--------------------------------
;Descriptions
  ;Assign language strings to sections
  !insertmacro MUI_FUNCTION_DESCRIPTION_BEGIN
  !insertmacro MUI_DESCRIPTION_TEXT ${SecCopyUI} $(DESC_SecCopyUI)
  !insertmacro MUI_DESCRIPTION_TEXT ${DesktopIcon} $(DESC_DesktopIcon)
  !insertmacro MUI_DESCRIPTION_TEXT ${StartMenu} $(DESC_StartMenu)
  !insertmacro MUI_FUNCTION_DESCRIPTION_END
!endif

;-------------------------
;Uninstaller Section

Function un.onInit

  ; -----------------------------------------------------------
  ; begin UAC plugin : elevete user privileges to admin
  ; -----------------------------------------------------------
  Push $R0
  Push $R1
  Push $R2
  Push $R3

  UAC_Elevate:
    UAC::RunElevated 
    StrCmp 1223 $0 UAC_ElevationAborted ; UAC dialog aborted by user?
    StrCmp 0 $0 0 UAC_Err ; Error?
    StrCmp 1 $1 0 UAC_Success ;Are we the real deal or just the wrapper?
    Quit
    
  UAC_Err:
    MessageBox MB_ICONSTOP "$(DESC_UAC_Error) - Error $0"
    Abort

  UAC_ElevationAborted:
    ; elevation was aborted, run as normal?
    MessageBox MB_ICONSTOP "$(DESC_UAC_ReqAdmin)$\r$\r$(DESC_UAC_Aborted)"
    Abort

  UAC_Success:
    StrCmp 1 $3 +4 ; is Admin?
    StrCmp 3 $1 0 UAC_ElevationAborted ; Try again?
    MessageBox MB_ICONSTOP "$(DESC_UAC_ReqAdmin)$\r$\r$(DESC_UAC_TryAgain)"
    goto UAC_Elevate

  Pop $R3
  Pop $R2
  Pop $R1
  Pop $R0
  ; -----------------------------------------------------------
  ; end of UAC plugin
  ; -----------------------------------------------------------
FunctionEnd

; -----------------------------------------------------------
; begin UAC plugin : elevete user privileges to admin
; -----------------------------------------------------------
Function un.OnUninstFailed
    UAC::Unload ; Must call unload!
FunctionEnd

Function un.OnUninstSuccess
    UAC::Unload ; Must call unload!
FunctionEnd
; -----------------------------------------------------------
; end of UAC plugin
; -----------------------------------------------------------

Section "Uninstall"
  ;If Samurai Graph windows exist, close them.
  Call un.CloseProgram

  SetShellVarContext all

  ;Uninstall Files
  Delete "$INSTDIR\samurai-graph.exe"
;  Delete "$INSTDIR\samurai-graph-console.exe"
  Delete "$INSTDIR\samurai-graph.ini"

  Delete "$INSTDIR\COPYING.txt"
  Delete "$INSTDIR\LICENSE.txt"
  Delete "$INSTDIR\samurai-graph.jar"
;  Delete "$INSTDIR\samurai-graph-client.jar"

  Delete "$INSTDIR\lib\upgrade-helper.jar"

  Delete "$INSTDIR\lib\vectorgraphics\freehep-export-2.1.1.jar"
  Delete "$INSTDIR\lib\vectorgraphics\freehep-graphics2d-2.1.1.jar"
  Delete "$INSTDIR\lib\vectorgraphics\freehep-graphicsio-2.1.1.jar"
  Delete "$INSTDIR\lib\vectorgraphics\freehep-graphicsio-cgm-2.1-SNAPSHOT.jar"
  Delete "$INSTDIR\lib\vectorgraphics\freehep-graphicsio-emf-2.1.1.jar"
  Delete "$INSTDIR\lib\vectorgraphics\freehep-graphicsio-java-2.1.1.jar"
  Delete "$INSTDIR\lib\vectorgraphics\freehep-graphicsio-pdf-2.1.1.jar"
  Delete "$INSTDIR\lib\vectorgraphics\freehep-graphicsio-ps-2.1.1.jar"
  Delete "$INSTDIR\lib\vectorgraphics\freehep-graphicsio-svg-2.1.1.jar"
  Delete "$INSTDIR\lib\vectorgraphics\freehep-graphicsio-swf-2.1.1.jar"
  Delete "$INSTDIR\lib\vectorgraphics\freehep-io-2.0.2.jar"
  Delete "$INSTDIR\lib\vectorgraphics\freehep-swing-2.0.3.jar"
  Delete "$INSTDIR\lib\vectorgraphics\freehep-util-2.0.2.jar"
  Delete "$INSTDIR\lib\vectorgraphics\freehep-xml-2.1.1.jar"
  Delete "$INSTDIR\lib\vectorgraphics\openide-lookup-1.9-patched-1.0.jar"

  Delete "$INSTDIR\doc\vectorgraphics\LGPL.txt"
  Delete "$INSTDIR\doc\vectorgraphics\LICENSE.txt"
  Delete "$INSTDIR\doc\vectorgraphics\README.txt"
  Delete "$INSTDIR\doc\vectorgraphics\SAMURAIGRAPH-ChangeLog.txt"
  Delete "$INSTDIR\doc\vectorgraphics\ReleaseNotes-2.1.1.html"

  Delete "$INSTDIR\lib\foxtrot\foxtrot.jar"
  Delete "$INSTDIR\doc\foxtrot\README"
  Delete "$INSTDIR\doc\foxtrot\LICENSE"
  Delete "$INSTDIR\doc\foxtrot\SAMURAIGRAPH-ChangeLog.txt"

  Delete "$INSTDIR\lib\netCDF\netcdf-4.2.jar"
  Delete "$INSTDIR\lib\netCDF\log4j-1.2.15.jar"
  Delete "$INSTDIR\lib\netCDF\slf4j-api-1.5.6.jar"
  Delete "$INSTDIR\lib\netCDF\slf4j-log4j12-1.5.6.jar"
  Delete "$INSTDIR\lib\netCDF\jdom.jar"
  Delete "$INSTDIR\lib\netCDF\commons-codec-1.3.jar"
  Delete "$INSTDIR\lib\netCDF\commons-httpclient-3.1.jar"
  Delete "$INSTDIR\lib\netCDF\commons-logging-1.1.jar"
  Delete "$INSTDIR\doc\netCDF\README.txt"
;  Delete "$INSTDIR\doc\netCDF\LICENSE"
  Delete "$INSTDIR\doc\netCDF\SAMURAIGRAPH-ChangeLog.txt"

  Delete "$INSTDIR\lib\cisd-jhdf5\cisd-args4j.jar"
  Delete "$INSTDIR\lib\cisd-jhdf5\cisd-base.jar"
  Delete "$INSTDIR\lib\cisd-jhdf5\cisd-jhdf5.jar"
  Delete "$INSTDIR\lib\cisd-jhdf5\cisd-jhdf5-batteries_included_lin_win_mac_sun.jar"
  Delete "$INSTDIR\lib\cisd-jhdf5\cisd-jhdf5-core.jar"
  Delete "$INSTDIR\lib\cisd-jhdf5\cisd-jhdf5-tools.jar"
  Delete "$INSTDIR\lib\cisd-jhdf5\commons-io.jar"
  Delete "$INSTDIR\lib\cisd-jhdf5\commons-lang.jar"
  Delete "$INSTDIR\doc\cisd-jhdf5\CONTENT"
  Delete "$INSTDIR\doc\cisd-jhdf5\COPYING"
  Delete "$INSTDIR\doc\cisd-jhdf5\COPYING-args4j"
  Delete "$INSTDIR\doc\cisd-jhdf5\COPYING-commons"
  Delete "$INSTDIR\doc\cisd-jhdf5\COPYING-hdf5"
  Delete "$INSTDIR\doc\cisd-jhdf5\LICENSE"

  Delete "$INSTDIR\lib\jmatio\jamtio.jar"
  Delete "$INSTDIR\doc\jmatio\license.txt"
  Delete "$INSTDIR\doc\jmatio\readme.txt"

  Delete "$INSTDIR\lib\jna\jna.jar"
  Delete "$INSTDIR\lib\jna\platform.jar"
  Delete "$INSTDIR\doc\jna\license.txt"
  Delete "$INSTDIR\doc\jna\LGPL.txt"

  Delete "$INSTDIR\lib\juniversalchardet\juniversalchardet-1.0.3.jar"
  Delete "$INSTDIR\doc\juniversalchardet\license.txt"
  Delete "$INSTDIR\doc\juniversalchardet\MPL.txt"

  Delete "$INSTDIR\lib\joda-time\joda-time-2.1.jar"
  Delete "$INSTDIR\doc\joda-time\LICENSE.txt"
  Delete "$INSTDIR\doc\joda-time\NOTICE.txt"

  Delete "$INSTDIR\examples\ReadMe.txt"
  Delete "$INSTDIR\examples\Example1.txt"
  Delete "$INSTDIR\examples\Example2.txt"
  Delete "$INSTDIR\examples\Example3.txt"
  Delete "$INSTDIR\examples\Example4.txt"
  Delete "$INSTDIR\examples\Example5.txt"
  Delete "$INSTDIR\examples\Example6.txt"
  Delete "$INSTDIR\examples\Example7.txt"
  Delete "$INSTDIR\examples\Example8.txt"
  Delete "$INSTDIR\examples\Example9.txt"
  Delete "$INSTDIR\examples\Example10.txt"
  Delete "$INSTDIR\examples\Example11.txt"
  Delete "$INSTDIR\examples\Example12.txt"
  Delete "$INSTDIR\examples\Example13.txt"
  Delete "$INSTDIR\examples\Example14.txt"
  Delete "$INSTDIR\examples\Example15.nc"
  Delete "$INSTDIR\examples\Example16.nc"
  Delete "$INSTDIR\examples\Property1.sgp"
  Delete "$INSTDIR\examples\Property2.sgp"
  Delete "$INSTDIR\examples\Property3.sgp"
  Delete "$INSTDIR\examples\Property4.sgp"
  Delete "$INSTDIR\examples\Property5.sgp"
  Delete "$INSTDIR\examples\Property6.sgp"
  Delete "$INSTDIR\examples\Property7.sgp"
  Delete "$INSTDIR\examples\Property8.sgp"

  Delete "$INSTDIR\Uninstall.exe"

  ; desktop
  Delete "$DESKTOP\${SG_PRODUCT} ${SG_VERSION}.lnk"

  ; start menu
  Delete "$SMPROGRAMS\${SG_PRODUCT} ${SG_VERSION}\${SG_PRODUCT}.lnk"
  Delete "$SMPROGRAMS\${SG_PRODUCT} ${SG_VERSION}\${SG_PRODUCT} Examples Directory.lnk"
  Delete "$SMPROGRAMS\${SG_PRODUCT} ${SG_VERSION}\${SG_PRODUCT} Site.url"
  Delete "$SMPROGRAMS\${SG_PRODUCT} ${SG_VERSION}\Uninstall.lnk"
  RMDir  "$SMPROGRAMS\${SG_PRODUCT} ${SG_VERSION}"

  RMDir "$INSTDIR\lib\vectorgraphics"
  RMDir "$INSTDIR\lib"
  RMDir "$INSTDIR\doc\vectorgraphics"
  RMDir "$INSTDIR\doc"
  RMDir "$INSTDIR\examples\data"
  RMDir "$INSTDIR\examples"
  RMDir /r "$INSTDIR"

  ; delete registry
  Call un.RegistKeys

SectionEnd

;; ----------------- function utilities ---------------------

!include "WinMessages.nsh"

Function CheckVersion
  SetShellVarContext all
  ;Check an old version
  Push $R0
  Push $R1
  Push $R2
  Push $R3
  ReadRegStr $R0 HKLM "${SG_REGKEY}" ""
  StrCmp $R0 "" NoOldVer
  ReadRegDWORD $R1 HKLM "${SG_REGKEY}" "VersionMajor"
  ReadRegDWORD $R2 HKLM "${SG_REGKEY}" "VersionMinor"
  ReadRegDWORD $R3 HKLM "${SG_REGKEY}" "VersionMicro"
  IntCmp $R1 ${SG_VER_MAJOR} IsSameMajorVer MessageOldVer MessageNewVer
IsSameMajorVer:
  IntCmp $R2 ${SG_VER_MINOR} IsSameMinorVer MessageOldVer MessageNewVer
IsSameMinorVer:
  IntCmp $R3 ${SG_VER_MICRO} MessageSameVer MessageOldVer MessageNewVer
MessageOldVer:
  MessageBox MB_YESNO|MB_ICONEXCLAMATION|MB_TOPMOST $(DESC_OverwriteOldVer) IDYES DoUnInstPrevInst
  goto DoAbortInst
MessageNewVer:
  MessageBox MB_YESNO|MB_ICONEXCLAMATION|MB_TOPMOST $(DESC_OverwriteNewVer) IDYES DoUnInstPrevInst
  goto DoAbortInst
MessageSameVer:
  MessageBox MB_YESNO|MB_ICONEXCLAMATION|MB_TOPMOST $(DESC_OverwriteSameVer) IDYES DoUnInstPrevInst
DoAbortInst:
  DetailPrint "The installation was canceled.."
  Abort
DoUnInstPrevInst:
  ExecWait '"$R0\Uninstall.exe" /S _?=$INSTDIR'
  IfErrors DoAbortInst
NoOldVer:
  Pop $R3
  Pop $R2
  Pop $R1
  Pop $R0
FunctionEnd


;;
;; regist registry keys
;;
Function RegistKeys
  SetShellVarContext all
  Push $R0
  ; Write to Registry Uninstall information
  WriteRegExpandStr HKLM "${SG_UNINST_REGKEY}" "UninstallString" '"$INSTDIR\Uninstall.exe"'
  WriteRegExpandStr HKLM "${SG_UNINST_REGKEY}" "InstallLocation" "$INSTDIR"
  WriteRegStr   HKLM "${SG_UNINST_REGKEY}" "DisplayName" "${SG_NAME}"
  WriteRegStr   HKLM "${SG_UNINST_REGKEY}" "DisplayIcon" "$INSTDIR\samurai-graph.exe,0"
  WriteRegStr   HKLM "${SG_UNINST_REGKEY}" "DisplayVersion" "${SG_VERSION}"
  WriteRegDWORD HKLM "${SG_UNINST_REGKEY}" "VersionMajor"   "${SG_VER_MAJOR}"
  WriteRegDWORD HKLM "${SG_UNINST_REGKEY}" "VersionMinor"   "${SG_VER_MINOR}"
  WriteRegStr   HKLM "${SG_UNINST_REGKEY}" "Publisher"      "${SG_PUBLISHER}"
  WriteRegStr   HKLM "${SG_UNINST_REGKEY}" "URLInfoAbout"   "${SG_PUBLISHER_URL}"
  WriteRegStr   HKLM "${SG_UNINST_REGKEY}" "HelpLink"       "${SG_URL}"
  WriteRegStr   HKLM "${SG_UNINST_REGKEY}" "URLUpdateInfo"  "${SG_URL}"
  WriteRegDWORD HKLM "${SG_UNINST_REGKEY}" "NoModify" "1"
  WriteRegDWORD HKLM "${SG_UNINST_REGKEY}" "NoRepair" "1"

  ; Write to Registry Samurai Graph Property File Icon
  WriteRegStr HKCR "${SG_PROPERTY_REGKEY}" "" "${SG_PROPERTY_LABEL}"
  WriteRegStr HKCR "${SG_PROPERTY_REGKEY}\DefaultIcon" "" "$INSTDIR\${SG_PROPERTY_ICONFILE},0"
  ReadRegStr $R0 HKCR "${SG_PROPERTY_EXT_REGKEY}" ""
  StrCmp $R0 "" 0 DoWriteProp
  StrCmp $R0 "${SG_PROPERTY_LABEL}" 0 DoWriteProp
  WriteRegStr HKCR "${SG_PROPERTY_EXT_REGKEY}" "${SG_PROPERTY_EXT_BACKUP_REGKEY}" $R0
DoWriteProp:
  WriteRegStr HKCR "${SG_PROPERTY_EXT_REGKEY}" "" "${SG_PROPERTY_REGKEY}"

  ; Write to Registry Samurai Graph Property File Association 
  SetOutPath "$INSTDIR\"
  WriteRegStr HKCR "${SG_PROPERTY_REGKEY}\shell\open\command" "" '"$INSTDIR\samurai-graph.exe" -prop "%1"'

  ; Write to Registry Samurai Graph Archive File Icon
  WriteRegStr HKCR "${SG_ARCHIVE_REGKEY}" "" "${SG_ARCHIVE_LABEL}"
  WriteRegStr HKCR "${SG_ARCHIVE_REGKEY}\DefaultIcon" "" "$INSTDIR\${SG_ARCHIVE_ICONFILE},0"
  ReadRegStr $R0 HKCR "${SG_ARCHIVE_EXT_REGKEY}" ""
  StrCmp $R0 "" 0 DoWriteArc
  StrCmp $R0 "${SG_ARCHIVE_LABEL}" 0 DoWriteArc
  WriteRegStr HKCR "${SG_ARCHIVE_EXT_REGKEY}" "${SG_ARCHIVE_EXT_BACKUP_REGKEY}" $R0
DoWriteArc:
  WriteRegStr HKCR "${SG_ARCHIVE_EXT_REGKEY}" "" "${SG_ARCHIVE_REGKEY}"

  ; Write to Registry Samurai Graph Archive File Association 
  SetOutPath "$INSTDIR\"
  WriteRegStr HKCR "${SG_ARCHIVE_REGKEY}\shell\open\command" "" '"$INSTDIR\samurai-graph.exe" -dataset "%1"'

  ; Write to Registry Samurai Graph Script File Icon
  WriteRegStr HKCR "${SG_SCRIPT_REGKEY}" "" "${SG_SCRIPT_LABEL}"
  WriteRegStr HKCR "${SG_SCRIPT_REGKEY}\DefaultIcon" "" "$INSTDIR\${SG_SCRIPT_ICONFILE},0"
  ReadRegStr $R0 HKCR "${SG_SCRIPT_EXT_REGKEY}" ""
  StrCmp $R0 "" 0 DoWriteScr
  StrCmp $R0 "${SG_SCRIPT_LABEL}" 0 DoWriteScr
  WriteRegStr HKCR "${SG_SCRIPT_EXT_REGKEY}" "${SG_SCRIPT_EXT_BACKUP_REGKEY}" $R0
DoWriteScr:
  WriteRegStr HKCR "${SG_SCRIPT_EXT_REGKEY}" "" "${SG_SCRIPT_REGKEY}"

  ; Write to Registry Samurai Graph Script File Association 
  SetOutPath "$INSTDIR\"
  WriteRegStr HKCR "${SG_SCRIPT_REGKEY}\shell\open\command" "" '"$INSTDIR\samurai-graph.exe" -s "%1"'

  Pop $R0
FunctionEnd

;;
;; close running samurai graph application.
;;
Function un.CloseProgram
;; for Java 6
;; since Java 6, windows adapter was many changed.
;; 1. window class name was changed from java class path to fixed common 
;;    string for all awt window panels. 
;;    thus this function will try to get window handle by window title.
;; 2. window panel may not recive WM_CLOSE window message.
;;    thus this function will wait for user operation to terminate process.
  Push $R0
  Push $R1
  StrCpy $R1 1
 loop:
    FindWindow $R0 "" "Samurai Graph - Window : $R1"
    IntCmp $R0 0 next
    MessageBox MB_OKCANCEL|MB_ICONEXCLAMATION|MB_TOPMOST $(DESC_CloseProgram) IDOK loop
    DetailPrint "The uninstallation was canceled.."
    Abort
 next:
  IntCmp $R1 30 done
  IntOp $R1 $R1 + 1
  Goto loop
 done:
  Pop $R1
  Pop $R0
FunctionEnd

;;
;; delete registry keys
;;
Function un.RegistKeys
  SetShellVarContext all
  Push $R0
  ; delete regkeys for uninstall information 
  DeleteRegValue HKLM "${SG_REGKEY}" "VersionMajor"
  DeleteRegValue HKLM "${SG_REGKEY}" "VersionMinor"
  DeleteRegValue HKLM "${SG_REGKEY}" "VersionMicro"
  DeleteRegKey /ifempty HKLM "${SG_REGKEY}"
  DeleteRegKey HKLM "${SG_UNINST_REGKEY}"

  ; delete regkeys for property file
  DeleteRegKey HKCR "${SG_PROPERTY_EXT_REGKEY}"
  ReadRegStr $R0 HKCR "${SG_PROPERTY_EXT_REGKEY}" ""
  StrCmp $R0 "${SG_PROPERTY_LABEL}" 0 NoOwnProp
  ReadRegStr $R0 HKCR "${SG_PROPERTY_EXT_REGKEY}" "${SG_PROPERTY_EXT_BACKUP_REGKEY}"
  StrCmp $R0 "" 0 RestoreBackupProp
  DeleteRegKey HKCR "${SG_PROPERTY_EXT_REGKEY}"
  Goto NoOwnProp
RestoreBackupProp:
  WriteRegStr HKCR "${SG_PROPERTY_EXT_REGKEY}" "" $R0
  DeleteRegValue HKCR "${SG_PROPERTY_EXT_REGKEY}" "${SG_PROPERTY_EXT_BACKUP_REGKEY}"
NoOwnProp:

  ; delete regkeys for archive file
  DeleteRegKey HKCR "${SG_ARCHIVE_EXT_REGKEY}"
  ReadRegStr $R0 HKCR "${SG_ARCHIVE_EXT_REGKEY}" ""
  StrCmp $R0 "${SG_ARCHIVE_LABEL}" 0 NoOwnArc
  ReadRegStr $R0 HKCR "${SG_ARCHIVE_EXT_REGKEY}" "${SG_ARCHIVE_EXT_BACKUP_REGKEY}"
  StrCmp $R0 "" 0 RestoreBackupArc
  DeleteRegKey HKCR "${SG_ARCHIVE_EXT_REGKEY}"
  Goto NoOwnArc
RestoreBackupArc:
  WriteRegStr HKCR "${SG_ARCHIVE_EXT_REGKEY}" "" $R0
  DeleteRegValue HKCR "${SG_ARCHIVE_EXT_REGKEY}" "${SG_ARCHIVE_EXT_BACKUP_REGKEY}"
NoOwnArc:

  ; delete regkeys for script file
  DeleteRegKey HKCR "${SG_SCRIPT_EXT_REGKEY}"
  ReadRegStr $R0 HKCR "${SG_SCRIPT_EXT_REGKEY}" ""
  StrCmp $R0 "${SG_SCRIPT_LABEL}" 0 NoOwnScr
  ReadRegStr $R0 HKCR "${SG_SCRIPT_EXT_REGKEY}" "${SG_SCRIPT_EXT_BACKUP_REGKEY}"
  StrCmp $R0 "" 0 RestoreBackupScr
  DeleteRegKey HKCR "${SG_SCRIPT_EXT_REGKEY}"
  Goto NoOwnScr
RestoreBackupScr:
  WriteRegStr HKCR "${SG_SCRIPT_EXT_REGKEY}" "" $R0
  DeleteRegValue HKCR "${SG_SCRIPT_EXT_REGKEY}" "${SG_SCRIPT_EXT_BACKUP_REGKEY}"
NoOwnScr:

  Pop $R0
FunctionEnd
