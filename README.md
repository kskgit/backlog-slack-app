
# Slack-to-Backlog とは
Slack-to-Backlog は SlackのメッセージをBacklogの課題に登録出来るSlack appです

### 今回のSlack Appを作成した理由
- インテグレーションチームの募集ということで、外部サービスとヌーラボ製品を繋ぐようなサービスを作りたいと考えました
- その中でも、自分自身がSlackとBacklogを使用して業務を行っていた経験から、ユースケースが想像し易いため今回のSlack Appを作成しました

# 使用方法
1. 以下リンクをクリック
   1. https://lit-eyrie-67457.herokuapp.com/slack/oauth/start
2. `Add to Slack`ボタンが表示されるためクリック
3. 後はSlack Appの追加画面へ遷移するため、チャンネルを指定し追加いただければ使用可能になります
   1. Slack ワークスペースの管理者権限が必要です
   2. くまモンのLGTM画像が表示されるとチャンネルへのインストール成功です 

### 注意点
- 何かしらプロジェクトが作成されているスペースを想定しています。
    - プロジェクト未作成のスペースの場合、キー登録時にエラーとなります

### 既知の不具合
- インストール後一定時間が経過するとチャンネルへの再インストールが求められます。
  - その際はお手数ですが再インストールいただくとまた使用可能になります

### 動作イメージ
https://nulab-exam.backlog.jp/alias/wiki/1025473

# 主なパッケージの役割分担
全体的にDIを用いてパッケージ間は疎結合となるよう工夫しました

### repository
##### 外部API通信用の処理を記載
- clientパッケージ配下のクラスを経由して外部通信を行う

#### client
##### クライアントライブラリをラップ
- clientライブラリ仕様変更の影響範囲を小さくする目的

### service
##### ビジネスロジックを記載
- Slackからのイベントを受け取り各repositoryへ処理を投げ、結果をチャットへ返す

### endpoint
##### slackのイベントを登録

# やっていないこと
今回の期間で最低限動くサービスを形にすることを優先した結果、対応しなかったものです

### 機能面
##### スペースIDとキーの更新機能
- 一度登録したスペースIDとキーを更新出来ない仕様となっているため、現在の仕様では1スペースのみでしか利用出来ない

### 実装面

##### コード上にTODOコメントで残しているもの

##### 例外処理・ロギング処理
- 外部API呼び出し時のエラーハンドリング
  - エラー時はSlack モーダルのデフォルトのエラーが出ますが、本来であればエラーのより詳細な情報をモーダルに返すのがベター
- 入力値不正時のエラーハンドリング（バリデーション）

##### 全クラス・全メソッドへのテストコード実装
- 処理が比較的複雑な`repository`・`service`へ優先してテストコードを実装

# その他
- 実装上で意図が伝わりづらそうな箇所は`NOTE:`でコメントしております