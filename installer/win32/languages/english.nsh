;
; Installer script
; Language file: English

!ifndef LANGUAGE_ENGLISH_USED
  !define LANGUAGE_ENGLISH_USED

  ; sections descriptions
  LangString DESC_SecCopyUI ${LANG_ENGLISH} "Copy the ${SG_NAME} application to your local folder."
  LangString DESC_DesktopIcon ${LANG_ENGLISH} "Create a shortcut to your desktop."
  LangString DESC_StartMenu ${LANG_ENGLISH} "Create a shortcut to your start menu."

  ; message boxes
  LangString DESC_OverwriteOldVer ${LANG_ENGLISH} "An old installation of ${SG_PRODUCT} $R1.$R2.$R3 was detected. $\rThis poses normally no problem, $\r$\rDo you want to overwrite the old installation?"
  LangString DESC_OverwriteNewVer ${LANG_ENGLISH} "You are trying to install a version of ${SG_NAME} older than the one already installed on your computer. Are you sure you want to continue?"
  LangString DESC_OverwriteSameVer ${LANG_ENGLISH} "The version of ${SG_NAME} installed on your computer is the same than the one you are trying to install now. Are you sure you want to continue?"

  ; java
  LangString DESC_JreNotFound ${LANG_ENGLISH} "The Java Runtime Environment was not detected. $\r$\rPlease download Java software from Java download page$\r$(DESC_JavaUrl)$\rand install it before you continue.$\r$\rThe ${SG_NAME} installation will cancel."
  LangString DESC_JavaUrl ${LANG_ENGLISH} "http://www.java.com/en/download/manual.jsp"

  ; UAC
  LangString DESC_UAC_Error ${LANG_ENGLISH} "Unable to elevate user privileges."
  LangString DESC_UAC_ReqAdmin ${LANG_ENGLISH} "This program requires admin access."
  LangString DESC_UAC_Aborted ${LANG_ENGLISH} "Aborting.."
  LangString DESC_UAC_TryAgain ${LANG_ENGLISH} "Try again."

  ; close program
  LangString DESC_CloseProgram ${LANG_ENGLISH} "Samurai Graph process found.$\r$\rPlease close the program before continue."

!endif
