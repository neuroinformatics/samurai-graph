;Samurai Graph installer script for Win32
;
; Language file: Japanese
;

!ifndef LANGUAGE_JAPANESE_USED
  !define LANGUAGE_JAPANESE_USED

  ; section descriptions
  LangString DESC_SecCopyUI ${LANG_JAPANESE} "${SG_NAME} アプリケーションをローカルフォルダにコピーします。"

  LangString DESC_DesktopIcon ${LANG_JAPANESE} "デスクトップにショートカットを作ります。"

  LangString DESC_StartMenu ${LANG_JAPANESE} "スタートメニューにショートカットを作ります。"

  ; message boxs
  LangString DESC_OverwriteOldVer ${LANG_JAPANESE} "古いバージョンの ${SG_PRODUCT} $R1.$R2.$R3 を検知しました。$\r$\rこれらのファイルを削除して、このインストールを継続しますか？"

  LangString DESC_OverwriteNewVer ${LANG_JAPANESE} "より新しいバージョンの ${SG_PRODUCT} $R1.$R2.$R3 が既にインストールされています。$\r$\r本当にインストールしますか？"

  LangString DESC_OverwriteSameVer ${LANG_JAPANESE} "同じバージョンの ${SG_PRODUCT} $R1.$R2.$R3 が既にインストールされています。$\r$\rこれらのファイルを削除して、このインストールを継続しますか？"

  ; java
  LangString DESC_JreNotFound ${LANG_JAPANESE} "Java 実行環境が見つかりませんでした。$\r$\rJava のダウンロードページ$\r$(DESC_JavaUrl)$\rから Java を入手してインストールしてください。$\r$\r${SG_NAME} のインストールを中断します。"
  LangString DESC_JavaUrl ${LANG_JAPANESE} "http://www.java.com/ja/download/manual.jsp"

  ; UAC
  LangString DESC_UAC_Error ${LANG_JAPANESE} "ユーザ権限の昇格に失敗しました。"
  LangString DESC_UAC_ReqAdmin ${LANG_JAPANESE} "プログラムの実行には管理者権限が必要です。"
  LangString DESC_UAC_Aborted ${LANG_JAPANESE} "処理を中止します。"
  LangString DESC_UAC_TryAgain ${LANG_JAPANESE} "再処理を試みます。"

  ; close program
  LangString DESC_CloseProgram ${LANG_JAPANESE} "Samurai Graph のプロセスが見つかりました。$\r$\r処理を継続するには先にプログラムを終了させて下さい。"
  
!endif
