# Samurai Graph - 改善計画

## 1. プロジェクト概要

Samurai Graphは、理化学研究所脳科学総合研究センターによって開発された科学用グラフ作成デスクトップアプリケーション（Java Swing）です。各種データ形式（CSV, NetCDF, HDF5, MATLAB）から論文掲載品質のグラフを生成します。

| 項目 | 詳細 |
|------|------|
| バージョン | 2.2.0 |
| Java | 21 |
| ビルド | Maven（maven-shade-pluginによるfat JAR） |
| ライセンス | GNU LGPL 2.1 |
| ソースファイル | 539 Javaファイル |
| コード行数 | 273,242行 |
| パッケージ | 7（`application`, `base`, `data`, `figure`, `figure.java2d`, `export`, `platform`） |
| 初回リリース | 2004-09-06（v0.1.1） |
| 最終changelogエントリー | 2010-11-23（v2.0.0） |
| 最近の活動 | Java 21への移行（2026-06） |

---

## 2. アーキテクチャ概要

本アプリケーションは、インターフェースベースの設計を採用したレイヤードMVC風のアーキテクチャを採用しています。

```
application/    -- エントリポイント、ウィンドウ/データ/コマンド管理、ウィザード、プラグインシステム
base/           -- コア抽象化: インターフェース、データモデル、GUIコンポーネント、ユーティリティ
data/           -- データ層: ファイル形式リーダー（NetCDF, HDF5, MATLAB, CSV）、バッファ
figure/         -- Figureレベルの抽象化: 描画要素、スタイル、定数
figure/java2d/  -- 全figure要素のJava2Dレンダリング実装
export/         -- カスタムPNG/JPEGエクスポートファイルタイプ
```

主要なアーキテクチャパターン:
- 約50以上の `SGI*` インターフェースによるインターフェースベース設計
- ダイアログコールバックのためのObserverパターン
- プラグインアーキテクチャ（JARベースJavaプラグイン + JNA経由ネイティブCプラグイン）
- エクスポートファイルタイプ登録のためのService Provider Interface（SPI）

---

## 3. 現状の問題点と改善提案

### 3.1 重大問題（Critical）

#### C1. テストカバレッジがゼロ

**問題:** `src/test/` ディレクトリが存在せず、テストフレームワークの設定も `pom.xml` へのテスト依存宣言もありません。27万行の科学可視化アプリケーションでテストカバレッジが0%は極めて脆い状態です。

**影響:** リファクタリングやバグ修正のたびに回帰リスクが大きい。直近のJava 21移行で未発見の問題が存在する可能性が高い。

**改善案:**
1. JUnit 5（`junit-jupiter`）とAssertJをテスト依存として追加
2. `maven-surefire-plugin` 3.5.2+ を設定
3. 最も重要/データ関連のコードからテスト追加を開始:
   - `SGDataUtility`（データパース、値フォーマット、シリアライズ）
   - `SGSXYDataBuffer` / `SGVXYDataBuffer`（データバッファ操作）
   - `SGDefaultColumnTypeUtility`（カラムタイプ自動検出）
   - CSV/テキストパースロジック
4. 高カバレッジモジュールには後ほどミューテーションテスト（PITest）を追加

**優先度:** 高 | **工数:** 大（継続的）

---

#### C2. CI/CDパイプラインなし

**問題:** GitHub Actionsのワークフローが存在しません。`.github/` ディレクトリには1回限りの移行作業用の `modernize/java-upgrade/` ワークスペースのみが含まれています。ビルド、フォーマットチェック、デプロイはすべて手動です。

**影響:** 自動品質ゲートなし、ビルドステータスの可視性なし、自動リリースなし。

**改善案:** `.github/workflows/ci.yml` を作成:
```yaml
- checkout
- setup Java 21
- mvn spotless:check（フォーマット検証）
- mvn clean compile（コンパイル + lint）
- mvn test（ユニットテスト）
- mvn package（fat JARビルド）
```

**優先度:** 高 | **工数:** 小

---

#### C3. Godクラス（単一責任の原則違反）

**問題:** 2,800行を超えるクラスが20存在します。上位20クラスで約95,000行（全コードの35%）を占めています。

| クラス | 行数 | 責務 |
|--------|------|------|
| `SGDataUtility` | 8,243 | CSVパース、NetCDF、変換、フォーマット、アニメーション、シリアライズ |
| `SGFigureElementLegend` | 7,966 | レジェンド描画、データ名、表示切替、ダイアログ、シリアライズ |
| `SGDrawingWindow` | 7,150 | ウィンドウ、メニュー、ツールバー、ステータスバー、エクスポート、印刷、ズーム、undo/redo、ドラッグ＆ドロップ |
| `SGMainFunctions` | 6,818 | アプリケーションライフサイクル、ウィザード、ファイルI/O、クリップボード、設定、プラグイン |
| `SGAxisElement` | 5,763 | 軸描画、スケール、目盛、グリッド、ダイアログ、マウス操作 |
| `SGPropertyDialogSXYData` | 5,047 | SXYデータのプロパティダイアログ（広範なGUI管理） |
| `SGFigureElementShape` | 4,991 | シェイプ描画、ダイアログ、マウス操作、シリアライズ |

**影響:** 理解・修正・テストが困難。コントリビュータの認知負荷が大きい。一部の修正が予期せぬ副作用を発生させる。

**改善案:**
1. `SGDataUtility`: 焦点を絞ったユーティリティに分割:
   - `SGCSVParser`（CSV/テキストパース）
   - `SGValueFormatter`（値フォーマット）
   - `SGPropertySerializer`（プロパティファイルシリアライズ）
   - `SGAnimationUtility`（アニメーション関連ロジック）
   - `SGNetCDFDataUtility`（NetCDF固有操作）
2. `SGMainFunctions`: マネージャを別クラスに抽出:
   - `SGWizardManager`（ウィザードダイアログ調整）
   - `SGFileOperationManager`（load/save/export）
   - `SGClipboardManager`（copy/paste/cut）
3. `SGDrawingWindow`: ツールバー/メニュー/ステータスバーを専用のビルダークラスに抽出
4. テストを安全網とした漸進的リファクタリング（C1を先に実施）

**優先度:** 高 | **工数:** 非常に大（長期的）

---

#### C4. リソースフィルタリングがバイナリファイルを破損する可能性

**問題:** `pom.xml` で `src/main/resources/` 全体に `<filtering>true</filtering>` が有効になっており、同ディレクトリには48のPNG/GIF画像ファイルが含まれています。Mavenのリソースフィルタリングはテキスト置換を行うため、バイナリファイルを破損する可能性があります。

**影響:** ビルドされたJARに破損した画像が含まれ、ツールバーアイコンが表示されなくなったりスプラッシュ画面が壊れたりする。

**改善案:** リソースを2つのセクションに分割:
```xml
<resources>
  <!-- フィルタリングあり: .properties, .dtd, .xml のみ -->
  <resource>
    <directory>src/main/resources</directory>
    <filtering>true</filtering>
    <includes>
      <include>**/*.properties</include>
      <include>**/*.dtd</include>
      <include>**/*.xml</include>
    </includes>
  </resource>
  <!-- フィルタリングなし: バイナリファイル -->
  <resource>
    <directory>src/main/resources</directory>
    <filtering>false</filtering>
    <excludes>
      <exclude>**/*.properties</exclude>
      <exclude>**/*.dtd</exclude>
      <exclude>**/*.xml</exclude>
    </excludes>
  </resource>
</resources>
```

**優先度:** 高 | **工数:** 小

---

### 3.2 高優先度問題

#### H1. 古い依存関係

| 依存 | 現在 | 問題 |
|------|------|------|
| `org.freehep:freehep-*`（8アーティファクト） | 2.4（~2016年） | 維持終了; `ExportFileTypeRegistry` の既知バグに対処するためカスタム回避策が必要 |
| `cisd:jhdf5` | 19.04.1（~2019年） | ネイティブライブラリ抽出の問題; `SGMainFunctions.Initializer` でクリーンアップが必要 |
| `cisd:base` | 18.09.0（~2018年） | 古いHDF5ベースライブラリ |
| `joda-time:joda-time` | 2.12.7 | Java 8以降 `java.time` に置き換え済み。本プロジェクトはJava 21 |

**改善案:**
1. `joda-time` を `java.time` に置き換え（`SGXYFigure.java` で `Period` を使用）
2. FreeHEPの代替を検討: Apache XMLGraphics、Java2Dネイティブエクスポート、または現状をリスク記載付きで受容
3. 現代的なHDF5ライブラリ（OMEの `hdf5-hdf` など）を jhdf5 の代替として調査
4. `freehep-graphicsio-swf` モジュールを削除（SWF形式は廃止済み）

**優先度:** 高 | **工数:** 中

---

#### H2. 中央ハブクラスを介した強い結合

**問題:** `SGMainFunctions` が8以上のマネージャへの参照を持ち、主要な操作すべてを調整しています。`SGDrawingWindow` は `ActionListener`、`MenuListener`、`ComponentListener`、`PropertyChangeListener` を実装し、全UIコンポーネントのイベントを処理しています。

**影響:** 修正にはアプリケーション全体のフローを理解する必要がある。テストや再利用のためのコンポーネント分離が困難。

**改善案:**
1. コンポーネント間通信にイベントバス（Google Guava `EventBus` または軽量カスタム実装）を導入
2. `SGMainFunctions` のウィザードロジックを専用のウィザードコーディネータークラスに抽出
3. `SGDrawingWindow` のイベント処理を専用のリスナークラスに分割
4. 軽量DI（`SGApplicationContext` による手動依存性注入）の検討

**優先度:** 高 | **工数:** 大

---

#### H3. インターフェース爆発（~50以上、多くは定数アンチパターン）

**問題:** 約50の `SGI*Constants` インターフェースが「定数インターフェース」アンチパターンを使用しています。Javaには `enum` や `static final` フィールドを持つ `final class` というより適切な代替があります。

**改善案:**
1. 値が固定/離散のものは `enum` に変換（例: `SGILineStyleConstants` -> `LineStyle` enum）
2. 残りの定数インターフェースは `private constructor` と `static final` フィールドを持つ `final class` に変換
3. 振る舞いインターフェース（`SGIUndoable`、`SGIMovable` など）は現状維持
4. IDE支援による一括リファクタリングで全実装を更新

**優先度:** 中 | **工数:** 中

---

#### H4. log4j設定ファイルなし

**問題:** `pom.xml` で `log4j-api`、`log4j-core`、`log4j-slf4j2-impl` を宣言していますが、`src/main/resources` に `log4j2.xml` や `log4j2.properties` が存在しません。Log4jはデフォルト設定（コンソールへのエラー出力のみ）にフォールバックします。

**影響:** アプリケーションログが実行時に不可視になり、ユーザーと開発者のデバッグが困難。

**改善案:** `src/main/resources/log4j2.xml` を追加:
- コンソールアペンダ（INFOレベル）
- トラブルシューティング用ローリングファイルアペンダ（DEBUGレベル）
- データI/O操作用の別ロガー

**優先度:** 中 | **工数:** 小

---

### 3.3 中優先度問題

#### M1. Changelogの空白（v2.0.0 → v2.2.0）

**問題:** `changelog/product.xml` は v2.0.0（2010-11-23）で終了しています。POMはバージョン 2.2.0 を宣言しています。重要なJava 21移行作業（直近のgitコミットで確認可能）が文書化されていません。

**改善案:** `changelog/product.xml` に v2.1.0 と v2.2.0 のエントリーを追加:
- Java 21への移行
- 依存関係の更新
- ビルドシステムの改善（Maven shade plugin, Spotless）
- 直近コミットのバグ修正

**優先度:** 中 | **工数:** 小

---

#### M2. 未使用のMavenリポジトリ

**問題:** `pom.xml` で `imagej` と `ome` リポジトリを宣言していますが、宣言された依存関係のいずれもこれらを必要としません。以前の開発の名残りの可能性があります。

**改善案:** `mvn dependency:tree` で確認後、未使用のリポジトリを削除。

**優先度:** 低 | **工数:** 微小

---

#### M3. POMメタデータ不足

**問題:** POMに標準的なメタデータセクション（`<description>`, `<licenses>`, `<developers>`, `<scm>`, `<issueManagement>`, `<distributionManagement>`）がありません。

**改善案:** 最小限のメタデータを追加:
```xml
<description>各種データ形式から論文掲載品質のグラフを作成する科学用グラフ作成アプリケーション。</description>
<licenses>
  <license>
    <name>GNU Lesser General Public License v2.1</name>
    <url>https://www.gnu.org/licenses/old-licenses/lgpl-2.1.html</url>
  </license>
</licenses>
<scm>
  <connection>scm:git:git://github.com/neuroinformatics/samurai-graph.git</connection>
  <developerConnection>scm:git:ssh://git@github.com/neuroinformatics/samurai-graph.git</developerConnection>
  <url>https://github.com/neuroinformatics/samurai-graph</url>
</scm>
```

**優先度:** 低 | **工数:** 微小

---

#### M4. `maven-enforcer-plugin` が設定されていない

**問題:** 依存関係の収束、Javaバージョン、禁止依存の強制がありません。推移的依存の競合（例: `commons-csv` と `jhdf5` 間の `commons-io` バージョン競合）は手動で管理されています。

**改善案:** ルール付き `maven-enforcer-plugin` を追加:
- `requireJavaVersion` [21,)
- `requireMavenVersion` [3.9,)
- `banDynamicVersions`
- `bannedDependencies`（既知の問題アーティファクトを除外）

**優先度:** 低 | **工数:** 小

---

#### M5. 古いinstallerスクリプトが削除済みAntビルドを参照

**問題:** `installer/` ディレクトリにはプラットフォーム固有のスクリプト（Windows用NSIS, Unix/macOS用shellスクリプト）が含まれていますが、削除されたAntビルドシステムの `dist/` ディレクトリを参照しています。Mavenでfat JARを生成する現在、これらのスクリプトは機能しません。

**改善案:** `target/samurai-graph-2.2.0.jar` fat JARに対応するようinstallerスクリプトを更新するか、廃止済みであることを文書化し現代的な配布手順を提供。

**優先度:** 中 | **工数:** 中

---

#### M6. 未解決のTODO/FIXMEコメント

**問題:** コードベースに8件の未解決TODO/FIXMEコメントが存在します:

| ファイル | 行 | コメント |
|---------|-----|---------|
| `SGWindowDialog.java` | 386 | `// TODO add your handling code here:` |
| `SGSXYNetCDFData.java` | 224 | `// TODO set decimal place?` |
| `SGSDArrayData.java` | 836 | `// TODO` |
| `SGNetCDFData.java` | 2525, 2538, 2552 | `// TODO`（3箇所） |
| `SGMDArrayData.java` | 1747 | `// TODO` |
| `SGDrawingElementString2DExtended.java` | 662 | `// FIXME: calculate right side` |

**改善案:** 各TODO/FIXMEをレビューし、解決するか適切なコンテキスト付きGitHub issueに変換。

**優先度:** 低 | **工数:** 小

---

#### M7. 空の `platform/` パッケージ

**問題:** `jp.riken.brain.ni.samuraigraph.platform` ディレクトリは存在しますがファイルが含まれていません。プラットフォーム固有コードのプレースホルダのようです。

**改善案:** プラットフォーム固有の抽象化を実装するか、空のパッケージを削除。

**優先度:** 低 | **工数:** 微小

---

#### M8. ネイティブライブラリの扱い（HDF5）

**問題:** HDF5ライブラリ（`jhdf5`）はネイティブの `.dll`/`.so`/`.dylib` ファイルを必要とします。`SGMainFunctions.Initializer` に `removeHDF5TemporaryFiles()` クリーンアップロジックが含まれており、ネイティブライブラリの抽出が脆弱な状態を示しています。

**改善案:** より現代的なHDF5 Javaバインディング（JNAを使用しネイティブをバンドルする OME の `hdf5-hdf` など）への切り替えを調査。

**優先度:** 中 | **工数:** 中

---

### 3.4 良好な点（維持すべき事項）

以下の点は適切に設計されており、維持すべきです:

- **パッケージ構造:** `application`, `base`, `data`, `figure`, `figure/java2d` 間で明確な責務分離
- **インターフェースベース設計:** 抽象figure層とJava2D実装の分離により、レンダリングバックエンドの交換が可能
- **プラグインアーキテクチャ:** JARプラグインとJNA経由のネイティブCプラグインで拡張可能
- **ビルドツール:** Spotlessによるフォーマット、`-Xlint:all` によるコンパイラ警告、shade pluginによるfat JAR
- **データ形式サポート:** CSV, NetCDF, HDF5, MATLAB を包括的にサポート
- **Service Providerパターン:** エクスポートファイルタイプをSPI（`META-INF/services/`）で登録
- **リソース管理:** UTF-8エンコーディングを明示的に設定、`.gitattributes` で改行コードを強制
- **最近のモダナイズ:** Java 21への移行成功、非推奨APIの置き換え、デッドコードの削除

---

## 4. 推奨実装順序

| フェーズ | 対象 | 目標 |
|---------|------|------|
| **フェーズ1: 安全網** | C2（CI/CD）、C4（リソースフィルタリング）、H4（log4j設定）、M3（POMメタデータ）、M2（未使用リポジトリ） | 自動品質ゲート確立と即時リスクの修正 |
| **フェーズ2: テスト基盤** | C1（テストフレームワーク追加）、C3（Godクラス分析開始） | 安全なリファクタリングを可能に |
| **フェーズ3: 依存関係** | H1（古い依存）、M8（ネイティブライブラリ） | 外部ライブラリ由来の技術的負債削減 |
| **フェーズ4: アーキテクチャ** | C3（Godクラスリファクタリング）、H2（強い結合）、H3（定数アンチパターン） | 保守性向上 |
| **フェーズ5: 仕上げ** | M1（changelog）、M5（installer）、M6（TODO）、M7（空パッケージ）、M4（enforcer） | 残る問題の整理 |

---

## 5. クイックウィン（直ちに実行可能）

1. バイナリファイルのリソースフィルタリングを修正（C4） -- 10分
2. `log4j2.xml` を追加（H4） -- 30分
3. GitHub Actionsワークフローを作成（C2） -- 1時間
4. `joda-time` を削除し `java.time` に移行（H1） -- 2時間
5. `freehep-graphicsio-swf` を削除（H1） -- 10分
6. POMメタデータを追加（M3） -- 15分
7. TODO/FIXMEコメントを解決（M6） -- 1時間
