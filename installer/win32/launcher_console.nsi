;--------------
; Java Launcher
;--------------
!include "x64.nsh"
!include "common.nsh"

SetCompressor /SOLID lzma
 
; generic 
!define SG_FILE_NAME "samurai-graph-console.exe"

Name    "${SG_PRODUCT}"
Caption "${SG_PRODUCT}"
Icon    "resources/samurai-graph.ico"
OutFile ${SG_FILE_NAME}
 
SilentInstall silent
RequestExecutionLevel user
AutoCloseWindow true
ShowInstDetails nevershow

; version info
VIProductVersion "${SG_PRODUCT_VERSION}"
VIAddVersionKey "ProductName"      "${SG_PRODUCT}"
VIAddVersionKey "Comments"         "${SG_COMMENT}"
VIAddVersionKey "CompanyName"      "${SG_COMPANY}"
VIAddVersionKey "ProductVersion"   "${SG_PRODUCT_VERSION}"
VIAddVersionKey "LegalCopyright"   "${SG_COPYRIGHT}"
VIAddVersionKey "FileDescription"  "Java launcher for Samurai Graph"
VIAddVersionKey "FileVersion"      "1.0.0"
VIAddVersionKey "InternalName"     "${SG_FILE_NAME}"
VIAddVersionKey "OriginalFilename" "${SG_FILE_NAME}"

!define BASENAME    "samurai-graph"
!define JARFILE     "${BASENAME}.jar"
!define INIFILE     "${BASENAME}.ini"
!define DEFAULT_JAVAVM "java.exe"
!define JAVAVM_STR  "javavm"
!define VMARGS_STR  "vmargs"

;;
;; main
;;
Section ""
  SetShellVarContext all
  ReadINIStr $R0 "$EXEDIR\${INIFILE}" ${BASENAME} ${JAVAVM_STR}
  ClearErrors
  Push $R0
  Call CheckJava
  Pop $R0
  Call GetParameters
  Pop $R1
  ReadINIStr $R2 "$EXEDIR\${INIFILE}" ${BASENAME} ${VMARGS_STR}
  ClearErrors
  StrCpy $0 '"$R0" $R2 -jar "$EXEDIR\${JARFILE}" $R1 -i'
;  SetOutPath $EXEDIR
  Exec $0
SectionEnd

; GetParameters
; input, none
; output, top of stack (replaces, with e.g. whatever)
; modifies no other variables.
Function GetParameters
  Push $R0
  Push $R1
  Push $R2
  Push $R3
  
  StrCpy $R2 1
  StrLen $R3 $CMDLINE
  
  ;Check for quote or space
  StrCpy $R0 $CMDLINE $R2
  StrCmp $R0 '"' 0 +3
  StrCpy $R1 '"'
  Goto loop
  StrCpy $R1 " "
  
 loop:
  IntOp $R2 $R2 + 1
  StrCpy $R0 $CMDLINE 1 $R2
  StrCmp $R0 $R1 get
  StrCmp $R2 $R3 get
  Goto loop
  
 get:
  IntOp $R2 $R2 + 1
  StrCpy $R0 $CMDLINE 1 $R2
  StrCmp $R0 " " get
  StrCpy $R0 $CMDLINE "" $R2
  
  Pop $R3
  Pop $R2
  Pop $R1
  Exch $R0
FunctionEnd

;;
;; Java environement checker
;;
!define JAVA_DEFAULT_VM  "java.exe"
!define JAVA_URL         "http://www.java.com/"
!define JAVA_NOT_FOUND   "The Java Runtime Environment was not detected. $\r$\rPlease download Java software from Java download page$\r${JAVA_URL}"

;;
;; Check Java installed
;;
Function CheckJava
  Exch $R0
  StrCmp $R0 "" SetDefaultJavaVm
  goto CheckJavaVm
 SetDefaultJavaVm:
  StrCpy $R0 ${JAVA_DEFAULT_VM}
 CheckJavaVm:
  Push $R0
; Check JRE installed
  Call GetJRE
  Pop $R0
  StrCmp $R0 "" JreNotFound
  goto JreFound
 JreNotFound:
  ExecShell "open" ${JAVA_URL}
  MessageBox MB_OK|MB_ICONSTOP|MB_TOPMOST "${JAVA_NOT_FOUND}"
  DetailPrint "The process was stoped.."
  Abort
 JreFound:
  Exch $R0
FunctionEnd

;;
;;  Get JRE
;;
;;  @param  stack1 : Java VM command (javaw.exe or java.exe)
;;  @return stack1 : full path for Java VM
Function GetJRE
;  1 - in .\jre directory (JRE Installed with application)
;  2 - in JAVA_HOME environment variable
;  3 - in the Java Runtime Environment registry 64-bit (if supported)
;  4 - in the Java Runtime Environment registry 32-bit
;  5 - in JDK_HOME environment variable
;  6 - in the Java Development Kit registry 64-bit (if supported)
;  7 - in the Java Development Kit registry 32-bit
;  8 - give up..

  Exch $R0
  Push $R1
  Push $R2

; JreCheck1:
  ClearErrors
  StrCpy $R1 "$EXEDIR\jre\bin\$R0"
  IfFileExists $R1 JreFound

; JreCheck2:
  ClearErrors
  StrCpy $R1 ""
  ReadEnvStr $R1 "JAVA_HOME"
  IfErrors JreCheck3
  StrCpy $R1 "$R1\bin\$R0"
  IfFileExists $R1 JreFound

 JreCheck3:
  ClearErrors
  ${If} ${RunningX64}
    SetRegView 64
    ReadRegStr $R2 HKLM "SOFTWARE\JavaSoft\Java Runtime Environment" "CurrentVersion"
    ReadRegStr $R1 HKLM "SOFTWARE\JavaSoft\Java Runtime Environment\$R2" "JavaHome"
    IfErrors JreCheck4
    StrCpy $R1 "$R1\bin\$R0"
    IfFileExists $R1 JreFound
  ${EndIf}

 JreCheck4:
  ClearErrors
  ${If} ${RunningX64}
    SetRegView 32
  ${EndIf}
  ReadRegStr $R2 HKLM "SOFTWARE\JavaSoft\Java Runtime Environment" "CurrentVersion"
  ReadRegStr $R1 HKLM "SOFTWARE\JavaSoft\Java Runtime Environment\$R2" "JavaHome"
  IfErrors JreCheck5
  StrCpy $R1 "$R1\bin\$R0"
  IfFileExists $R1 JreFound

 JreCheck5:
  ClearErrors
  StrCpy $R1 ""
  ReadEnvStr $R1 "JDK_HOME"
  IfErrors JreCheck6
  StrCpy $R1 "$R1\bin\$R0"
  IfFileExists $R1 JreFound

 JreCheck6:
  ClearErrors
  ${If} ${RunningX64}
    SetRegView 64
    ReadRegStr $R2 HKLM "SOFTWARE\JavaSoft\Java Development Kit" "CurrentVersion"
    ReadRegStr $R1 HKLM "SOFTWARE\JavaSoft\Java Development Kit\$R2" "JavaHome"
    IfErrors JreCheck7
    StrCpy $R1 "$R1\jre\bin\$R0"
    IfFileExists $R1 JreFound
  ${EndIf}

 JreCheck7:
  ClearErrors
  ${If} ${RunningX64}
    SetRegView 32
  ${EndIf}
  ReadRegStr $R2 HKLM "SOFTWARE\JavaSoft\Java Development Kit" "CurrentVersion"
  ReadRegStr $R1 HKLM "SOFTWARE\JavaSoft\Java Development Kit\$R2" "JavaHome"
  IfErrors JreCheck8
  StrCpy $R1 "$R1\jre\bin\$R0"
  IfFileExists $R1 JreFound

 JreCheck8:
  ClearErrors
  StrCpy $R1 ""

 JreFound:
  StrCpy $R0 $R1
  Pop $R2
  Pop $R1
  Exch $R0
FunctionEnd

