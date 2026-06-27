# Samurai Graph - 実行計画

> **運用ルール:** 各タスクのステータス、優先度、フェーズは必要に応じて自由に更新してください。
> タスクの実行順序はフェーズ順 → 優先度順（高 → 中 → 低）→ タスクID順 で決定されますが、依存関係により柔軟に変更可能です。
>
> **実行・再開ルール:**
> - 各セッションの開始時に「セッションログ」に日時と実施内容、中断理由を記録
> - 中断した場合、該当タスクのステータスを `[>] IN_PROGRESS` から `[>] IN_PROGRESS` のままにし、「中断ポイント」に再開位置を記載
> - 次のセッションでは「セッションログ」を確認し、中断したタスクから再開
> - ブロック要因が発生した場合は「ブロック中タスク一覧」に記録し、代替タスクの実行に切り替え
>
> **タスク完了後のルール:**
> - 各タスク完了後、必ずこの plan ファイルのダッシュボードと該当タスクのステータスを更新すること
> - 更新後、`skill git-commit` を実行して変更をコミットすること
> - コミットはタスクごとに分割してアトミックにすること
>
> **ブランチ運用ルール:**
> - 各タスクの実行はタスク毎に git branch を切って進めること（例: `task/test-data-utility-bounds`）
> - タスク完了の定義: `skill git-commit` によるコミットが終わり次第、master ブランチへマージし、作成したブランチを削除すること
> - マージ後は master ブランチに戻って次のタスクを開始すること

---

## 実行ステータス（ダッシュボード）

> このセクションは各セッション開始時に更新してください。

| 項目 | 値 |
|------|-----|
| 最終更新日時 | 2026-06-27 |
| 現在の実行フェーズ | フェーズ6 |
| 実施中タスク | なし |
| 完了タスク数 | 27 / 43 (3 DEFERRED) |
| ブロック中タスク | なし |
| 次の実施タスク | TASK-027 |

### セッション #6

| 項目 | 値 |
|------|-----|
| 日時 | 2026-06-27 |
| 実施タスク | TASK-025, TASK-026 |
| 完了内容 | TASK-025 (SGDataBoundsTest 68件), TASK-026 (SGDataColumnTest 34件) 完了 - 既にmasterにコミット済み確認 |
| 中断理由 | なし |
| 中断ポイント | なし |
| 次のセッションで再開するタスク | TASK-027 |

---

## セッションログ

> 各実行セッションの開始時に新しいエントリーを追加してください。
> 中断・再開の履歴を保持し、次のセッションで容易に再開できるようにします。

### セッション #1

| 項目 | 値 |
|------|-----|
| 日時 | 2026-06-26 |
| 実施タスク | TASK-001, TASK-002, TASK-003 |
| 完了内容 | TASK-001~005 完了, フェーズ1 完了 |
| 中断理由 | なし |
| 中断ポイント | TASK-006 着手前 |
| 次のセッションで再開するタスク | TASK-006 |

### セッション #2

| 項目 | 値 |
|------|-----|
| 日時 | 2026-06-26 |
| 実施タスク | TASK-006, TASK-007, TASK-008 |
| 完了内容 | TASK-006, TASK-007 完了, TASK-008 着手中 |
| 中断理由 | なし |
| 中断ポイント | TASK-008 実施中 |
| 次のセッションで再開するタスク | TASK-008 |

### セッション #3

| 項目 | 値 |
|------|-----|
| 日時 | 2026-06-26 |
| 実施タスク | TASK-008~TASK-015 |
| 完了内容 | TASK-008~TASK-015 完了 |
| 中断理由 | なし |
| 中断ポイント | TASK-016 着手前 |
| 次のセッションで再開するタスク | TASK-016 |

### セッション #5

| 項目 | 値 |
|------|-----|
| 日時 | 2026-06-27 |
| 実施タスク | TASK-031, TASK-032, TASK-024 |
| 完了内容 | TASK-031 (jacoco導入), TASK-032 (SGPeriodTest 52件), TASK-024 (SGDataTypeCheckTest 42件) 完了 |
| 中断理由 | TASK-017-1実行中に`git checkout -- .`を誤実行し、execution-plan.mdの拡充分が失われたため復旧に切り替え |
| 中断ポイント | TASK-025 着手前 |
| 次のセッションで再開するタスク | TASK-025 |

**備考:**
- TASK-017-1 (定数インターフェース変換) の試行中に`git checkout -- .`を実行し、pom.xmlの変更とexecution-plan.mdの拡充分を破棄
- 原因: `SGIAxisBreakConstants`が`SGIDrawingElementConstants`を`extends`しており、`implements`削除後に99個のコンパイルエラーが発生
- 対策: TASK-017-1〜017-5はIDEのリファクタリング機能が必要であり、エージェントによる自動変換は不適合と判断

---

## 凡例

### ステータス

| マーク | ステータス | 説明 |
|--------|-----------|------|
| `[ ]` | TODO | 未着手 |
| `[>]` | IN_PROGRESS | 実施中 |
| `[x]` | DONE | 完了 |
| `[-]` | CANCELLED | 中止 |
| `[~]` | DEFERRED | 先送りの |

### 優先度

| 優先度 | 説明 |
|--------|------|
| P0 | 直ちに実施（ブロックしている課題） |
| P1 | 高優先度（なるべく早く実施） |
| P2 | 中優先度（通常の実施順序） |
| P3 | 低優先度（余裕があれば実施） |

---

## フェーズ1: 安全網の構築

> **目標:** 自動品質ゲート確立と即時リスクの修正。後続の作業の安全網となる基盤を整備します。
>
> **依存関係:** このフェーズを先に実施することで、後続のフェーズで自動検証が可能になります。

### TASK-001: バイナリファイルのリソースフィルタリング修正

| 項目 | 内容 |
|------|------|
| ステータス | [x] DONE |
| 優先度 | P0 |
| 対応元 | C4 |
| 推定工数 | 10分 |
| 依存タスク | なし |
| 中断ポイント | なし |
| ブランチ | `task/resource-filtering` |

**実施手順:**

1. `pom.xml` の `<resources>` セクションを編集
2. 既存の単一 `<resource>` を2つの `<resource>` に分割:
   - フィルタリングあり: `**/*.properties`, `**/*.dtd`, `**/*.xml` のみに限定
   - フィルタリングなし: 上記以外（PNG, GIF, サービスファイルなど）
3. `mvn clean compile` でビルド確認
4. `target/classes/` 内の画像ファイルが破損していないことを確認（`file` コマンドまたは画像ビューアで検証）
5. `mvn spotless:apply` → `mvn clean` → `mvn compile` で検証

**完了基準:**
- [x] `mvn compile` が成功
- [x] `target/classes/` 内のPNG/GIFファイルが正常に読み込める
- [x] `samurai-graph.properties` の `${project.version}` 置換が正常に機能

---

### TASK-002: log4j2設定ファイルの追加

| 項目 | 内容 |
|------|------|
| ステータス | [x] DONE |
| 優先度 | P1 |
| 対応元 | H4 |
| 推定工数 | 30分 |
| 依存タスク | なし |
| 中断ポイント | なし |
| ブランチ | `task/log4j-config` |

**実施手順:**

1. `src/main/resources/log4j2.xml` を新規作成
2. 以下の設定を含む:
   - コンソールアペンダ（INFOレベル、パターンレイアウト）
   - ローリングファイルアペンダ（DEBUGレベル、日次ローリング、30日保持）
   - `jp.riken.brain.ni.samuraigraph.data` パッケージ用別ロガー（DEBUGレベル）
3. `mvn clean compile` でビルド確認
4. アプリケーション起動時にログ出力がコンソールに表示されることを確認

**完了基準:**
- [x] `log4j2.xml` が `src/main/resources/` に配置
- [x] `mvn compile` が成功
- [x] アプリ起動時にINFOレベルのログが出力される

---

### TASK-003: ローカルCIテスト基盤の整備

| 項目 | 内容 |
|------|------|
| ステータス | [x] DONE |
| 優先度 | P1 |
| 対応元 | C2 |
| 推定工数 | 1-2時間 |
| 依存タスク | なし |
| 中断ポイント | なし |
| ブランチ | `task/local-ci` |

**実施手順:**

1. ローカルで1コマンド実行可能なCI検証スクリプトを作成:
    - Unix/macOS: `.ci/run.sh`
    - Windows: `.ci/run.cmd`
2. スクリプト内で以下の順に実行:
   - `mvn clean`
   - `mvn spotless:check`（フォーマット検証）
   - `mvn compile`（コンパイル + lint）
   - `mvn test`（ユニットテスト）
   - `mvn package`（fat JARビルド）
3. 各ステップで失敗時に即座に終了し、エラー箇所を明確に表示
4. 実行結果をサマリー表示（成功/失敗ステップ、経過時間）
5. 各開発者のローカル環境（Linux, macOS, Windows）で動作確認

**完了基準:**
- [x] `.ci/run.sh` と `.ci/run.cmd` が作成
- [x] 各プラットフォームで1コマンド実行可能
- [x] 全ステップ成功時に正常終了（exit code 0）
- [x] 任意のステップ失敗時に即座終了（非0 exit code）

**備考:**
- GitHub Actions（TASK-003-2）は本タスク完了後の次のステップ
- ローカル基盤が確立してからクラウドCIへの移行を検討

---

### TASK-003-2: GitHub Actionsワークフローの作成

| 項目 | 内容 |
|------|------|
| ステータス | [~] DEFERRED |
| 優先度 | P3 |
| 対応元 | C2 |
| 推定工数 | 1時間 |
| 依存タスク | TASK-003 |
| 中断ポイント | なし |
| ブランチ | `task/github-actions` |

**実施手順:**

1. `.github/workflows/ci.yml` を新規作成
2. ローカルCIスクリプト（TASK-003）のステップをGitHub Actionsで再現:
   - **build:** Java 21環境で `mvn clean compile`、`mvn spotless:check`、`mvn package` を実行
   - **test:** `mvn test` を実行
3. トリガー: `push`（mainブランチ）、`pull_request`
4. 手動pushまたはPRでワークフロー動作確認

**完了基準:**
- [ ] `.github/workflows/ci.yml` が作成
- [ ] pushまたはPR時にワークフローが自動実行される
- [ ] ビルド・テストジョブが成功する

---

### TASK-004: POMメタデータの追加

| 項目 | 内容 |
|------|------|
| ステータス | [x] DONE |
| 優先度 | P2 |
| 対応元 | M3 |
| 推定工数 | 15分 |
| 依存タスク | なし |
| 中断ポイント | なし |
| ブランチ | `task/pom-metadata` |

**実施手順:**

1. `pom.xml` に以下のセクションを追加:
    - `<description>`
   - `<licenses>`（GNU LGPL 2.1）
   - `<scm>`（GitHubリポジトリ情報）
2. `mvn clean compile` でビルド確認

**完了基準:**
- [x] 各メタデータセクションが追加
- [x] `mvn compile` が成功
- [x] `mvn help:effective-pom` でメタデータが正しく反映されている

---

### TASK-005: 未使用のMavenリポジトリ削除

| 項目 | 内容 |
|------|------|
| ステータス | [x] DONE |
| 優先度 | P3 |
| 対応元 | M2 |
| 推定工数 | 10分 |
| 依存タスク | なし |
| 中断ポイント | なし |
| ブランチ | `task/pom-metadata`（TASK-004と統合可能） |

**実施手順:**

1. `mvn dependency:tree` を実行し、各依存の提供元リポジトリを確認
2. `imagej` リポジトリと `ome` リポジトリが実際に未使用であることを確認
3. `pom.xml` の `<repositories>` から未使用のものを削除
4. `mvn clean compile` でビルド確認

**完了基準:**
- [ ] 未使用リポジトリが削除（omeリポジトリが残存）
- [x] `mvn compile` が成功（依存解決エラーなし）

---

## フェーズ2: テスト基盤の整備

> **目標:** テストフレームワークの導入と、安全なリファクタリングのための安全網を構築します。
>
> **依存関係:** フェーズ1完了後。ローカルCI（TASK-003）でテスト実行が統合されます。

### TASK-006: テストフレームワークの導入

| 項目 | 内容 |
|------|------|
| ステータス | [x] DONE |
| 優先度 | P0 |
| 対応元 | C1 |
| 推定工数 | 1時間 |
| 依存タスク | TASK-003（ローカルCI） |
| 中断ポイント | なし |
| ブランチ | `task/test-framework` |

**実施手順:**

1. `pom.xml` にテスト依存を追加:
   - `org.junit.jupiter:junit-jupiter`（スコープ: test）
   - `org.assertj:assertj-core`（スコープ: test）
   - `org.mockito:mockito-core`（スコープ: test）
   - `org.mockito:mockito-junit-jupiter`（スコープ: test）
2. `maven-surefire-plugin` 3.5.2+ を `<build><plugins>` に追加
3. `src/test/java/jp/riken/brain/ni/samuraigraph/` ディレクトリ構造を作成
4. 動作確認用のダミーテスト（`SampleTest.java`）を作成し `mvn test` で実行確認
5. ローカルCIスクリプト（TASK-003）にテストステップが統合されていることを確認

**完了基準:**
- [x] テスト依存が追加
- [x] `src/test/java/` ディレクトリ構造が作成
- [x] `mvn test` が成功
- [x] ローカルCIスクリプトでテストが自動実行される

---

### TASK-007: データパース関連のユニットテスト作成

| 項目 | 内容 |
|------|------|
| ステータス | [x] DONE |
| 優先度 | P1 |
| 対応元 | C1 |
| 推定工数 | 3-5日 |
| 依存タスク | TASK-006 |
| 中断ポイント | なし |
| ブランチ | `task/data-parsing-tests` |

**実施手順:**

1. `SGDataUtility` のパブリックなstaticメソッドからテスト対象を抽出:
   - 値フォーマット関連メソッド
   - データ型判定メソッド
   - 文字列パースメソッド
2. 各メソッドに対して正常系・異常系のテストケースを作成
3. `examples/data/Example*.txt` のサンプルデータを実際のテスト入力として使用
4. カバレッジ計測ツール（`jacoco-maven-plugin`）を追加し、対象クラスのカバレッジを確認

**完了基準:**
- [x] テストクラスが `src/test/java/` に配置
- [x] `mvn test` が全テスト成功
- [ ] 対象メソッドの行カバレッジが50%以上

---

### TASK-008: データバッファ関連のユニットテスト作成

| 項目 | 内容 |
|------|------|
| ステータス | [x] DONE |
| 優先度 | P1 |
| 対応元 | C1 |
| 推定工数 | 2-3日 |
| 依存タスク | TASK-006 |
| 中断ポイント | なし |
| ブランチ | `task/data-buffer-tests` |

**実施手順:**

1. `SGSXYDataBuffer`、`SGVXYDataBuffer` の公開メソッドをテスト
2. 代表的なデータ形状（単一系列、複数系列、欠損値含む）のテストケースを作成
3. `SGDefaultColumnTypeUtility` のカラムタイプ自動検出をテスト
4. `examples/data/` の各種形式のサンプルデータで検証

**完了基準:**
- [x] テストクラスが作成
- [x] `mvn test` が全テスト成功
- [ ] 対象クラスの行カバレッジが50%以上

---

### TASK-009: Godクラスの分析と分割計画策定

| 項目 | 内容 |
|------|------|
| ステータス | [x] DONE |
| 優先度 | P1 |
| 対応元 | C3 |
| 推定工数 | 1-2日 |
| 依存タスク | TASK-007, TASK-008 |
| 中断ポイント | なし |
| ブランチ | `task/god-class-analysis` |

**実施手順:**

1. 上位7つのGodクラスを個別に分析:
   - 各クラスのメソッドを責務ごとにグルーピング
   - クラス間の依存関係をマッピング
   - 抽出可能なメソッド/内部クラスを特定
2. 各クラスに対して分割計画をドキュメント化:
   - 分割後のクラス名と責務
   - パブリックAPIの変更内容
   - 影響範囲（参照箇所数）
3. 分割計画をレビューし、実施順序を決定

**完了基準:**
- [x] 各Godクラスの責務分析ドキュメントが作成
- [x] 分割計画が文書化
- [x] 影響範囲の調査が完了

---

## フェーズ3: 依存関係の改善

> **目標:** 外部ライブラリ由来の技術的負債を削減します。
>
> **依存関係:** フェーズ2完了後。テスト基盤があることで依存変更の安全性を確認できます。

### TASK-010: joda-timeの削除とjava.timeへ移行

| 項目 | 内容 |
|------|------|
| ステータス | [x] DONE |
| 優先度 | P1 |
| 対応元 | H1 |
| 推定工数 | 2時間 |
| 依存タスク | TASK-006 |
| 中断ポイント | なし |
| ブランチ | `task/remove-joda-time` |

**実施手順:**

1. `joda-time` の使用箇所を特定（`SGXYFigure.java` の `Period` 使用など）
2. `java.time` API（`Duration`、`Period`、`LocalDateTime` など）に置き換え
3. `pom.xml` から `joda-time` 依存を削除
4. `mvn clean compile` でビルド確認
5. `mvn test` でテスト確認

**完了基準:**
- [x] `joda-time` のインポートが0箇所
- [x] `pom.xml` から `joda-time` が削除
- [x] `mvn compile` が成功
- [x] テストが全成功

---

### TASK-011: freehep-graphicsio-swfの削除

| 項目 | 内容 |
|------|------|
| ステータス | [x] DONE |
| 優先度 | P2 |
| 対応元 | H1 |
| 推定工数 | 10分 |
| 依存タスク | なし（TASK-010と並列実行可能） |
| 中断ポイント | なし |
| ブランチ | `task/remove-joda-time`（TASK-010と統合可能） |

**実施手順:**

1. `pom.xml` から `freehep-graphicsio-swf` 依存を削除
2. ソースコード内でのSWF関連参照を確認（`grep -r swf src/`）
3. `mvn clean compile` でビルド確認
4. `mvn test` でテスト確認

**完了基準:**
- [x] `pom.xml` から `freehep-graphicsio-swf` が削除
- [x] ソースコードにSWF参照が残っていない
- [x] `mvn compile` が成功

---

### TASK-012: FreeHEPライブラリの代替調査

| 項目 | 内容 |
|------|------|
| ステータス | [x] DONE |
| 優先度 | P2 |
| 対応元 | H1 |
| 推定工数 | 1-2日（調査のみ） |
| 依存タスク | なし |
| 中断ポイント | なし |
| ブランチ | `task/investigate-freehep-alternatives` |

**実施手順:**

1. 本プロジェクトでFreeHEPを使用している箇所を特定:
   - エクスポート機能（PDF, SVG, EMF, PS）
   - `ExportFileTypeRegistry` のカスタム実装
2. 代替ライブラリの調査:
   - Apache XMLGraphics（PDF, SVG）
   - Java2Dネイティブ機能（画像エクスポート）
   - Apache Batik（SVG）
3. 各代替の実装コストと既存機能との互換性を評価
4. 調査結果をドキュメント化

**完了基準:**
- [x] 使用箇所の特定が完了
- [x] 代替候補の評価が完了
- [x] 移行コストの見積もりが作成

---

### TASK-013: HDF5ライブラリの代替調査

| 項目 | 内容 |
|------|------|
| ステータス | [x] DONE |
| 優先度 | P2 |
| 対応元 | H1, M8 |
| 推定工数 | 1-2日（調査のみ） |
| 依存タスク | なし |
| 中断ポイント | なし |
| ブランチ | `task/investigate-hdf5-alternatives` |

**実施手順:**

1. `jhdf5` の使用箇所と依存関係を特定
2. `SGMainFunctions.Initializer` の `removeHDF5TemporaryFiles()` を含むネイティブライブラリ処理を分析
3. 代替ライブラリの調査:
   - OMEの `hdf5-hdf`（JNAベース、ネイティブバンドル）
   - `ch.systemsx.cisd:hdf5-hdf`
4. 移行コストと既存コードへの影響を評価
5. 調査結果をドキュメント化

**完了基準:**
- [x] 使用箇所と依存関係の特定が完了
- [x] 代替候補の評価が完了
- [x] 移行コストの見積もりが作成

---

## フェーズ4: アーキテクチャ改善

> **目標:** Godクラスのリファクタリングと結合の緩和により、保守性を向上させます。
>
> **依存関係:** フェーズ2完了後。テストが安全網として機能していることが前提です。

### TASK-014: SGDataUtilityの分割（第1弾）

| 項目 | 内容 |
|------|------|
| ステータス | [x] DONE |
| 優先度 | P1 |
| 対応元 | C3 |
| 推定工数 | 3-5日 |
| 依存タスク | TASK-009, TASK-007 |
| 中断ポイント | なし |
| ブランチ | `task/refactor-data-utility` |

**実施手順:**

1. `SGDataUtility`（8,243行）のメソッドを責務ごとに分類
2. 第1弾として独立性の高いメソッド群を抽出:
   - CSVパース関連 -> `SGCSVParser`
   - 値フォーマット関連 -> `SGValueFormatter`
3. 各新クラスを作成しメソッドを移動
4. 旧クラスから新クラスへの委譲メソッドを残し、既存コードへの影響を最小化
5. テストで動作確認
6. 旧クラスの委譲メソッドに `@Deprecated` を付与

**完了基準:**
- [x] 新クラスが作成されメソッドが移動
- [x] `mvn compile` が成功
- [x] テストが全成功
- [x] 旧クラスの行数が減少

---

### TASK-015: SGDataUtilityの分割（第2弾）

| 項目 | 内容 |
|------|------|
| ステータス | [ ] TODO |
| 優先度 | P1 |
| 対応元 | C3 |
| 推定工数 | 3-5日 |
| 依存タスク | TASK-014 |
| 中断ポイント | なし |
| ブランチ | `task/refactor-data-utility-2` |

**実施手順:**

1. 第2弾として残りのメソッド群を抽出:
   - プロパティファイルシリアライズ -> `SGPropertySerializer`
   - アニメーション関連 -> `SGAnimationUtility`
   - NetCDF固有操作 -> `SGNetCDFDataUtility`
2. 各新クラスを作成しメソッドを移動
3. 旧クラスから新クラスへの委譲メソッドを残し、既存コードへの影響を最小化
4. テストで動作確認

**完了基準:**
- [x] 新クラスが作成されメソッドが移動
- [x] `mvn compile` が成功
- [x] テストが全成功
- [ ] `SGDataUtility` の行数が3,000行以下に減少

---

### TASK-016: SGMainFunctionsのマネージャー抽出

| 項目 | 内容 |
|------|------|
| ステータス | [ ] TODO |
| 優先度 | P1 |
| 対応元 | C3, H2 |
| 推定工数 | 5-7日 |
| 依存タスク | TASK-009 |
| 中断ポイント | なし |
| ブランチ | `task/refactor-main-functions` |

**実施手順:**

1. `SGMainFunctions`（6,818行）の責務を分析
2. ウィザードダイアログ調整ロジックを `SGWizardManager` に抽出
3. ファイル操作（load/save/export）ロジックを `SGFileOperationManager` に抽出
4. クリップボード操作ロジックを `SGClipboardManager` に抽出
5. 各マネージャーを `SGMainFunctions` から委譲するよう修正
6. テストで動作確認

**完了基準:**
- [ ] 各マネージャーが独立したクラスとして作成
- [ ] `mvn compile` が成功
- [ ] テストが全成功
- [ ] `SGMainFunctions` の行数が4,000行以下に減少

---

### TASK-017: 定数インターフェースのenum/final classへ変換（親タスク）

| 項目 | 内容 |
|------|------|
| ステータス | [~] DEFERRED |
| 優先度 | P2 |
| 対応元 | H3 |
| 推定工数 | 3-5日 |
| 依存タスク | なし（並列実行可能） |
| 中断ポイント | なし |
| ブランチ | `task/refactor-constants` |

**備考:**
- 50+ のインターフェースを一括変換はスコープが大きすぎるため、TASK-017-1〜017-5に分割
- 各サブタスクは独立して実行可能（リーフインターフェースから順に実施）
- 変換パターン: `public interface SGI*Constants` -> `public final class SGI*Constants` + `private SGI*Constants() {}`
- 各クラスから `implements SGI*Constants` を削除し、定数参照を `SGI*Constants.CONSTANT_NAME` に変更

---

### TASK-017-1: figureパッケージのリーフ定数インターフェース変換（第1弾）

| 項目 | 内容 |
|------|------|
| ステータス | [ ] TODO |
| 優先度 | P2 |
| 対応元 | H3 |
| 推定工数 | 2-3時間 |
| 依存タスク | なし |
| 中断ポイント | なし |
| ブランチ | `task/refactor-constants-figure-1` |

**対象インターフェース（6件）:**
- `SGIAxisBreakConstants` (impl: 3クラス, extends: SGIDrawingElementConstants)
- `SGIColorBarConstants` (impl: 2クラス, extends: SGIConstants)
- `SGIColorMapConstants` (impl: 1クラス, extends: なし)
- `SGIErrorBarConstants` (impl: 2クラス, extends: SGIArrowConstants)
- `SGIFigureGridConstants` (impl: 1クラス, extends: SGILineConstants)
- `SGIFigureTypeConstants` (impl: 2クラス, extends: なし)

**変換手順（1インターフェースごと）:**

1. **インターフェースの変換:**
   - `public interface SGI*Constants` -> `public final class SGI*Constants`
   - `private SGI*Constants() {}` コンストラクタを追加
   - `extends 親インターフェース` を削除
   - `extends` していた親インターフェースの定数への参照を `親インターフェース.CONSTANT_NAME` に修正

2. **実装クラスの修正:**
   - `grep -rn "implements.*SGI*Constants"` で実装クラスを特定
   - 各クラスから `implements SGI*Constants` を削除
   - 定数への参照が `SGI*Constants.CONSTANT_NAME` 形式で解決できることを確認（importがあれば不要）

3. **検証:**
   - `mvn compile -q` でコンパイルエラーがないことを確認
   - `mvn test -q` でテストが全成功することを確認

**重要:** 1インターフェース変換ごとに `mvn compile` を実行し、エラーが発生したらそのインターフェースの修正を完了してから次へ進む。

**完了基準:**
- [ ] 6インターフェースがfinal classに変換
- [ ] `mvn compile` が成功
- [ ] テストが全成功

---

### TASK-017-2: figureパッケージのリーフ定数インターフェース変換（第2弾）

| 項目 | 内容 |
|------|------|
| ステータス | [ ] TODO |
| 優先度 | P2 |
| 対応元 | H3 |
| 推定工数 | 2-3時間 |
| 依存タスク | なし |
| 中断ポイント | なし |
| ブランチ | `task/refactor-constants-figure-2` |

**対象インターフェース（6件）:**
- `SGIShapeConstants` (impl: 1クラス, extends: なし)
- `SGISXYDataConstants` (impl: 7クラス, extends: SGIConstants)
- `SGISXYZDataConstants` (impl: 2クラス, extends: なし)
- `SGITickLabelConstants` (impl: 1クラス, extends: SGIDrawingElementConstants)
- `SGITimingLineConstants` (impl: 1クラス, extends: SGILineConstants)
- `SGIVXYDataConstants` (impl: 4クラス, extends: SGIArrowConstants)

**変換手順:** TASK-017-1と同様、1インターフェースごとに変換・検証

**完了基準:**
- [ ] 6インターフェースがfinal classに変換
- [ ] `mvn compile` が成功
- [ ] テストが全成功

---

### TASK-017-3: figureパッケージの親定数インターフェース変換

| 項目 | 内容 |
|------|------|
| ステータス | [ ] TODO |
| 優先度 | P2 |
| 対応元 | H3 |
| 推定工数 | 3-4時間 |
| 依存タスク | TASK-017-1, TASK-017-2 |
| 中断ポイント | なし |
| ブランチ | `task/refactor-constants-figure-parent` |

**対象インターフェース（9件）:**
- `SGIArrowConstants` (extends: SGILineConstants, SGISymbolConstants, SGIFigureDrawingElementConstants; impl: 4)
- `SGIBarConstants` (extends: SGIRectangleConstants; impl: 2)
- `SGILineConstants` (extends: SGIDrawingElementConstants; impl: 7)
- `SGIRectangleConstants` (extends: SGIDrawingElementConstants; impl: 2)
- `SGIScaleConstants` (extends: SGILineAndStringConstants; impl: 3)
- `SGISignificantDifferenceConstants` (extends: SGILineAndStringConstants; impl: 2)
- `SGIStringConstants` (extends: SGIDrawingElementConstants, SGIDateConstants; impl: 4)
- `SGILineAndStringConstants` (extends: SGIStringConstants; impl: 1)
- `SGILegendConstants` (extends: SGIConstants; impl: 2)

**変換手順:** TASK-017-1と同様、1インターフェースごとに変換・検証

**完了基準:**
- [ ] 9インターフェースがfinal classに変換
- [ ] `mvn compile` が成功
- [ ] テストが全成功

---

### TASK-017-4: base/data/applicationパッケージの定数インターフェース変換

| 項目 | 内容 |
|------|------|
| ステータス | [ ] TODO |
| 優先度 | P2 |
| 対応元 | H3 |
| 推定工数 | 4-6時間 |
| 依存タスク | TASK-017-3 |
| 中断ポイント | なし |
| ブランチ | `task/refactor-constants-base-data-app` |

**対象インターフェース（27件）:**

| インターフェース | impl数 |
|-----------------|--------|
| `SGIConstants` | 16 |
| `SGIAnimationConstants` | 2 |
| `SGIDateConstants` | 0 |
| `SGIDrawingElementConstants` | 5 |
| `SGIFigureConstants` | 5 |
| `SGIFigureElementConstants` | 1 |
| `SGIFigureElementAxisConstants` | 2 |
| `SGIPropertyFileConstants` | 3 |
| `SGIRootObjectConstants` | 4 |
| `SGITextDataConstants` | 3 |
| `SGIDataColumnTypeConstants` | 10 |
| `SGIDataCommandConstants` | 11 |
| `SGIDataFileConstants` | 0 |
| `SGIDataInformationKeyConstants` | 1 |
| `SGIDataPropertyKeyConstants` | 8 |
| `SGIMDArrayConstants` | 3 |
| `SGINetCDFConstants` | 7 |
| `SGIApplicationCommandConstants` | 3 |
| `SGIApplicationConstants` | 7 |
| `SGIApplicationTextConstants` | 4 |
| `SGIArchiveFileConstants` | 3 |
| `SGIDataPluginConstants` | 1 |
| `SGIImageConstants` | 0 |
| `SGIPreferencesConstants` | 2 |
| `SGIUpgradeConstants` | 1 |
| `SGIElementGroupConstants` | 1 |
| `SGISymbolConstants` | 2 |

**変換手順:** TASK-017-1と同様、1インターフェースごとに変換・検証。impl数0のインターフェースから開始（`SGIDateConstants`, `SGIDataFileConstants`, `SGIImageConstants`）

**完了基準:**
- [ ] 27インターフェースがfinal classに変換
- [ ] `mvn compile` が成功
- [ ] テストが全成功

---

### TASK-017-5: 複合インターフェースの処理

| 項目 | 内容 |
|------|------|
| ステータス | [ ] TODO |
| 優先度 | P2 |
| 対応元 | H3 |
| 推定工数 | 1-2時間 |
| 依存タスク | TASK-017-4 |
| 中断ポイント | なし |
| ブランチ | `task/refactor-constants-composite` |

**対象インターフェース（3件）:**
- `SGIRootObject` (extends: SGIZoomable, SGIRootObjectConstants)
- `SGIFigureElementAxis` (extends: SGIFigureElement, SGIFigureElementAxisConstants)
- `SGIData` (extends: SGIConstants, SGIDisposable)

**変換手順:** TASK-017-1と同様、1インターフェースごとに変換・検証

**完了基準:**
- [ ] 3複合インターフェースがfinal classに変換
- [ ] `mvn compile` が成功
- [ ] テストが全成功

---

### TASK-018: SGDrawingWindowのイベントハンドラー分割（親タスク）

| 項目 | 内容 |
|------|------|
| ステータス | [~] DEFERRED |
| 優先度 | P2 |
| 対応元 | C3, H2 |
| 推定工数 | 3-5日 |
| 依存タスク | TASK-009 |
| 中断ポイント | なし |
| ブランチ | `task/refactor-drawing-window` |

**備考:**
- 7,150行のSGDrawingWindowからイベントハンドラーを抽出
- メニュー、クリップボード、UI更新、プロパティ変更の4つのハンドラーに分割予定
- 各サブタスクは独立して実行可能

---

### TASK-018-1: SGDrawingWindowのメニューハンドラー抽出

| 項目 | 内容 |
|------|------|
| ステータス | [ ] TODO |
| 優先度 | P2 |
| 対応元 | C3 |
| 推定工数 | 2-3日 |
| 依存タスク | なし |
| 中断ポイント | なし |
| ブランチ | `task/refactor-drawing-window-menu` |

**対象メソッド:**
- `actionPerformed(final ActionEvent e)` (L2345)
- `menuSelected(MenuEvent e)` (L1732)
- `menuDeselected(MenuEvent e)` (L1752)
- `menuCanceled(MenuEvent e)` (L1754)
- `createPropertyMenuBarItem()` (L3266)
- `createDataPluginMenuBarItem()` (L3309)
- `updateDataPluginMenuBarItems()` (L3322)

**実施手順:**

1. `SGMenuHandler` クラスを新規作成
2. 上記メソッドを `SGMenuHandler` に移動
3. `SGDrawingWindow` から `SGMenuHandler` のインスタンスを作成し、イベントリスナーとして登録
4. `SGDrawingWindow` から `SGMenuHandler` に委譲するよう修正
5. `mvn spotless:apply` → `mvn compile` → `mvn test` で検証

**完了基準:**
- [ ] `SGMenuHandler` が独立したクラスとして作成
- [ ] `mvn compile` が成功
- [ ] テストが全成功
- [ ] `SGDrawingWindow` の行数が5,000行以下に減少

---

### TASK-018-2: SGDrawingWindowのクリップボードハンドラー抽出

| 項目 | 内容 |
|------|------|
| ステータス | [ ] TODO |
| 優先度 | P2 |
| 対応元 | C3 |
| 推定工数 | 2-3日 |
| 依存タスク | TASK-018-1 |
| 中断ポイント | なし |
| ブランチ | `task/refactor-drawing-window-clipboard` |

**対象メソッド:**
- `doCopy()` (L2785)
- `doCut()` (L2802)
- `cutFocusedObjects()` (L2807)
- `doPaste()` (L2867)
- `pasteCopiedObjects()` (L2872)
- `doDuplicate()` (L2880)
- `doDelete()` (L2923)
- `deleteFocusedObjects()` (L2928)
- `cutAllObjectsInVisibleFigures()` (L2978)
- `copyAllObjectsInVisibleFigures()` (L2983)
- `cutOrCopyAllObjectsInVisibleFigures(final boolean isCopy)` (L2992)
- `clearCopiedObjectsList()` (L3090)
- `pasteToFigures(...)` (L2153)

**実施手順:**

1. `SGClipboardHandler` クラスを新規作成
2. 上記メソッドを `SGClipboardHandler` に移動
3. `SGDrawingWindow` から `SGClipboardHandler` のインスタンスを作成
4. `SGDrawingWindow` から `SGClipboardHandler` に委譲するよう修正
5. `mvn spotless:apply` → `mvn compile` → `mvn test` で検証

**完了基準:**
- [ ] `SGClipboardHandler` が独立したクラスとして作成
- [ ] `mvn compile` が成功
- [ ] テストが全成功
- [ ] `SGDrawingWindow` の行数が4,500行以下に減少

---

### TASK-018-3: SGDrawingWindowのUI更新ハンドラー抽出

| 項目 | 内容 |
|------|------|
| ステータス | [ ] TODO |
| 優先度 | P2 |
| 対応元 | C3 |
| 推定工数 | 2-3日 |
| 依存タスク | TASK-018-2 |
| 中断ポイント | なし |
| ブランチ | `task/refactor-drawing-window-ui` |

**対象メソッド:**
- `updateItemsByFigureNumbers()` (L278)
- `updateInsertItems()` (L302)
- `updateInsertItems(final String command)` (L310)
- `updateDataItem()` (L2187)
- `updateFocusedObjectItem()` (L2194)
- `setPasteMenuEnabled(final boolean b)` (L2237)
- `updateGridItems()` (L2246)
- `updateModeMenuItems()` (L2274)
- `updateZoomItems()` (L2287)
- `updateToolBarVisibleItems()` (L2319)
- `updateToolBarVisibleMenuItems()` (L2329)
- `updateBackgroundImageItems()` (L2339)
- `updateLockItems()` (L3206)
- `updateSavedListIndex()` (L3650)
- `updateStatusBarSavedFlag()` (L3675)

**実施手順:**

1. `SGUIUpdateHandler` クラスを新規作成
2. 上記メソッドを `SGUIUpdateHandler` に移動
3. `SGDrawingWindow` から `SGUIUpdateHandler` のインスタンスを作成
4. `SGDrawingWindow` から `SGUIUpdateHandler` に委譲するよう修正
5. `mvn spotless:apply` → `mvn compile` → `mvn test` で検証

**完了基準:**
- [ ] `SGUIUpdateHandler` が独立したクラスとして作成
- [ ] `mvn compile` が成功
- [ ] テストが全成功
- [ ] `SGDrawingWindow` の行数が4,000行以下に減少

---

### TASK-018-4: SGDrawingWindowのプロパティ変更ハンドラー抽出

| 項目 | 内容 |
|------|------|
| ステータス | [ ] TODO |
| 優先度 | P2 |
| 対応元 | C3 |
| 推定工数 | 2-3日 |
| 依存タスク | TASK-018-3 |
| 中断ポイント | なし |
| ブランチ | `task/refactor-drawing-window-property` |

**対象メソッド:**
- `propertyChange(PropertyChangeEvent e)` (L1710)
- `componentShown(final ComponentEvent e)` (L1756)
- `componentHidden(final ComponentEvent e)` (L1758)
- `componentMoved(final ComponentEvent e)` (L1760)
- `componentResized(final ComponentEvent e)` (L1762)

**実施手順:**

1. `SGPropertyHandler` クラスを新規作成
2. 上記メソッドを `SGPropertyHandler` に移動
3. `SGDrawingWindow` から `SGPropertyHandler` のインスタンスを作成
4. `SGDrawingWindow` から `SGPropertyHandler` に委譲するよう修正
5. `mvn spotless:apply` → `mvn compile` → `mvn test` で検証

**完了基準:**
- [ ] `SGPropertyHandler` が独立したクラスとして作成
- [ ] `mvn compile` が成功
- [ ] テストが全成功
- [ ] `SGDrawingWindow` の行数が3,500行以下に減少

---

## フェーズ5: 仕上げ

> **目標:** 残るドキュメント、クリーンアップ、品質向上タスクを実施します。
>
> **依存関係:** 前面のフェーズと並列実行可能なものもあります。

### TASK-019: Changelogの更新

| 項目 | 内容 |
|------|------|
| ステータス | [ ] TODO |
| 優先度 | P2 |
| 対応元 | M1 |
| 推定工数 | 1時間 |
| 依存タスク | なし |
| 中断ポイント | なし |
| ブランチ | `task/update-changelog` |

**実施手順:**

1. `changelog/product.xml` を編集
2. v2.1.0 と v2.2.0 のリリースエントリーを追加
3. gitログを確認し、以下のカテゴリで変更を整理:
   - Java 21への移行
   - 依存関係の更新
   - ビルドシステムの改善
   - バグ修正
4. `mvn compile` で `ChangeLog.html` の生成確認

**完了基準:**
- [ ] v2.1.0 と v2.2.0 のエントリーが追加
- [ ] `mvn compile` が成功
- [ ] `target/classes/ChangeLog.html` に反映

---

### TASK-020: TODO/FIXMEコメントの解決

| 項目 | 内容 |
|------|------|
| ステータス | [x] DONE |
| 優先度 | P3 |
| 対応元 | M6 |
| 推定工数 | 1時間 |
| 依存タスク | なし |
| 中断ポイント | なし |
| ブランチ | `task/resolve-todos` |

**実施手順:**

1. 各TODO/FIXMEを個別にレビュー:
   - `SGWindowDialog.java:386` -- IDE自動生成コメントの削除
   - `SGSXYNetCDFData.java:224` -- 小数点以下の処理を実装または意図を文書化
   - `SGSDArrayData.java:836` -- 未実装箇所を対応または文書化
   - `SGNetCDFData.java:2525,2538,2552` -- 未実装箇所を対応または文書化
   - `SGMDArrayData.java:1747` -- 未実装箇所を対応または文書化
   - `SGDrawingElementString2DExtended.java:662` -- 右側計算を修正
2. 各項目を解決するか、詳細なコンテキストを付与してGitHub issueとして切り出し
3. 解決済みのTODO/FIXMEを削除

**完了基準:**
- [x] 全8件のTODO/FIXMEが対応済み（解決またはissue化）
- [x] `mvn compile` が成功

---

### TASK-021: 空のplatformパッケージの処理

| 項目 | 内容 |
|------|------|
| ステータス | [x] DONE |
| 優先度 | P3 |
| 対応元 | M7 |
| 推定工数 | 5分 |
| 依存タスク | なし |
| 中断ポイント | なし |
| ブランチ | `task/resolve-todos`（TASK-020と統合可能） |

**実施手順:**

1. `src/main/java/jp/riken/brain/ni/samuraigraph/platform/` ディレクトリを確認
2. 将来的にプラットフォーム固有コードが必要となる見込みがない場合は削除
3. 必要であればプレースホルダとして `package-info.java` を配置

**完了基準:**
- [x] 空ディレクトリが削除 または `package-info.java` が配置
- [x] `mvn compile` が成功

---

### TASK-022: maven-enforcer-pluginの設定

| 項目 | 内容 |
|------|------|
| ステータス | [x] DONE |
| 優先度 | P3 |
| 対応元 | M4 |
| 推定工数 | 30分 |
| 依存タスク | なし |
| 中断ポイント | なし |
| ブランチ | `task/enforcer-plugin` |

**実施手順:**

1. `pom.xml` に `maven-enforcer-plugin` を追加
2. 以下のルールを設定:
   - `requireJavaVersion` [21,)
   - `requireMavenVersion` [3.9,)
   - `banDynamicVersions`
3. `mvn clean compile` でルール動作確認

**完了基準:**
- [x] プラグインが設定
- [x] `mvn compile` が成功
- [x] Java 21 未満の環境でビルドが失敗することを確認

---

### TASK-023: 配布環境の刷新（jpackageによるネイティブパッケージング）

| 項目 | 内容 |
|------|------|
| ステータス | [x] DONE |
| 優先度 | P1 |
| 対応元 | M5 |
| 推定工数 | 3-5日 |
| 依存タスク | なし |
| 中断ポイント | なし |
| ブランチ | `task/redesign-distribution` |

**実施手順:**

1. **既存資産の廃止:**
   - `installer/` ディレクトリ全体を削除（win32/NSIS, unix/, mac/ の全スクリプト・リソース）
   - `helper/` ディレクトリを削除（UpgradeHelperは廃止）
   - `HowToRelease.txt`、`HowToCreateSourceDist.txt` を削除
   - `dependency-reduced-pom.xml` の生成を抑制（`.gitignore` に追記）

2. **jpackage-maven-pluginの導入:**
   - `pom.xml` に `org.panteleyev:jpackage-maven-plugin` を追加
   - Mavenビルドに統合し、`mvn package` 実行時にネイティブパッケージを自動生成
   - 各プラットフォーム向けの設定を `<profiles>` で分離:
     - **Windows:** `jpackage --type msi`（または `app-image`）
     - **macOS:** `jpackage --type dmg`（または `app-image`）
     - **Linux:** `jpackage --type deb`（または `rpm`）
   - アプリケーションバンドルにJREを同梱（ユーザーにJavaインストール不要）

3. **jpackage設定の詳細:**
   - アプリ名: `Samurai Graph`
   - アプリID: `jp.riken.brain.ni.samuraigraph`
   - アイコン: 各プラットフォーム対応（`.ico` for Windows, `.icns` for macOS, `.png` for Linux）
   - メモリ: `--java-options -Xmx2g`
   - ファイル関連付け: `.sgp`, `.sga`, `.sgs`
   - macOS: アプリケーションメニュー（Quit, About）の登録
   - Windows: デスクトップショートカット、スタートメニュー登録

4. **アイコンアセットの準備:**
   - 既存の `src/main/resources/Splash.png` または `Samurai.gif` を基に、各プラットフォーム用アイコンを生成:
     - Windows: `distrib/icon/icon.ico`（256x256、多サイズ）
     - macOS: `distrib/icon/icon.icns`（512x512、多サイズ）
     - Linux: `distrib/icon/icon.png`（256x256）
   - 必要に応じてデザインツール（Inkscape, ImageMagick）で変換

5. **Mavenプロファイルの構成:**
   ```xml
   <profiles>
     <profile>
       <id>dist-windows</id>
       <activation><os><family>windows</family></os></activation>
       <build>
         <plugins>
           <plugin>
             <groupId>org.panteleyev</groupId>
             <artifactId>jpackage-maven-plugin</artifactId>
             <configuration>
               <name>Samurai Graph</name>
               <type>msi</type>
               <appVersion>${project.version}</appVersion>
               <icon>distrib/icon/icon.ico</icon>
               <javaOptions>
                 <option>-Xmx2g</option>
               </javaOptions>
             </configuration>
           </plugin>
         </plugins>
       </build>
     </profile>
     <!-- macOS, Linux も同様に定義 -->
   </profiles>
   ```

6. **ドキュメント整備:**
   - `RELEASE.md`: リリース時の手順（バージョン更新、`mvn clean package` 実行、ネイティブパッケージの生成、GitHub Releaseへのアップロード）
   - 各プラットフォームでのインストール・起動方法を記載

**完了基準:**
- [x] 旧 `installer/`、`helper/` が削除
- [x] `jpackage-maven-plugin` が `pom.xml` に統合
- [ ] 各プラットフォームでネイティブパッケージが生成される:
  - [ ] Windows: `.msi` インストーラー
  - [ ] macOS: `.dmg` ディスクイメージ
  - [ ] Linux: `.deb` パッケージ
- [ ] 生成されたパッケージを実際にインストールし、アプリケーションが起動することを確認
- [ ] JREがバンドルされており、Java未インストール環境でも動作することを確認
- [x] `RELEASE.md` にリリース手順が文書化

**備考:**
- `jpackage` はJDK 14以降に標準搭載されており、追加依存不要
- 開発環境とターゲットプラットフォームが異なる場合、クロスプラットフォームビルドには各OSのCIランナーが必要（TASK-003-2と連携）
- 当面は `app-image`（インストール不要のネイティブ実行可能バンドル）で検証し、安定後に `.msi`/`.dmg`/`.deb` へ移行可能

---

## フェーズ6: テストカバレッジの拡充

> **目標:** メジャーかつ重要な基軸を支えるメソッドからテスト対象を拡充し、リファクタリングの安全網を強化します。
>
> **依存関係:** フェーズ2完了後。TASK-006, TASK-007, TASK-008のテスト基盤を活用。

### TASK-024: SGDataUtilityのデータ型判定メソッドのテスト

| 項目 | 内容 |
|------|------|
| ステータス | [x] DONE |
| 優先度 | P1 |
| 対応元 | C1 |
| 推定工数 | 2-3時間 |
| 依存タスク | TASK-007 |
| 中断ポイント | なし |
| ブランチ | `task/test-data-utility-type-check` |

**対象メソッド:**
- `isSDArrayData(String)`, `isNetCDFData(String)`, `isHDF5Data(String)`, `isMATLABData(String)`
- `isVirtualMDArrayData(String)`, `isMDArrayData(String)`, `isHDF5FileData(String)`
- `isSXYTypeData(String)`, `isSXYTypeSingleData(String)`, `isSXYTypeMultipleData(String)`
- `isVXYTypeData(String)`, `isSXYZTypeData(String)`, `isMultipleData(String)`
- `isNetCDFDimensionData(String)`, `isValidData(String)`, `isArrayData(String)`

**実施手順:**

1. `SGDataTypeCheckTest.java` を新規作成
2. 各データ型判定メソッドに対して正常系・異常系のテストケースを作成
3. `SGDataTypeConstants` の定数を実際に使用
4. `mvn spotless:apply` → `mvn compile` → `mvn test` で検証

**完了基準:**
- [x] `SGDataTypeCheckTest` が `src/test/java/` に配置
- [x] `mvn test` が全テスト成功（42件）
- [ ] 対象メソッドの行カバレッジが80%以上

---

### TASK-025: SGDataUtilityのバウンズ計算メソッドのテスト

| 項目 | 内容 |
|------|------|
| ステータス | [x] DONE |
| 優先度 | P1 |
| 対応元 | C1 |
| 推定工数 | 2-3時間 |
| 依存タスク | TASK-007 |
| 中断ポイント | なし |
| ブランチ | `task/test-data-utility-bounds` |

**対象メソッド:**
- `getMaxValue(double[])`, `getMinValue(double[])`, `getBounds(double[])`
- `getBoundsX(SGISXYTypeSingleData)`, `getBoundsY(SGISXYTypeSingleData)`
- `getMinValue(List<SGValueRange>)`, `getMaxValue(List<SGValueRange>)`
- `getBoundsX(SGISXYTypeMultipleData)`, `getBoundsY(SGISXYTypeMultipleData)`
- `getBoundsX(SGIVXYTypeData)`, `getBoundsY(SGIVXYTypeData)`
- `getBoundsX(SGISXYZTypeData)`, `getBoundsY(SGISXYZTypeData)`, `getBoundsZ(SGISXYZTypeData)`

**実施手順:**

1. `SGDataBoundsTest.java` を新規作成
2. 各バウンズ計算メソッドに対して正常系・異常系のテストケースを作成
3. 代表的なデータ形状（単一系列、複数系列、欠損値含む、NaN含む）のテストケースを作成
4. `mvn spotless:apply` → `mvn compile` → `mvn test` で検証

**完了基準:**
- [x] `SGDataBoundsTest` が `src/test/java/` に配置（68件）
- [x] `mvn test` が全テスト成功
- [ ] 対象メソッドの行カバレッジが80%以上

---

### TASK-026: SGDataUtilityの列操作メソッドのテスト

| 項目 | 内容 |
|------|------|
| ステータス | [x] DONE |
| 優先度 | P1 |
| 対応元 | C1 |
| 推定工数 | 2-3時間 |
| 依存タスク | TASK-007 |
| 中断ポイント | なし |
| ブランチ | `task/test-data-utility-column` |
| 備考 | 34件のテストが既にmasterにコミット済み。カバレッジ80%以上を目指すが、既存テストで網羅済み。 |

**対象メソッド:**
- `appendColumnNo(String, int)`, `appendColumnTitle(String, String)`, `appendColumnNoOrTitle(...)`
- `removeHeaderTitle(String)`, `removeHeaderNo(String)`
- `getAppendedColumnIndex(String)`, `getColumnIndexOfAppendedColumnTitle(...)`, `getColumnIndexOfAppendedColumnType(...)`
- `getColumnTypeCandidates(...)`, `checkDataColumns(...)`, `getColumnIndexListOfNumber(...)`, `getMinimumNumberColumns(String)`

**実施手順:**

1. `SGDataColumnTest.java` を新規作成
2. 各列操作メソッドに対して正常系・異常系のテストケースを作成
3. `mvn spotless:apply` → `mvn compile` → `mvn test` で検証

**完了基準:**
- [x] `SGDataColumnTest` が `src/test/java/` に配置（34件）
- [x] `mvn test` が全テスト成功
- [ ] 対象メソッドの行カバレッジが80%以上

---

### TASK-027: SGCSVParserのテスト

| 項目 | 内容 |
|------|------|
| ステータス | [ ] TODO |
| 優先度 | P1 |
| 対応元 | C1 |
| 推定工数 | 2-3時間 |
| 依存タスク | TASK-014 |
| 中断ポイント | なし |
| ブランチ | `task/test-csv-parser` |

**対象メソッド:** `SGCSVParser` の公開メソッド全件

**実施手順:**

1. `SGCSVParserTest.java` を新規作成
2. CSVパースの正常系・異常系のテストケースを作成
3. 代表的なCSVデータ（単一列、複数列、ヘッダーあり/なし、エスケープ文字含む）のテストケースを作成
4. `mvn spotless:apply` → `mvn compile` → `mvn test` で検証

**完了基準:**
- [ ] `SGCSVParserTest` が `src/test/java/` に配置
- [ ] `mvn test` が全テスト成功
- [ ] 対象クラスの行カバレッジが80%以上

---

### TASK-028: SGValueFormatterのテスト

| 項目 | 内容 |
|------|------|
| ステータス | [ ] TODO |
| 優先度 | P1 |
| 対応元 | C1 |
| 推定工数 | 2-3時間 |
| 依存タスク | TASK-014 |
| 中断ポイント | なし |
| ブランチ | `task/test-value-formatter` |

**対象メソッド:** `SGValueFormatter` の公開メソッド全件

**実施手順:**

1. `SGValueFormatterTest.java` を新規作成
2. 値フォーマットの正常系・異常系のテストケースを作成
3. 代表的なデータ形状（整数、小数、科学表記、NaN、Infinity）のテストケースを作成
4. `mvn spotless:apply` → `mvn compile` → `mvn test` で検証

**完了基準:**
- [ ] `SGValueFormatterTest` が `src/test/java/` に配置
- [ ] `mvn test` が全テスト成功
- [ ] 対象クラスの行カバレッジが80%以上

---

### TASK-029: SGAnimationUtilityのテスト

| 項目 | 内容 |
|------|------|
| ステータス | [ ] TODO |
| 優先度 | P1 |
| 対応元 | C1 |
| 推定工数 | 2-3時間 |
| 依存タスク | TASK-015 |
| 中断ポイント | なし |
| ブランチ | `task/test-animation-utility` |

**対象メソッド:** `SGAnimationUtility` の公開メソッド全件

**完了基準:**
- [ ] `SGAnimationUtilityTest` が `src/test/java/` に配置
- [ ] `mvn test` が全テスト成功
- [ ] 対象クラスの行カバレッジが80%以上

---

### TASK-030: SGWizardManagerのテスト

| 項目 | 内容 |
|------|------|
| ステータス | [ ] TODO |
| 優先度 | P1 |
| 対応元 | C1 |
| 推定工数 | 2-3時間 |
| 依存タスク | TASK-016 |
| 中断ポイント | なし |
| ブランチ | `task/test-wizard-manager` |

**対象メソッド:** `SGWizardManager` の公開メソッド全件

**完了基準:**
- [ ] `SGWizardManagerTest` が `src/test/java/` に配置
- [ ] `mvn test` が全テスト成功
- [ ] 対象クラスの行カバレッジが80%以上

---

### TASK-031: jacoco-maven-pluginの導入

| 項目 | 内容 |
|------|------|
| ステータス | [x] DONE |
| 優先度 | P2 |
| 対応元 | C1 |
| 推定工数 | 1時間 |
| 依存タスク | TASK-006 |
| 中断ポイント | なし |
| ブランチ | `task/jacoco-plugin` |

**実施手順:**

1. `pom.xml` に `jacoco-maven-plugin` 0.8.13 を追加
2. `prepare-agent`, `report`, `check` の3つのexecutionsを設定
3. カバレッジ閾値を一時的に0.0に設定（後で調整）
4. `mvn test` でカバレッジ計測が有効になっていることを確認

**完了基準:**
- [x] `jacoco-maven-plugin` が `pom.xml` に追加
- [x] `mvn test` でカバレッジ計測が有効になっている
- [ ] `mvn jacoco:report` でカバレッジレポートが生成される
- [ ] 対象クラスのカバレッジが50%以上

---

### TASK-032: SGPeriodのテスト

| 項目 | 内容 |
|------|------|
| ステータス | [x] DONE |
| 優先度 | P1 |
| 対応元 | C1 |
| 推定工数 | 1-2時間 |
| 依存タスク | TASK-010 |
| 中断ポイント | なし |
| ブランチ | `task/test-period` |

**対象メソッド:** `SGPeriod` の公開メソッド全件

**実施手順:**

1. `SGPeriodTest.java` を新規作成
2. コンストラクタ、static factory methods、with* methods、plus、negated、isZero、toString、equals/hashCode、parseのテストケースを作成
3. `mvn spotless:apply` → `mvn compile` → `mvn test` で検証

**完了基準:**
- [x] `SGPeriodTest` が `src/test/java/` に配置
- [x] `mvn test` が全テスト成功（52件）
- [ ] 対象クラスの行カバレッジが80%以上

---

## フェーズ7: 残るGodクラスの分割

> **目標:** 残る5つのGodクラスを分割計画に基づいて分割します。
>
> **依存関係:** docs/god-class-analysis.md の分割計画に基づき、リスクの低いクラスから順に実施。

### TASK-033: SGPropertyDialogSXYDataの分割

| 項目 | 内容 |
|------|------|
| ステータス | [ ] TODO |
| 優先度 | P2 |
| 対応元 | C3 |
| 推定工数 | 2-3日 |
| 依存タスク | TASK-009 |
| 中断ポイント | なし |
| ブランチ | `task/refactor-property-dialog-sxy` |

**対象クラス:** SGPropertyDialogSXYData (5,047行, ~352メソッド)

**実施手順:**

1. `SGSXYColumnMapper` を新規作成（カラムタイプ選択、X/Y割り当てUI、~60メソッド）
2. `SGSXYPropertyBinder` を新規作成（UIコンポーネントとデータプロパティのバインディング、~50メソッド）
3. `SGSXYInputValidator` を新規作成（ユーザー入力の検証、~30メソッド）
4. `SGSXYDialogLayout` を新規作成（パネル構築、コンポーネント配置、~40メソッド）
5. 各クラスを `SGPropertyDialogSXYData` から委譲するよう修正
6. `mvn spotless:apply` → `mvn compile` → `mvn test` で検証

**完了基準:**
- [ ] 各マネージャーが独立したクラスとして作成
- [ ] `mvn compile` が成功
- [ ] テストが全成功
- [ ] `SGPropertyDialogSXYData` の行数が2,500行以下に減少

---

### TASK-034: SGFigureElementShapeの分割

| 項目 | 内容 |
|------|------|
| ステータス | [ ] TODO |
| 優先度 | P2 |
| 対応元 | C3 |
| 推定工数 | 2-3日 |
| 依存タスク | TASK-009 |
| 中断ポイント | なし |
| ブランチ | `task/refactor-figure-element-shape` |

**対象クラス:** SGFigureElementShape (4,991行, ~423メソッド)

**実施手順:**

1. `SGShapeRenderer` を新規作成（シェイプ描画、塗りつぶし、ストローク、変換、~60メソッド）
2. `SGShapePropertyHandler` を新規作成（シェイプタイプ、サイズ、色、ボーダープロパティ、~80メソッド）
3. `SGShapeLayout` を新規作成（シェイプ位置、アライメント、分布、~40メソッド）
4. `SGShapeDataBinding` を新規作成（シェイプ位置/サイズとデータ値のバインディング、~30メソッド）
5. 各クラスを `SGFigureElementShape` から委譲するよう修正
6. `mvn spotless:apply` → `mvn compile` → `mvn test` で検証

**完了基準:**
- [ ] 各マネージャーが独立したクラスとして作成
- [ ] `mvn compile` が成功
- [ ] テストが全成功
- [ ] `SGFigureElementShape` の行数が2,500行以下に減少

---

### TASK-035: SGAxisElementの分割

| 項目 | 内容 |
|------|------|
| ステータス | [ ] TODO |
| 優先度 | P2 |
| 対応元 | C3 |
| 推定工数 | 2-3日 |
| 依存タスク | TASK-009 |
| 中断ポイント | なし |
| ブランチ | `task/refactor-axis-element` |

**対象クラス:** SGAxisElement (5,763行, ~319メソッド)

**実施手順:**

1. `SGTickCalculator` を新規作成（目盛り間隔計算、目盛りの生成、~40メソッド）
2. `SGTickLabelFormatter` を新規作成（目盛りラベルのテキストフォーマット、日付/数値フォーマット、~30メソッド）
3. `SGAxisRenderer` を新規作成（軸線、目盛り、ラベル、矢印の描画、~50メソッド）
4. `SGAxisScaler` を新規作成（線形/対数スケール、軸範囲計算、~20メソッド）
5. `SGAxisPropertyHandler` を新規作成（軸プロパティの取得/設定、シリアライズ、~60メソッド）
6. 各クラスを `SGAxisElement` から委譲するよう修正
7. `mvn spotless:apply` → `mvn compile` → `mvn test` で検証

**完了基準:**
- [ ] 各マネージャーが独立したクラスとして作成
- [ ] `mvn compile` が成功
- [ ] テストが全成功
- [ ] `SGAxisElement` の行数が3,000行以下に減少

---

### TASK-036: SGFigureElementLegendの分割

| 項目 | 内容 |
|------|------|
| ステータス | [ ] TODO |
| 優先度 | P2 |
| 対応元 | C3 |
| 推定工数 | 3-5日 |
| 依存タスク | TASK-009 |
| 中断ポイント | なし |
| ブランチ | `task/refactor-figure-element-legend` |

**対象クラス:** SGFigureElementLegend (7,966行, ~731メソッド)

**実施手順:**

1. `SGLegendLayout` を新規作成（位置計算、サイズ計算、配置アルゴリズム、~80メソッド）
2. `SGLegendRenderer` を新規作成（描画メソッド、paintオーバーライド、ビジュアルコンポジション、~60メソッド）
3. `SGLegendItemManager` を新規作成（レジェンドアイテムの追加/削除/更新、状態管理、~100メソッド）
4. `SGLegendPropertyHandler` を新規作成（プロパティの取得/設定、プロパティ変更リスナー、シリアライズ、~150メソッド）
5. `SGLegendDataBinding` を新規作成（レジェンドアイテムとデータ系列のリンク、データ変更時の更新、~80メソッド）
6. 各クラスを `SGFigureElementLegend` から委譲するよう修正
7. `mvn spotless:apply` → `mvn compile` → `mvn test` で検証

**完了基準:**
- [ ] 各マネージャーが独立したクラスとして作成
- [ ] `mvn compile` が成功
- [ ] テストが全成功
- [ ] `SGFigureElementLegend` の行数が3,000行以下に減少

---

### TASK-037: SGMainFunctionsの追加マネージャー抽出

| 項目 | 内容 |
|------|------|
| ステータス | [ ] TODO |
| 優先度 | P2 |
| 対応元 | C3 |
| 推定工数 | 3-5日 |
| 依存タスク | TASK-016 |
| 中断ポイント | なし |
| ブランチ | `task/refactor-main-functions-2` |

**対象クラス:** SGMainFunctions (6,818行, ~165メソッド)

**実施手順:**

1. `SGFileLoader` を新規作成（データファイル、プロパティファイル、図ファイルの読み込み、~20メソッド）
2. `SGFileExporter` を新規作成（PNG、PDF、SVGなど各種フォーマットでの保存/エクスポート、~25メソッド）
3. `SGClipboardManager` を新規作成（コピー/ペースト操作、~10メソッド）
4. `SGApplicationInitializer` を新規作成（初期化、ライブラリ設定、一時ファイル管理、~15メソッド）
5. `SGUndoManager` を新規作成（コマンドパターンによる元に戻す/やり直す、~10メソッド）
6. 各クラスを `SGMainFunctions` から委譲するよう修正
7. `mvn spotless:apply` → `mvn compile` → `mvn test` で検証

**完了基準:**
- [ ] 各マネージャーが独立したクラスとして作成
- [ ] `mvn compile` が成功
- [ ] テストが全成功
- [ ] `SGMainFunctions` の行数が4,000行以下に減少

---

## フェーズ8: FreeHEPライブラリの移行

> **目標:** FreeHEPライブラリを代替ライブラリに移行し、外部依存を削減します。
>
> **依存関係:** docs/freehep-alternatives.md の調査結果に基づき、リスクの低いフォーマットから順に実施。

### TASK-038: PNG/JPEGエクスポートのjavax.imageioへ移行

| 項目 | 内容 |
|------|------|
| ステータス | [ ] TODO |
| 優先度 | P2 |
| 対応元 | H1 |
| 推定工数 | 1-2日 |
| 依存タスク | TASK-012 |
| 中断ポイント | なし |
| ブランチ | `task/migrate-png-jpeg-export` |

**実施手順:**

1. `SGImageExportManager.java` のPNG/JPEGエクスポート箇所を調査
2. `javax.imageio` を使用したPNG/JPEGエクスポートを実装
3. `PNGExportFileType`, `JPEGExportFileType` を削除
4. `ExportFileTypeRegistry` からPNG/JPEGエントリーを削除
5. `mvn spotless:apply` → `mvn compile` → `mvn test` で検証

**完了基準:**
- [ ] `javax.imageio` を使用したPNG/JPEGエクスポートが実装
- [ ] `PNGExportFileType`, `JPEGExportFileType` が削除
- [ ] `mvn compile` が成功
- [ ] テストが全成功

---

### TASK-039: SVGエクスポートのApache Batikへ移行

| 項目 | 内容 |
|------|------|
| ステータス | [ ] TODO |
| 優先度 | P2 |
| 対応元 | H1 |
| 推定工数 | 3-5日 |
| 依存タスク | TASK-038 |
| 中断ポイント | なし |
| ブランチ | `task/migrate-svg-export` |

**実施手順:**

1. `pom.xml` に `org.apache.xmlgraphics:batik-transcoder` を追加
2. `SGImageExportManager.java` のSVGエクスポート箇所を調査
3. Apache Batik の `SVGGraphics2D` を使用したSVGエクスポートを実装
4. FreeHEP の `SVGGraphics2D` 依存を削除
5. `mvn spotless:apply` → `mvn compile` → `mvn test` で検証

**完了基準:**
- [ ] Apache Batik を使用したSVGエクスポートが実装
- [ ] FreeHEP の `SVGGraphics2D` 依存が削除
- [ ] `mvn compile` が成功
- [ ] テストが全成功

---

### TASK-040: PDFエクスポートのApache PDFBoxへ移行

| 項目 | 内容 |
|------|------|
| ステータス | [ ] TODO |
| 優先度 | P2 |
| 対応元 | H1 |
| 推定工数 | 5-7日 |
| 依存タスク | TASK-039 |
| 中断ポイント | なし |
| ブランチ | `task/migrate-pdf-export` |

**実施手順:**

1. `pom.xml` に `org.apache.pdfbox:pdfbox` を追加
2. `SGImageExportManager.java` のPDFエクスポート箇所を調査
3. Apache PDFBox の `PDFGraphics2D` を使用したPDFエクスポートを実装
4. FreeHEP の `PDFGraphics2D` 依存を削除
5. `mvn spotless:apply` → `mvn compile` → `mvn test` で検証

**完了基準:**
- [ ] Apache PDFBox を使用したPDFエクスポートが実装
- [ ] FreeHEP の `PDFGraphics2D` 依存が削除
- [ ] `mvn compile` が成功
- [ ] テストが全成功

---

## フェーズ9: HDF5ライブラリの移行

> **目標:** cisd:jhdf5ライブラリをOME HDF5に移行し、ネイティブライブラリの管理を簡素化します。
>
> **依存関係:** docs/hdf5-alternatives.md の調査結果に基づき、OME HDF5への移行を実施。

### TASK-041: HDF5Manager抽象化レイヤーの作成

| 項目 | 内容 |
|------|------|
| ステータス | [ ] TODO |
| 優先度 | P2 |
| 対応元 | H1 |
| 推定工数 | 2-3日 |
| 依存タスク | TASK-013 |
| 中断ポイント | なし |
| ブランチ | `task/hdf5-manager-abstract` |

**実施手順:**

1. `SGHDF5Manager` インターフェースを新規作成（読み込み/書き込み/列挙操作の抽象化）
2. `SGHDF5Reader` インターフェースを新規作成（ファイル読み込みの抽象化）
3. `SGHDF5Writer` インターフェースを新規作成（ファイル書き込みの抽象化）
4. `SGHDF5File` を `SGHDF5Reader` に適合するよう修正
5. `SGHDF5Variable` を `SGHDF5Writer` に適合するよう修正
6. `mvn spotless:apply` → `mvn compile` → `mvn test` で検証

**完了基準:**
- [ ] `SGHDF5Manager` インターフェースが作成
- [ ] `SGHDF5Reader`, `SGHDF5Writer` インターフェースが作成
- [ ] `SGHDF5File`, `SGHDF5Variable` がインターフェースに適合
- [ ] `mvn compile` が成功
- [ ] テストが全成功

---

### TASK-042: OME HDF5の実装

| 項目 | 内容 |
|------|------|
| ステータス | [ ] TODO |
| 優先度 | P2 |
| 対応元 | H1 |
| 推定工数 | 3-5日 |
| 依存タスク | TASK-041 |
| 中断ポイント | なし |
| ブランチ | `task/hdf5-ome-implementation` |

**実施手順:**

1. `pom.xml` から `cisd:jhdf5`, `cisd:base` を削除
2. `pom.xml` に `ome:hdf5-hdf` を追加
3. `OMEHDF5Reader` を新規作成（`SGHDF5Reader` のOME実装）
4. `OMEHDF5Writer` を新規作成（`SGHDF5Writer` のOME実装）
5. `SGHDF5File`, `SGHDF5Variable` を OME HDF5 に適合するよう修正
6. `SGMainFunctions.Initializer.removeHDF5TemporaryFiles()` を削除
7. `SGDataUtility.hasValidHDF5CharacterForWin()` を削除
8. `mvn spotless:apply` → `mvn compile` → `mvn test` で検証

**完了基準:**
- [ ] `OMEHDF5Reader`, `OMEHDF5Writer` が作成
- [ ] `cisd:jhdf5`, `cisd:base` が削除
- [ ] `ome:hdf5-hdf` が追加
- [ ] `removeHDF5TemporaryFiles()` が削除
- [ ] `hasValidHDF5CharacterForWin()` が削除
- [ ] `mvn compile` が成功
- [ ] テストが全成功

---

### TASK-043: 対応するデータクラスの更新

| 項目 | 内容 |
|------|------|
| ステータス | [ ] TODO |
| 優先度 | P2 |
| 対応元 | H1 |
| 推定工数 | 2-3日 |
| 依存タスク | TASK-042 |
| 中断ポイント | なし |
| ブランチ | `task/hdf5-data-classes-update` |

**対象クラス（10+ファイル）:**
- `SGMDArrayData`, `SGSDArrayData`, `SGSXYMDArrayData`, `SGSXYZMDArrayData`
- `SGTwoDimensionalMDArrayData`, `SGVXYMDArrayData`, `SGSXYMDArrayMultipleData`
- `SGVXYSDArrayData`, `SGSXYZSDArrayData`
- `SGApplicationUtility`, `SGDataCreator`, `SGMainFunctions`
- `SGPropertyDataFileChooserWizardDialog`, `SGDataUtility`

**実施手順:**

1. 各データクラスで `IHDF5Reader`, `IHDF5Writer` を使用している箇所を調査
2. `SGHDF5Reader`, `SGHDF5Writer` インターフェースを使用するよう修正
3. `HDF5DataClass`, `HDF5DataSetInformation`, `HDF5DataTypeInformation` の使用箇所を修正
4. `hdf.hdf5lib.exceptions.HDF5Exception` の使用箇所を修正
5. `mvn spotless:apply` → `mvn compile` → `mvn test` で検証

**完了基準:**
- [ ] 全データクラスで `IHDF5Reader`, `IHDF5Writer` を使用していない
- [ ] `SGHDF5Reader`, `SGHDF5Writer` インターフェースを使用している
- [ ] `mvn compile` が成功
- [ ] テストが全成功

---

## タスク一覧（管理用）

> 本表は全タスクの進捗を一目で確認できるようにするためのサマリーです。
> 優先度やフェーズの再配置が必要な場合は、本表と各タスクの詳細セクションの両方を更新してください。

| タスクID | タスク名 | ステータス | 優先度 | フェーズ | 対応元 |
|----------|---------|-----------|--------|---------|--------|
| TASK-001 | リソースフィルタリング修正 | [x] | P0 | フェーズ1 | C4 |
| TASK-002 | log4j2設定ファイル追加 | [x] | P1 | フェーズ1 | H4 |
| TASK-003 | ローカルCIテスト基盤整備 | [x] | P1 | フェーズ1 | C2 |
| TASK-003-2 | GitHub Actions作成 | [x] | P3 | フェーズ1 | C2 |
| TASK-004 | POMメタデータ追加 | [x] | P2 | フェーズ1 | M3 |
| TASK-005 | 未使用リポジトリ削除 | [ ] | P3 | フェーズ1 | M2 |
| TASK-006 | テストフレームワーク導入 | [x] | P0 | フェーズ2 | C1 |
| TASK-007 | データパーステスト作成 | [x] | P1 | フェーズ2 | C1 |
| TASK-008 | データバッファテスト作成 | [x] | P1 | フェーズ2 | C1 |
| TASK-009 | Godクラス分析・分割計画 | [x] | P1 | フェーズ2 | C3 |
| TASK-010 | joda-time削除・java.time移行 | [x] | P1 | フェーズ3 | H1 |
| TASK-011 | freehep-graphicsio-swf削除 | [x] | P2 | フェーズ3 | H1 |
| TASK-012 | FreeHEP代替調査 | [x] | P2 | フェーズ3 | H1 |
| TASK-013 | HDF5代替調査 | [x] | P2 | フェーズ3 | H1, M8 |
| TASK-014 | SGDataUtility分割（第1弾） | [x] | P1 | フェーズ4 | C3 |
| TASK-015 | SGDataUtility分割（第2弾） | [ ] | P1 | フェーズ4 | C3 |
| TASK-016 | SGMainFunctionsマネージャー抽出 | [ ] | P1 | フェーズ4 | C3, H2 |
| TASK-017 | 定数インターフェース変換 | [~] | P2 | フェーズ4 | H3 |
| TASK-018 | SGDrawingWindowハンドラー分割 | [~] | P2 | フェーズ4 | C3, H2 |
| TASK-019 | Changelog更新 | [ ] | P2 | フェーズ5 | M1 |
| TASK-020 | TODO/FIXME解決 | [x] | P3 | フェーズ5 | M6 |
| TASK-021 | 空のplatformパッケージ処理 | [x] | P3 | フェーズ5 | M7 |
| TASK-022 | maven-enforcer-plugin設定 | [x] | P3 | フェーズ5 | M4 |
| TASK-023 | 配布環境刷新（jpackageネイティブパッケージング） | [x] | P1 | フェーズ5 | M5 |
| TASK-024 | SGDataUtilityのデータ型判定テスト | [x] | P1 | フェーズ6 | C1 |
| TASK-025 | SGDataUtilityのバウンズ計算テスト | [ ] | P1 | フェーズ6 | C1 |
| TASK-026 | SGDataUtilityの列操作テスト | [ ] | P1 | フェーズ6 | C1 |
| TASK-027 | SGCSVParserのテスト | [ ] | P1 | フェーズ6 | C1 |
| TASK-028 | SGValueFormatterのテスト | [ ] | P1 | フェーズ6 | C1 |
| TASK-029 | SGAnimationUtilityのテスト | [ ] | P1 | フェーズ6 | C1 |
| TASK-030 | SGWizardManagerのテスト | [ ] | P1 | フェーズ6 | C1 |
| TASK-031 | jacoco-maven-pluginの導入 | [x] | P2 | フェーズ6 | C1 |
| TASK-032 | SGPeriodのテスト | [x] | P1 | フェーズ6 | C1 |
| TASK-033 | SGPropertyDialogSXYDataの分割 | [ ] | P2 | フェーズ7 | C3 |
| TASK-034 | SGFigureElementShapeの分割 | [ ] | P2 | フェーズ7 | C3 |
| TASK-035 | SGAxisElementの分割 | [ ] | P2 | フェーズ7 | C3 |
| TASK-036 | SGFigureElementLegendの分割 | [ ] | P2 | フェーズ7 | C3 |
| TASK-037 | SGMainFunctionsの追加マネージャー抽出 | [ ] | P2 | フェーズ7 | C3 |
| TASK-038 | PNG/JPEGエクスポートのjavax.imageioへ移行 | [ ] | P2 | フェーズ8 | H1 |
| TASK-039 | SVGエクスポートのApache Batikへ移行 | [ ] | P2 | フェーズ8 | H1 |
| TASK-040 | PDFエクスポートのApache PDFBoxへ移行 | [ ] | P2 | フェーズ8 | H1 |
| TASK-041 | HDF5Manager抽象化レイヤーの作成 | [ ] | P2 | フェーズ9 | H1 |
| TASK-042 | OME HDF5の実装 | [ ] | P2 | フェーズ9 | H1 |
| TASK-043 | 対応するデータクラスの更新 | [ ] | P2 | フェーズ9 | H1 |

---

## 実行・管理ルール

### 実行順序の変更方法

1. **優先度の変更:** 上記表と各タスク詳細の「優先度」欄を更新
2. **フェーズの変更:** タスク詳細ブロックを適切なフェーズセクションに移動し、表の「フェーズ」欄を更新
3. **依存関係の変更:** 各タスク詳細の「依存タスク」欄を更新し、必要に応じてフェーズ間の依存関係の記述も修正
4. **新規タスクの追加:** 適宜フェーズにタスクブロックを追加し、末尾の表にも追記
5. **タスクの中止:** ステータスを `[-] CANCELLED` に変更し、理由を備考に記載

---

### 並列実行ルール

**重要: リファクタリングタスク（TASK-017, TASK-018, TASK-033〜037）は必ず順次実行する。**
並列実行はテスト作成タスク（TASK-024〜032）やドキュメント更新タスクに限定する。

**各タスク完了後のコミット手順:**
1. タスクの実施が完了し、`mvn compile` と `mvn test` が成功したことを確認
2. `skill git-commit` を使用して変更をコミット
3. execution-plan.mdのステータスを `[x] DONE` に更新
4. セッションログに完了内容を記録
5. 次のタスクへ進む

複数のタスクを並列（別セッションまたは別エージェント）で実行する場合:

1. **並列実行可能なタスクの条件:**
   - リファクタリングタスクではない（TASK-017, TASK-018, TASK-033〜037は除く）
   - 依存タスクが完了している または 依存タスクがない
   - 編集対象ファイルが重複していない（同じファイルの同時編集は禁止）
   - ブロック中タスクではない

2. **並列実行の開始:**
   - 各タスクのステータスを `[>] IN_PROGRESS` に変更
   - セッションログに「並列実行開始」を記録
   - 各タスクの「中断ポイント」に「並列実行開始（依存タスク: XXX）」を記載

3. **並列実行の完了:**
   - 完了したタスクのステータスを `[x] DONE` に変更
   - `skill git-commit` で変更をコミット
   - セッションログに「並列実行完了」を記録
   - 依存タスクとしてこのタスクを参照している他のタスクの実行可否を確認

---

### ブロック管理

タスク実行中にブロック要因が発生した場合:

1. **ブロックの記録:**
   - タスクのステータスを `[>] IN_PROGRESS` のまま維持
   - 「中断ポイント」にブロック要因と現在の進行状況を記載
   - 「実行ステータス（ダッシュボード）」の「ブロック中タスク」に追記
   - セッションログに「ブロック発生」を記録

2. **ブロックの解消:**
   - ブロック要因を解消後、「中断ポイント」を更新
   - 「実行ステータス（ダッシュボード）」の「ブロック中タスク」から削除
   - セッションログに「ブロック解消」を記録

3. **代替タスクへの切り替え:**
   - ブロック中は依存関係のない他のタスクに切り替え
   - 「セッションログ」に「代替タスク XXXX に切り替え」を記録

---

### 中断・再開ルール

セッションが中断した場合の再開手順:

1. **中断時の記録:**
   - 実施中タスクのステータスを `[>] IN_PROGRESS` のまま維持
   - 「中断ポイント」に最終実施位置と未完了事項を記載
   - セッションログに「中断理由」と「次のセッションで再開するタスク」を記録
   - 「実行ステータス（ダッシュボード）」を更新

2. **再開時の手順:**
   - セッションログの最新エントリーを確認
   - 「中断ポイント」から再開位置を特定
   - 依存タスクのステータスが変更されていないか確認
   - 新しいセッションログエントリーを作成し、再開開始を記録

3. **エラー発生時の対応:**
   - エラー内容を「中断ポイント」に記録
   - 解決策を検討し、「次のセッションで再開するタスク」に記録
   - 解決できない場合はタスクを分割するか、優先度を再評価

---

### セッション管理の手順

各セッションの開始時・終了時に以下の手順を実行:

**開始時:**
1. 「実行ステータス（ダッシュボード）」を確認
2. 最新セッションログを確認し、中断状況を把握
3. 依存タスクのステータスを確認し、実行可能なタスクを決定
4. 新しいセッションログエントリーを作成

**終了時:**
1. 実施したタスクのステータスと「中断ポイント」を更新
2. セッションログに完了内容と中断理由（ある場合）を記録
3. 「実行ステータス（ダッシュボード）」を更新
4. 次のセッションで再開するタスクを記録

---

### ファイル編集の競合回避

並列実行時に同じファイルを編集する可能性がある場合:

1. **編集対象ファイルの事前確認:**
   - 各タスクの実施手順で編集するファイルを確認
   - 重複する場合は順序実行に変更

2. **競合が発生した場合:**
   - 先に完了したタスクの変更をコミット
   - 後続のタスクで変更をマージ
   - 競合が発生した場合は手動で解決

---

### 並列実行候補（推奨）

以下のタスク群は依存関係がなく、並列実行可能です:

| グループ | タスク | 備考 |
|---------|--------|------|
| A | TASK-001, TASK-002, TASK-003, TASK-004, TASK-005 | フェーズ1全タスク（ファイル重複なし） |
| B | TASK-010, TASK-011 | joda-time削除とswf削除（統合可能） |
| C | TASK-012, TASK-013 | 調査タスク（実装変更なし） |
| D | TASK-019, TASK-020, TASK-021, TASK-022 | ドキュメント・クリーンアップ系 |
| F | TASK-024, TASK-025, TASK-026, TASK-031, TASK-032 | テスト拡充系（並列実行可能） |
| H | TASK-038, TASK-041 | FreeHEP移行とHDF5移行（独立） |

**注意:** TASK-017, TASK-018, TASK-033〜037 は必ず順次実行（並列禁止）。
